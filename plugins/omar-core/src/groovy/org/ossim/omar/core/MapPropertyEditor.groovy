package org.ossim.omar.core

import java.beans.PropertyEditorSupport
import grails.converters.JSON

/**
 * Created by IntelliJ IDEA.
 * User: sbortman
 * Date: Dec 2, 2010
 * Time: 1:05:49 PM
 * To change this template use File | Settings | File Templates.
 */
class MapPropertyEditor extends PropertyEditorSupport
{
  @Override
  public String getAsText()
  {
    Object object = getValue();

    return object as JSON
  }

  @Override
  public void setAsText(String text)
  {
    setValue(JSON.parse(text))
  }
}
