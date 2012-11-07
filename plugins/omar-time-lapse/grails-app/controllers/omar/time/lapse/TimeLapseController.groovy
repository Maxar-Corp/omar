package omar.time.lapse

class TimeLapseController 
{
	def exportTimeLapsePdfService
	def rasterEntrySearchService

	def timeLapse() 
	{ 
	
		def rasterEntries = rasterEntrySearchService.findRasterEntries(params.imageIds?.split(","))
		def bbox = params.bbox
		render(
			view: "timeLapse.gsp",
			model:
			[
				acquisitionDates: rasterEntries.acquisitionDate,
				bbox: bbox,
				countryCodes: rasterEntries.countryCode,
				imageIds: rasterEntries.title,
				indexIds: rasterEntries.indexId
			]	
		)		
	}

	def exportTimeLapse()
	{
		def imageUrls = params.imageUrls.split(">")
		def filename = exportTimeLapsePdfService.serviceMethod(imageUrls)
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
