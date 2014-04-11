package chipper

/**
 * Created by sbortman on 4/10/14.
 */
class HillShade extends Chip
{
  Double azimuthAngle
  Double elevationAngle
  Double gain

  Integer colorBlue
  Integer colorGreen
  Integer colorRed

  String outputRadiometry
  String resamplerFilter
  String writer

  HillShade(def params)
  {
    super( params )

    azimuthAngle = params.azimuthAngle as Double ?: 270
    elevationAngle = params.elevationAngle as Double ?: 45
    gain = params.gain as Double ?: 1.5

    colorBlue = params.colorBlue as Integer ?: 139
    colorGreen = params.colorGreen as Integer ?: 26
    colorRed = params.colorRed as Integer ?: 85

    outputRadiometry = params.outputRadiometry ?: 'U8'
    resamplerFilter = params.resamplerFilter ?: 'cubic'
    writer = params.writer ?: 'ossim_png'
  }

  Map<String, String> createChipperOptions()
  {
    def chipperOptionsMap = [
        operation        : 'hillshade',

        azimuth_angle    : azimuthAngle as String,
        color_blue       : colorBlue as String,
        color_green      : colorGreen as String,
        color_red        : colorRed as String,
        elevation_angle  : elevationAngle as String,
        gain             : gain as String,

        cut_min_lon      : bbox?.minX as String,
        cut_min_lat      : bbox?.minY as String,
        cut_max_lon      : bbox?.maxX as String,
        cut_max_lat      : bbox?.maxY as String,
        cut_height       : ( size.height as Integer ) as String,
        srs              : bbox?.proj?.id,
        cut_width        : ( size.width as Integer ) as String,

        output_radiometry: outputRadiometry,
        resampler_filter : resamplerFilter,
        writer           : writer
    ]

    // Add DEMs

    def dems = findElevationCells( grailsApplication?.config?.chipper?.hillShade?.elevationPath as String, bounds )

    dems?.eachWithIndex { file, index -> chipperOptionsMap["dem${index}.file"] = file }

    // Add Images
    layers?.eachWithIndex { image, i ->
      chipperOptionsMap["image${i}.file"] = image.filename
    }

    return chipperOptionsMap
  }
}
