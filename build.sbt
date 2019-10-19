import java.util.Locale

val Name = "RSTrade"
val Version = "0.1"
val Organisation = "com.janboerman"
val ScalaVer = "2.13.1"

ThisBuild / name := Name
ThisBuild / version := Version
ThisBuild / organization := Organisation
ThisBuild / scalaVersion := ScalaVer
ThisBuild / scalacOptions ++= Seq("-language:implicitConversions", "-deprecation")

lazy val Resolvers = Seq(
    "spigot-snapshots" at "https://hub.spigotmc.org/nexus/content/repositories/snapshots/",
    "spigot-public" at "https://hub.spigotmc.org/nexus/content/groups/public/",
    "jitpack" at "https://jitpack.io",
    Resolver.mavenLocal
)

lazy val common = (project in file("common"))
    .settings(
        resolvers ++= Resolvers,
        libraryDependencies += "org.spigotmc" % "spigot-api" % "1.14.4-R0.1-SNAPSHOT" % "provided",
        libraryDependencies += "com.github.MilkBowl" % "VaultAPI" % "1.7" % "provided",
        libraryDependencies += "com.github.Jannyboy11.GuiLib" % "GuiLib-API" % "v1.9.2" % "provided",
        libraryDependencies += "net.md-5" % "bungeecord-chat" % "1.14-SNAPSHOT" % "provided"
    )

lazy val compat_nms1_12_R1 = (project in file("compat_nms1_12_R1"))
    .settings(
        resolvers ++= Resolvers,
        libraryDependencies += "org.spigotmc" % "spigot" % "1.12.2-R0.1-SNAPSHOT" % "provided",
        libraryDependencies += "com.github.Jannyboy11.GuiLib" % "GuiLib-API" % "v1.9.2" % "provided"
    ).dependsOn(common)

lazy val plugin = (project in file("plugin"))
    .settings(
        resolvers ++= Resolvers,
        libraryDependencies ++= Seq(
            "org.spigotmc" % "spigot" % "1.12.2-R0.1-SNAPSHOT" % "provided",
            //"org.spigotmc" % "spigot" % "1.14.4-R0.1-SNAPSHOT" % "provided",
            "com.github.MilkBowl" % "VaultAPI" % "1.7" % "provided",
            "com.github.Jannyboy11.GuiLib" % "GuiLib-API" % "v1.9.2",
        ),

        packageOptions in (Compile, packageBin) +=
            Package.ManifestAttributes(("Automatic-Module-Name", Organisation + "." + Name.toLowerCase(Locale.ROOT))),
        assemblySettings
    ).dependsOn(common, compat_nms1_12_R1)

//assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false) //uncomment when the scala library is accessible to the plugin's classloader

lazy val assemblySettings = Seq(
    assemblyShadeRules in assembly := Seq(
        ShadeRule.rename("scala.**" -> "com.janboerman.rstrade.scala.@1").inAll,
        ShadeRule.rename("xyz.janboerman.guilib.**" -> "com.janboerman.rstrade.guilib.@1").inAll
    ),
    assemblyMergeStrategy in assembly := {
        case "plugin.yml"   => MergeStrategy.first
        case x              =>
            val oldStrategy = (assemblyMergeStrategy in assembly).value
            oldStrategy(x)
    },
    assemblyJarName in assembly := Name + "-" + Version + ".jar",
)