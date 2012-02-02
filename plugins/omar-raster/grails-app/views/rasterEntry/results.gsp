<%--
  Created by IntelliJ IDEA.
  User: sbortman
  Date: 1/27/12
  Time: 7:50 AM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <title>OMAR <g:meta name="app.version"/>: Raster Search Results</title>
  <meta content="resultsPageLayout" name="layout"/>
  <r:require modules="resultsPageLayout"/>
  <g:set var="entityName" value="${message(code: 'rasterEntry.label', default: 'RasterEntry')}"/>
</head>

<body class=" yui-skin-sam">

<content tag="top">
  <g:render template="resultsMenu"/>
  <h1><g:message code="default.list.label" args="[entityName]"/></h1>

  <g:render template="resultsPaginator" model="${[totalCount: totalCount, queryParams: queryParams, params: params]}"/>
</content>

<content tag="bottom">
</content>

<%--
<content tag="left">
</content>

<content tag="right">
</content>
--%>

<content tag="center">

  <g:if test="${flash.message}">
    <div class="message">${flash.message}</div>
  </g:if>

  <div id="demo" class="yui-navset">
    <ul class="yui-nav">
      <li><a href="#tab1"><em>Image</em></a></li>
      <li class="selected"><a href="#tab2"><em>Metadata</em></a></li>
      <li><a href="#tab3"><em>File</em></a></li>
      <li><a href="#tab4"><em>Links</em></a></li>
    </ul>

    <div class="yui-content">
      <div id="tab1">
        <g:render template="imageTab" model="${[rasterEntries: rasterEntries, queryParams: queryParams]}"/>
      </div>

      <div id="tab2">
        <g:render template="metadataTab" model="${[rasterEntries: rasterEntries, tagNameList: tagNameList,
            tagHeaderList: tagHeaderList, queryParams: queryParams]}"/>
      </div>

      <div id="tab3">
        <g:render template="fileTab" model="${[rasterEntries: rasterEntries, queryParams: queryParams]}"/>
      </div>

      <div id="tab4">
        <g:render template="linksTab" model="${[rasterEntries: rasterEntries]}"/>
      </div>

    </div>
  </div>
</content>

<r:script>

    var tabView = new YAHOO.widget.TabView( 'demo' );
    var oMenu = new YAHOO.widget.MenuBar("resultsMenu", {
      autosubmenudisplay: true,
      hidedelay: 750,
      lazyload: true,
      showdelay: 0,
      zIndex:9999});
    oMenu.render();
    function exportAs(format)
    {
      form = document.getElementById("exportForm");
      if ( format&&form )
      {
        var exportURL = "${createLink(controller: 'rasterEntryExport', action: 'export', params: params)}";

        exportURL += "&format=" + format;

        //alert(exportURL);

        form.action = exportURL;
        form.submit();
      }
    }

      //YAHOO.util.Dom.setStyle(document.body, 'display', 'none');
    var Dom = YAHOO.util.Dom;
    var Event = YAHOO.util.Event;
  var omarSearchResults= new OmarSearchResults();

      omarSearchResults.setProperties(${params.encodeAsJSON()});
      omarSearchResults.setProperties(document);

    updatePageOffset();

  function updateOffset()
  {

      validDataFlag = true;
      maxValue = parseInt(document.getElementById("max").value);
      pageOffsetValue = parseInt(document.getElementById("pageOffset").value);


      if(!YAHOO.lang.isNumber(pageOffsetValue))
      {
        validDataFlag = false;
        alert("Page offset must be a number");
      }
      if(!YAHOO.lang.isNumber(maxValue))
      {
        validDataFlag = false;
        alert("Max value must be a number");
      }

      if(validDataFlag)
      {
          document.getElementById("max").value        = maxValue
          document.getElementById("pageOffset").value = pageOffsetValue

          pages = Math.ceil(${totalCount ?: 0} / maxValue);

          if( pageOffsetValue >= 1 && pageOffsetValue <= pages)
          {
              document.getElementById("offset").value = (document.getElementById("pageOffset").value - 1) * maxValue;

              omarSearchResults.setProperties(document);

              var url = "${createLink(action: 'results')}?" + omarSearchResults.toUrlParams();
              document.paginateForm.action = url;
              document.paginateForm.submit();
          }
          else
          {
              alert("Input must be between 1 and " + pages + ".");
          }
      }


  }

  function updateMaxCount()
  {
    maxElement    = document.getElementById("max");
    offsetElement = document.getElementById("offset");
    if(offsetElement)
    {
       offsetElement.value = 0;
    }
    if(!maxElement ||(parseInt(maxElement.value) < 1))
    {
        alert("Max value can't be zero");
        if(maxElement) maxElement.value = omarSearchResults["max"];
        return;
    }
    omarSearchResults.setProperties(document);
    updatePageOffset();

    updateOffset();
  }

  function updatePageOffset(){
      offsetValue = omarSearchResults["offset"];
      maxValue    = omarSearchResults["max"];
      totalCountValue    = omarSearchResults["totalCount"];
      if(!offsetValue) offsetValue = "0"
      if(maxValue &&totalCountValue)
      {
        offsetValue      = parseInt(offsetValue);
        maxValue    = parseInt(maxValue);
        totalCountValue  = parseInt(totalCountValue);
        var pageOffset = document.getElementById("pageOffset");
        if(pageOffset&&maxValue)
        {
           pageOffset.value = (offsetValue/maxValue) + 1;
        }
      }
  }
</r:script>
</body>
</html>
