package omar.time.lapse

class TimeLapseController 
{
	def rasterEntrySearchService

	def viewer() 
	{ 
	
		def rasterEntries = rasterEntrySearchService.findRasterEntries(params.layer?.split(","))
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
			'			"azimuth" : "' + obj.azimuthAngle + '",\n' +
			'			"countryCode" : "' + obj.countryCode + '",\n' +
			'			"graze" : "' + 'obj.graze' + '",\n' +
			'			"id" : "' + obj.id + '",\n' +
			'			"indexId" : "' + obj.indexId + '",\n' +
			'			"imageId" : "' + obj.title + '"\n' +
			'		}'

			if (i != rasterEntries.size() - 1) { timeLapseJson += ',\n' }
			else { timeLapseJson += '\n' }
		}			

		timeLapseJson +=
			'	]\n' +
			'}'
	
		render(
			view: "timeLapse.gsp",
			model: [timeLapseObject: timeLapseJson]	
		)		
	}
}
