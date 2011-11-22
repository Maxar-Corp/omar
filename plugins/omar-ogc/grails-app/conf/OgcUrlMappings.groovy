class OgcUrlMappings {


	static mappings = {

    "/ogc/wms"(controller: "wms", action: "wms")
    "/ogc/wcs"(controller: "wcs", action: "wcs")

		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}

		"/"(view:"/index")
		"500"(view:'/error')
	}
}
