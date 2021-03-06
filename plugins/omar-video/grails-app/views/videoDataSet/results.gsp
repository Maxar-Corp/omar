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
  <title>OMAR <g:meta name="app.version"/>: Video Search Results</title>
  <meta content="resultsPageLayout" name="layout"/>
  <asset:stylesheet src="resultsPage.css"/>
  <g:set var="entityName" value="${message(code: 'videoDataSet.label', default: 'VideoDataSet')}"/>
</head>

<body class=" yui-skin-sam" onload="init()">

<content tag="top">
    <omar:logout/>
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
      <li><a href="#tab1"><em>Video</em></a></li>
      <li><a href="#tab2"><em>File</em></a></li>
      <li><a href="#tab3"><em>Links</em></a></li>
    </ul>

    <div class="yui-content">
      <div id="tab1">
        <g:render template="videoTab" model="${[videoDataSets: videoDataSets, queryParams: queryParams]}"/>
      </div>

      <div id="tab2">
        <g:render template="fileTab" model="${[videoDataSets: videoDataSets, queryParams: queryParams]}"/>
      </div>

      <div id="tab3">
        <g:render template="linksTab" model="${[videoDataSets: videoDataSets]}"/>
      </div>

    </div>
  </div>
</content>

<asset:javascript src="resultsPage.js"/>

<g:javascript>

    var tabView;
    var oMenu;
    var Dom;
    var Event;
    var omarSearchResult;

    function init()
    {
       console.log('here');

      tabView = new YAHOO.widget.TabView( 'demo', { activeIndex: ${videoDataSetResultCurrentTab} } );

      tabView.selectTab(${videoDataSetResultCurrentTab});
      tabView.getTab(0).addListener('click', handleClickTab);
      tabView.getTab(1).addListener('click', handleClickTab);
      tabView.getTab(2).addListener('click', handleClickTab);

      oMenu = new YAHOO.widget.MenuBar("resultsMenu", {
        autosubmenudisplay: true,
        hidedelay: 750,
        lazyload: true,
        showdelay: 0,
        zIndex:9999
      });
      oMenu.render();

      Dom = YAHOO.util.Dom;
      Event = YAHOO.util.Event;

      omarSearchResults= new OmarSearchResults();
      omarSearchResults.setProperties(${raw( (params.encodeAsJSON()).toString())});
      omarSearchResults.setProperties(document);

      updatePageOffset();
    }

    function exportAs(format)
    {
      form = document.getElementById("exportForm");
      if ( format&&form )
      {
        var exportURL = "${raw(createLink(controller: 'videoDataSetExport', action: 'export', params: params))}";

        exportURL += "&format=" + format;

        //alert(exportURL);

        form.action = exportURL;
        form.submit();
      }
    }

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

              var url = "${raw(createLink(action: 'results'))}?" + omarSearchResults.toUrlParams();
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

  function updateCurrentTab(variable, tabIndex)
  {
      var link = "${raw(createLink(action: sessionAction, controller: sessionController))}";
      new Ajax.Request(link+"?"+variable+"="+tabIndex, {method: 'post'});
  }

  function handleClickTab(e) {
    updateCurrentTab("videoDataSetResultCurrentTab", tabView.get('activeIndex'));
  }
</g:javascript>
</body>
</html>
