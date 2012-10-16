package omar.time.lapse

class TimeLapseController 
{
	def rasterEntrySearchService

	def timeLapse() 
	{ 
		if (params.layers)
		{
			def rasterEntries = rasterEntrySearchService.findRasterEntries(params.layers?.split(","))
			println "${rasterEntries.indexId}"
		}
	}
}
