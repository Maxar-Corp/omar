package org.ossim.omar

class SuperOverlayService {

    static transactional = true

    def isAnEdgeTile(def level, def row, def col)
    {
        def result = true

        if(row&&col)
        {
          def maxValue = (2**level) - 1
          if((row != maxValue)&&
             (col != maxValue))
          {
            // must be interior tile
            result = false
          }
        }

        result
    }
    def canSplit(def tileBounds, def tileSize, def metersPerDegree, def fullResMetersPerPixel)
    {
        def deltax = (tileBounds.maxx-tileBounds.minx)
        def deltay = (tileBounds.maxy-tileBounds.miny)
        def maxDelta = deltax>deltay?deltay:deltax
        def maxTileSize = tileSize.width>tileSize.height?tileSize.width:tileSize.height
        def metersPerPixel = (maxDelta*metersPerDegree)/maxTileSize

        // keep splitting if we can zoom further
        metersPerPixel > fullResMetersPerPixel
    }
    def tileBound(def params, def fullResBbox)
    {
        def level = params.level?params.level as Integer:0
        def row   = params.row?params.row as Integer:0
        def col   = params.col?params.col as Integer:0
        def minx  = fullResBbox.minx
        def maxx  = fullResBbox.maxx
        def miny  = fullResBbox.miny
        def maxy  = fullResBbox.maxy
        def deltax = (maxx-minx)/(2**level)
        def deltay = (maxy-miny)/(2**level)

        def llx = minx + deltax*col
        def lly = miny + deltay*row

        [minx:llx, miny:lly, maxx:(llx+deltax), maxy:(lly+deltay)]
    }
    def generateSubTiles(def params, def fullResBbox)
    {
        def level = params.level as Integer
        def row   = params.row   as Integer
        def col   = params.col   as Integer
        def nrow = row*2
        def ncol = col*2
        def minx  = fullResBbox.minx
        def maxx  = fullResBbox.maxx
        def miny  = fullResBbox.miny
        def maxy  = fullResBbox.maxy
        ++level
        def deltax = (maxx-minx)/(2**level)
        def deltay = (maxy-miny)/(2**level)

        def llx = minx + deltax*ncol
        def lly = miny + deltay*nrow

        [[minx:llx, miny:lly, maxx:(llx+deltax), maxy:(lly+deltay), level:level, col:ncol, row:nrow],
         [minx:llx+deltax, miny:lly, maxx:(llx+2.0*deltax), maxy:(lly+deltay), level:level, col:(ncol+1), row:nrow],
         [minx:llx+deltax, miny:(lly+deltay), maxx:(llx+2.0*deltax), maxy:(lly+2.0*deltay), level:level, col:(ncol+1), row:(nrow+1)],
         [minx:llx, miny:lly+deltay, maxx:(llx+deltax), maxy:(lly+2.0*deltay), level:level, col:ncol, row:(nrow+1)]
        ]
    }
}
