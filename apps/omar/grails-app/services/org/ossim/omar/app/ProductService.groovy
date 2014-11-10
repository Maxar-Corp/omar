package org.ossim.omar.app

import com.budjb.rabbitmq.RabbitMessageBuilder
import groovy.json.*
import org.ossim.omar.raster.RasterEntry
import org.apache.commons.collections.map.CaseInsensitiveMap
import org.ossim.omar.Job
import org.ossim.omar.JobStatus
import org.apache.commons.io.FilenameUtils
import java.text.SimpleDateFormat
import java.util.TimeZone;
import org.ossim.oms.job.ChipperParams
import org.ossim.oms.job.ArchiveParams
import org.ossim.oms.job.ChipperMessage

class ProductService {
  def springSecurityService
  def grailsApplication
  def diskCacheService

  def newProduct(def params) {
    def result
    def caseInsensitiveParams = new CaseInsensitiveMap( params )


    def chipperMessage = buildMessage(caseInsensitiveParams)

    result = [message:chipperMessage?.toJsonString(), jobId:chipperMessage?.jobId]

    Job.withTransaction{
      def date = new Date()
      def job = new Job(jobId:chipperMessage.jobId,
              jobDir:chipperMessage.jobDir,
              name:params?.name?:"",
              username:params.username?:"anonymous",
              type:chipperMessage.type,
              status:JobStatus.READY.toString(),
              message:"${result.message}",
              statusMessage:"",
              submitDate:date
      )

      job.save()
    }

   // println result.message
    def messageBuilder = new RabbitMessageBuilder()
    messageBuilder.send("omar.job.product", result.message)
    result
  }
  synchronized def createDirectoryIfNeeded(def directory)
  {
    def directoryTemp = directory as File
    if(!directoryTemp.exists())
    {
      directoryTemp.mkdirs()
    }
  }
  private def getCurrentUtcDateAsYearMonthDay()
  {
    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
    formatter.setTimeZone(TimeZone.getTimeZone("UTC"));

    formatter.format(new Date()).toString()
  }
  def getNextAvailableProductDir() {
    // get the root output directory in the format of <cache-dir>/product/<yyyyMMdd>/
    def result
    def cacheDir =  diskCacheService.getBestLocation()
    if(cacheDir)
    {
      def dateDirectory = FilenameUtils.concat(cacheDir,"product")
      result = FilenameUtils.concat(dateDirectory,
              getCurrentUtcDateAsYearMonthDay())
      createDirectoryIfNeeded(dateDirectory)
    }

    result
  }

  /**
   *  Really need to move this to it's own class so others can use it outside of OMAR to parse
   *  and create product messages
   *
   */
  def buildMessage(def params) {
    def chipperParams = new ChipperParams()

    chipperParams.properties.each { k, v ->

      if (params."${k}" != null) chipperParams."${k}" = params."${k}"
//      println "*"*40
//      def temp = params."${k}"
//      println "${k} = ${temp}"
    }
    def messageString = ""

    def rasterEntries = params.layers.split(",")
    def centerX = 0.0
    def centerY = 0.0
    RasterEntry.withTransaction {
      rasterEntries.each { value ->
        def rasterEntry = RasterEntry.findById(value.toInteger())
        //println rasterEntry
        if (rasterEntry) {
          def center = rasterEntry.geometryCenter
          centerY+=center.y
          centerX+=center.x
          chipperParams.addInput(new org.ossim.oms.job.LocalFile(file: rasterEntry.filename,
                  entries: [new org.ossim.oms.job.FileEntry(entryId: rasterEntry.entryId)]))
          //def fileInfo = [type:"LOCAL_FILE",file:rasterEntry.filename,entries:[[entryId:rasterEntry.entryId]]]
          //fileInfoArray<<fileInfo

        }
      }
    }

    centerY/=rasterEntries.size()
    centerX/=rasterEntries.size()
    if (!chipperParams.inputs) {
      return null
    }
    switch (params.writer.toUpperCase()) {
      case ~/.*GPKG.*/:
        chipperParams.output_file += ".gpkg"
        break
      case ~/.*TIF.*/:
        chipperParams.output_file += ".tif"
        break
      default:
        break
    }
    def chipperMessage
    def archiveParams = new ArchiveParams(type:"zip", deleteInputAfterArchiving:true)
    def productDir = getNextAvailableProductDir().toString()
    if (productDir) {
      chipperMessage = ChipperMessage.newMessage(chipperParams: chipperParams, archiveParams:archiveParams)
      def productDirFile = FilenameUtils.concat(productDir, "${chipperMessage.jobId}")
      def productFile = FilenameUtils.concat(productDirFile, "${chipperParams.output_file}")
      chipperMessage.jobDir = productDirFile
      chipperParams.output_file = productFile
      def testSrs = chipperMessage?.chipperParams?.srs
      if(testSrs)
      {
        if(testSrs.toLowerCase().contains("auto"))
        {
          if(!testSrs.contains(","))
          {
            testSrs = "${testSrs},${centerX},${centerY}"
            chipperMessage?.chipperParams?.srs = testSrs
          }
        }
      }

    }
    chipperMessage
  }
}