class RasterKmlUrlMappings {

	static mappings = {
    "/kmlQuery/topImages"(controller: "rasterKmlQuery", action: "topImages")
    "/kmlQuery/imageFootprints"(controller: "rasterKmlQuery", action: "imageFootprints")
    "/kmlQuery/getImagesKml"(controller: "rasterKmlQuery", action: "getImagesKml")
		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}

		"/"(view:"/index")
		"500"(view:'/error')
	}
}
