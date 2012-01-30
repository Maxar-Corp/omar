<%--
  Created by IntelliJ IDEA.
  User: dlucas
  Date: Nov 16, 2010
  Time: 8:09:29 PM
  To change this template use File | Settings | File Templates.
--%>
<html>
<head>

  <style>
  body {
    height: 100%;
    width: 100%;
    text-align: left;
    margin: 0;
    padding: 0;
    overflow-y: hidden;
  }

  #content {
    height: 100%;
    min-height: 100%;
    margin-bottom: -20px;
    text-align: left;
  }

  #header {
    position: absolute;
    top: 0;
    width: 100%;
  }

  #header {
    position: relative;
    top: 0;
    width: 100%;
  }

  #center {
    position: absolute;
    height: 100%;
    width: 100%;
    overflow-x: auto;
    overflow-y: auto;
  }

  #footer {
    position: relative;
    bottom: 0;
    height: 20px;
    width: 100%;
  }
  </style>
  <title><g:layoutTitle default="Grails"/></title>
  <g:layoutHead/>
  <r:require modules="resultsView"/>
  <r:layoutResources/>
</head>

<body class="yui-skin-sam" onresize="onBodyResize();${pageProperty(name: 'body.onresize')}"
      onload="onBodyResize();${pageProperty(name: 'body.onload')}">


<div id="header">
  <omar:securityClassificationBanner/>
</div>

<div id="content">

  <div id="top">
    <g:pageProperty name="page.top"/>
  </div>

  <div id="center">
    <g:pageProperty name="page.body"/>
  </div>


</div>

<div id="footer">
  <g:pageProperty name="page.footer"/>
  <omar:securityClassificationBanner/>
</div>


<g:layoutBody/>

<r:script>
   //YAHOO.util.Dom.setStyle(document.body, 'display', 'none');
    var Dom = YAHOO.util.Dom;
    var Event = YAHOO.util.Event;
  var omarSearchResults= new OmarSearchResults();

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
              //alert(url);
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
  var onBodyResize = function()
  {
    var Dom = YAHOO.util.Dom;
    var headerDiv     = Dom.get("header");
    var topDiv        = Dom.get("top");
    var centerDiv  = Dom.get("center");
    var footerDiv     = Dom.get("footer");
    var bottomValue  = footerDiv.offsetTop;
    // IE6 seems to do better to use the root content div and then adjust everyone from  that
    var topValue    = topDiv.offsetTop+topDiv.offsetHeight;

    centerHeight     = Math.abs(bottomValue-topValue);
    centerDiv.style.height = centerHeight + "px";
    centerDiv.style.width  = "100%";
  }
</r:script>
<r:layoutResources/>
</body>
</html>