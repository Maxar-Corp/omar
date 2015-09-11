package org.ossim.omar.raster

import org.grails.databinding.BindingHelper
import org.grails.databinding.DataBindingSource

/**
 * Created by sbortman on 7/6/15.
 */
class GetMapRequestBindingHelper implements BindingHelper<GetMapRequest>
{
  @Override
  GetMapRequest getPropertyValue(Object obj, String propertyName, DataBindingSource source)
  {
//    println "${obj} ${propertyName} ${source.propertyNames}"
//    println "getPropertyValue"

    obj['service'] = source['SERVICE'] ?: null
    obj['version'] = source['VERSION'] ?: null
    obj['request'] = source['REQUEST'] ?: null

    obj['srs'] = source['SRS'] ?: null
    obj['bbox'] = source['BBOX'] ?: null
    obj['layers'] = source['LAYERS'] ?: null
    obj['styles'] = source['STYLES'] ?: null

    obj['width'] = Integer.valueOf( source['WIDTH'] ?: null )
    obj['height'] = Integer.valueOf( source['HEIGHT'] ?: null )
    obj['format'] = source['FORMAT']
    obj['transparent'] = Boolean.valueOf( source['TRANSPARENT'] ?: null )

    obj['filter'] = source['FILTER']

    return obj
  }
}
