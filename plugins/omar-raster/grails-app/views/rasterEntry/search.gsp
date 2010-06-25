<%--
 Created by IntelliJ IDEA.
 User: sbortman
 Date: Sep 14, 2009
 Time: 9:24:47 AM
 To change this template use File | Settings | File Templates.
--%>

<%@ page import="org.ossim.omar.RasterEntryQuery; org.ossim.omar.RasterEntrySearchTag" contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <title>OMAR - Raster Search</title>
  <meta name="layout" content="main9"/>

   <meta name="apple-mobile-web-app-capable" content="yes" />
  <meta name="apple-mobile-web-app-status-bar-style" content="black" />
  <meta name="viewport" content="minimum-scale=1.0, width=device-width, maximum-scale=1.6, user-scalable=no">

  <style type="text/css">
    div.niceBox {
    margin-top: 3px;
    margin-bottom: 8px;
    background-color: #F0F8FF;
    border: 1px solid #000000;
    }

    div.niceBoxHd {
    font-size: 87%;
    font-weight: bold;
    padding: 2px;
    color: white;
    background-color: #003366;
    }

    div.niceBoxBody {
    font-size: 87%;
    padding: 3px;
    }

    div.niceBoxMetadataBody {
    font-size: 87%;
    padding: 3px;
    height: 80px;
    overflow-x: hidden;
    overflow-y: scroll;
    }
  </style>

  <style type="text/css">
  #map {
    border: 1px solid black;
    width: 100%;
    height: 100%;
  }

  #controls {
    padding-left: 2em;
    margin-left: 0;
    width: 12em;
  }

  #controls li {
    padding-top: 0.5em;
    list-style: none;
  }

  #panel {
    right: 0px;
    height: 30px;
    width: 200px;
  }

  #panel div {
    float: left;
    margin: 5px;
  }
  </style>

  <openlayers:loadMapToolBar/>
  <openlayers:loadTheme theme="default"/>
  <openlayers:loadJavascript/>
  <resource:include components="dateChooser"/>


  <g:javascript plugin="omar-core" src="mapwidget.js"/>
  <g:javascript plugin="omar-core" src="coordinateConversion.js"/>

  <g:javascript plugin="omar-core" src="touch.js"/>
   

  <g:javascript>
    var mapWidget = new MapWidget();

    function init()
    {
      var setupBaseLayers = function()
      {
        var baseLayer = null;

        <g:each var="foo" in="${baseWMS}">
          baseLayer = new OpenLayers.Layer.WMS("${foo.title}", "${foo.url}",
            {layers: "${foo.layers}", format: "${foo.format}"},
            {isBaseLayer: true, buffer: 0,transitionEffect: "resize"});

            mapWidget.setupBaseLayers(baseLayer);
        </g:each>
      };

    mapWidget.setupMapWidget();
    setupBaseLayers();
    mapWidget.setupDataLayer("${dataWMS.title}", "${dataWMS.url}", "${dataWMS.layers}", "${dataWMS.styles}", "${dataWMS.format}");
    mapWidget.changeMapSize();
    mapWidget.setupAoiLayer();
    mapWidget.setupToolBar();
    mapWidget.setupMapView("${queryParams?.viewMinLon ?: -180}", "${queryParams?.viewMinLat ?: -90}", "${queryParams?.viewMaxLon ?: 180}", "${queryParams?.viewMaxLat ?: 90}");
    mapWidget.setupQueryFields("${queryParams.searchMethod}");

    var minLon = ${queryParams?.aoiMinLon ?: 'null'};
    var minLat = ${queryParams?.aoiMinLat ?: 'null'};
    var maxLon = ${queryParams?.aoiMaxLon ?: 'null'};
    var maxLat = ${queryParams?.aoiMaxLat ?: 'null'};

    if ( minLon && minLat && maxLon && maxLat)
    {
      mapWidget.initAOI(minLon, minLat, maxLon, maxLat);
    }

    updateOmarFilters();
  }

  function updateOmarFilters()
  {
    var numberOfNames = parseInt("${queryParams?.searchTagNames.size()}");
    var numberOfValues = parseInt(${queryParams?.searchTagValues.size()});

    mapWidget.updateOmarFilters(
        $("startDate_day").value, $("startDate_month").value, $("startDate_year").value, $("startDate_hour").value, $("startDate_minute").value,
        $("endDate_day").value, $("endDate_month").value, $("endDate_year").value, $("endDate_hour").value, $("endDate_minute").value,
        numberOfNames, numberOfValues
        );
  }
  </g:javascript>

</head>

<body onload="init();" onresize="mapWidget.changeMapSize();">

