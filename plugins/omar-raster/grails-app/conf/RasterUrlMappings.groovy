class RasterUrlMappings {

	static mappings = {
    "/icp/getTile"(controller: "imageSpace", action: "getTile")

        /**
         *  For some reason grails is replacing anything with:
         *  url="[action:'topImages', controller:'rasterKmlQuery' ...........
         *
         *  With kmlQuery/topImages instead of  rasterKmlQuery/topImages.
         *  Commenting out the URL mappings fixes this.  Not sure why yet.
         */

//    "/kmlQuery/topImages"(controller: "rasterKmlQuery", action: "topImages")
//    "/kmlQuery/imageFootprints"(controller: "rasterKmlQuery", action: "imageFootprints")
//    "/kmlQuery/getImagesKml"(controller: "rasterKmlQuery", action: "getImagesKml")
		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}

		"/"(view:"/index")
		"500"(view:'/error')
	}
}
