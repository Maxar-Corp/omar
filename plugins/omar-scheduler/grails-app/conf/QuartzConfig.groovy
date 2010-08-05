quartz {
  autoStartup = true
  jdbcStore = true
  waitForJobsToCompleteOnShutdown = true
}

environments {
  test {
    quartz {
      autoStartup = false
    }
  }
}
