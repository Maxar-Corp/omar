<p>Display Unit: <g:select name="displayUnit" from="${['DD', 'DMS', 'MGRS']}"
                           title="Select decimal degrees, degrees minutes seconds, or military grid reference system for coordinate readouts."
                           onChange="omar.setMapCenterTextField();omar.setBoundBoxTextField();"/></p>

<div class="niceBox">
  <div class="niceBoxHd">Geographic Criteria:</div>

  <div class="niceBoxBody">
    <g:checkBox id="spatialSearchFlag" value="true" checked="true"
                onclick="javascript:this.value = this.checked"/>
    <label>Include Criteria</label>

    <div id="tabview1" class="yui-navset">
      <ul class="yui-nav">
        <li><a href="#tab1" id="pointRadiusTab"><em>Point Radius</em></a></li>
        <li><a href="#tab2" id="boundBoxTab"><em>Bound Box</em></a></li>
      </ul>

      <div class="yui-content">
        <div id="tab1">
          <p><b>Map Center:</b></p>

          <p><g:textField name="point" onChange="omar.setMapCenter()"
                          title="Enter map center coordinates in decimal degrees, degrees minutes seconds, or military grid reference system."/></p>

          <p><b>Radius in Meters:</b></p>

          <p><g:textField name="radius" title="Enter aoi radius in meters."/></p>

          <p>&nbsp;</p>

          <p><span id="linkbutton1" class="yui-button yui-link-button" title="Click to set map center."><span
              class="first-child"><a href="javascript:omar.setMapCenter();">Set Map Center</a></span></span></p>
        </div>

        <div id="tab2">
          <p><b>Lower Left:</b></p>

          <p><g:textField name="lowerLeft"
                          title="Enter lower left bound box coordinates in decimal degrees, degrees minutes seconds, or military grid reference system."/></p>

          <p><b>Upper Right:</b></p>

          <p><g:textField name="upperRight"
                          title="Enter upper right bound box coordinates in decimal degrees, degrees minutes seconds, or military grid reference system."/></p>

          <p>&nbsp;</p>

          <p><span id="linkbutton2" class="yui-button yui-link-button" title="Click to clear bound box."><span
              class="first-child"><a href="javascript:omar.clearBoundBox();">Clear Bound Box</a></span></span></p>
        </div>
      </div>
    </div>
  </div>
</div>

<input type="hidden" id="baseQueryType" name="baseQueryType"/>

<input type="hidden" id="centerLat" name="centerLat"/>
<input type="hidden" id="centerLon" name="centerLon"/>
<input type="hidden" id="aoiRadius" name="aoiRadius"/>

<input type="hidden" id="aoiMinLat" name="aoiMinLat"/>
<input type="hidden" id="aoiMinLon" name="aoiMinLon"/>
<input type="hidden" id="aoiMaxLat" name="aoiMaxLat"/>
<input type="hidden" id="aoiMaxLon" name="aoiMaxLon"/>

<input type="hidden" id="viewMinLat" name="viewMinLat" value="${fieldValue( bean: queryParams, field: 'viewMinLat' )}"/>
<input type="hidden" id="viewMinLon" name="viewMinLon" value="${fieldValue( bean: queryParams, field: 'viewMinLon' )}"/>
<input type="hidden" id="viewMaxLat" name="viewMaxLat" value="${fieldValue( bean: queryParams, field: 'viewMaxLat' )}"/>
<input type="hidden" id="viewMaxLon" name="viewMaxLon" value="${fieldValue( bean: queryParams, field: 'viewMaxLon' )}"/>
