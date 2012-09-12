<%--
  Created by IntelliJ IDEA.
  User: sbortman
  Date: 2/7/12
  Time: 9:08 AM
  To change this template use File | Settings | File Templates.
--%>
<g:form name="wmsFormId" method="POST"/>

<div id="rasterMenu" class="yuimenubar yuimenubarnav">
  <div class="bd">
    <ul class="first-of-type">
      <li class="yuimenubaritem first-of-type">
        <a class="yuimenubaritemlabel" id="homeMenu"
           href="${createLink(controller: 'home', action: 'index')}"
           title="OMAR™ Home">&nbsp;&nbsp;&nbsp;&nbsp;OMAR™ Home</a>
      </li>
       <li class="yuimenubaritem first-of-type">
        <a class="yuimenubaritemlabel" href="#exportMenu">Export</a>

        <div id="exportMenu" class="yuimenu">
          <div class="bd">
              <ul>
              <li class="yuimenuitem">
                <a class="yuimenuitemlabel" href="javascript: chipImage('jpeg')"
                   title="Export JPEG">JPEG</a>
              </li>
              <li class="yuimenuitem">
                <a class="yuimenuitemlabel" href="javascript: chipImage('png')"
                   title="Export PNG">PNG</a>
              </li>
              <li class="yuimenuitem">
                <a class="yuimenuitemlabel" href="javascript: chipImage('gif')"
                   title="Export GIF">GIF</a>
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
              </ul>
          </div>
        </div>
      </li>
        <li class="yuimenubaritem first-of-type"><a class="yuimenubaritemlabel" href="#viewMenu">View</a>

            <div id="viewMenu" class="yuimenu">
                <div class="bd">
                    <ul>
                        <li class="yuimenuitem">
                            <a class="yuimenuitemlabel" href="javascript:changeToSingleLayer();"
                               title="Ortho Viewer">Orthorectified</a>
                        </li>
                        <li class="yuimenuitem">
                            <a class="yuimenuitemlabel"
                               href="${createLink(controller: "mapView", action: "multiLayer", params: [layers: rasterEntry?.indexId])}"
                            title="Multi Layer Ortho Viewer">Orthorectified Multi Layer</a>
                        </li>
                        <li class="yuimenuitem">
                            <a class="yuimenuitemlabel" href="javascript:getDetailedMetadata()"
                               title="Detailed Metadata">Detailed Metadata</a>
                        </li>
                    </ul>
                    <ul>
                        <li class="yuimenuitem">
                            <a class="yuimenuitemlabel"
                               href="javascript:getTileLog()"
                            title="List Image space Chipping Logs">Chipping Logs</a>
                        </li>
                        <li class="yuimenuitem">
                            <a class="yuimenuitemlabel"
                               href="${createLink(action: "imageSpace", params: [layers: rasterEntry?.indexId])}"
                            title="Reset Image space">Reset</a>
                        </li>

                    </ul>
                </div>
            </div>
        </li>

        <li class="yuimenubaritem first_of_type">
 	<a class="yuimenubaritemlabel" href="javascript:shareImage()">Share</a>
      </li>
    </ul>
  </div>
</div>

