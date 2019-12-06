/*
 * Copyright (C) 2015-2019 Lightbend Inc. <https://www.lightbend.com>
 */
package play.soap.sbtplugin

import org.apache.commons.codec.digest.DigestUtils
import org.apache.cxf.tools.common.ToolContext
import org.apache.cxf.tools.util.OutputStreamCreator
import org.apache.cxf.tools.wsdlto.WSDLToJava
import play.sbt.PlayWeb
import play.sbt.PlayJava
import sbt._
import sbt.Keys._
import sbt.plugins.JvmPlugin

/**
 * The imports that the WSDL plugin brings in to all .sbt files
 */
object Imports {

  /**
   * The keys that the WSDL plugin uses
   */
  object WsdlKeys {
    val wsdlUrls  = SettingKey[Seq[URL]]("wsdlUrls", "A set of URLs to get the WSDL from.")
    val futureApi = SettingKey[FutureApi]("wsdlFutureApi", "Which Future API to use in generated interfaces.")

    val packageName = SettingKey[Option[String]](
      "wsdlPackageName",
      "The default package name to generate the WSDL interfaces into if no explicit namespace mapping is set. Uses the default namespace described in the WSDL if none set."
    )
    val packageMappings = SettingKey[Seq[(String, String)]](
      "wsdlPackageMappings",
      "Mappings of namespaces to package names to generate the interfaces into."
    )

    val serviceName = SettingKey[Option[String]]("wsdlServiceName", "The service to generate.")

    val wsdlToCodeArgs =
      SettingKey[Seq[String]]("wsdlToCodeArgs", "Additional arguments that should be passed to wsdl2java")
    val wsdlToJavaHelp = TaskKey[Unit]("wsdlHelp", "Runs the wsdltocode help to get the list of available options")
    val wsdlTasks = TaskKey[Seq[WsdlTask]](
      "wsdlTasks",
      "The WSDL tasks. By default, this will include one task for each wsdl detected or configured. If multiple different configurations are needed for wsdltojava invocation, they can be added to this."
    )
    val wsdlToCode      = TaskKey[WsdlTaskResult]("wsdlToCode", "Generate code from the WSDLs")
    val playSoapVersion = SettingKey[String]("wsdlPlaySoapVersion", "The version of Play soap to use")

    /**
     * The future API
     */
    trait FutureApi {

      /**
       * The fully qualify class name of the future API
       */
      def fqn: String

      /**
       * The type that the future returns if the method returns void
       */
      def voidType: String
    }

    /**
     * The Scala Future API
     */
    case object ScalaFutureApi extends FutureApi {
      val fqn      = "scala.concurrent.Future"
      val voidType = "scala.Unit"
    }

    /**
     * The Play Java Promise API
     */
    case object PlayJavaFutureApi extends FutureApi {
      val fqn      = "java.util.concurrent.CompletionStage"
      val voidType = "Void"
    }

    /**
     * A task for wsdl2code to run
     *
     * @param url The url of the WSDL
     * @param futureApi The future API to use
     * @param packageName The name of the package to generate into, if overriding is desired
     * @param packageMappings Mappings of namespaces to package names to se
     * @param serviceName The name of the service to generate
     * @param args Any additional args for wsdl2code
     */
    case class WsdlTask(
        url: URL,
        futureApi: FutureApi = ScalaFutureApi,
        packageName: Option[String] = None,
        packageMappings: Map[String, String] = Map.empty,
        serviceName: Option[String] = None,
        args: Seq[String] = Nil
    )

    /**
     * The result of running the wsdltocode task
     *
     * @param sources The sources generated
     * @param plugins The plugins generated
     */
    case class WsdlTaskResult(sources: Seq[File], plugins: Seq[String])
  }
}

import Imports.WsdlKeys._

/**
 * The sbt WSDL plugin.
 */
object SbtWsdl extends AutoPlugin {
  override def trigger  = allRequirements
  override def requires = JvmPlugin

  val autoImport = Imports

  override def projectSettings: Seq[Setting[_]] =
    dependencySettings ++
      inConfig(Compile)(wsdlSettings) ++
      inConfig(Test)(wsdlSettings) ++
      defaultSettings

  /**
   * The WSDL settings to be scoped to a particular config
   */
  def wsdlSettings: Seq[Setting[_]] = Seq(
    wsdlUrls := Nil,
    includeFilter in wsdlToCode := "*.wsdl",
    excludeFilter in wsdlToCode := HiddenFileFilter,
    sourceDirectories in wsdlToCode := Seq(sourceDirectory.value / "wsdl"),
    sources in wsdlToCode := Defaults
      .collectFiles(
        sourceDirectories in wsdlToCode,
        includeFilter in wsdlToCode,
        excludeFilter in wsdlToCode
      )
      .value,
    watchSources in Defaults.ConfigGlobal ++= (sources in wsdlToCode).value,
    target in wsdlToCode := target.value / "wsdl" / Defaults.nameForSrc(configuration.value.name),
    wsdlTasks := wsdlTasksTask.value,
    wsdlToCode := wsdlToCodeTask.value,
    sourceGenerators += Def.task(wsdlToCode.value.sources).taskValue,
    managedSourceDirectories += (target in wsdlToCode).value / "sources",
    managedResourceDirectories += (target in wsdlToCode).value / "resources"
  )

