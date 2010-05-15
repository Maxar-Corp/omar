package org.ossim.postgis

/**
 * Created by IntelliJ IDEA.
 * User: sbortman
 * Date: May 14, 2010
 * Time: 7:01:43 PM
 * To change this template use File | Settings | File Templates.
 */

import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.PropertyEditorRegistry;

import com.vividsolutions.jts.geom.Point
import com.vividsolutions.jts.geom.PointEditor


public class CustomEditorRegistrar implements PropertyEditorRegistrar
{
  public void registerCustomEditors(PropertyEditorRegistry reg)
  {
    reg.registerCustomEditor(Point.class, new PointEditor());
  }
}
