import Dependencies._

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.example",
      scalaVersion := "2.12.1",
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "Hello",
    libraryDependencies += scalaTest % Test,

    libraryDependencies += "com.twitter" %% "util-core" % "6.42.0",
    libraryDependencies += "com.google.guava" % "guava" % "19.0", // 21 is newest
    libraryDependencies += "com.gilt" %% "gfc-guava" % "0.2.5"
  )