  /**
   * The default settings that don't apply to a particular scope
   */
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

  /**
   * The settings that add the play-soap-client to a projects dependencies
   */
  def dependencySettings: Seq[Setting[_]] = Seq(
    playSoapVersion := Version.clientVersion,
    libraryDependencies += "com.typesafe.play" %% "play-soap-client" % playSoapVersion.value
  )

  private def wsdlTasksTask = Def.task {
    val allUrls = (sources in wsdlToCode).value.map(_.toURI.toURL) ++ wsdlUrls.value
    allUrls.map { url =>
      WsdlTask(
        url,
        futureApi.value,
        packageName.value,
        packageMappings.value.toMap,
        serviceName.value,
        wsdlToCodeArgs.value
      )
    }
  }

  private def wsdlToCodeTask = Def.task {
    val cacheDir = streams.value.cacheDirectory
    val tasks    = wsdlTasks.value
    val outdir   = (target in wsdlToCode).value
    val sources  = outdir / "sources"
    val log      = streams.value.log

    import com.typesafe.sbt.web.incremental._

    withContextClassLoader {
      // The result here is a map of WsdlTask to the plugins that were generated
      val (files, plugins) = syncIncremental[WsdlTask, Map[WsdlTask, Seq[String]]](cacheDir, tasks) { ops =>
        val results = ops.map { task =>
          val packageArg = task.packageName.map(pkg => Seq("-p", pkg)).getOrElse(Nil)
          val packageArgs = task.packageMappings.flatMap {
            case (namespace, pkg) => Seq("-p", s"$namespace=$pkg")
          }
          val serviceNameArg = task.serviceName.map(sn => Seq("-sn", sn)).getOrElse(Nil)

          val args = Array(
            "-frontend",
            "play",
            "-d",
            sources.getAbsolutePath
          ) ++ packageArg ++ packageArgs ++ serviceNameArg ++ task.args :+ task.url.toString

          val toolContext = new ToolContext()

          toolContext.put(classOf[FutureApi], task.futureApi)

          var filesWritten = Set.empty[File]
          // Custom OutputStreamCreator is used to track every file generated
          toolContext.put(classOf[OutputStreamCreator], new OutputStreamCreator() {
            override def createOutputStream(file: File) = {
              filesWritten += file
              super.createOutputStream(file)
            }
          })

          log.info("Processing WSDL: " + task.url)

          new WSDLToJava(args).run(toolContext)

          val plugins = Option(toolContext.get("play.plugins").asInstanceOf[Seq[String]]).getOrElse(Nil)

          val filesRead = if (task.url.getProtocol == "file") {
            Set(new File(task.url.toURI))
          } else {
            Set.empty[File]
          }

          // Map the task to the result of the operation and the plugins generated
          task -> (OpSuccess(filesRead, filesWritten), plugins)
        }.toMap

        (results.mapValues(_._1), results.mapValues(_._2))
      }

      // At this point we have all the new plugins that were generated, but we don't have any of the plugins that
      // were generated from the previous run that were generated by wsdl tasks that haven't changed.  We use a
      // separate cache to get them.
      val pluginsCacheFile = cacheDir / "plugins.cache"
      // Load the cache
      val cachedPlugins = if (pluginsCacheFile.exists()) {
        IO.read(pluginsCacheFile)
          .split("\n")
          .map { v =>
            val splitted = v.split("=", 2)
            if (splitted.length == 2) {
              splitted(0) -> splitted(1).split(",").toSeq
            } else {
              splitted(0) -> Nil
            }
          }
          .toMap
      } else Map.empty[String, Seq[String]]

      // Filter out any tasks that no longer exist
      val hashedTasks = tasks.map(hashTask).toSet
      val existing    = cachedPlugins.filterKeys(hashedTasks)
      // Add new/overwrite updated ones
      val allPlugins = existing ++ plugins.map {
        case (task, value) => hashTask(task) -> value
      }

      // Write out cached plugins
      IO.write(
        pluginsCacheFile,
        allPlugins
          .map {
            case (key, value) => key + "=" + value.mkString(",")
          }
          .mkString("\n")
      )

      WsdlTaskResult(files.toSeq, allPlugins.flatMap(_._2).map("900:" + _).toSeq)
    }
  }

  private def hashTask(task: WsdlTask) = {
    DigestUtils.md5Hex(task.toString)
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
    val props  = new java.util.Properties
    val stream = getClass.getClassLoader.getResourceAsStream(resource)
    try {
      props.load(stream)
    } catch { case e: Exception => }
    finally {
      if (stream ne null) stream.close()
    }
    props.getProperty(property)
  }
}

/**
 * Auto plugin that activates when PlayJava is in use to switch the future API to use to be the Play Java future API.
 */
object SbtWsdlJava extends AutoPlugin {
  override def trigger  = allRequirements
  override def requires = SbtWsdl && PlayJava

  override def projectSettings = Seq(
    futureApi := PlayJavaFutureApi
  )
}

/**
 * Auto plugin that activates when the Play plugin is use to switch the wsdl directory to conf/wsdls.
 */
object SbtWsdlPlay extends AutoPlugin {
  override def trigger  = allRequirements
  override def requires = SbtWsdl && PlayWeb

  override def projectSettings = Seq(
    sourceDirectories in (Compile, wsdlToCode) := Seq((resourceDirectory in Compile).value / "wsdls")
  )
}
