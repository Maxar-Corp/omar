package org.ossim.omar.raster

import geoscript.filter.Filter
import geoscript.layer.Layer
import geoscript.style.Style


import org.geotools.data.Query
import org.geotools.map.FeatureLayer

/**
 * Created by sbortman on 7/7/15.
 */
class QueryLayer extends FeatureLayer
{
  public QueryLayer(Layer layer, Style style, String title = "")
  {
    super( layer.fs, style.gtStyle, title )
  }

  public void setFilter(Filter filter)
  {
    this.query = new Query( this.featureSource.name.localPart, filter.filter )
  }
}