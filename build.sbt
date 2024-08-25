val scala3Version = "3.3.3"

lazy val root = project
  .in(file("."))
  .settings(
    name := "httrps",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    // Apparently Finagle doesn't support Scala 3, this GitHub comment saved me
    // https://github.com/twitter/finagle/issues/932#issuecomment-1225897764
    libraryDependencies += "com.twitter" %% "util-core" % "22.12.0" cross CrossVersion.for3Use2_13,
    libraryDependencies += "com.twitter" %% "finagle-http" % "22.12.0" cross CrossVersion.for3Use2_13,
    libraryDependencies += "com.twitter" %% "finagle-core" % "22.12.0" cross CrossVersion.for3Use2_13
  )
