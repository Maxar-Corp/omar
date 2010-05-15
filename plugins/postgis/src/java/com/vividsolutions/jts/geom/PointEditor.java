package com.vividsolutions.jts.geom;

/**
 * Created by IntelliJ IDEA.
 * User: sbortman
 * Date: May 14, 2010
 * Time: 6:17:25 PM
 * To change this template use File | Settings | File Templates.
 */

import java.beans.PropertyEditorSupport;

import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

public class PointEditor
    extends PropertyEditorSupport
{

  private Point point;

  @Override
  public String getAsText()
  {
    point = (Point) super.getValue();
    return String.format( "POINT(%f %f)",
        point.getX(), point.getY() );
  }

  @Override
  public void setAsText( String text )
      throws IllegalArgumentException
  {
    WKTReader reader = new WKTReader();
    try
    {
      point = (Point) reader.read( text );

      super.setValue( point );
    }
    catch ( ParseException e )
    {
      e.printStackTrace();
    }
  }
}