<content tag="top">

     <div id="rastermenubar" class="yuimenubar yuimenubarnav">

         <div class="bd">
             <ul class="first-of-type">

                 <li class="yuimenubaritem first-of-type">
                     <a class="yuimenubaritemlabel" href="#omar">OMAR</a>

                 </li>
                 <li class="yuimenubaritem">
                     <a class="yuimenubaritemlabel" href="#kml">KML</a>

                 </li>
                 <li class="yuimenubaritem">
                     <a class="yuimenubaritemlabel" href="#footprints">Footprints</a>

                 </li>

                 <li class="yuimenubaritem">
                     <a class="yuimenubaritemlabel" href="#search">Search</a>
                 </li>


               <li class="yuimenubaritem">
                   <a class="yuimenubaritemlabel" href="#mensuration">Mensuration</a>
               </li>


             </ul>
         </div>
     </div>


        <div id="toolBar" class="olControlPanel"></div>
                       
                <input type="hidden" id="pathUnits" name="pathUnits" value="1" />
            

  <div id="pathMeasurementOutput"></div>
             <div id="areaMeasurementOutput"></div>


</content>

<content tag="left">
  <g:form name="searchForm">

    <div id="demo" class="yui-navset">
      <ul class="yui-nav">
        <li class="selected"><a href="#dd"><em>DD</em></a></li>
        <li><a href="#dms"><em>DMS</em></a></li>
        <li><a href="#mgrs"><em>MGRS</em></a></li>
      </ul>

      <div class="yui-content">
        <div id="dd">
          <div class="niceBox">
          <div class="niceBoxHd">Map Center:</div>
          <div class="niceBoxBody">
            <ol>
              <li>
                <g:checkBox name="searchMethod" id="radiusSearchButton" value="${RasterEntryQuery.RADIUS_SEARCH}" checked="${queryParams?.searchMethod == RasterEntryQuery.RADIUS_SEARCH}" onclick="mapWidget.togglePointRadiusCheckBox()"/>
                <label>Use Radius Search</label>
              </li>
              <li><br/></li>
              <li>
                <label for='centerLat'>Latitude:</label>
              </li>
              <li>
                <g:textField name="centerLat" value="${queryParams?.centerLat}"/>
              </li>
              <li>
                <label for='centerLon'>Longitude:</label>
              </li>
              <li>
                <g:textField name="centerLon" value="${queryParams?.centerLon}"/>
              </li>
              <li><br/></li>
              <li>
                <label for='aoiRadius'>Radius in Meters:</label>
              </li>
              <li>
                <g:textField name="aoiRadius" value="${fieldValue(bean: queryParams, field: 'aoiRadius')}" onChange="updateOmarFilters()"/>
              </li>
              <li><br/></li>
              <li>
                <span class="formButton">
                  <input type="button" onclick="mapWidget.setCenterDd()" value="Set Center">
                </span>
              </li>
            </ol>
          </div>
          </div>

          <div class="niceBox">
          <div class="niceBoxHd">Geospatial Criteria:</div>
          <div class="niceBoxBody">

            <input type="hidden" id="viewMinLon" name="viewMinLon" value="${fieldValue(bean: queryParams, field: 'viewMinLon')}"/>
            <input type="hidden" id="viewMinLat" name="viewMinLat" value="${fieldValue(bean: queryParams, field: 'viewMinLat')}"/>
            <input type="hidden" id="viewMaxLon" name="viewMaxLon" value="${fieldValue(bean: queryParams, field: 'viewMaxLon')}"/>
            <input type="hidden" id="viewMaxLat" name="viewMaxLat" value="${fieldValue(bean: queryParams, field: 'viewMaxLat')}"/>

            <ol>
              <li>
                <g:checkBox name="searchMethod" id="bboxSearchButton" value="${RasterEntryQuery.BBOX_SEARCH}" checked="${queryParams?.searchMethod == RasterEntryQuery.BBOX_SEARCH}" onclick="mapWidget.toggleBboxCheckBox()"/>
                <label>Use BBox Search</label>
            </li>
            <li><br/></li>
            <li>
              <label for='aoiMaxLat'>North Latitude:</label>
            </li>
            <li>
              <input type="text" id="aoiMaxLat" name="aoiMaxLat" value="${fieldValue(bean: queryParams, field: 'aoiMaxLat')}"/>
            </li>
            <li>
              <label for='aoiMinLon'>West Longitude:</label>
            </li>
            <li>
              <input type="text" id="aoiMinLon" name="aoiMinLon" value="${fieldValue(bean: queryParams, field: 'aoiMinLon')}"/>
            </li>
            <li>
              <label for='aoiMinLat'>South Latitude:</label>
            </li>
            <li>
              <input type="text" id="aoiMinLat" name="aoiMinLat" value="${fieldValue(bean: queryParams, field: 'aoiMinLat')}"/>
            </li>
            <li>
              <label for='aoiMaxLon'>East Longitude:</label>
            </li>
            <li>
              <input type="text" id="aoiMaxLon" name="aoiMaxLon" value="${fieldValue(bean: queryParams, field: 'aoiMaxLon')}"/>
            </li>
            <li><br/></li>
            <li>
              <span class="formButton">
                <input type="button" onclick="mapWidget.clearAOI()" value="Clear AOI">
              </span>
            </li>
          </ol>
          </div>
          </div>
        </div>

        <div id="dms">
          <div class="niceBox">
          <div class="niceBoxHd">Map Center:</div>
          <div class="niceBoxBody">
            <ol>
              <li>
                <g:checkBox name="searchMethod" id="radiusSearchButton2" onclick="mapWidget.togglePointRadiusCheckBox()"/>
                <label>Use Radius Search</label>
              </li>
              <li><br/></li>
              <li>
                <label for='centerLat'>Latitude:</label>
              </li>
              <li>
                <g:textField name="centerLatDms" value=""/>
              </li>
              <li>
                <label for='centerLon'>Longitude:</label>
              </li>
              <li>
                <g:textField name="centerLonDms" value=""/>
              </li>
              <li><br/></li>
               <li>
                <label for='aoiRadius'>Radius in Meters:</label>
              </li>
              <li>
                <g:textField name="aoiRadius2"/>
              </li>
              <li><br/></li>
              <li>
                <span class="formButton">
                  <input type="button" onclick="mapWidget.setCenterDms()" value="Set Center">
                </span>
              </li>
            </ol>
          </div>
          </div>

          <div class="niceBox">
          <div class="niceBoxHd">Geospatial Criteria:</div>
          <div class="niceBoxBody">
            <ol>
              <li>
                <g:checkBox name="searchMethod" id="bboxSearchButton2" onclick="mapWidget.toggleBboxCheckBox()"/>
                <label>Use BBox Search</label>
              </li>
              <li><br/></li>
              <li>
                <label for='aoiMaxLat'>North Latitude:</label>
              </li>
              <li>
                <input type="text" id="aoiMaxLatDms" name="aoiMaxLatDms"/>
              </li>
              <li>
                <label for='aoiMinLon'>West Longitude:</label>
              </li>
              <li>
                <input type="text" id="aoiMinLonDms" name="aoiMinLonDms"/>
              </li>
              <li>
                <label for='aoiMinLat'>South Latitude:</label>
              </li>
              <li>
                <input type="text" id="aoiMinLatDms" name="aoiMinLatDms"/>
              </li>
              <li>
                <label for='aoiMaxLon'>East Longitude:</label>
              </li>
              <li>
                <input type="text" id="aoiMaxLonDms" name="aoiMaxLonDms"/>
              </li>
              <li><br/></li>
              <li>
                <span class="formButton">
                  <input type="button" onclick="mapWidget.clearAOI()" value="Clear AOI">
                </span>
              </li>
            </ol>
          </div>
          </div>
        </div>

        <div id="mgrs">
          <div class="niceBox">
          <div class="niceBoxHd">Map Center:</div>
          <div class="niceBoxBody">
            <ol>
              <li>
                <g:checkBox name="searchMethod" id="radiusSearchButton3" onclick="mapWidget.togglePointRadiusCheckBox()"/>
                <label>Use Radius Search</label>
              </li>
              <li><br/></li>
              <li>
                <label for='centerLat'>MGRS:</label>
              </li>
              <li>
                <g:textField name="centerMgrs" value=""/>
              </li>
              <li><br/></li>
              <li>
                <label for='aoiRadius'>Radius in Meters:</label>
              </li>
              <li>
                <g:textField name="aoiRadius3"/>
              </li>
              <li><br/></li>
              <li>
                <span class="formButton">
                  <input type="button" onclick="mapWidget.setCenterMgrs()" value="Set Center">
                </span>
              </li>
            </ol>
          </div>
          </div>

          <div class="niceBox">
          <div class="niceBoxHd">Geospatial Criteria:</div>
          <div class="niceBoxBody">
            <ol>
              <li>
                <g:checkBox name="searchMethod" id="bboxSearchButton3" onclick="mapWidget.toggleBboxCheckBox()"/>
                <label>Use BBox Search</label>
              </li>
              <li><br/></li>
              <li>
                <label for='aoiNeMgrs'>MGRS NE:</label>
              </li>
              <li>
                <input type="text" id="aoiNeMgrs" name="aoiNeMgrs" value=""/>
              </li>
              <li>
                <label for='aoiSwMgrs'>MGRS SW:</label>
              </li>
              <li>
                <input type="text" id="aoiSwMgrs" name="aoiSwMgrs" value=""/>
              </li>
              <li><br/></li>
              <li>
                <span class="formButton">
                  <input type="button" onclick="mapWidget.clearAOI()" value="Clear AOI">
                </span>
              </li>
            </ol>
          </div>
          </div>
        </div>
      </div>

    </div>

    <input type="hidden" id="units" name="units"/>

    <script>
      (function() {
        var tabView = new YAHOO.widget.TabView('demo');

        var tab0 = tabView.getTab(0);
        var tab1 = tabView.getTab(1);
        var tab2 = tabView.getTab(2);

        function handleClickDd(e) {
          $("units").value = "DD";
        }

        function handleClickDms(e) {
          $("units").value = "DMS";
        }

        function handleClickMgrs(e) {
          $("units").value = "MGRS";
        }

        tab0.addListener('click', handleClickDd);
        tab1.addListener('click', handleClickDms);
        tab2.addListener('click', handleClickMgrs);
      })();
    </script>

    <style type="text/css">
      div.datechooser { display:none; position:absolute; left:10px; top:10px; z-index:2}
      div.datechooser table.yui-calendar { width: 150px;}
    </style>

    <div class="niceBox">
      <div class="niceBoxHd">Temporal Criteria:</div>
      <div class="niceBoxBody">
        <ol>
          <li>
            <label for='startDate'>Start Date:</label>
          </li>
          <li>
            <richui:dateChooser name="startDate" format="MM/dd/yyyy" timezone="${TimeZone.getTimeZone('UTC')}" style="width:75px" time="true" hourStyle="width:25px" minuteStyle="width:25px" value="${queryParams.startDate}" onChange="updateOmarFilters()"/>
            <g:hiddenField name="startDate_timezone" value="UTC"/>
          </li>
          <li>
            <label for='endDate'>End Date:</label>
          </li>
          <li>
            <richui:dateChooser name="endDate" format="MM/dd/yyyy" timezone="${TimeZone.getTimeZone('UTC')}" style="width:75px" time="true" hourStyle="width:25px" minuteStyle="width:25px" value="${queryParams.endDate}" onChange="updateOmarFilters()"/>
            <g:hiddenField name="endDate_timezone" value="UTC"/>
          </li>
        </ol>
      </div>
    </div>

    <div class="niceBox">
      <div class="niceBoxHd">Metadata Criteria:</div>
      <div class="niceBoxMetadataBody">
        <ol>
          <g:each in="${queryParams?.searchTagValues}" var="searchTagValue" status="i">
            <g:select
                    noSelection="${['null':'Select One...']}"
                    name="searchTagNames[${i}]"
                    value="${queryParams?.searchTagNames[i]}"
                    from="${RasterEntrySearchTag.list()}"
                    optionKey="name" optionValue="description"/>
            </li>
            <li>
              <g:textField name="searchTagValues[${i}]" value="${searchTagValue}" onChange="updateOmarFilters()"/>
            </li>
          </g:each>
        </ol>
      </div>
    </div>

    <div class="niceBox">
      <div class="niceBoxHd">Options:</div>
      <div class="niceBoxBody">
        <ol>
          <li><label for="max">Max Results:</label></li>
          <li><input type="text" id="max" name="max" value="${params?.max}"></li>
        </ol>
      </div>
    </div>
    <br/>
  </g:form>
