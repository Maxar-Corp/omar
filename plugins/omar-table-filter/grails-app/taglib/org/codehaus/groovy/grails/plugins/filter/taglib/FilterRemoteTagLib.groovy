package org.codehaus.groovy.grails.plugins.filter.taglib

import org.springframework.validation.Errors;
import org.springframework.context.NoSuchMessageException;
import org.springframework.web.servlet.support.RequestContextUtils as RCU;
import org.codehaus.groovy.grails.commons.GrailsClassUtils as GCU
import com.opensymphony.module.sitemesh.PageParserSelector
import com.opensymphony.module.sitemesh.Factory
import org.codehaus.groovy.grails.web.pages.GroovyPagesTemplateEngine
import org.springframework.web.context.ServletConfigAware
import javax.servlet.ServletConfig
import org.springframework.beans.factory.InitializingBean;
import org.codehaus.groovy.grails.web.sitemesh.FactoryHolder
import org.apache.commons.lang.StringUtils as SU
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.commons.DomainClassArtefactHandler


class FilterRemoteTagLib
{

  static namespace = 'filter'
  def grailsApplication

  /**
   * Creates next/previous links to support pagination for the current controller
   *
   *
   */
  def paginate = { attrs ->

    def writer = out

    if ( attrs.total == null )
      throwTagError("Tag [paginate] is missing required attribute [total]")

    if ( !attrs.bean )
      throwTagError("Tag [paginate] is missing required attribute [bean]")

    def messageSource = grailsAttributes.getApplicationContext().getBean("messageSource")
    def locale = RCU.getLocale(request)

    def total = attrs.total.toInteger()

    def offset = params.offset?.toInteger()
    def max = params.max?.toInteger()
    def maxsteps = (attrs.maxsteps ? attrs.maxsteps.toInteger() : 10)

    if ( !offset ) offset = (attrs.offset ? attrs.offset.toInteger() : 0)
    if ( !max ) max = (attrs.max ? attrs.max.toInteger() : 10)

    def linkParams = [offset: offset - max, max: max]
    if ( params.sort ) linkParams.sort = params.sort
    if ( params.order ) linkParams.order = params.order
    if ( attrs.params ) linkParams.putAll(attrs.params)
    if ( params.searchStr ) linkParams.searchStr = params.searchStr
    if ( params.searchDate ) linkParams.searchDate = params.searchDate
    if ( params.searchField ) linkParams.searchField = params.searchField

    // filter params
    if ( params.filterValue )
    {
      linkParams.filterValue = params.filterValue
      if ( params.filterField ) linkParams.filterField = params.filterField
      if ( params.filterCriteria ) linkParams.filterCriteria = params.filterCriteria
      if ( params.filterValue2 ) linkParams.filterValue2 = params.filterValue2
      if ( params.bean ) linkParams.filterBean = params.bean
    }

    GrailsDomainClass domainClass = (GrailsDomainClass)grailsApplication.getArtefact(DomainClassArtefactHandler.TYPE, attrs.bean);

    linkParams.filterBean = attrs.bean
    linkParams.filterTemplate = attrs.template ? attrs.template : "/${SU.uncapitalize(domainClass.name)}/list"

    if ( !attrs.update ) attrs.update = "list"

    def linkTagAttrs = [update: attrs.update]

    linkTagAttrs.controller = attrs.controller ? attrs.controller : "filter"
    linkTagAttrs.action = attrs.action ? attrs.action : "filter"

    if ( attrs.id != null )
    {
      linkTagAttrs.id = attrs.id
    }

    linkTagAttrs.params = linkParams

    // determine paging variables
    def steps = maxsteps > 0
    int currentstep = (offset / max) + 1
    int firststep = 1
    int laststep = Math.round(Math.ceil(total / max))

    //add filter spinner
    linkTagAttrs.onLoading = attrs.onLoading ? attrs.onLoading : 'filterInitialized()'
    linkTagAttrs.onComplete = attrs.onComplete ? attrs.onComplete : 'filterFinished()'

    // display previous link when not on firststep
    if ( currentstep > firststep )
    {
      linkTagAttrs.class = 'prevLink'
      writer << remoteLink(linkTagAttrs.clone()) {
        (attrs.prev ? attrs.prev : messageSource.getMessage('paginate.prev', null, messageSource.getMessage('default.paginate.prev', null, 'Previous', locale), locale))
      }
    }

    // display steps when steps are enabled and laststep is not firststep
    if ( steps && laststep > firststep )
    {
      linkTagAttrs.class = 'step'

      // determine begin and endstep paging variables
      int beginstep = currentstep - Math.round(maxsteps / 2) + (maxsteps % 2)
      int endstep = currentstep + Math.round(maxsteps / 2) - 1

      if ( beginstep < firststep )
      {
        beginstep = firststep
        endstep = maxsteps
      }
      if ( endstep > laststep )
      {
        beginstep = laststep - maxsteps + 1
        if ( beginstep < firststep )
        {
          beginstep = firststep
        }
        endstep = laststep
      }

      // display firststep link when beginstep is not firststep
      if ( beginstep > firststep )
      {
        linkParams.offset = 0
        writer << remoteLink(linkTagAttrs.clone()) {firststep.toString()}
        writer << '<span class="step">..</span>'
      }

      // display paginate steps
      (beginstep..endstep).each { i ->
        if ( currentstep == i )
        {
          writer << "<span class=\"currentStep\">${i}</span>"
        }
        else
        {
          linkParams.offset = (i - 1) * max
          writer << remoteLink(linkTagAttrs.clone()) {i.toString()}
        }
      }

      // display laststep link when endstep is not laststep
      if ( endstep < laststep )
      {
        writer << '<span class="step">..</span>'
        linkParams.offset = (laststep - 1) * max
        writer << remoteLink(linkTagAttrs.clone()) { laststep.toString() }
      }
    }

    // display next link when not on laststep
    if ( currentstep < laststep )
    {
      linkTagAttrs.class = 'nextLink'
      linkParams.offset = offset + max
      writer << remoteLink(linkTagAttrs.clone()) {
        (attrs.next ? attrs.next : messageSource.getMessage('paginate.next', null, messageSource.getMessage('default.paginate.next', null, 'Next', locale), locale))
      }
    }

  }

