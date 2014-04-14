package chipper

/**
 * Created by sbortman on 4/10/14.
 */
class PanSharpen extends Chip
{
  GeospatialImage colorImage
  GeospatialImage panImage

  PanSharpen(def params)
  {
    super( params )

    if ( layers )
    {
      colorImage = layers[0]
      panImage = layers[1]
    }
    else
    {
      if ( params.colorImage && params.panImage )
      {
        colorImage = GeospatialImage.findByFilename( params.colorImage as String )
        panImage = GeospatialImage.findByFilename( params.panImage as String )
      }
    }
  }

  Map<String, String> createChipperOptions()
  {
    def chipperOptionsMap = [
        cut_min_lon     : bbox?.minX as String,
        cut_min_lat     : bbox?.minY as String,
        cut_max_lon     : bbox?.maxX as String,
        cut_max_lat     : bbox?.maxY as String,
        cut_height      : ( size?.height as Integer ) as String,
        cut_width       : ( size?.width as Integer ) as String,
        scale_2_8_bit   : 'true',
        srs             : bbox?.proj?.id,
        'hist-op'       : 'auto-minmax',
        operation       : 'psm',
        resampler_filter: 'sinc'
    ]

    layers?.eachWithIndex { image, i ->
      chipperOptionsMap["image${i}.file"] = image.filename
    }

    if ( bands )
    {
      chipperOptionsMap['bands'] = bands
    }

    return chipperOptionsMap
  }
}
