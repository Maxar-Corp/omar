<g:form name="wcsForm" method="POST"/>
<g:form name="wmsFormId" method="POST"/>

<div id="rasterMenu" class="yuimenubar yuimenubarnav">
  <div class="bd">
    <ul class="first-of-type">

      <li class="yuimenubaritem first-of-type">
        <a class="yuimenubaritemlabel" id="homeMenu" href="${createLink( controller: 'home', action: 'index' )}"
           title="OMAR™ Home">&nbsp;&nbsp;&nbsp;&nbsp;OMAR™ Home</a>
      </li>

      <li class="yuimenubaritem first-of-type"><a class="yuimenubaritemlabel" href="#exportMenu">Export</a>

        <div id="exportMenu" class="yuimenu">
          <div class="bd"> 
            <ul>
              <li class="yuimenuitem">
                <a class="yuimenuitemlabel"
                   href="javascript:getProjectedImage({'format':'image/jpeg', 'crs':'EPSG:4326', 'coverage':'${( rasterEntries*.indexId ).join( ',' )}'})"
                   title="Export JPEG">JPEG</a>
              </li>
              <li class="yuimenuitem">
                <a class="yuimenuitemlabel"
                   href="javascript:getProjectedImage({'format':'image/png', 'crs':'EPSG:4326', 'coverage':'${( rasterEntries*.indexId ).join( ',' )}'})"
                   title="Export PNG">PNG</a>
              </li>
              <li class="yuimenuitem">
                <a class="yuimenuitemlabel"
                   href="javascript:getProjectedImage({'format':'png_uint8', 'crs':'EPSG:4326', 'coverage':'${( rasterEntries*.indexId ).join( ',' )}'})"
                   title="Export PNG">PNG 8-Bit</a>
              </li>
              <li class="yuimenuitem">
                <a class="yuimenuitemlabel"
                   href="javascript:getProjectedImage({'format':'geotiff', 'crs':'EPSG:4326', 'coverage':'${( rasterEntries*.indexId ).join( ',' )}'})"
                   title="Export GeoTIFF">GeoTIFF</a>
              </li>
              <li class="yuimenuitem">
                <a class="yuimenuitemlabel"
                   href="javascript:getProjectedImage({'format':'geotiff_uint8', 'crs':'EPSG:4326', 'coverage':'${( rasterEntries*.indexId ).join( ',' )}'})"
                   title="Export GeoTIFF 8-Bit">GeoTIFF 8-Bit</a>
              </li>
              <li class="yuimenuitem">
                <a class="yuimenuitemlabel"
                   href="javascript:getProjectedImage({'format':'geojp2', 'crs':'EPSG:4326', 'coverage':'${( rasterEntries*.indexId ).join( ',' )}'})"
                   title="Export Geo JPEG 2000">Geo JPEG 2000</a>
              </li>
              <li class="yuimenuitem">
                <a class="yuimenuitemlabel"
                   href="javascript:getProjectedImage({'format':'geojp2_uint8', 'crs':'EPSG:4326', 'coverage':'${( rasterEntries*.indexId ).join( ',' )}'})"
                   title="Export Geo JPEG 2000 8-Bit">Geo JPEG 2000 8-Bit</a>
              </li>
            </ul>
	    <ul>
	      <li class="yuimenuitem">
		<a class="yuimenuitemlabel" href="javascript:exportTemplate()" title="Template">Template</a>
	      </li>
	    </ul>
            <ul>
              <li class="yuimenuitem">
                <a class="yuimenuitemlabel"
                   href="javascript:getCapabilities()"
                   title="Show OGC WMS Capabilities">OGC WMS Capabilities</a>
              </li>
                <%--
                <li class="yuimenuitem">
                    <a class="yuimenuitemlabel"
                       href="${createLink( controller: 'ogc', action: 'wms', params: [request:'GetCapabilities', layers: ( rasterEntries*.indexId ).join( ',' )] )}"
                    title="Show OGC WMS Capabilities">OGC WMS Capabilities</a>
                </li>
                --%>
                <li class="yuimenuitem">
                <a class="yuimenuitemlabel" href="javascript:getKML('${( rasterEntries*.indexId ).join( ',' )}')"
                   title="Export KML">KML</a>
              </li>
              <li class="yuimenuitem">
                <a class="yuimenuitemlabel" href="javascript:getKmlSuperOverlay()"
                   title="Export Image as Super Overlay">KML Super Overlay</a>
              </li>
            </ul>
            <ul>
              <li class="yuimenuitem">
                <a class="yuimenuitemlabel"
                   href="javascript:getLocalKmz({'format':'image/png', 'transparent':'false','layers':'${( rasterEntries*.indexId ).join( ',' )}'})"
                   title="Export to a local KMZ with PNG chip">KMZ PNG</a>
              </li>
              <li class="yuimenuitem">
                <a class="yuimenuitemlabel"
                   href="javascript:getLocalKmz({'format':'image/png', 'transparent':'true','layers':'${( rasterEntries*.indexId ).join( ',' )}'})"
                   title="Export to a local KMZ with PNG chip and transparent">KMZ PNG Transparent</a>
              </li>
              <li class="yuimenuitem">
                <a class="yuimenuitemlabel"
                   href="javascript:getLocalKmz({'format':'image/jpeg', 'transparent':'false','layers':'${( rasterEntries*.indexId ).join( ',' )}'})"
                   title="Export to a local KMZ with JPEG chip">KMZ JPEG</a>
              </li>
            </ul>
          </div>
        </div>
      </li>

      <li class="yuimenubaritem first-of-type"><a class="yuimenubaritemlabel" href="#viewMenu">View</a>

        <div id="viewMenu" class="yuimenu">
          <div class="bd">
            <ul>
                <li class="yuimenuitem">
                    <a class="yuimenuitemlabel" href="javascript:rotateNorthUp();"
                       title="Image Space (North)">Image Space (North)</a>
                </li>
                <li class="yuimenuitem">
                    <a class="yuimenuitemlabel" href="javascript:rotateUpIsUp();"
                       title="Image Space (Up Is Up)">Image Space (Up Is Up)</a>
                </li>
                <li class="yuimenuitem">
                <a class="yuimenuitemlabel"
                   href="${createLink( controller: 'mapView', action: 'multiLayer', params: [layers: ( rasterEntries*.indexId ).join( ',' )] )}"
                   title="Multi Layer Ground Space Viewer">Orthorectified Multi Layer</a>
              </li>
	      <li class="yuimenuitem">
		<a class="yuimenuitemlabel" href="javascript:getDetailedMetadata()"
		   title="Detailed Metadata">Detailed Metadata</a>	 
	      </li>
            </ul>
            <ul>
                    <li class="yuimenuitem">
                        <a class="yuimenuitemlabel"
                           href="javascript:getWmsLog()"
                        title="List WMS Logs">WMS Logs</a>
                    </li>
                <li class="yuimenuitem">
                <a class="yuimenuitemlabel"
                   href="${createLink( action: "index", params: [layers: ( rasterEntries*.indexId ).join( ',' )] )}"
                   title="Reset the view">Reset</a>
              </li>
            </ul>
          </div>
        </div>
      </li>

      <li class="yuiMenubaritem first-of-type">
	<a class="yuimenubaritemlabel" href="javascript:shareImage();">Share</a>
      </li>
    </ul>
  </div>
</div>
