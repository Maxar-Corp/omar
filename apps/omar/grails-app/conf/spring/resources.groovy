// Place your Spring DSL code here
beans = {
  sql(groovy.sql.Sql, ref('dataSource')) { bean ->
    bean.scope = 'prototype'
  }    
}