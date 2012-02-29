<%@ page import="org.ossim.omar.video.VideoDataSetSearchTag; grails.converters.JSON; org.ossim.omar.core.BaseQuery; org.ossim.omar.video.VideoDataSetQuery; org.ossim.omar.video.VideoDataSetSearchTag" contentType="text/html;charset=UTF-8" %>

<div class="niceBox">
  <div class="niceBoxHd">Metadata Criteria:</div>

  <div class="niceBoxMetadataBody">

    <div id="tabview2" class="yui-navset">
      <ul class="yui-nav">
        <li class="selected"><a href="#tab1" id="metadataTab"><em>Metadata</em></a></li>
        <li><a href="#tab2" id="cqlTab"><em>CQL</em></a></li>
      </ul>

      <div class="yui-content">

        <div id="tab1">

          <ol>
            <g:each in="${queryParams?.searchTagValues}" var="searchTagValue" status="i">
              <g:select
                  noSelection="${['null': 'Select One...']}"
                  id="searchTagNames[${i}]"
                  name="searchTagNames[${i}]"
                  value="${queryParams?.searchTagNames[i]}"
                  from="${org.ossim.omar.video.VideoDataSetSearchTag.list(sort: 'description')}"
                  optionKey="name" optionValue="description"/>
              <li>
                <g:textField id="searchTagValues[${i}]" name="searchTagValues[${i}]" value="${searchTagValue}"
                             onChange="updateOmarFilters()"/>
              </li>
            </g:each>
          </ol>
        </div>

        <div id="tab2">
          <g:textArea id="filter" name="filter" value="${queryParams.filter}" style='width: 100%; height: 200px;'
                      onChange="updateOmarFilters()"/>
        </div>
      </div>
    </div>
  </div>
</div>
