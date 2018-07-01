name := "RSTrade"
version := "0.1"
organization := "xyz.janboerman"

scalaVersion := "2.12.6"
scalacOptions += "-language:implicitConversions"

resolvers += "vault-repo" at "http://nexus.hc.to/content/repositories/pub_releases"
resolvers += Resolver.mavenLocal

libraryDependencies ++= Seq(
    "org.scala-lang" % "scala-library" % "2.12.6",

    "org.spigotmc" % "spigot" % "1.12.2-R0.1-SNAPSHOT" % "provided",
    "net.milkbowl.vault" % "VaultAPI" % "1.6" % "provided"
)

assemblyShadeRules in assembly := Seq(
    ShadeRule.rename("scala.**" -> "xyz.janboerman.rstrade.scala.@1").inAll
)

assemblyJarName in assembly := "RSTrade.jar"