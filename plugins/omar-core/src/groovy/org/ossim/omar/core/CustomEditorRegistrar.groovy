package org.ossim.omar.core

import org.springframework.beans.PropertyEditorRegistry
import org.springframework.beans.PropertyEditorRegistrar

/**
 * Created by IntelliJ IDEA.
 * User: sbortman
 * Date: Dec 2, 2010
 * Time: 1:26:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class CustomEditorRegistrar implements PropertyEditorRegistrar
{
  public void registerCustomEditors(PropertyEditorRegistry reg)
  {
    reg.registerCustomEditor(Map.class, new MapPropertyEditor());
  }
}