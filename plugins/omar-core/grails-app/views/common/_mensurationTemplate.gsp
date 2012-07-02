<div class="niceBox">
  <div class="niceBoxHd">Map Measurement Tool:</div>

  <div class="niceBoxBody">
    <p>Measurement Units:</p>

    <p>Not certified for targeting.</p>

    <p>
      <g:select name="measurementUnits" from="${['kilometers', 'meters', 'feet', 'miles', 'yards', 'nautical miles']}"
                title="Select a unit of measurement and use the path and polygon measurment tools in the map toolbar."
                onChange="omar.changeMeasureUnit(this.value)"/>
    </p>

    <p>

    <div id="pathMeasurement"></div>
  </p>
    <p>

    <div id="polygonMeasurement"></div>
  </p>
  </div>
</div>

<input type="hidden" id="units" name="units"/>