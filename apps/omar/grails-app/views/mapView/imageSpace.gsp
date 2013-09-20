<%--
  Created by IntelliJ IDEA.
  User: sbortman
  Date: 2/7/12
  Time: 10:27 AM
  To change this template use File | Settings | File Templates.
--%>

<%@ page import="grails.converters.JSON; org.ossim.omar.ChipFormat" contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>OMAR <g:meta name="app.version"/>: Image Space - ${( rasterEntry?.filename )}</title>
    <meta content="imageSpacePageLayout" name="layout">
    <r:require modules="imageSpacePageLayout"/>
    <style type="text/css">
    #slider-brightness-bg, #slider-contrast-bg {
        background: url(${resource(plugin: 'yui', dir:'js/yui/slider/assets', file:'bg-fader.gif')}) 5px 0 no-repeat;
        width: 120px;
    }

    #slider-rotate-bg {
        background: url(${resource(plugin: 'yui', dir:'js/yui/slider/assets', file:'bg-fader.gif')}) 5px 0 no-repeat;
        width: 180px;
    }


    </style>
</head>

<body class=" yui-skin-sam">
<g:form name="wmsFormId" method="POST"></g:form>
<input type="hidden" name="request" value=""/>
<input type="hidden" name="layers" value=""/>
<input type="hidden" name="bbox" value=""/>
<input type="hidden" id="contrast" name="contrast" value="${params.contrast ?: 0}"/>
<input type="hidden" id="brightness" name="brightness" value="${params.brightness ?: 0}"/>

<content tag="top1">
    <g:render template="imageSpaceMenu" model="${[rasterEntry: rasterEntry]}"/>
    <div id='imageIdField'>No Image ID present</div>
</content>

<content tag="bottom1"></content>

<content tag="left1">
    <g:render template="imageSpaceAdjustments" model="${[rasterEntry: rasterEntry, params: params]}"/>
</content>

<%--
<content tag="right1"></content>
--%>

<content tag="top2">
    <div id="toolBar" class="olControlPanel"></div>

    <div id="AOI_TEMPLATES_DIV_ID" style='display: none'>
        <small>AOI:
        <g:select name="selectAoiTemplateId"
                  id="selectAoiTemplateId"
                  noSelection="['Custom': 'Custom']"
                  from="${ChipFormat.list()}"
                  onchange="selectAoiTemplateClicked(this.value)">
        </g:select>
        Output Scale:<g:select
                from="${['Screen']}"
                name="aoiScaleId"
                id="aoiScaleId"
                noSelection="['Image': 'Image']"
                onclick="setOutScale(this.value);genAOI( document.getElementById('selectAoiTemplateId').value)"/>
        </small>
    </div>

    <div id="busyCursor" style="position:absolute;top:-9999px;right:-9999px">
        <label id="busyCursorLabel" onclick="" style="font-weight:bold;"></label>
        <img id="busyCursorImage" src="../images/spinner.gif"/>
    </div>
</content>

<content tag="bottom2">

    <div id="mouseDisplayId" align="left"></div>

    <!--  <table><tr>
         <td width="33%"><div id="ddMousePosition">&nbsp;</div></td>
         <td width="33%"><div id="dmsMousePosition">&nbsp;</div></td>
         <td width="33%"><div id="mgrsMousePosition">&nbsp;</div></td>
       </tr></table>
     -->
</content>

<content tag="center2">

    <div id="eventDivId"></div>

    <div id="compassDivId" align="left">
        <img src="${resource( plugin: 'omar', dir: 'images', file: 'north_arrow.gif' )}">
    </div>

    <div id="hudDivId">
    </div>

    <div id="popDivId">
    </div>

    <div id="map"></div>
</content>

<form id="exportForm"
      action="${createLink( action: 'index', controller: 'templateExport', plugin: 'omar-image-magick' )}" method="post"
      target="_blank">
    <input id="countryCodeFormInput" name="countryCode" type="hidden"/>
    <input id="footerAcquisitionDateTextFormInput" name="footerAcquisitionDateText" type="hidden"/>
    <input id="footerLocationTextFormInput" name="footerLocationText" type="hidden"/>
    <input id="footerSecurityClassificationTextFormInput" name="footerSecurityClassificationText" type="hidden"/>
    <input id="headerDescriptionTextFormInput" name="headerDescriptionText" type="hidden"/>
    <input id="headerSecurityClassificationTextFormInput" name="headerSecurityClassificationText" type="hidden"/>
    <input id="headerTitleTextFormInput" name="headerTitleText" type="hidden"/>
    <input id="imageUrlFormInput" name="imageUrl" type="hidden"/>
    <input id="northAngleFormInput" name="northAngle" type="hidden"/>
</form>

<r:script>
 
var links = {
    getCapabilities: "${createLink( controller: 'ogc', action: 'wms', params: [request: 'GetCapabilities', layers: rasterEntry.indexId] )}",
     detailedMetadata: "${createLink( controller: 'rasterEntry', action: 'show', params: [id: rasterEntry?.id] )}",
     tileLog: '${createLink( controller: "GetTileLog", action: "list" )}',
     orthoView: "${createLink( controller: 'mapView', action: 'index' )}",
     getTile: "${createLink( controller: 'imageSpace', action: 'getTile' )}",
     getTileOpenLayers: "${createLink( controller: 'imageSpace', action: 'getTile' )}",
     baseUrlAbsolute: "${createLink( absolute: 'true', action: 'imageSpace', base: grailsApplication.config.omar.serverURL )}",
     imageToGround: "${createLink( controller: 'imageSpace', action: 'imageToGround' )}",
     getTileAbsolute: "${createLink( absolute: 'true', base: grailsApplication.config.omar.serverURL, controller: 'imageSpace', action: 'getTile' )}",
    openLayersImgPath: "${resource( plugin: 'openlayers', dir: 'js/img' )}/"
};

var imageIds = "${imageIds}";
var onDemand = ("${onDemand}" == "true");
var upIsUpRotation = parseFloat("${upIsUpRotation}");
var pqeDisplayUnit = "${pqeDisplayUnit ?: "DMS"}";

var rasterEntry = {
    id: "${rasterEntry?.id}",
    numberOfResLevels: parseInt( "${rasterEntry.numberOfResLevels}" ),
    indexId: "${rasterEntry.indexId}",
    width: parseFloat("${rasterEntry.width}"),
    height: parseFloat("${rasterEntry.height}"),
    gsdY: ${rasterEntry.gsdY},
    azimuthAngle: parseFloat("${rasterEntry.azimuthAngle}"),
    acquisitionDate: "${rasterEntry.acquisitionDate}",
    countryCode: "${rasterEntry.countryCode}",
    title: "${rasterEntry.title}"
};

var params = <%=params as JSON%>;

</r:script>
</body>
</html>
