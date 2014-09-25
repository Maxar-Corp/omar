package org.ossim.omar.app

import com.budjb.rabbitmq.RabbitMessageBuilder
import groovy.json.*
import org.ossim.omar.raster.RasterEntry
import org.apache.commons.collections.map.CaseInsensitiveMap
import org.ossim.omar.Job
import org.ossim.omar.JobStatus

class ProductService {
	def grailsApplication
    def newProduct(def params) {
    	def caseInsensitiveParams = new CaseInsensitiveMap( params )

    	def generatedId = UUID.randomUUID()
    	caseInsensitiveParams.jobId = generatedId.toString();

    	def message = buildMessage(caseInsensitiveParams)
    	def messageString = message.toString()

    	//println "JOB ID = ${message.content.jobId}"
		//println "MESSAGE = ${messageString}"
		
		Job.withTransaction{
			def date = new Date()
			println "def job"
			def job = new Job(jobId:generatedId.toString(),
							  jobType:"ProductMessage",
							  status:JobStatus.READY.toString(),
							  data:"",
							  statusMessage:"",
							  submitDate:date
							  )

			job.save(flush:true)
			println "job save"
		}

		def messageBuilder = new RabbitMessageBuilder()
		messageBuilder.send("omar.job.product", messageString)
		
		def result = [message:messageString, jobId:generatedId]
		println result
		println "end"
		result
    }

    def nextAvailableProductDir() {
    	grailsApplication.config.omar.product.dirs[0] as File
    }

    def buildMessage(def params) {
		def rasterEntries = params.layers.split(",")

		def fileInfoArray = []

		RasterEntry.withTransaction{
			rasterEntries.each{value->
				def rasterEntry = RasterEntry.findById(value.toInteger())
				//println rasterEntry
				def fileInfo = [type:"LOCAL_FILE",file:rasterEntry.filename,entries:[[entryId:rasterEntry.entryId]]]
				fileInfoArray<<fileInfo
			}
		}

		def srsCode
		switch(params.outputProjection.toUpperCase()) {
			case "GEOGRAPHIC":
				srsCode = "EPSG:4326"
				break
			default:
				srsCode = "EPSG:4326"
				break
		}

		def outputFile = "${params.outputFileName}"
		switch(params.outputType.toUpperCase()) {
			case ~/.*GPKG.*/:
				outputFile += ".gpkg"
				break
			case ~/.*TIF.*/:
				outputFile += ".tif"
				break
			default:
				break
		}

		def productDir = nextAvailableProductDir().toString()
		def entry = "0"
		
		def productDirFile = new File(productDir,"${params.jobId}")
		def productFile = new File(productDirFile, "${outputFile}")
		def builder = new groovy.json.JsonBuilder()
		def root = builder {
		   jobId params.jobId
		   jobDir productDirFile.toString()
		   messageType "ChipperMessage"
		   inputs fileInfoArray
		   options{
		       operation "ortho"
		       combinerType "${params.combinerType}"
		       srs srsCode
		       cutBbox "${params.bbox}"
		   }
		   output{
		       file productFile.toString()
		       type "${params.outputType}"
		   }
		   archive{
		       type "zip"
		   }
		}
		builder
    }
}