name := "RSTrade"
version := "0.1"
organization := "xyz.janboerman"

scalaVersion := "2.12.6"
scalacOptions += "-language:implicitConversions"

resolvers += "vault-repo" at "http://nexus.hc.to/content/repositories/pub_releases"
resolvers += "jitpack" at "https://jitpack.io"
resolvers += Resolver.mavenLocal

libraryDependencies ++= Seq(
    "org.scala-lang" % "scala-library" % "2.12.6",

    "org.spigotmc" % "spigot" % "1.12.2-R0.1-SNAPSHOT" % "provided",
    "net.milkbowl.vault" % "VaultAPI" % "1.6" % "provided",
    "com.github.Jannyboy11" % "GuiLib" % "v1.3.4"
)

assemblyShadeRules in assembly := Seq(
    ShadeRule.rename("scala.**" -> "xyz.janboerman.rstrade.scala.@1").inAll,
    ShadeRule.rename("xyz.janboerman.guilib.**" -> "xyz.janboerman.rstrade.guilib.@1").inAll
)

assemblyMergeStrategy in assembly := {
    case "plugin.yml"   => MergeStrategy.discard
    case x              =>
        val oldStrategy = (assemblyMergeStrategy in assembly).value
        oldStrategy(x)
}

assemblyJarName in assembly := "RSTrade.jar"