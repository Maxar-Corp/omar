package geoscript

import geoscript.geom.Geometry

class BufferController {

    def index = {
        def g = Geometry.fromWKT(params.geom)
        def d = params.distance as Double
        def wkt = g.buffer(d).wkt
        render(wkt)
    }

}