  /**
   * Renders a sortable column to support sorting in list views
   *
   * Attributes:
   *
   * property - name of the property relating to the field
   * defaultOrder (optional) - default order for the property; choose between asc (default if not provided) and desc
   * title (optional*) - title caption for the column
   * titleKey (optional*) - title key to use for the column, resolved against the message source
   * params (optional) - a map containing request parameters
   * action (optional) - the name of the action to use in the link, if not specified the list action will be linked
   * Attribute title or titleKey is required. When both attributes are specified then titleKey takes precedence,
   * resulting in the title caption to be resolved against the message source. In case when the message could
   * not be resolved, the title will be used as title caption.
   *
   * Examples:
   *
   * <g:sortableColumn property="title" title="Title" />
   * <g:sortableColumn property="title" title="Title" style="width: 200px" />
   * <g:sortableColumn property="title" titleKey="book.title" />
   * <g:sortableColumn property="releaseDate" defaultOrder="desc" title="Release Date" />
   * <g:sortableColumn property="releaseDate" defaultOrder="desc" title="Release Date" titleKey="book.releaseDate" />
   */
  def sortableColumn = { attrs ->
    def writer = out
    if ( !attrs.property )
      throwTagError("Tag [sortableColumn] is missing required attribute [property]")

    if ( !attrs.title && !attrs.titleKey )
      throwTagError("Tag [sortableColumn] is missing required attribute [title] or [titleKey]")

    if ( !attrs.bean )
      throwTagError("Tag [sortableColumn] is missing required attribute [bean]")

    //println "sortable - > " + attrs
    //println "params - > " + params

    def property = attrs.remove("property")

    def action = attrs.action ? attrs.remove("action") : "filter"
    def controller = attrs.controller ? attrs.remove("controller") : "filter"
    def defaultOrder = attrs.remove("defaultOrder")
    if ( defaultOrder != "desc" ) defaultOrder = "asc"

    // update
    if ( !attrs.update )
      attrs.update = "list"

    // current sorting property and order
    def sort = params.sort
    def order = params.order

    // add sorting property and params to link params
    def linkParams = [sort: property]
    if ( params.id ) linkParams.put("id", params.id)
    if ( attrs.params ) linkParams.putAll(attrs.remove("params"))

    // filter params
    GrailsDomainClass domainClass = (GrailsDomainClass)grailsApplication.getArtefact(DomainClassArtefactHandler.TYPE, attrs.bean);

    linkParams.filterBean = attrs.bean
    linkParams.filterTemplate = attrs.template ? attrs.template : "/${SU.uncapitalize(domainClass.name)}/list"

    if ( params.filterValue )
    {
      linkParams.filterValue = params.filterValue
      if ( params.filterField ) linkParams.filterField = params.filterField
      if ( params.filterCriteria ) linkParams.filterCriteria = params.filterCriteria
      if ( params.filterValue2 ) linkParams.filterValue2 = params.filterValue2
      if ( params.bean ) linkParams.filterBean = params.bean
    }

    // determine and add sorting order for this column to link params
    attrs.class = "sortable"
    if ( property == sort )
    {
      attrs.class = attrs.class + " sorted " + order
      if ( order == "asc" )
      {
        linkParams.order = "desc"
      }
      else
      {
        linkParams.order = "asc"
      }
    }
    else
    {
      linkParams.order = defaultOrder
    }
    if ( params.auditSearchStr )
    {
      linkParams.put('logSearchStr', params.auditSearchStr)
    }
    if ( params.logSearchStr )
    {
      linkParams.put('logSearchStr', params.logSearchStr)
    }
    if ( params.searchDate )
    {
      linkParams.put('searchDate', params.searchDate)
    }
    if ( params.searchField )
    {
      linkParams.put('searchField', params.searchField)
    }
    // determine column title
    def title = attrs.remove("title")
    def titleKey = attrs.remove("titleKey")
    if ( titleKey )
    {
      if ( !title ) title = titleKey
      def messageSource = grailsAttributes.getApplicationContext().getBean("messageSource")
      def locale = RCU.getLocale(request)
      title = messageSource.getMessage(titleKey, null, title, locale)
    }

    //set bean used on filter controller to determine the domain class
    linkParams.put("bean", attrs.bean)

    writer << "<th "
    // process remaining attributes
    attrs.each { k, v ->
      writer << "${k}=\"${v.encodeAsHTML()}\" "
    }
    writer << ">${remoteLink(action: action, controller: controller, 'update': attrs.update, params: linkParams, onLoading: 'filterInitialized()', onComplete: 'filterFinished()') { title }}</th>"
  }

}
