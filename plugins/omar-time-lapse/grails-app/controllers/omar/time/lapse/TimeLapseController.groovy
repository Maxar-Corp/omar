package omar.time.lapse

class TimeLapseController 
{
	def imageSpaceService
	def rasterEntrySearchService

	def viewer() 
	{ 
	
		def rasterEntries = rasterEntrySearchService.findRasterEntries(params.layer?.split(","))
		def bbox = params.bbox ?: [-180,-90,180,90]
		def upAngle

		def timeLapseJson = '\n' + 
			'{\n' + 
			'	"bbox" : [' + bbox +'],\n' +
			'	"layers" : \n' +
			'	[\n'

		rasterEntries.eachWithIndex 
		{ 
			obj, i -> 
			upAngle = imageSpaceService.computeUpIsUp(obj.filename, obj.entryId.toInteger())
			timeLapseJson += 
			'		{\n' + 
			'			"acquisitionDate" : "' + obj.acquisitionDate + '",\n' +
			'			"azimuth" : "' + obj.azimuthAngle + '",\n' +
			'			"countryCode" : "' + obj.countryCode + '",\n' +
			'			"graze" : "' + obj.grazingAngle + '",\n' +
			'			"id" : "' + obj.id + '",\n' +
			'			"indexId" : "' + obj.indexId + '",\n' +
			'			"imageId" : "' + obj.title + '",\n' +
			'			"upAngle" : "' + upAngle + '"\n' +
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
