package chipper

/**
 * Created by sbortman on 4/10/14.
 */
class TwoColorMultiView extends Chip
{
  GeospatialImage redImage
  GeospatialImage blueImage

  TwoColorMultiView(def params)
  {
    super( params )

    if ( layers )
    {
      redImage = layers[0]
      blueImage = layers[1]
    }
    else
    {
      if ( params.redImage && params.blueImage )
      {
        redImage = GeospatialImage.findByFilename( params.redImage as String )
        blueImage = GeospatialImage.findByFilename( params.blueImage as String )
        layers = [redImage, blueImage]
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
        operation       : '2cmv',
        resampler_filter: 'sinc',
        'image0.file'   : redImage?.filename,
        'image1.file'   : blueImage?.filename
    ]

    return chipperOptionsMap
  }
}
