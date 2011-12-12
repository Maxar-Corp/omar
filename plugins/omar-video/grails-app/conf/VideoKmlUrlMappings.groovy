class VideoKmlUrlMappings {

	static mappings = {
    "/kmlQuery/topVideos"(controller: "videoKmlQuery", action: "topVideos")
    "/kmlQuery/videoFootprints"(controller: "videoKmlQuery", action: "videoFootprints")
    "/kmlQuery/getVideosKml"(controller: "videoKmlQuery", action: "getVideosKml")

		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}
		"/"(view:"/index")
		"500"(view:'/error')
	}
}
