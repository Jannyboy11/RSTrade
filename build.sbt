import java.util.Locale

val Name = "RSTrade"
val Version = "0.1"
val Organisation = "com.janboerman"
val ScalaVer = "2.13.1"

name := Name
version := Version
organization := Organisation
scalaVersion := ScalaVer

scalacOptions += "-language:implicitConversions"

packageOptions in (Compile, packageBin) +=
    Package.ManifestAttributes(("Automatic-Module-Name", Organisation + "." + Name.toLowerCase(Locale.ROOT)))

resolvers ++= Seq(
    "jitpack" at "https://jitpack.io",
    Resolver.mavenLocal,
)

libraryDependencies ++= Seq(
    "org.spigotmc" % "spigot" % "1.12.2-R0.1-SNAPSHOT" % "provided",
    //"org.spigotmc" % "spigot" % "1.14.4-R0.1-SNAPSHOT" % "provided",
    "com.github.MilkBowl" % "VaultAPI" % "1.7" % "provided",
    "com.github.Jannyboy11.GuiLib" % "GuiLib-API" % "v1.9.2",
)

//assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false) //uncomment when the scala library is accessible to the plugin's classloader

assemblyShadeRules in assembly := Seq(
    ShadeRule.rename("scala.**" -> "com.janboerman.rstrade.scala.@1").inAll,
    ShadeRule.rename("xyz.janboerman.guilib.**" -> "com.janboerman.rstrade.guilib.@1").inAll
)

assemblyMergeStrategy in assembly := {
    case "plugin.yml"   => MergeStrategy.first
    case x              =>
        val oldStrategy = (assemblyMergeStrategy in assembly).value
        oldStrategy(x)
}

assemblyJarName in assembly := Name + "-" + Version + ".jar"