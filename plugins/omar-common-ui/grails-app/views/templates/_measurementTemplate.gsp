<div id="measurementUnitViewId">

    <div class="niceBox">
        <div class="niceBoxHeader">
            <b>Map Measurement Tool:</b>
        </div>

        <div class="niceBoxBody">

            <p>Measurement Units:</p>

            <p>Not certified for targeting.</p>

            <p>
                <g:select id="selectUnitsId"
                          name="selectUnitsId"
                          from="${['kilometers', 'meters', 'feet', 'miles', 'yards', 'nautical miles']}"
                          title="Select a unit of measurement and use the path and polygon measurment tools in the map toolbar."
                        />
            </p>

            <p><div id="pathMeasurement"></div></p>

            <p><div id="polygonMeasurement"></div></p>



        </div>
    </div>

    <input type="hidden" id="units" name="units"/>

</div>
