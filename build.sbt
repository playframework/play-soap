lazy val root = (project in file("."))
 .aggregate(client)

lazy val client = project in file("client")

lazy val plugin = (project in file("sbt-plugin"))
 .settings(
    (resourceGenerators in Compile) <+= generateVersionFile
 )

def generateVersionFile = Def.task {
 val version = (Keys.version in client).value
 val file = (resourceManaged in Compile).value / "play-soap.version.properties"
 val content = s"play-soap.version=$version"
 if (!file.exists() || !(IO.read(file) == content)) {
  IO.write(file, content)
 }
 Seq(file)
}