package org.ossim.omar
/*
  This uses the prototype.js.  We have added the js file to the omar-core library.
  The actual source is found at http://www.prototypejs.org/api/element/observe
 */
class OmarCoreTagLib
{
  static namespace = "omar"
  def observe = {attrs ->
    if ( !attrs.noScript )
    {
      out << '<script type="text/javascript">'
    }
    if ( attrs.element && attrs.element instanceof String )
    {
      printObserve("\$('${attrs.element}')", attrs.event, attrs.function, out)
    }
    if ( attrs.element && attrs.element instanceof List )
    {
      attrs.element.each {it -> printObserve("\$('${it}')", attrs.event, attrs.function, out)}
    }
    if ( attrs.classes && attrs.classes instanceof String )
    {
      printObserveClass(attrs.classes, attrs.event, attrs.function, out)
    }
    if ( attrs.classes && attrs.classes instanceof List )
    {
      attrs.classes.each { it -> printObserveClass(it, attrs.event, attrs.function, out)}
    }
    if ( !attrs.noScript )
    {
      out << '</script>'
    }
  }

  def printObserveClass(className, event, function, out)
  {
    out << "var classes  = \$\$('.' + '${className}');"
    out << "for(i = 0; i < classes.length; i++) {"
    printObserve("classes[i]", event, function, out)
    out << "}"
  }

  def printObserve(element, event, function, out)
  {
    if ( event && event instanceof String )
    {
      out << "${element}.observe('${event}', ${function});"
    }
    if ( event && event instanceof List )
    {
      attrs.event.each { it -> out << "${element}.observe('${it}', ${function});"}
    }
  }

  def bundle = { attrs ->

    //println attrs

    def files = attrs['files']
    def contentType = attrs.contentType
    def disableBundle = grailsApplication.config.disableBundle ?: true

    if ( disableBundle )
    {
      switch ( contentType )
      {
      case "css":
        files?.each { file ->
          //println "file: ${file}"
          out << """<link rel="stylesheet" href="${resource(file)}"/>\n""".toString()
        }
        break
      case "javascript":
        files?.each { file ->
          out << """<script type="text/javascript" src="${resource(file)}"></script>\n""".toString()
        }
        break
      default:
        println "Unknown contentType"
      }
    }
    else
    {
      out << createLink(controller: 'compressor', action: 'compress',
              params: [files: files.join(','), contentType: contentType])
    }
  }
}
