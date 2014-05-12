package org.ossim.omar.raster

import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.ossim.omar.core.DateUtil
import org.ossim.omar.core.HttpStatusMessage
import org.ossim.omar.core.HttpStatus

class RasterEntryExportController
{
  def exportService
  def rasterEntrySearchService
  def grailsApplication
  def ogcExceptionService
  def index( )
  {
  }

  def exportGclProject( )
  {
    try{
      if (SpringSecurityUtils.ifAllGranted("ROLE_DOWNLOAD"))
      {
        def fNames = params?.filenames?.split(",")
        def files = []

        fNames.each{file->
          def fileObjects = []
          def tempFile
          try{
            tempFile = new File(file);
          }
          catch(def e)
          {
            tempFile = null;
          }
          if (tempFile&&!tempFile.exists())
          {
            def rasterEntry = RasterEntry.compositeId( file ).findWhere()

            if (rasterEntry)
            {
              def mainFile
              if(rasterEntry.rasterDataSet)
              {
                mainFile = rasterEntry.rasterDataSet.getFileFromObjects()
              }
              if(!mainFile) mainFile = rasterEntry.mainFile;
              tempFile = new File(mainFile.name);
              if (tempFile.exists()&&tempFile.canRead())
              {
                rasterEntry?.rasterDataSet?.fileObjects.each{
                  if(it!= "main")
                  {
                    fileObjects << [name:it.name,
                            type:it.type]
                  }
                }
                rasterEntry.fileObjects.each{
                  fileObjects << [name:it.name,
                                      type:it.type]
                }
                files << [mainFile:tempFile.toString(),
                          entryId:rasterEntry.entryId,
                          fileObjects:fileObjects]
              }
            }
          }
          else if (tempFile.exists())
          {
            files << [mainFile:tempFile.toString(),
                      fileObjects:[]]
          }
        }

        def includeGeocellProject = params.includeGeocellProject!=null?params.includeGeocellProject.toBoolean():true
        def rootPathName = params.rootPathName!=null?params.rootPathName:"ossim-geocell"
        def options = [includeGeocellProject:includeGeocellProject, 
                       rootPathName:rootPathName]
       // println "OPTIONS: ${options}"
        exportService.exportGclWithResponse(files.unique{it.mainFile}, options, response)
        /*
          def (file, mimeType) = exportService.exportGcl(fNames, cNames)

          response.setHeader( "Content-disposition", "attachment; filename=${file.name}" );
          response.contentType = mimeType
          response.outputStream << file.newInputStream()
          response.outputStream.flush()
          if (file.exists())
          {
              file.delete();

          }
          */
        response.outputStream.flush()
      }
      else
      {
          def httpResponse = new HttpStatusMessage()
          httpResponse.status = HttpStatus.UNAUTHORIZED
          httpResponse.message = "You are unauthorized to download the files.  " +
                                 "You must be logged into OMAR™ \n and have download privileges."

          httpResponse.initializeResponse(response)
          response.contentType = "text/plain"
          response.outputStream << httpResponse.message
          response.outputStream.flush()
      }
    }
    catch(def e)
    {
      //println "EXCEPTION!!! ${e}"
    }
    null
  }

  def export( )
  {
    def format = params.format
    def queryParams = new RasterEntryQuery()
    bindData( queryParams, params )

    queryParams.startDate = DateUtil.initializeDate( "startDate", params )
    queryParams.endDate = DateUtil.initializeDate( "endDate", params )

    def objects = rasterEntrySearchService.runQuery( queryParams, params )

//    def fields = ["id", "acquisitionDate", "groundGeom"] as String[]
//    def labels = ["id", "acquisition_date", "ground_geom"] as String[]

//    def domainClass = grailsApplication.getArtefact("Domain", "org.ossim.omar.raster.RasterEntry")
//    def fields = domainClass.properties*.name
//    def labels = domainClass.properties*.naturalName

    def fields = grailsApplication.config.export.rasterEntry.fields
    def labels = grailsApplication.config.export.rasterEntry.labels
    def formatters = grailsApplication.config.export.rasterEntry.formatters

    if (params.fields)
    {
        def tempFields = params?.fields?.split(",")
        fields = []
        tempFields.each{field->
            fields<<field
        }
    }
    if (params.labels)
    {
        def tempLabels = params?.labels?.split(",")
        labels = []
        tempLabels.each{label->
            labels << label;
        }
    }


    def (file, mimeType) = exportService.export(
            format,
            objects,
            fields,
            labels,
            formatters,
            [featureClass: RasterEntry.class]
    )

      if (file instanceof String)
      {
          //response.setHeader( "Content-disposition", "attachment; filename=" );
          response.contentType = mimeType
          response.outputStream << file
          response.outputStream.flush()
      }
      else
      {
          response.setHeader( "Content-disposition", "attachment; filename=${file.name}" );
          response.contentType = mimeType
          response.outputStream << file.newInputStream()
          response.outputStream.flush()
          file.delete();
      }
  }
}
