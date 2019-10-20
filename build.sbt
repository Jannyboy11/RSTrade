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

lazy val SpigotSnapshotsRepo = "spigot-snapshots" at "https://hub.spigotmc.org/nexus/content/repositories/snapshots/"
lazy val SpigotPublicRepo = "spigot-public" at "https://hub.spigotmc.org/nexus/content/groups/public/"
lazy val JitpackRepo = "jitpack" at "https://jitpack.io"

lazy val GuiLib = "com.github.Jannyboy11.GuiLib" % "GuiLib-API" % "v1.9.2"
lazy val SpigotApi = "org.spigotmc" % "spigot-api" % "1.14.4-R0.1-SNAPSHOT" % "provided"
lazy val VaultApi = "com.github.MilkBowl" % "VaultAPI" % "1.7" % "provided"
lazy val BungeeCordChat = "net.md-5" % "bungeecord-chat" % "1.14-SNAPSHOT" % "provided"

lazy val plugin = (project in file("plugin"))
    .settings(
        resolvers ++= Seq(SpigotSnapshotsRepo, JitpackRepo),
        libraryDependencies ++= Seq(SpigotApi, VaultApi),

        packageOptions in (Compile, packageBin) +=
            Package.ManifestAttributes(("Automatic-Module-Name", Organisation + "." + Name.toLowerCase(Locale.ROOT))),
        
        assemblySettings
    ).dependsOn(common, compat_nms1_12_R1)

lazy val common = (project in file("common"))
    .settings(
        resolvers ++= Seq(SpigotSnapshotsRepo, SpigotPublicRepo, JitpackRepo),
        libraryDependencies ++= Seq(SpigotApi, BungeeCordChat, GuiLib, VaultApi)
    )

lazy val compat_nms1_12_R1 = (project in file("compat_nms1_12_R1"))
    .settings(
        resolvers += Resolver.mavenLocal,
        libraryDependencies += "org.spigotmc" % "spigot" % "1.12.2-R0.1-SNAPSHOT" % "provided"
    ).dependsOn(common)

lazy val root = (project in file("."))
    .aggregate(plugin)
    .settings(assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false))

lazy val assemblySettings = Seq(
    assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = true),
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