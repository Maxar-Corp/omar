<%--
  Created by IntelliJ IDEA.
  User: sbortman
  Date: 2/3/12
  Time: 3:45 PM
  To change this template use File | Settings | File Templates.
--%>

<div id="rasterMenu" class="yuimenubar yuimenubarnav">
  <div class="bd">
    <ul class="first-of-type">
      <li class="yuimenubaritem first-of-type">
        <a class="yuimenubaritemlabel" id="homeMenu" href="${createLink(controller: 'home', action: 'index')}"
           title="OMAR™ Home">&nbsp;&nbsp;&nbsp;&nbsp;OMAR™ Home</a>
      </li>

      <li class="yuimenubaritem first-of-type">
        <a class="yuimenubaritemlabel" href="#exportMenu">Export</a>

        <div id="exportMenu" class="yuimenu">
          <div class="bd">
            <ul>

                <li class="yuimenuitem">
                    <a class="yuimenuitemlabel"
                       href="javascript:getCapabilities()"
                       title="Show OGC WMS Capabilities">OGC WMS Capabilities</a>
                </li>
                <li class="yuimenuitem">
                <a class="yuimenuitemlabel"
                   href="${createLink(controller: "ogc", action: "wms", params: [request: "GetKML", layers: (rasterEntries*.id).join(',')])}"
                   title="Export KML">KML</a>
              </li>
            </ul>
            <ul>
              <li class="yuimenuitem">
                <a class="yuimenuitemlabel"
                   href="javascript:getProjectedImage({'format':'image/jpeg', 'crs':'EPSG:4326', 'coverage':'${(rasterEntries*.indexId).join(',')}'})"
                   title="Export Jpeg">Jpeg</a>
              </li>
              <li class="yuimenuitem">
                <a class="yuimenuitemlabel"
                   href="javascript:getProjectedImage({'format':'geotiff', 'crs':'EPSG:4326', 'coverage':'${(rasterEntries*.indexId).join(',')}'})"
                   title="Export Geotiff">Geotiff</a>
              </li>
              <li class="yuimenuitem">
                <a class="yuimenuitemlabel"
                   href="javascript:getProjectedImage({'format':'geotiff_uint8', 'crs':'EPSG:4326', 'coverage':'${(rasterEntries*.indexId).join(',')}'})"
                   title="Export Geotiff 8-Bit">Geotiff 8-Bit</a>
              </li>
              <li class="yuimenuitem">
                <a class="yuimenuitemlabel"
                   href="javascript:getProjectedImage({'format':'geojp2', 'crs':'EPSG:4326', 'coverage':'${(rasterEntries*.indexId).join(',')}'})"
                   title="Export Geo Jpeg 2000">Geo Jpeg 2000</a>
              </li>
              <li class="yuimenuitem">
                <a class="yuimenuitemlabel"
                   href="javascript:getProjectedImage({'format':'geojp2_uint8', 'crs':'EPSG:4326', 'coverage':'${(rasterEntries*.indexId).join(',')}'})"
                   title="Export Geo Jpeg 2000 8-Bit">Geo Jpeg 2000 8-Bit</a>
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
                <a class="yuimenuitemlabel"
                   href="${createLink(controller: 'mapView', action: 'index', params: [layers: (rasterEntries*.indexId).join(',')])}"
                   title="Ortho Viewer">Orthorectified</a>
              </li>
              <g:if test="${rasterEntries?.size() == 1}">
                <li class="yuimenuitem">
                  <a class="yuimenuitemlabel"
                     href="${createLink(controller: 'mapView', action: 'imageSpace', params: [layers: (rasterEntries*.indexId).join(',')])}"
                     title="Image Space Viewer (Rotate)">Image Space</a>
                </li>
              </g:if>
            </ul>
          </div>
        </div>
      </li>
    </ul>
  </div>
</div>
