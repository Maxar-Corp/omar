package org.ossim.omar.app

import org.ossim.omar.DiskCache
import org.ossim.omar.Job
import org.ossim.omar.chipper.FetchDataCommand

class DiskCacheService {
  def springSecurityService
  def grailsApplication
  def messageSource

  static columnNames = [
          'id','directory', 'directoryType', 'maxSize', 'currentSize','expirePeriod'
  ]
  def createTableModel()
  {
    def clazz = DiskCache.class
    def domain = grailsApplication.getDomainClass( clazz.name )

    def columns = columnNames?.collect {column->
      def property = ( column == 'id' ) ? domain?.identifier : domain?.getPersistentProperty( column )
      def sortable = !(property?.name in ["type"])
      [field: property?.name, type: property?.type, title: property?.naturalName, sortable: sortable]
    }

    def tableModel = [
            columns: [columns]
    ]
    // println tableModel
    return tableModel
  }
  def getBestLocation(){
    /**
     * Fetch data that basically has the smallest size
     *
     */
    FetchDataCommand cmd = new FetchDataCommand(sort:"currentSize",
            order:"asc",
            filter:null,
            rows:1, page:1)

    String result = ""

    DiskCache.withTransaction {
      def row

      try{
        row = DiskCache.withCriteria {
          maxResults( cmd.rows )
          order( cmd.sort, cmd.order )
          firstResult( 0 )
          setReadOnly(true)
        }.get(0)
      }
      catch(e)
      {
        row = null
      }

      result = row?.directory
    }

    result
  }
  def create(def params){
    def result = [success:true];
    params.remove("id")
    try{
      DiskCache.withTransaction {
        def diskCache = new DiskCache(params);
        if(!diskCache.save(flush:true))
        {
          diskCache.errors.allErrors.each {result.message = "${result.message?'\n':''} ${messageSource.getMessage(it, null)}"  }
          result.success = false;
        }
        else
        {
          result.id = diskCache.id;
        }
      }
    }
    catch(e)
    {
      result.success=false;
      result.message = e.toString();
    }
    result;
  }
  def update(def params){
    def result = [success:true];
    try{
      DiskCache.withTransaction {
        def row
        if(params?.id!=null) row = DiskCache.findById(params?.id.toInteger());
        else if(params?.directory) row = DiskCache.findByDirectory(params.directory);
        params.remove("id")
        if(row)
        {
          row.properties = params
          row.save(flush:true)
        }
        else
        {
          result.success = false;
          result.message = "Unable to find directory = ${params?.directory} for updating";
        }
      }

    }
    catch(e)
    {
      result.success=false;
      result.mesage = e.toString();
    }
    result;
  }
  def remove(def params){
    def result = [success:false]

    try{
      DiskCache.withTransaction {
        def row

        if(params?.id) row = DiskCache.findById(params?.id?.toInteger());
        else if(params?.directory) row = DiskCache.findByDirectory(params.directory);

        if(row)
        {
          row.delete()
          result.success = true;
        }
      }
    }
    catch(e)
    {
      result.success = false;
      result.message = e.toString()
    }

    result;
  }
  def list(def cmd){

    def total = 0
    def rows  = [:]
    DiskCache.withTransaction{
      total = DiskCache.createCriteria().count {
        if ( cmd.filter )
        {
          sqlRestriction cmd.filter
        }
      }

      def tempRows = DiskCache.withCriteria {
        if ( cmd.filter )
        {
          sqlRestriction cmd.filter
        }
        //      projections {
        //        columnNames.each {
        //          property(it)
        //        }
        //      }
        maxResults( cmd.rows )
        order( cmd.sort, cmd.order )
        firstResult( ( cmd.page - 1 ) * cmd.rows )
      }
      rows = tempRows.collect { row ->
        columnNames.inject( [:] ) { a, b -> a[b] = row[b].toString(); a }
      }
    }

    return [total: total, rows: rows]
  }
}
