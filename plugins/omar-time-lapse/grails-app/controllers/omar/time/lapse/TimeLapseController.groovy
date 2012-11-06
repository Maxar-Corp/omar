package omar.time.lapse

class TimeLapseController 
{
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
}
