<div class="niceBox">
  <div class="niceBoxHd" style="background: #0B0B65">Image Adjustments:</div>

  <div class="niceBoxBody" style="background: #2F2F2F">
    <ol>
      <li style="color: #00CCFF">Interpolation:</li>
      <li>
        <g:select
            id="interpolation"
            name="interpolation"
            value="${params.interpolation ?: bilinear}"
            from="${['bilinear', 'nearest neighbor', 'cubic', 'sinc']}"
            onChange="chgInterpolation()"
            style="background: black; color: white"/>
      </li>
      <hr/>
      <label style="color: #00CCFF">Brightness: <input type="text" readonly="true" id="brightnessTextField" size="3"
                                                       maxlength="5" value="" style="background: black; color: white">
      </label>
      <li>
        <div id="slider-brightness-bg" class="yui-h-slider" tabindex="-1" hidefocus="false">
          <div id="slider-brightness-thumb" class="yui-slider-thumb"><img
              src="${resource(plugin: 'yui', dir: 'js/yui/slider/assets', file: 'thumb-n.gif')}"></div>
        </div>
      </li>

      <label style="color: #00CCFF">Contrast: <input type="text" readonly="true" id="contrastTextField" size="3"
                                                     maxlength="5" value="" style="background: black; color: white">
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

      <li style="color: #00CCFF">Sharpen:</li>
      <li>
        <g:select
            id="sharpen_mode"
            name="sharpen_mode"
            value="${params.sharpen_mode ?: 'none'}"
            from="${['none', 'light', 'heavy']}"
            onChange="changeSharpenOpts()"
            style="background: black; color: white"/>
      </li>

      <li style="color: #00CCFF">Dynamic Range Adjustment:</li>
      <li>
        <g:select
            id="stretch_mode"
            name="stretch_mode"
            value="${params.stretch_mode ?: 'linear_auto_min_max'}"
            from="${[[name: 'Automatic', value: 'linear_auto_min_max'], [name: '1st Std', value: 'linear_1std_from_mean'], [name: '2nd Std', value: 'linear_2std_from_mean'], [name: '3rd Std', value: 'linear_3std_from_mean'], [name: 'No Adjustment', value: 'none']]}"
            optionValue="name"
            optionKey="value"
            onChange="changeHistoOpts()"
            style="background: black; color: white"/>
      </li>

      <li style="color: #00CCFF">Region:</li>
      <li>
        <g:select
            id="stretch_mode_region"
            name="stretch_mode_region"
            from="${['global', 'viewport']}"
            onChange="changeHistoOpts()"
            value="${params.stretch_mode_region ?: 'viewport'}"
            style="background: black; color: white"/>
      </li>

      <li style="color: #00CCFF">Band:</li>
      <g:if test="${rasterEntry?.numberOfBands == 1}">
        <li>
          <g:select
              id="bands"
              name="bands"
              value="${params.bands ?: '0'}"
              from="${['0']}"
              onChange="changeBandsOpts()"
              style="background: black; color: white"/>
        </li>
      </g:if>
      <g:if test="${rasterEntry?.numberOfBands == 2}">
        <li>
          <g:select
              id="bands"
              name="bands"
              value="${params.bands ?: '0,1'}"
              from="${['0,1', '1,0', '0', '1']}"
              onChange="changeBandsOpts()"
              style="background: black; color: white"/>
        </li>
      </g:if>
      <g:if test="${rasterEntry?.numberOfBands >= 3}">
        <li>
          <g:select
              id="bands"
              name="bands"
              value="${params.bands ?: '0,1,2'}"
              from="${['0,1,2', '2,1,0', '1,0,2', '1,2,0', '2,0,1', '0,2,1', '0', '1', '2']}"
              onChange="changeBandsOpts()"
              style="background: black; color: white"/>
        </li>
      </g:if>
      <hr/>

      <li style="color: #00CCFF">Rotate:</li>
      <li>
        <g:textField name="rotateAngle" value="${params.rotation ?: 0}" onChange="rotateTextFieldChange(this.value)"
                     size="1" style="background: black; color: white"/>
        <button id="rotateApply" type="button" onclick="">Apply</button>
        <br>

      <li>
        <div id="slider-rotate-bg" class="yui-h-slider" tabindex="-1" hidefocus="false">
          <div id="slider-rotate-thumb" class="yui-slider-thumb"><img
              src="${resource(plugin: 'yui', dir: 'js/yui/slider/assets', file: 'thumb-n.gif')}"></div>
        </div>
      </li>

      <div align="center">
        <button id="upIsUpButtonId" type="button"
                onclick="javascript:rotateSlider.setRealValue( 0 )">Reset</button>
        <button id="upIsUpButtonId" type="button"
                onclick="javascript:rotateSlider.setRealValue( northAngle )">North</button>
        <button id="northUp" type="button"
                onclick="javascript:rotateSlider.setRealValue( upIsUpRotation );">Up</button>
      </div>
    </li>
    </ol>
  </div>
</div>
