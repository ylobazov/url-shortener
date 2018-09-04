lazy val root = (project in file(".")).
  configs(IntegrationTest).
  settings(Defaults.itSettings).
  settings(
    organization                             := "com.github.ylobazov",
    name                                     := "url-shortener",

    scalaVersion         in Global           := "2.12.6",

    fork                 in Test             := false,
    parallelExecution    in Test             := false,
    fork                 in IntegrationTest  := true,
    parallelExecution    in IntegrationTest  := true,
  )
