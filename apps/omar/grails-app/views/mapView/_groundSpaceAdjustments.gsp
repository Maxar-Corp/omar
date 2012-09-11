<div class="niceBox">
  <div class="niceBoxHd">Map Center:</div>

  <div class="niceBoxBody">
    <ol>
      <li>DD:</li>
      <li><g:textField name="ddMapCtr" id="ddMapCtr" value="" onChange="setMapCtr('dd', this.value)" size="28"
                       title="Enter decimal degree coordinates and click off the text field to re-center the map. Example: 25.77, -80.18"
                       /></li>
    </ol>
    <ol>
      <li>DMS:</li>
      <li><g:textField name="dmsMapCtr" id="dmsMapCtr" value="" onChange="setMapCtr('dms', this.value)" size="28"
                       title="Enter degree minute seconds coordinates and click off the text field to re-center the map. Example: 25Â°46'20.66'' N, 80Â°11'23.64'' W"
                       /></li>
    </ol>
    <ol>
      <li>MGRS:</li>
      <li><g:textField name="point" id="point" value="" onChange="setMapCtr('mgrs', this.value)" size="28"
                       title="Enter MGRS coordinates and click off the text field to re-center the map. Example: 17RNJ8123050729 or 17 RNJ 81230 50729"
                       /></li>
    </ol>

    <div align="center">
      <button id="applyCenterButton" type="button" onclick="">Apply</button>
      <button id="resetCenterButton" type="button" onclick="javascript:resetMapCenter()"
              title="Resets the view to the center of the image but keeps the current zoom level">Reset</button>
    </div>
  </div>
</div>


<input type="hidden" name="request" value=""/>
<input type="hidden" name="layers" value=""/>
<input type="hidden" name="bbox" value=""/>
<input type="hidden" id="contrast" name="contrast" value="${params.contrast ?: ''}"/>
<input type="hidden" id="brightness" name="brightness" value="${params.brightness ?: ''}"/>

<div class="niceBox">
  <div class="niceBoxHd">Image Adjustments:</div>

  <div class="niceBoxBody">
    <ol>
      <li>Interpolation:</li>
      <li>
        <g:select id="interpolation" name="interpolation" value="${params.interpolation ?: bilinear}"
                  from="${['bilinear', 'nearest neighbor', 'cubic', 'sinc']}" onChange="chgInterpolation()"/>
      </li>
      <hr/>
      <label>Brightness: <input type="text" readonly="true" id="brightnessTextField" size="3"
                                                       maxlength="5" value=""
                                                       ></label>

      <li>
        <div id="slider-brightness-bg" class="yui-h-slider" tabindex="-1" hidefocus="false">
          <div id="slider-brightness-thumb" class="yui-slider-thumb"><img
              src="${resource(plugin: 'yui', dir: 'js/yui/slider/assets', file: 'thumb-n.gif')}"></div>
        </div>
      </li>
      <label>Contrast: <input type="text" readonly="true" id="contrastTextField" size="3"
                                                     maxlength="5" value="" >
      </label>
      <li>
        <div id="slider-contrast-bg" class="yui-h-slider" tabindex="-1" hidefocus="false">
          <div id="slider-contrast-thumb" class="yui-slider-thumb"><img
              src="${resource(plugin: 'yui', dir: 'js/yui/slider/assets', file: 'thumb-n.gif')}"></div>
        </div>
      </li>

      <div align="center">
        <button id="brightnessContrastReset" type="button"
                onclick="javascript:resetBrightnessContrast()">Reset</button>

      </div>
      <hr/>
      <li>Sharpen:</li>
      <li>
        <g:select id="sharpen_mode" name="sharpen_mode" value="${params.sharpen_mode ?: 'none'}"
                  from="${['none', 'light', 'heavy']}" onChange="mergeNewParams()"/>
      </li>
      <li>Dynamic Range Adjustment:</li>
      <li>
        <g:select id="stretch_mode" name="stretch_mode" value="${params.stretch_mode ?: 'linear_auto_min_max'}"
                  from="${[[name: 'Automatic', value: 'linear_auto_min_max'], [name: '1st Std', value: 'linear_1std_from_mean'], [name: '2nd Std', value: 'linear_2std_from_mean'], [name: '3rd Std', value: 'linear_3std_from_mean'], [name: 'No Adjustment', value: 'none']]}"
                  optionValue="name" optionKey="value"
                  onChange="mergeNewParams()"/>
      </li>
      <li>Region:</li>
      <li>
        <g:select id="stretch_mode_region" name="stretch_mode_region"
                  value="${params.stretch_mode_region ?: 'viewport'}" from="${['global', 'viewport']}"
                  onChange="mergeNewParams()"/>
      </li>

      <g:if test="${rasterEntries[0]?.numberOfBands == 1}">
        <li>Band:</li>
        <li><g:select id="bands" name="bands" value="${params.bands ?: 'default'}" from="${['default','0']}"
                      onChange="mergeNewParams()"/></li>
      </g:if>
      <g:if test="${rasterEntries[0]?.numberOfBands == 2}">
        <li>Bands:</li>
        <li><g:select id="bands" name="bands" value="${params.bands ?: 'default'}" from="${['default','0,1', '1,0', '0', '1']}"
                      onChange="mergeNewParams()"/></li>
      </g:if>
      <g:if test="${rasterEntries[0]?.numberOfBands >= 3}">
        <li>Bands:</li>
        <li>
            <g:select id="bands" name="bands" value="${params.bands ?: 'default'}"
                      from="${['default','0,1,2', '2,1,0', '1,0,2', '1,2,0', '2,0,1', '0,2,1', '0', '1', '2']}" onChange="mergeNewParams()"/></li>
      </g:if>

      <li>Orthorectification:</li>
      <li>
        <g:select id="quicklook" name="quicklook"
                  from="${[[name: 'Rigorous', value: 'false'], [name: 'Simple', value: 'true']]}"
                  optionValue="name" optionKey="value"
                  onChange="mergeNewParams()"/>
      </li>

        <hr/>

    </ol>
  </div>
</div>


<div class="niceBox">
  <div class="niceBoxHd">Map Measurement Tool:</div>

  <div class="niceBoxBody">
    <ul>
        <li><P ALIGN=Center><i><small>NOT CERTIFIED FOR TARGETING</small></i></li><hr>
        <li>Measurement Units:</li>
      <li><g:select name="measurementUnits" from="${['kilometers', 'meters', 'feet', 'miles', 'yards','nautical miles']}"
                    title="Select a unit of measurement and use the path and polygon measurement tools in the map toolbar."
                    onChange="measureUnitChanged(this.value)"/></li>

      <div id="pathMeasurement"></div>

      <div id="polygonMeasurement"></div>
    </ul>
  </div>
</div>
