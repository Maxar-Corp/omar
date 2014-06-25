def useP6Spy = false // use this to enable p6spy logging

dataSource {
    pooled = true
  driverClassName = ( useP6Spy ) ? "com.p6spy.engine.spy.P6SpyDriver" : "org.postgis.DriverWrapper"
  username = "postgres"
  password = "postgres"
  dialect = org.ossim.omar.postgis.PostGISDialect
}
hibernate {
    cache.use_second_level_cache = true
    cache.use_query_cache = false
    cache.region.factory_class = 'net.sf.ehcache.hibernate.EhCacheRegionFactory'
    jdbc.batch_size = 20
}
// environment specific settings
environments {
    development {
        dataSource {
            dbCreate = "create-drop" // one of 'create', 'create-drop', 'update', 'validate', ''
            url = "jdbc:postgresql_postGIS:omardb-${appVersion}-dev"
        }
    }
    test {
        dataSource {
            dbCreate = "update"
            url = "jdbc:postgresql_postGIS:omardb-${appVersion}-test"
        }
    }
    production {
        dataSource {
            dbCreate = "update"
            url = "jdbc:postgresql_postGIS:omardb-${appVersion}-prod"
            pooled = true
            properties {
               maxActive = -1
               minEvictableIdleTimeMillis=1800000
               timeBetweenEvictionRunsMillis=1800000
               numTestsPerEvictionRun=3
               testOnBorrow=true
               testWhileIdle=true
               testOnReturn=true
               validationQuery="SELECT 1"
            }
        }
    }
}
