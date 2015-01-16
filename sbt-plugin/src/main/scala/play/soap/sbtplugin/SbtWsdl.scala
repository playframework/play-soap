package play.soap.sbtplugin

import org.apache.cxf.tools.common.ToolContext
import org.apache.cxf.tools.util.OutputStreamCreator
import org.apache.cxf.tools.wsdlto.WSDLToJava
import play.PlayImport.PlayKeys
import play.{Play, PlayJava}
import sbt._
import sbt.Keys._
import sbt.plugins.JvmPlugin

object Imports {
  object WsdlKeys {

    val wsdlUrls = SettingKey[Seq[URL]]("wsdlUrls", "A set of URLs to get the WSDL from.")
    val futureApi = SettingKey[FutureApi]("wsdlFutureApi", "Which Future API to use in generated interfaces.")

    val packageName = SettingKey[Option[String]]("wsdlPackageName", "The default package name to generate the WSDL interfaces into if no explicit namespace mapping is set. Uses the default namespace described in the WSDL if none set.")
    val packageMappings = SettingKey[Seq[(String, String)]]("wsdlPackageMappings", "Mappings of namespaces to package names to generate the interfaces into.")

    val serviceName = SettingKey[Option[String]]("wsdlServiceName", "The service to generate.")

    val wsdlToCodeArgs = SettingKey[Seq[String]]("wsdlToCodeArgs", "Additional arguments that should be passed to wsdl2java")
    val wsdlToJavaHelp = TaskKey[Unit]("wsdlHelp", "Runs the wsdltocode help to get the list of available options")
    val wsdlTasks = TaskKey[Seq[WsdlTask]]("wsdlTasks", "The WSDL tasks. By default, this will include one task for each wsdl detected or configured. If multiple different configurations are needed for wsdltojava invocation, they can be added to this.")
    val wsdlToCode = TaskKey[WsdlTaskResult]("wsdlToCode", "Generate code from the WSDLs")
    val playSoapVersion = SettingKey[String]("wsdlPlaySoapVersion", "The version of Play soap to use")
    val playPlugins = TaskKey[Seq[String]]("wsdlPlayPlugins", "The Play plugins")

    trait FutureApi {
      def fqn: String
    }
    case object ScalaFutureApi extends FutureApi {
      val fqn = "scala.concurrent.Future"
    }
    case object PlayJavaFutureApi extends FutureApi {
      val fqn = "play.libs.F.Promise"
    }

    case class WsdlTask(url: URL,
                        futureApi: FutureApi = ScalaFutureApi,
                        packageName: Option[String] = None,
                        packageMappings: Map[String, String] = Map.empty,
                        serviceName: Option[String] = None,
                        args: Seq[String] = Nil)
    case class WsdlTaskResult(sources: Seq[File], plugins: Seq[String])
  }

}

import Imports.WsdlKeys._

object SbtWsdl extends AutoPlugin {
  override def trigger = allRequirements
  override def requires = JvmPlugin

  val autoImport = Imports

  override def projectSettings: Seq[Setting[_]] =
    dependencySettings ++
    inConfig(Compile)(wsdlSettings) ++
    inConfig(Test)(wsdlSettings) ++
    defaultSettings

  def wsdlSettings: Seq[Setting[_]] = Seq(
    wsdlUrls := Nil,

    includeFilter in wsdlToCode := "*.wsdl",
    excludeFilter in wsdlToCode := HiddenFileFilter,
    sourceDirectories in wsdlToCode := Seq(sourceDirectory.value / "wsdl"),

    sources in wsdlToCode <<= Defaults.collectFiles(
      sourceDirectories in wsdlToCode,
      includeFilter in wsdlToCode,
      excludeFilter in wsdlToCode
    ),

    watchSources in Defaults.ConfigGlobal <++= sources in wsdlToCode,

    target in wsdlToCode := crossTarget.value / "wsdl" / Defaults.nameForSrc(configuration.value.name),

    wsdlTasks <<= wsdlTasksTask,

    wsdlToCode <<= wsdlToCodeTask,

    playPlugins := wsdlToCode.value.plugins,

    sourceGenerators += Def.task(wsdlToCode.value.sources).taskValue,
    managedSourceDirectories += (target in wsdlToCode).value / "sources",
    resourceGenerators += Def.task {
      val plugins = playPlugins.value
      if (plugins.nonEmpty) {
        val contents = playPlugins.value.mkString("\n")
        val pluginsFile = (target in wsdlToCode).value / "resources" / "play.plugins"
        IO.write(pluginsFile, contents)
        Seq(pluginsFile)
      } else {
        Nil
      }
    }.taskValue,
    managedResourceDirectories += (target in wsdlToCode).value / "resources"
  )

