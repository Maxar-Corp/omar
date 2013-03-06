package omar.time.lapse

class TimeLapseController 
{
	def exportTimeLapseGifService
	def exportTimeLapsePdfService
	def rasterEntrySearchService

	def viewer() 
	{ 
	
		def rasterEntries = rasterEntrySearchService.findRasterEntries(params.layers?.split(","))
		def bbox = params.bbox ?: [-180,-90,180,90]
		def timeLapseJson = '\n' + 
			'{\n' + 
			'	"bbox" : [' + bbox +'],\n' +
			'	"layers" : \n' +
			'	[\n'

		rasterEntries.eachWithIndex 
		{ 
			obj, i -> timeLapseJson += 
			'		{\n' + 
			'			"acquisitionDate" : "' + obj.acquisitionDate + '",\n' +
			'			"indexId" : "' + obj.indexId + '",\n' +
			'			"imageId" : "' + obj.title + '"\n' +
			'		}'

			if (i != rasterEntries.size() - 1) { timeLapseJson += ',\n' }
			else { timeLapseJson += '\n' }
		}			

		timeLapseJson +=
			'	]\n' +
			'}'
	
		def markers = params.markers?.split(",") ?: ["null"]
		render(
			view: "timeLapse.gsp",
			model:
			[
				acquisitionDates: rasterEntries.acquisitionDate,
				bbox: bbox,
				countryCodes: rasterEntries.countryCode,
				entryIds: rasterEntries.id,
				imageIds: rasterEntries.title,
				indexIds: rasterEntries.indexId,
				markers: markers,
				niirsValues: rasterEntries.niirs,
				timeLapseObject: timeLapseJson
			]	
		)		
	}

	def exportTimeLapseGif()
	{
		def imageUrls = params.imageUrls.split(">")
		def filename = exportTimeLapseGifService.exportGif(imageUrls)
		def file = new File( "${filename}" )
		if ( file.exists() )
		{
			response.setContentType( "application/octet-stream" )
			response.setHeader( "Content-disposition", "attachment; filename=${file.name}" )
			response.outputStream << file.bytes

			def removeImageFile = "rm ${file}"
			def removeImageFileProc = removeImageFile.execute()
			removeImageFileProc.waitFor()
		}
	}

	def exportTimeLapsePdf()
	{
		def imageUrls = params.imageUrls.split(">")
		def filename = exportTimeLapsePdfService.exportPdf(imageUrls)
		def file = new File( "${filename}" )
		if ( file.exists() )
		{
			response.setContentType( "application/octet-stream" )
			response.setHeader( "Content-disposition", "attachment; filename=${file.name}" )
			response.outputStream << file.bytes

			def removeImageFile = "rm ${file}"
			def removeImageFileProc = removeImageFile.execute()
			removeImageFileProc.waitFor()
		}
	}
}
