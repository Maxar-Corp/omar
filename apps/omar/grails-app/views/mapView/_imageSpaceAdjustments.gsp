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
      <button id="resetCenterButton" type="button" onclick="javascript:OMAR.imageManipulator.moveToCenter()"
              title="Resets the view to the center of the image but keeps the current zoom level">Reset</button>
    </div>
  </div>
</div>

<div class="niceBox">
  <div class="niceBoxHd">Image Adjustments:</div>

  <div class="niceBoxBody">
    <ol>
      <li>Interpolation:</li>
      <li>
        <g:select
            id="interpolation"
            name="interpolation"
            value="${params.interpolation ?: bilinear}"
            from="${['bilinear', 'nearest neighbor', 'cubic', 'sinc']}"
            onChange="chgInterpolation()"/>
      </li>
      <hr/>
      <label>Brightness: <input type="text" readonly="true" id="brightnessTextField" size="3"
                                                       maxlength="5" value="">
      </label>
      <li>
        <div id="slider-brightness-bg" class="yui-h-slider" tabindex="-1" hidefocus="false">
          <div id="slider-brightness-thumb" class="yui-slider-thumb"><img
              src="${resource(plugin: 'yui', dir: 'js/yui/slider/assets', file: 'thumb-n.gif')}"></div>
        </div>
      </li>

      <label>Contrast: <input type="text" readonly="true" id="contrastTextField" size="3"
                                                     maxlength="5" value="">
      </label>
      <li>
        <div id="slider-contrast-bg" class="yui-h-slider" tabindex="-1" hidefocus="false">
          <div id="slider-contrast-thumb" class="yui-slider-thumb"><img
              src="${resource(plugin: 'yui', dir: 'js/yui/slider/assets', file: 'thumb-n.gif')}"></div>
        </div>
      </li>

      <div align="center"><button id="brightnessContrastReset" type="button"
                                  onclick="javascript:resetBrightnessContrast()">Reset</button></div>
      <hr/>

      <li>Sharpen:</li>
      <li>
        <g:select
            id="sharpen_mode"
            name="sharpen_mode"
            value="${params.sharpen_mode ?: 'none'}"
            from="${['none', 'light', 'heavy']}"
            onChange="changeSharpenOpts()"/>
      </li>

      <li>Dynamic Range Adjustment:</li>
      <li>
        <g:select
            id="stretch_mode"
            name="stretch_mode"
            value="${params.stretch_mode ?: 'linear_auto_min_max'}"
            from="${[[name: 'Automatic', value: 'linear_auto_min_max'], [name: '1st Std', value: 'linear_1std_from_mean'], [name: '2nd Std', value: 'linear_2std_from_mean'], [name: '3rd Std', value: 'linear_3std_from_mean'], [name: 'No Adjustment', value: 'none']]}"
            optionValue="name"
            optionKey="value"
            onChange="changeHistoOpts()"/>
      </li>

      <li>Region:</li>
      <li>
        <g:select
            id="stretch_mode_region"
            name="stretch_mode_region"
            from="${['global', 'viewport']}"
            onChange="changeHistoOpts()"
            value="${params.stretch_mode_region ?: 'viewport'}"/>
      </li>

      <li>Band:</li>
      <g:if test="${rasterEntry?.numberOfBands == 1}">
        <li>
          <g:select
              id="bands"
              name="bands"
              value="${params.bands ?: 'default'}"
              from="${['default','0']}"
              onChange="changeBandsOpts()"/>
        </li>
      </g:if>
      <g:if test="${rasterEntry?.numberOfBands == 2}">
        <li>
          <g:select
              id="bands"
              name="bands"
              value="${params.bands ?: 'default'}"
              from="${['default','0,1', '1,0', '0', '1']}"
              onChange="changeBandsOpts()"/>
        </li>
      </g:if>
      <g:if test="${rasterEntry?.numberOfBands >= 3}">
        <li>
          <g:select
              id="bands"
              name="bands"
              value="${params.bands ?: 'default'}"
              from="${['default','0,1,2', '2,1,0', '1,0,2', '1,2,0', '2,0,1', '0,2,1', '0', '1', '2']}"
              onChange="changeBandsOpts()"/>
        </li>
      </g:if>
      <hr/>

      <li>Rotate:</li>
      <li>
        <g:textField name="rotateAngle" value="${params.rotation ?: 0}" onChange="rotateTextFieldChange(this.value)"
                     size="1"/>
        <button id="rotateApply" type="button" onclick="">Apply</button>
        <br>

      <li>
        <div id="slider-rotate-bg" class="yui-h-slider" tabindex="-1" hidefocus="false">
          <div id="slider-rotate-thumb" class="yui-slider-thumb"><img
              src="${resource(plugin: 'yui', dir: 'js/yui/slider/assets', file: 'thumb-n.gif')}"></div>
        </div>
      </li>

     <div align="center">
        <button id="resetButtonId" type="button"
                onclick="javascript:resetRotate()">Reset</button>
        <button id="northUpUpButtonId" type="button"
                onclick="javascript:rotateNorthUp()">North</button>
         <button id="upIsUpButtonId" type="button"
                 onclick="javascript:rotateUpIsUp();">Up</button>
     </div>
    </li>
    </ol>
  </div>
</div>


<div class="niceBox">
    <div class="niceBoxHd">Map Measurement Tool:</div>

    <div class="niceBoxBody">
        <ul>
            <li><P ALIGN=Center><i><small>NOT CERTIFIED FOR TARGETING</small></i></li><hr>
            <li>Measurement Units:</li>
            <li>       <g:select
                    id="unitSelectionID"
                    name="Units"
                    value="meters"
                    from="${['kilometers', 'meters', 'feet', 'yards', 'miles', 'nautical miles']}"
                    onChange="unitsChanged(this.value)"/>
            </li>

            <div id="mensurationDivId"></div>

        </ul>
    </div>
</div>


<div class="niceBox">
    <div class="niceBoxHd">Position Quality Evaluator:</div>

    <div class="niceBoxBody">
        <ul>
            <li><P ALIGN=Center><i><small>NOT CERTIFIED FOR TARGETING</small></i></li><hr>

            <div id="pqeDivId"></div>

            <li><p>Confidence: <g:select
                    from="${['0.95P','0.5P']}"
                    name="probLevel"
                    noSelection="['0.9P':'0.9P']"
                    onChange="setPropLevel(this.value)"/>  </p>
            </li>
            <li>
                <p>Display Unit: <g:select id="pqeDisplayUnit" name="pqeDisplayUnit" from="${['DD', 'DMS', 'MGRS']}"
                                           title="Select decimal degrees, degrees minutes seconds, or military grid reference system for coordinate readouts."
                                           onChange="javascript:setCurrentPqeDisplayUnit(this.value);"/></p>

            </li>
        </ul>
    </div>
</div>

