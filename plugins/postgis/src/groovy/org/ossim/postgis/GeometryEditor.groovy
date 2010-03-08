package org.ossim.postgis
/**
 * Created by IntelliJ IDEA.
 * User: sbortman
 * Date: Jun 6, 2008
 * Time: 9:36:32 PM
 * To change this template use File | Settings | File Templates.
 */

import java.beans.PropertyEditorSupport

class GeometryEditor extends PropertyEditorSupport
{

  public String getAsText()
  {
    def text = getValue().geom.toString();

    return text
  }

  public void setAsText(String text)
  {
    Geometry geometry = new Geometry(geom: org.postgis.PGgeometry.geomFromString(text));

    setValue(geometry);
  }

}