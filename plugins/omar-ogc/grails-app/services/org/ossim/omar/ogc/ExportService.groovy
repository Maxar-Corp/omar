package org.ossim.omar.ogc

import au.com.bytecode.opencsv.CSVWriter
import org.apache.commons.io.FilenameUtils
import org.ossim.omar.core.Utility

import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class ExportService
{

  static transactional = true

  def grailsApplication

  def export(def format, def objects, def fields, def labels, def formatters, def parameters)
  {
    def prefix = grailsApplication.config.export.prefix ?: "omar-export-"
    def workDir = grailsApplication.config.export.workDir ?: "/tmp"

    def file
    def mimeType

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
          def outputString = new StringBuilder();
          if(fields.size()==labels.size())
          {

             // file = File.createTempFile(prefix, ".json", workDir as File)
              def outputFirstObject = false;
              outputString <<"["

              for ( object in objects )
              {
                  if(outputFirstObject) outputString <<","
                  outputString <<"{"
                  def outputFirstField = false;
                  for (idx in 0..fields.size()-1)
                  {
                      if ( formatters[fields[idx]] )
                      {
                          if(outputFirstField) outputString <<","
                          outputString << "\"${labels[idx]}\":\"${formatters[fields[idx]].call(object[fields[idx]])}\""
                      }
                      else
                      {
                          if(outputFirstField) outputString <<","
                          outputString << "\"${labels[idx]}\":\"${object[fields[idx]]}\""
                      }
                      outputFirstField = true;
                  }
                  outputString<<"}"
                  outputFirstObject = true;
              }
              outputString << "]"
          }
          mimeType = "application/json"
          file = outputString.toString()
          break


        case ~/.*geocell.*/:
            def tempDir = ExportUtils.createTempDir(prefix, "", workDir as File)
            def geocellProjFile = new File(tempDir, FilenameUtils.getBaseName(tempDir.name) + ".geocell")
            def outputString = new StringBuilder();
            def baseString = "dataManager.objectList.object"

            // TEMPORARY
            String preface = ""
            // TEMPORARY

            List files = new ArrayList()
            def associatedFiles

            for ( index in 0..objects.size()-1 )
            {
                def objString = baseString + "${index}"

                // Get filename
                def fileName
                for ( field in fields )
                {
                    if (field == "filename")
                    {
                        fileName = objects[index][field]
                    }
                }

                def imgFile= new File(fileName)

                // Get type
                def fileString = imgFile.name
                def type
                // TEMPORARY
                def itype = fileString.substring( fileString.lastIndexOf('.')+1, fileString.length())
                switch (itype) {
                    case ["ntf"]:
                        type = /.type: ossimNitfTileSource/
                        break
                    case ["tif"]:
                        type = /.type: ossimTiffTileSource/
                        break
                    case ["jpg","jpeg"]:
                        type = /.type: ossimJpegTileSource/
                        break
                    case ["hri","hsi"]:
                        type = /.type: ossimEnviTileSource/
                        break
                    default:
                        type = /.type:/
                }
                // TEMPORARY

                // Get associated file names & add to list
                def baseName = fileName.substring(fileName.lastIndexOf('/')+1, fileName.lastIndexOf('.'))
                def directory = fileName.substring(0, fileName.lastIndexOf('/')+1)
                def dir = new File(directory)
                dir.eachFileMatch(~/${baseName}.*/) {files.add(it.parent+"/"+it.name)}

                // Check for NavData sub-directory
                dir.eachFile {
                    if (it.isDirectory()) {
                        if (it.name == "NavData") {
                            it.eachFile {files.add(it.parent+"/"+it.name)}
                        }
                    }
                }

                // Check for NavData parallel directory
                def dirPar = new File(dir.parent)
                dirPar.eachFile {
                    if (it.isDirectory()) {
                        if (it.name == "NavData") {
                            it.eachFile {files.add(it.parent+"/"+it.name)}
                        }
                    }
                }

                // Check for NavData sub-directory
                dir.eachFile {
                    if (it.isDirectory()) {
                        if (it.name == "NavData") {
                            it.eachFile {files.add(it.parent+"/"+it.name)}
                        }
                    }
                }

                associatedFiles = files.unique()

                def downloadedFilename = preface + fileName.substring(1, fileName.size())

                // Fill project file entries
                outputString << objString + /.description:/ + "\n"
                outputString << objString + /.filename: / + downloadedFilename + "\n"
                outputString << objString + /.enable_cache:  0/ + "\n"
                outputString << objString + /.enabled:  1/ + "\n"
                outputString << objString + /.entry:  0/ + "\n"
                outputString << objString + /.image_id:/ + "\n"
                outputString << objString + /.open_overview_flag:  1/ + "\n"
                outputString << objString + /.input_list_fixed:  1/ + "\n"
                outputString << objString + /.name:  """ Entry 0: / + downloadedFilename + /"""/ + "\n"
                outputString << objString + /.number_inputs:  0/ + "\n"
                outputString << objString + /.number_outputs:  0/ + "\n"
                outputString << objString + /.output_list_fixed:  0/ + "\n"
                outputString << objString + /.overview_file: / + downloadedFilename.substring(0, downloadedFilename.lastIndexOf('.')) + ".ovr" + "\n"
                outputString << objString + /.start_res_level:  0/ + "\n"
                outputString << objString + /.supplementary_directory:/ + "\n"
                outputString << objString + type +  "\n"
                outputString << /dataManager.type:  DataManager/ + "\n"
            }

            geocellProjFile.write(outputString as String)

            mimeType = "application/octet-stream"
            file = ExportUtils.createZipFileFromList(geocellProjFile, associatedFiles)

            break
    }

    return [file, mimeType]

    }

    def exportGclWithResponse(def fNames, def response)
    {
      def files = []
      response.setHeader( "Content-disposition", "attachment; filename=geocell-export.zip" );
      response.contentType = "application/octet-stream"
      def outputString = new StringBuilder();
      def baseString = "dataManager.objectList.object"
      def index = 0
      def zos = new ZipOutputStream(response.outputStream);
      fNames.each{entry->
        def testFile
        try{
          def file = entry.mainFile
          def objString = baseString + "${index}"
          def path =FilenameUtils.getPathNoEndSeparator(file)
          def filename = FilenameUtils.getName(file)
          def target = (new File(new File("ossim-geocell",path).toString(), filename)).toString()
          files << [file:file,
                  target:(new File(new File("ossim-geocell",path).toString(), filename)).toString()
          ]
          def overview = entry.fileObjects.find{it->
            if(it.type.contains("overv")) return true

            return false
          }
          // Fill basic project file entries
          outputString << objString + /.description:/ + "\n"
          outputString << objString + /.filename: / + target + "\n"
          outputString << objString + /.enable_cache:  0/ + "\n"
          outputString << objString + /.enabled:  1/ + "\n"
          if (entry.entryId != null)
          {
            outputString << objString + /.entry:  / +entry.entryId+ "\n"
          }
          outputString << objString + /.image_id:/ + "\n"
          outputString << objString + /.open_overview_flag:  1/ + "\n"
          outputString << objString + /.input_list_fixed:  1/ + "\n"
          outputString << objString + /.name:  """ Entry 0: / + target + /"""/ + "\n"
          outputString << objString + /.number_inputs:  0/ + "\n"
          outputString << objString + /.number_outputs:  0/ + "\n"
          outputString << objString + /.output_list_fixed:  0/ + "\n"
          if (overview)
          {
            outputString << objString + /.overview_file: / + overview + "\n"
          }
          outputString << objString + /.start_res_level:  0/ + "\n"
          outputString << objString + /.supplementary_directory:/ + "\n"
          outputString << objString + /.type: ossimImageHandler/ +  "\n"
          outputString << /dataManager.type:  DataManager/ + "\n"

          ++index

          /*
          * Add all associated files to the path
          */
          entry.fileObjects.each{fileObject->
            try{
              testFile = new File(fileObject.name);
              if (testFile.exists()&&testFile.canRead())
              {
                path =FilenameUtils.getPathNoEndSeparator(fileObject.name)
                filename = FilenameUtils.getName(fileObject.name)
                target = (new File(new File("ossim-geocell",path).toString(), filename)).toString()
                files << [file:testFile.toString(),
                          target:(new File(new File("ossim-geocell",path).toString(), filename)).toString()
                ]
              }
            }
            catch(def e){
               println e
            }
          }

        }
        catch(def e)
        {
          println e
        }
      }
      Utility.zipFilesToZipOutputStream(files.unique(), zos);
      def outputBuffer = outputString.toString().bytes
      zos.putNextEntry(new ZipEntry("ossim-geocell.gcl"));
      zos.write(outputBuffer, 0, outputBuffer.length)
      zos.close();
    }

    def exportGcl(def fNames, def cNames)
    {

        def prefix = grailsApplication.config.export.prefix ?: "omar-export-"
        def workDir = grailsApplication.config.export.workDir ?: "/tmp"

        def tempDir = ExportUtils.createTempDir(prefix, "", workDir as File)
        def geocellProjFile = new File(tempDir, FilenameUtils.getBaseName(tempDir.name) + ".gcl")
        def outputString = new StringBuilder();
        def baseString = "dataManager.objectList.object"

        // TEMPORARY
        String preface = ""
        // TEMPORARY

        List files = new ArrayList()
        def associatedFiles

        for ( index in 0..<fNames.size() )
        {
            def objString = baseString + "${index}"
            def fileName = fNames[index] as File
            def type = cNames[index]

            // Get associated file names & add to list
            def baseName = FilenameUtils.getBaseName(fileName.absoluteFile.toString())//fileName.substring(fileName.lastIndexOf('/')+1, fileName.lastIndexOf('.'))
            def directory = FilenameUtils.getFullPath(fileName.absoluteFile.toString())//fileName.substring(0, fileName.lastIndexOf('/')+1)
            def dir = new File(directory)

            dir.eachFileMatch(~/${baseName}.*/) {files.add(it.parent+"/"+it.name)}

            // Add custom directory structures
            //   - check for NavData sub-directory
            dir.eachFile {
                if (it.isDirectory()) {
                    if (it.name == "NavData") {
                        it.eachFile {files.add(it.parent+"/"+it.name)}
                    }
                }
            }
            //   - check for NavData parallel directory
            def dirPar = new File(dir.parent)
            dirPar.eachFile {
                if (it.isDirectory()) {
                    if (it.name == "NavData") {
                        it.eachFile {files.add(it.parent+"/"+it.name)}
                    }
                }
            }


            associatedFiles = files.unique()

            def downloadedFilename = preface +
                                     FilenameUtils.concat(FilenameUtils.getPath(fileName.absoluteFile.toString()),
                                                          FilenameUtils.getName(fileName.absoluteFile.toString()))//fileName.substring(1, fileName.size())
            // Fill basic project file entries
            outputString << objString + /.description:/ + "\n"
            outputString << objString + /.filename: / + downloadedFilename + "\n"
            outputString << objString + /.enable_cache:  0/ + "\n"
            outputString << objString + /.enabled:  1/ + "\n"
            outputString << objString + /.entry:  0/ + "\n"
            outputString << objString + /.image_id:/ + "\n"
            outputString << objString + /.open_overview_flag:  1/ + "\n"
            outputString << objString + /.input_list_fixed:  1/ + "\n"
            outputString << objString + /.name:  """ Entry 0: / + downloadedFilename + /"""/ + "\n"
            outputString << objString + /.number_inputs:  0/ + "\n"
            outputString << objString + /.number_outputs:  0/ + "\n"
            outputString << objString + /.output_list_fixed:  0/ + "\n"
            outputString << objString + /.overview_file: / + downloadedFilename.substring(0, downloadedFilename.lastIndexOf('.')) + ".ovr" + "\n"
            outputString << objString + /.start_res_level:  0/ + "\n"
            outputString << objString + /.supplementary_directory:/ + "\n"
            outputString << objString + /.type: / + type +  "\n"
            outputString << /dataManager.type:  DataManager/ + "\n"
        }

        geocellProjFile.write(outputString as String)

        def mimeType = "application/octet-stream"
        def file = ExportUtils.createZipFileFromList(geocellProjFile, associatedFiles)

        return [file, mimeType]
    }

}