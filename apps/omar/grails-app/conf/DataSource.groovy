def useP6Spy = false // use this to enable p6spy logging

dataSource {
  pooled = true
  driverClassName = (useP6Spy) ? "com.p6spy.engine.spy.P6SpyDriver" : "org.postgis.DriverWrapper"
  username = "postgres"
  password = "postgres"
  dialect = "org.ossim.postgis.PostGISDialectNG"
//  loggingSql = true
}
hibernate {
  cache.use_second_level_cache = true
  cache.use_query_cache = true
  cache.provider_class = 'org.hibernate.cache.EhCacheProvider'
  jdbc.batch_size = 20
}
// environment specific settings
environments {
  development {
    dataSource {
      dbCreate = "create-drop" // one of 'create', 'create-drop','update'
      //dbCreate = "update"
      url = "jdbc:postgresql_postGIS:omardb-2.0-dev"
    }
  }
  test {
    dataSource {
      dbCreate = "update"
      //url = "jdbc:postgresql_postGIS:omardb-2.0-test"
      url = "jdbc:postgresql_postGIS:dbupgrade"
    }
  }
  production {
    dataSource {
      dbCreate = "update"
      url = "jdbc:postgresql_postGIS:omardb-2.0-prod"
    }
  }
}
