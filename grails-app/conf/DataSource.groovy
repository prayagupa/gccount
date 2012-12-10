dataSource {
    pooled = true
    driverClassName = "org.h2.Driver"
    username = "sa"
    password = ""
}
//mongo_conf = Off
//this is throwing error
/*
grails { 
  mongo { 
    host ="localhost"
    port = 27107 
    username = ""
    password="" 
    databaseName = "eccount" 
  } 
}
*/
hibernate {
    cache.use_second_level_cache = true
    cache.use_query_cache = false
    cache.region.factory_class = 'net.sf.ehcache.hibernate.EhCacheRegionFactory'
}
// environment specific settings
// for your SQL Server database
environments {
/*
    development {
        dataSource {
            dbCreate = "update" // one of 'create', 'create-drop', 'update', 'validate', ''
            driverClassName = "com.microsoft.sqlserver.jdbc.SQLServerDriver"
	    url = "jdbc:sqlserver://192.168.2.19:1433;databaseName=cps"
	    dialect = "org.hibernate.dialect.SQLServerDialect"
            username = "sa"
            password = ""
        }
    }
/*/
/*conf for MySQL database*/
    development {
        dataSource {
            dbCreate = "update" // one of 'create', 'create-drop', 'update', 'validate', ''
            driverClassName = "com.mysql.jdbc.Driver"
            url = "jdbc:mysql://localhost:3306/cashless"
            username = "root"
            password = "mysql55"
        }
    }

    test {
        dataSource {
            dbCreate = "update"
            url = "jdbc:h2:mem:testDb;MVCC=TRUE;LOCK_TIMEOUT=10000"
        }
    }
    production {
        dataSource {
            dbCreate = "update"
            url = "jdbc:h2:prodDb;MVCC=TRUE;LOCK_TIMEOUT=10000"
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
