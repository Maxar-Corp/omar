package org.ossim.omar.ogc

import au.com.bytecode.opencsv.CSVWriter
import org.apache.commons.io.FilenameUtils

class ExportService
{

  static transactional = true

  def grailsApplication

  def export(def format, def objects, def fields, def labels, def formatters, def parameters)
  {
    def file
    def mimeType

    def prefix = grailsApplication.config.export.prefix ?: "omar-export-"
    def workDir = grailsApplication.config.export.workDir ?: "/tmp"

    switch ( format )
    {
    case ~/.*csv.*/:
      def csvFile = File.createTempFile(prefix, ".csv", workDir as File)
      def csvWriter = new CSVWriter(csvFile.newWriter())

      csvWriter.writeNext(labels as String[])

      for ( object in objects )
      {
        def data = []
        for ( field in fields )
        {
          if ( formatters[field] )
          {
            data << formatters[field].call(object[field])
          }
          else
          {
            data << object[field]
          }
        }

        csvWriter.writeNext(data as String[])
      }

      csvWriter.close()
      file = csvFile
      mimeType = "text/csv"
     break
     case ~/.*shp.*/:
      def featureClass = parameters.featureClass
      def featureType = ExportUtils.createFeatureType(featureClass, fields, labels)
      def collection = ExportUtils.createFeatures(featureType, objects, fields, labels)
      def tempDir = ExportUtils.createTempDir(prefix, "", workDir as File)
      def newFile = new File(tempDir, FilenameUtils.getBaseName(tempDir.name) + ".shp")
      def newDataStore = ExportUtils.createShapefile(newFile, featureType)

      ExportUtils.addFeatures(newDataStore, collection)
      file = ExportUtils.createZipFileSet(newFile)
      //mimeType = "application/zip"
      mimeType = "application/octet-stream"


      break
      case ~/.*json.*/:
          if(fields.size()==labels.size())
          {
              file = File.createTempFile(prefix, ".json", workDir as File)
              def outputFirstObject = false;
              file <<"["

              for ( object in objects )
              {
                  if(outputFirstObject) file <<","
                  file <<"{"
                  def outputFirstField = false;
                  for (idx in 0..fields.size()-1)
                  {
                      if ( formatters[fields[idx]] )
                      {
                          if(outputFirstField) file <<","
                          file << "\"${labels[idx]}\":\"${formatters[fields[idx]].call(object[fields[idx]])}\""
                      }
                      else
                      {
                          if(outputFirstField) file <<","
                          file << "\"${labels[idx]}\":\"${object[fields[idx]]}\""
                      }
                      outputFirstField = true;
                  }
                  file<<"}"
                  outputFirstObject = true;
              }
              file << "]"
          }
          mimeType = "application/json"
        break;
    }

    return [file, mimeType]

  }
}
