<div id='generalQueryId'>
    <div class="Table niceBoxNoBorder">
        <div class="Title niceBoxHeader">
            Common Constraints
        </div>
        <div class="Heading">
            <div class="Cell">
                <p>Enabled</p>
            </div>
            <div class="Cell">
                <p>Name</p>
            </div>
            <div class="Cell">
                <p>Min</p>
            </div>
            <div class="Cell">
                <p>Max</p>
            </div>
            <div class="Cell">
                <p></p>
            </div>
        </div>
        <div class="Row">
            <div class="Cell">
                <input id="niirsCheckboxId" type="checkbox">
            </div>

            <div class="Cell">
                <label>Predicted NIIRS</label>
            </div>
            <div class="Cell">
                <input id="niirsMinId" type="text" value="0.0">
            </div>
            <div class="Cell">
                <input id="niirsMaxId" type="text" value="9.0">
            </div>
            <div class="Cell">
                <label>0-9.0</label>
            </div>
        </div>
        <div class="Row">
            <div class="Cell">
                <input id="azimuthAngleCheckboxId" type="checkbox">
            </div>
            <div class="Cell">
                <label>Azimuth</label>
            </div>
            <div class="Cell">
                <input id="azimuthAngleMinId" type="text" value="0">
            </div>
            <div class="Cell">
                <input id="azimuthAngleMaxId" type="text" value="360">
            </div>
            <div class="Cell">
                <label>0-360</label>
            </div>
        </div>
        <div class="Row">
            <div class="Cell">
                <input id="grazingAngleCheckboxId" type="checkbox">
            </div>
            <div class="Cell">
                <label>Grazing/Elev</label>
            </div>
            <div class="Cell">
                <input id="grazingAngleMinId" type="text" value="0">
            </div>
            <div class="Cell">
                <input id="grazingAngleMaxId" type="text" value="90">
            </div>
            <div class="Cell">
                <label>0-90</label>
            </div>
        </div>
    </div>
    <div class="Table niceBoxNoBorder">
        <div class="Title niceBoxHeader">
            EO/IR Constraints
        </div>
        <div class="Heading">
            <div class="Cell">
                <p>Enabled</p>
            </div>
            <div class="Cell">
                <p>Name</p>
            </div>
            <div class="Cell">
                <p>Min</p>
            </div>
            <div class="Cell">
                <p>Max</p>
            </div>
            <div class="Cell">
                <p></p>
            </div>
        </div>
        <div class="Row">
            <div class="Cell">
                <input id="sunAzimuthCheckboxId" type="checkbox">
            </div>

            <div class="Cell">
                <label>Sun Azimuth</label>
            </div>
            <div class="Cell">
                <input id="sunAzimuthMinId" type="text" value="0">
            </div>
            <div class="Cell">
                <input id="sunAzimuthMaxId" type="text" value="360">
            </div>
            <div class="Cell">
                <label>0-360</label>
            </div>
        </div>
        <div class="Row">
            <div class="Cell">
                <input id="sunElevationCheckboxId" type="checkbox">
            </div>
            <div class="Cell">
                <label>Sun Elevation</label>
            </div>
            <div class="Cell">
                <input id="sunElevationMinId" type="text" value="-90">
            </div>
            <div class="Cell">
                <input id="sunElevationMaxId" type="text" value="90">
            </div>
            <div class="Cell">
                <label>-90 to 90</label>
            </div>
        </div>
        <div class="Row">
            <div class="Cell">
                <input id="cloudCoverageCheckboxId" type="checkbox">
            </div>
            <div class="Cell">
                <label>Cloud Coverage</label>
            </div>
            <div class="Cell">
            </div>
            <div class="Cell">
                <input id="cloudCoverageMaxId" type="text" value="100">
            </div>
            <div class="Cell">
                <label>%</label>
            </div>
        </div>
    </div>


    <div class="Table niceBoxNoBorder">
        <div class="Title niceBoxHeader">
            Mission Constraints
        </div>
        <div class="Heading">
            <div class="Cell">
                <p>Enabled</p>
            </div>
            <div class="Cell">
                <p>Name</p>
            </div>
            <div class="Cell">
                <p>Value</p>
            </div>
        </div>
        <div class="Row">
            <div class="Cell">
                <input id="missionCheckboxId" type="checkbox">
            </div>
            <div class="Cell">
                <label>Mission Id</label>
            </div>
            <div class="Cell">
                <input id="missionId" type="text" value="">
            </div>
        </div>

    </div>
    <div class="Table niceBoxNoBorder">
        <div class="Title niceBoxHeader">
            Location Constraints
        </div>
        <div class="Heading">
            <div class="Cell">
                <p>Enabled</p>
            </div>
            <div class="Cell">
                <p>Name</p>
            </div>
            <div class="Cell">
                <p>Value</p>
            </div>
        </div>
        <div class="Row">
            <div class="Cell">
                <input id="beNumberCheckboxId" type="checkbox">
            </div>
            <div class="Cell">
                <label>BE Number</label>
            </div>
            <div class="Cell">
                <input id="beNumberId" type="text">
            </div>
        </div>
        <div class="Row">
            <div class="Cell">
                <input id="targetCheckboxId" type="checkbox">
            </div>
            <div class="Cell">
                <label>Target Id</label>
            </div>
            <div class="Cell">
                <input id="targetId" type="text">
            </div>
        </div>
        <div class="Row">
            <div class="Cell">
                <input id="wacCheckboxId" type="checkbox">
            </div>
            <div class="Cell">
                <label>WAC Number</label>
            </div>
            <div class="Cell">
                <input id="wacId" type="text">
            </div>
        </div>
    </div>



</div>
