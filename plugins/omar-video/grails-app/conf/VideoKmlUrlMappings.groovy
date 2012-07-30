class VideoKmlUrlMappings {

	static mappings = {

/**
 *  For some reason grails is replacing anything with:
 *  url="[action:'topVideos', controller:'videoKmlQuery' ...........
 *
 *  With kmlQuery/topVideos instead of  videoKmlQuery/topVideos.
 *  Commenting out the URL mappings fixes this.  Not sure why yet.
  */
//    "/kmlQuery/topVideos"(controller: "videoKmlQuery", action: "topVideos")
//    "/kmlQuery/videoFootprints"(controller: "videoKmlQuery", action: "videoFootprints")
//    "/kmlQuery/getVideosKml"(controller: "videoKmlQuery", action: "getVideosKml")

		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}
		"/"(view:"/index")
		"500"(view:'/error')
	}
}
