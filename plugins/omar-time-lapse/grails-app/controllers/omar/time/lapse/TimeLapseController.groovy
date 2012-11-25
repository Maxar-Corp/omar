package omar.time.lapse

class TimeLapseController 
{
	def exportTimeLapseGifService
	def exportTimeLapsePdfService
	def rasterEntrySearchService

	def timeLapse() 
	{ 
	
		def rasterEntries = rasterEntrySearchService.findRasterEntries(params.imageIds?.split(","))
		def bbox = params.bbox ?: [-180,-90,180,90]
		def markers = params.markers?.split(",") ?: ["null"]
		render(
			view: "timeLapse.gsp",
			model:
			[
				acquisitionDates: rasterEntries.acquisitionDate,
				bbox: bbox,
				countryCodes: rasterEntries.countryCode,
				imageIds: rasterEntries.title,
				indexIds: rasterEntries.indexId,
				markers: markers,
				niirsValues: rasterEntries.niirs
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
