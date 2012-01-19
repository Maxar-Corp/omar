package org.codehaus.groovy.grails.plugins.filter.taglib

import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.commons.DomainClassArtefactHandler
import org.codehaus.groovy.grails.plugins.filter.FilterType as Type
import org.codehaus.groovy.grails.commons.GrailsDomainClassProperty
import org.springframework.web.servlet.support.RequestContextUtils as RCU
import org.codehaus.groovy.grails.plugins.filter.FilterUtils

/**
 * Filter tag lib collection
 */
class FilterTagLib
{

  static namespace = 'filter'

  /**
   * Dynamic filter
   */
  def dynamic = {attrs, body ->

    if ( !attrs.bean )
      throwTagError("Tag [dynamic] is missing required attribute [bean]")

    GrailsDomainClass domainClass = (GrailsDomainClass)grailsApplication.getArtefact(DomainClassArtefactHandler.TYPE, attrs.bean);

    String prefix = attrs.prefix ? attrs.prefix : attrs.bean + ".field"
    String filter = attrs.filter ? attrs.filter : "${message(code: 'Filter.dynamic.filter')}"
    String label = attrs.label ? attrs.label : "${message(code: 'Filter.dynamic.search')}"
    String controller = attrs.controller ? attrs.controller : "filter"
    String action = attrs.action ? attrs.action : "filter"
    String template = attrs.template ? attrs.template : "/${controllerName}/list"
    String success = attrs.success ? attrs.success : "list"
    String failure = attrs.failure ? attrs.failure : "filterError"

//        List<String> f = FilterUtils.resolveFields(domainClass)

    // add grails-filter.js reference
    out << "<script type=\"text/javascript\" src=\"${resource(plugin: 'omar-table-filter', dir: "js", file: "grails-filter.js")}\" ></script>"

    out << "<div id='filterError'>&nbsp;</div>"

    out << "<div class='filter'><form>"

    out << "<input type='hidden' name='filterBean' value='${attrs.bean}'/>"

    out << "<input type='hidden' name='filterTemplate' value='${template}'/>"

    // Extra parameters
    def params = attrs.params ?: [:]

    params.each { k, v ->
      out << "<input type='hidden' name='${k}' value='${v}'/>"
    }

    out << label

    out << "&nbsp;&nbsp;"

    out << selectFields(bean: attrs.bean, prefix: prefix, onchange: "fieldChanged(this)")

    out << "&nbsp;&nbsp;"

    //TODO show only criteria that can be applied to field
    out << "${select(noSelection: ['': '-Choose operator-'], from: Type, name: 'filterCriteria', onchange: 'criteriaChanged()')}"

    out << "&nbsp;&nbsp;"

    out << "${textField(name: 'filterValue')}"

    out << "&nbsp;&nbsp;"

    out << "${textField(name: 'filterValue2', style: 'display:none;')}"

    out << "&nbsp;&nbsp;"

    out << "${submitToRemote(action: action, controller: controller, value: filter, update: [success: success, failure: failure], onLoading: 'filterInitialized()', onComplete: 'filterFinished()')}"

    out << "&nbsp;&nbsp;"

    //out << "<div class='divider'></div>"

    out << "Page Size: "
    out << g.select(from: [10, 25, 50, 100], name: "max", value: params.max ?: 10,
            onchange: "new Ajax.Updater({success:'${success}',failure:'${failure}'},'${createLink(action: action, controller: controller)}',{asynchronous:true,evalScripts:true,onLoading:function(e){filterInitialized()},onComplete:function(e){filterFinished()},parameters:Form.serialize(this.form)});return false")

    out << "<img id='filterBusy' style='display:none;' src='${resource(plugin: 'omar-table-filter', dir: 'images', file: 'filterSpinner.gif')}' />"

    out << "</form>"

    out << "</div>"

  }

  /**
   * Creates a html select tag with fields of domain class
   */
  def selectFields = { attrs, body ->

    if ( !attrs.bean )
      throwTagError("Tag [fields] is missing required attribute [bean]")

    String bean = attrs.remove("bean")

    def messageSource = grailsAttributes.getApplicationContext().getBean("messageSource")
    def locale = RCU.getLocale(request)
    GrailsDomainClass domainClass = (GrailsDomainClass)grailsApplication.getArtefact(DomainClassArtefactHandler.TYPE, bean);

    String prefix = attrs.prefix ? attrs.remove("prefix") : bean + ".field"

    List<String> fieldList = FilterUtils.resolveFields(domainClass)

    out << "<select id='filterField' name='filterField' "

    outputAttributes(attrs)

    out << " >"
    out << "<option value='' fieldtype=''>-Choose field-</option>"


    fieldList.each {
      out << "<option value='${it}' "
      out << "fieldtype='${FilterUtils.resolveFieldType(domainClass.clazz, it)}'>"
//      out << messageSource.getMessage("${prefix}.${it}", null, "${prefix}.${it}", locale)
      out << domainClass.getPropertyByName(it).naturalName
      out << "</option>"

    }

    out << "</select>"

  }

  /**
   * Dump out attributes in HTML compliant fashion
   */
  void outputAttributes(attrs)
  {

    attrs.remove('tagName') // Just in case one is left
    attrs.each {k, v ->
      out << k << "=\"" << v.encodeAsHTML() << "\" "
    }
  }

}