  def defaultSettings: Seq[Setting[_]] = Seq(
    futureApi := ScalaFutureApi,
    packageName := None,
    packageMappings := Nil,
    serviceName := None,
    wsdlToCodeArgs := Nil,
    wsdlToJavaHelp := {
      withContextClassLoader {
        new WSDLToJava(Array("-help")).run(new ToolContext())
      }
    }
  )

  def dependencySettings: Seq[Setting[_]] = Seq(
    playSoapVersion := Version.clientVersion,
    libraryDependencies += "com.typesafe.play" %% "play-soap-client" % playSoapVersion.value
  )

  private def wsdlTasksTask = Def.task {
    val allUrls = (sources in wsdlToCode).value.map(_.toURI.toURL) ++ wsdlUrls.value
    allUrls.map { url =>
      WsdlTask(url, futureApi.value, packageName.value, packageMappings.value.toMap,
        serviceName.value, wsdlToCodeArgs.value)
    }
  }

  private def wsdlToCodeTask = Def.task {
    val tasks = wsdlTasks.value
    val outdir = (target in wsdlToCode).value
    val sources = outdir / "sources"

    withContextClassLoader {

      var createdFiles = Set.empty[File]
      var plugins = Set.empty[String]

      tasks.foreach { task =>
        val packageArg = task.packageName.map(pkg => Seq("-p", pkg)).getOrElse(Nil)
        val packageArgs = task.packageMappings.flatMap {
          case (namespace, pkg) => Seq("-p", s"$namespace=$pkg")
        }
        val serviceNameArg = task.serviceName.map(sn => Seq("-sn", sn)).getOrElse(Nil)

        val args = Array(
          "-frontend", "play",
          "-d", sources.getAbsolutePath
        ) ++ packageArg ++ packageArgs ++ serviceNameArg ++ task.args :+ task.url.toString

        val toolContext = new ToolContext()

        toolContext.put(classOf[FutureApi], task.futureApi)

        // Custom OutputStreamCreator is used to track every file generated
        toolContext.put(classOf[OutputStreamCreator], new OutputStreamCreator() {
          override def createOutputStream(file: File) = {
            createdFiles += file
            super.createOutputStream(file)
          }
        })

        new WSDLToJava(args).run(toolContext)

        val ps = Option(toolContext.get("play.plugins").asInstanceOf[Seq[String]]).getOrElse(Nil)
        plugins ++= ps
      }

      WsdlTaskResult(createdFiles.toSeq, plugins.map("900:" + _).toSeq)
    }
  }

  private def withContextClassLoader[T](block: => T): T = {
    val oldClassLoader = Thread.currentThread.getContextClassLoader

    try {
      Thread.currentThread.setContextClassLoader(SbtWsdl.getClass.getClassLoader)
      block
    } finally {
      Thread.currentThread.setContextClassLoader(oldClassLoader)
    }

  }

  private def readResourceProperty(resource: String, property: String): String = {
    val props = new java.util.Properties
    val stream = getClass.getClassLoader.getResourceAsStream(resource)
    try { props.load(stream) }
    catch { case e: Exception => }
    finally { if (stream ne null) stream.close() }
    props.getProperty(property)
  }

}

/**
 * Auto plugin that activates when PlayJava is in use to switch the future API to use to be the Play Java future API.
 */
object SbtWsdlJava extends AutoPlugin {
  override def trigger = allRequirements
  override def requires = SbtWsdl && PlayJava

  override def projectSettings = Seq(
    futureApi := PlayJavaFutureApi
  )
}

object SbtWsdlPlay extends AutoPlugin {
  override def trigger = allRequirements
  override def requires = SbtWsdl && Play

  override def projectSettings = Seq(
    sourceDirectories in (Compile, wsdlToCode) := Seq(PlayKeys.confDirectory.value / "wsdls")
  )
}