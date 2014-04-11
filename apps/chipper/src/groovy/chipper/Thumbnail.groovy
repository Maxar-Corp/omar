package chipper

import java.awt.Dimension

/**
 * Created by sbortman on 4/9/14.
 */
class Thumbnail extends Chip
{
  GeospatialImage image

  Thumbnail(params)
  {
    super( params )

    image = layers[0]

    if ( !size )
    {
      if ( params.size )
      {
        size = new Dimension( params.size as Integer, params.size as Integer )
      }
      else
      {
        size = new Dimension( 128, 128 )
      }
    }
  }

  Map<String, String> createChipperOptions()
  {
    def chipperOptionsMap = [
        thumbnail_resolution: ( this.size.width as Integer ) as String,
        'hist-op'           : 'auto-minmax',
        operation           : 'chip',
        scale_2_8_bit       : 'true',
        //'srs'               : 'epsg:4326',
        three_band_out      : 'true',
        'image0.file'       : image.filename
    ]

    // HACK - Need a better way to determine default bands
    if ( image.numBands > 3 )
    {
      chipperOptionsMap.bands = '3,2,1'
    }

    return chipperOptionsMap
  }
}
