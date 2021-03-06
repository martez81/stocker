import Dependencies._

lazy val commonSettings = Seq(
    version := "0.1",
    scalaVersion := "2.12.4"
)

lazy val core = (project in file("core")).
        settings(commonSettings: _*).
        settings(
            name := "stocker-core",
            libraryDependencies ++= coreDeps
        ).
        dependsOn(util)

lazy val app = (project in file("app")).
        settings(commonSettings: _*).
        settings(
            name := "stocker-app",
            libraryDependencies ++= appDeps
        ).
        dependsOn(core, util)

lazy val portfolio = (project in file("portfolio")).
        settings(commonSettings: _*).
        settings(
            name := "stocker-portfolio",
            libraryDependencies ++= portfolioDeps
        ).
        dependsOn(core, util)

lazy val scanner = (project in file("scanner")).
        settings(commonSettings: _*).
        settings(
            name := "stocker-scanner"
        ).
        dependsOn(core, util)

lazy val updater = (project in file("updater")).
        settings(commonSettings: _*).
        settings(
            name := "stocker-updater",
            libraryDependencies ++= updaterDeps
        ).
        dependsOn(core, util)

lazy val util = (project in file("util")).
        settings(commonSettings: _*).
        settings(
            name := "stocker-util",
            libraryDependencies ++= utilDeps
        )
