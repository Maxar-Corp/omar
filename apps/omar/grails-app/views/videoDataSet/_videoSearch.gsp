<div class="niceBox">
  <div class="niceBoxHd">Map Mode:</div>
  <div class="niceBoxBody">
    <ol>
      <li>
        <input type="radio" name="type" value="none" id="noneToggle"
            onclick="polygonControl.deactivate()" checked="checked">
        <label for="noneToggle">Zoom/Pan</label>
      </li>
      <li>
        <input type="radio" name="type" value="polygon" id="polygonToggle"
            onclick="polygonControl.activate()">
        <label for="polygonToggle">Set AOI</label>
      </li>
      <%--
      <li><br/></li>
      <li>
        <label for="mapSize">Size:</label>
      </li>
       <li>
        <select id="mapSize" onchange="setMapSize()">
          <option>Small</option>
          <option>Medium</option>
          <option selected="true">Large</option>
          <option>Extra Large</option>
        </select>
      </li>
      --%>
    </ol>
  </div>
</div>

<div class="niceBox">
  <div class="niceBoxHd">Map Center:</div>
  <div class="niceBoxBody">
    <ol>
      <li>
        <label for='centerLon'>Lon:</label><br/>
      </li>
      <li>
        <input type="text" id="centerLon" name="center"/>
      </li>
      <li>
        <label for='centerLat'>Lat:</label>
      </li>
      <li>
        <input type="text" id="centerLat" name="center"/>
      </li>
      <li><br/></li>
      <li>
        <span class="formButton">
          <input type="button" onclick="goto()" value="Set Center">
        </span>
      </li>
    </ol>
  </div>
</div>

<g:form action="search" method="post">

  <div class="niceBox">
    <div class="niceBoxHd">Geospatial Criteria:</div>
    <div class="niceBoxBody">
      <input type="hidden" id="viewMinLon" name="viewMinLon" value="${fieldValue(bean: queryParams, field: 'viewMinLon')}"/>
      <input type="hidden" id="viewMinLat" name="viewMinLat" value="${fieldValue(bean: queryParams, field: 'viewMinLat')}"/>
      <input type="hidden" id="viewMaxLon" name="viewMaxLon" value="${fieldValue(bean: queryParams, field: 'viewMaxLon')}"/>
      <input type="hidden" id="viewMaxLat" name="viewMaxLat" value="${fieldValue(bean: queryParams, field: 'viewMaxLat')}"/>
      <ol>
        <li>
          <label for='aoiMinLon'>West Lon:</label>
        </li>
        <li>
          <input type="text" id="aoiMinLon" name="aoiMinLon" value="${fieldValue(bean: queryParams, field: 'aoiMinLon')}"/>
        </li>
        <li>
          <label for='aoiMaxLat'>North Lat:</label>
        </li>
        <li>
          <input type="text" id="aoiMaxLat" name="aoiMaxLat" value="${fieldValue(bean: queryParams, field: 'aoiMaxLat')}"/>
        </li>
        <li>
          <label for='aoiMaxLon'>East Lon:</label>
        </li>
        <li>
          <input type="text" id="aoiMaxLon" name="aoiMaxLon" value="${fieldValue(bean: queryParams, field: 'aoiMaxLon')}"/>
        </li>
        <li>
          <label for='aoiMinLat'>South Lat:</label>
        </li>
        <li>
          <input type="text" id="aoiMinLat" name="aoiMinLat" value="${fieldValue(bean: queryParams, field: 'aoiMinLat')}"/>
        </li>
        <li><br/></li>
        <li>
          <input type="button" onclick="clearAOI()" value="Clear AOI">
        </li>
      </ol>
    </div>
  </div>

  <div class="niceBox">
    <div class="niceBoxHd">Temporal Criteria:</div>
    <div class="niceBoxBody">
      <ol>
        <li>
          <label for='startDate'>Start Date:</label>
        </li>
        <li>
          <richui:dateChooser name="startDate" format="MM/dd/yyyy" value="${queryParams.startDate}"></richui:dateChooser>
        </li>
        <li>
          <label for='endDate'>End Date:</label>
        </li>
        <li>
          <richui:dateChooser name="endDate" format="MM/dd/yyyy" value="${queryParams.endDate}"></richui:dateChooser>
        </li>
      </ol>
    </div>
  </div>

  <%--
  <div class="niceBox">
    <div class="niceBoxHd">Metadata Criteria:</div>
    <div class="niceBoxBody">
      <ol>
        <li>
          <g:select id="searchTag.id" name='searchTag.id' value="${queryParams?.searchTag?.id}"
              noSelection="${['null':'Select One...']}"
              from='${SearchTag.list()}'
              optionKey="id" optionValue="description"></g:select>
        </li>
        <li>
          <input type="text" id="searchTagValue" name="searchTagValue" value="${fieldValue(bean: queryParams, field: 'searchTagValue')}"/>
        </li>
      </ol>
    </div>
  </div>
  --%>
  <br/><hr/><br/>
  <g:actionSubmit value="Search"/>
</g:form>
<script type="text/javascript">
  function setMapSize()
  {
    var sizeSelect = document.getElementById("mapSize")
    var sizeName = sizeSelect.options[sizeSelect.selectedIndex].text;

    var mapDiv = document.getElementById("map")

    if ( sizeName == "Small" )
    {
      mapDiv.style.width = "256px";
      mapDiv.style.height = "128px";
    }
    else if ( sizeName == "Medium" )
    {
      mapDiv.style.width = "512px";
      mapDiv.style.height = "256px";
    }
    else if ( sizeName == "Large" )
    {
      mapDiv.style.width = "1024px";
      mapDiv.style.height = "512px";
    }
    else if ( sizeName == "Extra Large" )
    {
      mapDiv.style.width = "2048px";
      mapDiv.style.height = "1024px";
    }

    map.updateSize();

  }
</script>