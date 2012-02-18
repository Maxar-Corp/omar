package org.ossim.omar.ogc

import java.text.SimpleDateFormat
import org.springframework.context.ApplicationContextAware
import org.springframework.context.ApplicationContext
import org.springframework.beans.factory.InitializingBean

class KmlService implements ApplicationContextAware, InitializingBean
{

  static transactional = false
  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
  ApplicationContext applicationContext
  def grailsApplication
  def tagLibBean
  def coordinateConversionService


  String buildUrl(String url, Map params)
  {
    def String result;
    def list = []
    params.each {k, v ->
      list << "$k=$v"
    }
    list = list.join("&")
    if ( url.indexOf("?") == -1 )
    {
      result = "${url}?"
    }
    else
    {
      result = "${url}&"
    }
    return "${result}${list}"
  }


  public void afterPropertiesSet()
  {
    tagLibBean = applicationContext.getBean("org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib")
  }
}