</content>

<content tag="center">
  <div class="body">
    <h1 id="mapTitle"></h1>
    <g:if test="${flash.message}">
      <div class="message">${flash.message}</div>
    </g:if>
    <div id="map"></div>
  </div>
</content>

<content tag="right">



  <div id="layerswitcher" class="olControlLayerSwitcher"></div>
    




</content>

<content tag="bottom">
  <div id="demo2" class="yui-navset">
    <ul class="yui-nav">
      <li class="selected"><a href="#tab1"><em>Mouse Hover</em></a></li>
      <li><a href="#tab2"><em>Mouse Click</em></a></li>
    </ul>

    <div class="yui-content">

      <div id="tab1">
        <font size=-2>
          <table borderColor=transparent>
            <tr>
              <td width=200><div id="mouseHoverDdOutput"></div></td>
              <td width=200><div id="mouseHoverDmsOutput"></div></td>
              <td width=200><div id="mouseHoverMgrsOutput"></div></td>
            </tr>
          </table>
        </font>
      </div>

      <div id="tab2">
        <font size=-2>
          <table borderColor=transparent>
            <tr>
              <td width=200><div id="mouseClickDdOutput">Select the pan button and click on the map.</div></td>
              <td width=200><div id="mouseClickDmsOutput"></div></td>
              <td width=200><div id="mouseClickMgrsOutput"></div></td>

            </tr>
          </table>
        </font>
      </div>

       

  <script>
    (function() {
      var tabView = new YAHOO.widget.TabView('demo2');
    })();

    /*
                  Initialize and render the MenuBar when its elements are ready
                  to be scripted.
             */

 function rasterMenuBar() {
                 /*
                     This code is from this example:
                     http://developer.yahoo.com/yui/examples/menu/topnavfromjs.html
                 */

                 /*
                      Instantiate a MenuBar:  The first argument passed to the
                      constructor is the id of the element in the page
                      representing the MenuBar; the second is an object literal
                      of configuration properties.
                 */

                 var oMenuBar = new YAHOO.widget.MenuBar("rastermenubar", {
                                                             zindex: 1,
                                                             autosubmenudisplay: true,
                                                             hidedelay: 750,
                                                             lazyload: false });



                 /*
                      Define an array of object literals, each containing
                      the data necessary to create a submenu.
                 */

                 var aSubmenuData = [

                     {
                         id: "omar",
                         itemdata: [
                             { text: "Home", url: "${createLink(controller: "home")}" },
                             { text: "About", url: "${createLink(plugin: "omar-security", controller: "login", action: "about")}" },
                             { text: "Logout", url: "${createLink(controller: "logout")}" }

                         ]
                     },

                     {
                         id: "kml",
                         itemdata: [
                             { text: "Generate KML", url: "javascript:mapWidget.generateKML();" }
                         ]
                     },

                     {
                         id: "footprints",
                         itemdata: [
                             { text: "Update Footprints", url: "javascript:updateOmarFilters();" }
                         ]
                     },

                     {
                         id: "search",
                         itemdata: [
                             { text: "Search Rasters", url: "javascript:mapWidget.search();" }
                         ]
                     },

                     {
                         id: "mensuration",
                         itemdata: [
                           {
                             text: "Path Unit Mode",
                             submenu: {
                               id: "pathUnitMode",
                               itemdata: [
                                 { text: "kilometer", url: "javascript:mapWidget.setPathUnits('kilometer');" },
                                  { text: "meter", url: "javascript:mapWidget.setPathUnits('meter');" },
                                { text: "centimeter", url: "javascript:mapWidget.setPathUnits('centimeter');" },
                                { text: "feet", url: "javascript:mapWidget.setPathUnits('feet');" },
                                { text: "inch", url: "javascript:mapWidget.setPathUnits('inch');" },
                                { text: "league", url: "javascript:mapWidget.setPathUnits('league');" },
                                { text: "nautical league", url: "javascript:mapWidget.setPathUnits('nauticalleague');" },
                                { text: "microinch", url: "javascript:mapWidget.setPathUnits('microinch');" },
                                { text: "mile", url: "javascript:mapWidget.setPathUnits('mile');" },
                                { text: "millimeter", url: "javascript:mapWidget.setPathUnits('millimeter');" },
                                { text: "yard", url: "javascript:mapWidget.setPathUnits('yard');" }
                               ]
                            }

                        },
                        { text: "Clear Path Measurment", url: "javascript:mapWidget.clearPathMeasurement();" },
                                 { text: "Clear Area Measurment", url: "javascript:mapWidget.clearAreaMeasurement();" }

                    ]



            }


                 ];


 

                 /*
                      Subscribe to the "beforerender" event, adding a submenu
                      to each of the items in the MenuBar instance.
                 */

                 oMenuBar.subscribe("beforeRender", function () {

                     if (this.getRoot() == this) {

                         this.getItem(0).cfg.setProperty("submenu", aSubmenuData[0]);
                         this.getItem(1).cfg.setProperty("submenu", aSubmenuData[1]);
                         this.getItem(2).cfg.setProperty("submenu", aSubmenuData[2]);
                         this.getItem(3).cfg.setProperty("submenu", aSubmenuData[3]);
                         this.getItem(4).cfg.setProperty("submenu", aSubmenuData[4]);
                     }

                 });

                 /*
                      Call the "render" method with no arguments since the
                      markup for this MenuBar instance is already exists in
                      the page.
                 */
                 oMenuBar.render();

                 }
      </script>

</content>

</body>
</html>