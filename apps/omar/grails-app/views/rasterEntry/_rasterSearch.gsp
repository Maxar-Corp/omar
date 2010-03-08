<%@ page import="org.ossim.omar.RasterEntrySearchTag" %><g:form action="search" method="post">

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
          <input type="checkbox" id="radiusact" name="radiusact"
                  value="${fieldValue(bean: queryParams, field: 'radiusact')}" onclick="toggleRadiusSearch()"/>
          <label for="radiusact">Use Radius Search</label>
        </li>
        <li><br/></li>
        <li>
          <label for='aoiRadius'>Radius in Meters:</label><br/>
        </li>
        <li>
          <input type="text" id="aoiRadius" name="aoiRadius" value="${fieldValue(bean: queryParams, field: 'aoiRadius')}"/>
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


  <div class="niceBox">
    <div class="niceBoxHd">Geospatial Criteria:</div>
    <div class="niceBoxBody">
      <input type="hidden" id="viewMinLon" name="viewMinLon" value="${fieldValue(bean: queryParams, field: 'viewMinLon')}"/>
      <input type="hidden" id="viewMinLat" name="viewMinLat" value="${fieldValue(bean: queryParams, field: 'viewMinLat')}"/>
      <input type="hidden" id="viewMaxLon" name="viewMaxLon" value="${fieldValue(bean: queryParams, field: 'viewMaxLon')}"/>
      <input type="hidden" id="viewMaxLat" name="viewMaxLat" value="${fieldValue(bean: queryParams, field: 'viewMaxLat')}"/>
      <ol>
        <li>
          <input type="checkbox" id="bboxact" name="bboxact"
                  value="${fieldValue(bean: queryParams, field: 'bboxact')}" onclick="toggleBboxSearch()"/>
          <label for="radiusact">Use BBox Search</label>
        </li>
        <li><br/></li>
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
        <li><br/></li>
        <li>
          <input type="button" onclick="updateOmarFilters()" value="Update Footprints">
        </li>
      </ol>
    </div>
  </div>


  <div class="niceBox">
    <div class="niceBoxHd">Metadata Criteria:</div>
    <div class="niceBoxMetadataBody">
      <ol>
        <g:each in="${1..8}">
          <li>
            <g:select id="searchTag${it}.id" name='searchTag${it}.id' value="searchTag${it}.id"
                    noSelection="${['null':'Select One...']}"
                    from='${RasterEntrySearchTag.list()}'
                    optionKey="id" optionValue="description"></g:select>
          </li>
          <li>
            <%--
            <g:textField name="searchTagValue${it}" value="${fieldValue(bean: queryParams, field: "${searchTagValue"${it}"}")}"/>
            --%>
            <g:textField name="searchTagValue${it}" value="${queryParams.searchTagValue1}"/>

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
  <g:actionSubmit value="Search"/>&nbsp;<g:actionSubmit action="kmlnetworklink" value="KML"/>
</g:form>