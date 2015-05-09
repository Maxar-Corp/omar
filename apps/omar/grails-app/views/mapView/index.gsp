<%--
  User: sbortman
  Date: 2/7/12
  Time: 2:52 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page import="grails.converters.JSON" contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>OMAR <g:meta name="app.version"/>: Orthorectified View - ${( rasterEntries*.id )?.join( ', ' )}</title>
    <meta content="groundSpacePageLayout" name="layout">
    <asset:stylesheet src="groundSpacePage.css"/>
    <style type="text/css">
    #slider-brightness-bg, #slider-contrast-bg {
        width: 120px;
        background: url(${resource(plugin: 'yui', dir:'js/yui/slider/assets', file:'bg-fader.gif')}) 5px 0 no-repeat;
    }
    </style>
</head>

<body class=" yui-skin-sam">

<content tag="top1">
    <g:render template="groundSpaceMenu" model="${[rasterEntries: rasterEntries]}"/>
    <div style="float: none"><div style="float: left" id='imageIdField'>No Image ID present</div>
        <g:if test="${flash.message}">
            <div style="float: left" class="messageNoBorder">${flash.message}</div>
        </g:if>
    </div>

</content>

<content tag="bottom1"></content>

<content tag="left1">
    <g:render template="groundSpaceAdjustments" model="${[rasterEntries: rasterEntries, params: params]}"/>
</content>

<%--
<content tag="right1"></content>
--%>

<content tag="top2">
    <div id="toolBar" class="olControlPanel"></div>

    <div id="busyCursor" style="position:absolute;top:-9999px;right:-9999px">
        <label id="busyCursorLabel" onclick="" style="font-weight:bold;"></label>
        <img id="busyCursorImage" src="../images/spinner.gif"/>
    </div>
</content>

<content tag="bottom2">
    <table><tr>
        <td width="33%"><div id="ddMouseMapCtr">&nbsp;</div></td>
        <td width="33%"><div id="dmsMouseMapCtr">&nbsp;</div></td>
        <td width="33%"><div id="mgrsMouseMapCtr">&nbsp;</div></td>
    </tr></table>
</content>

<content tag="center2">
    <div id="mapContainerDivId" style="width:100%;height:100%;">
        <div id="map"></div>
    </div>
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

<asset:javascript src="groundSpacePage.js"/>

<g:javascript>

var azimuthAngle = parseFloat("${azimuthAngle}");
var fullResScale = parseFloat("${fullResScale}");
var imageIds = '${imageIds}';
var largestScale = parseFloat("${largestScale}");
var maxLat = parseFloat("${top}");
var maxLon = parseFloat("${right}");
var minLat = parseFloat("${bottom}");
var minLon = parseFloat("${left}");
var numberOfResLevels = parseInt("${numberOfResLevels}");
var onDemand = ${raw(onDemand == true)};
var smallestScale = parseFloat("${smallestScale}");
var upIsUpAngle  = parseFloat("${upIsUpAngle}");

var defaultOverlayVisiblity = ${grailsApplication.config.views.mapView.defaultOverlayVisiblity};
var params = <%=raw((params as JSON).toString())%>;

var links = {
    getCapabilities: "${raw(createLink( controller: 'ogc', action: 'wms', params: [request: 'GetCapabilities', layers: ( rasterEntries*.indexId ).join( ',' )] ))}",
    getMap: "${raw(createLink( controller: "ogc", action: "wms" ))}",
    detailedMetadata:  "${raw(createLink( controller: 'rasterEntry', action: 'show', params: [id: ( rasterEntries*.id ).join( ',' )] ))}",
    superOverlay: "${raw(createLink( action: "createKml", controller: "superOverlay" ))}",
    wmsLog: '${raw(createLink( controller: "WmsLog", action: "list" ))}',
    imageSpace: "${raw(createLink( controller: 'mapView', action: 'imageSpace' ))}",
    wcsGetCoverage: "${raw(createLink( action: "wcs", controller: "wcs" ))}",
    shareImage: "${raw(createLink( absolute: 'true', action: 'index', base: grailsApplication.config.omar.serverURL ))}",
    getMapAbsolute: "${raw(createLink( absolute: true, action: "wms", base: grailsApplication.config.omar.serverURL, controller: "ogc" ))}",
    openLayersImgPath: "${raw(resource( plugin: 'openlayers', dir: 'js/img' ))}/"
};

var rasterEntries = {
  ids: "${( rasterEntries.id ).join( ',' )}",
  indexIds:  "${( rasterEntries*.indexId ).join( ',' )}",
  acquisitionDates:  "${( rasterEntries.acquisitionDate ).join( ',' )}",
  countryCodes: "${( rasterEntries.countryCode ).join( ',' )}",
  titles:  "${( rasterEntries.title ).join( ',' )}"
};

var kmlOverlays = <%= raw((kmlOverlays as JSON).toString())%>;

//console.log(rasterEntries);

</g:javascript>
</body>
</html>
