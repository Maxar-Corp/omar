<div id='generalQueryId'>
    <div class="Table niceBoxNoBorder">
        <div class="Title niceBoxHeader">
            Constraints
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
                <label>0 to 9.0</label>
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
                <label>0 to 360</label>
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
                <label>0 to 90</label>
            </div>
        </div>

        <!--
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
        -->
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
                <label>0 to 360</label>
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
            <div class="Cell">
                <p>Comparator</p>
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
            <div class="Cell">
                <select id="missionComparatorId">
                    <option value="iContains">Sub String (case insensitive)</option>
                    <option value="Contains">Sub String</option>
                    <option value="iStartsWith">Starts With (case insensitive)</option>
                    <option value="StartsWith">Starts With</option>
                    <option value="iEndsWith">Ends With (case insensitive)</option>
                    <option value="EndsWith">Ends With</option>
                    <option value="Equals">Equals</option>
                    <option value="NotEquals">Not Equals</option>
                </select>                <%--<input id="missionOperationId" type="text" value="">--%>
            </div>
        </div>
        <div class="Row">
            <div class="Cell">
                <input id="sensorCheckboxId" type="checkbox">
            </div>
            <div class="Cell">
                <label>Sensor Id</label>
            </div>
            <div class="Cell">
                <input id="sensorId" type="text" value="">
            </div>
            <div class="Cell">
                <select id="sensorComparatorId">
                    <option value="iContains">Sub String (case insensitive)</option>
                    <option value="Contains">Sub String</option>
                    <option value="iStartsWith">Starts With (case insensitive)</option>
                    <option value="StartsWith">Starts With</option>
                    <option value="iEndsWith">Ends With (case insensitive)</option>
                    <option value="EndsWith">Ends With</option>
                    <option value="Equals">Equals</option>
                    <option value="NotEquals">Not Equals</option>
                </select>                <%--<input id="missionOperationId" type="text" value="">--%>
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
            <div class="Cell">
                <select id="beNumberComparatorId">
                    <option value="iContains">Sub String (case insensitive)</option>
                    <option value="Contains">Sub String</option>
                    <option value="iStartsWith">Starts With (case insensitive)</option>
                    <option value="StartsWith">Starts With</option>
                    <option value="iEndsWith">Ends With (case insensitive)</option>
                    <option value="EndsWith">Ends With</option>
                    <option value="Equals">Equals</option>
                    <option value="NotEquals">Not Equals</option>
                </select>                <%--<input id="missionOperationId" type="text" value="">--%>
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
            <div class="Cell">
                <select id="targetComparatorId">
                    <option value="iContains">Sub String (case insensitive)</option>
                    <option value="Contains">Sub String</option>
                    <option value="iStartsWith">Starts With (case insensitive)</option>
                    <option value="StartsWith">Starts With</option>
                    <option value="iEndsWith">Ends With (case insensitive)</option>
                    <option value="EndsWith">Ends With</option>
                    <option value="Equals">Equals</option>
                    <option value="NotEquals">Not Equals</option>
                </select>                <%--<input id="missionOperationId" type="text" value="">--%>
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
            <div class="Cell">
                <select id="wacComparatorId">
                    <option value="iContains">Sub String (case insensitive)</option>
                    <option value="Contains">Sub String</option>
                    <option value="iStartsWith">Starts With (case insensitive)</option>
                    <option value="StartsWith">Starts With</option>
                    <option value="iEndsWith">Ends With (case insensitive)</option>
                    <option value="EndsWith">Ends With</option>
                    <option value="Equals">Equals</option>
                    <option value="NotEquals">Not Equals</option>
                </select>                <%--<input id="missionOperationId" type="text" value="">--%>
            </div>
        </div>
        <div class="Row">
            <div class="Cell">
                <input id="filenameCheckboxId" type="checkbox">
            </div>
            <div class="Cell">
                <label>Filename</label>
            </div>
            <div class="Cell">
                <input id="filenameId" type="text">
            </div>
            <div class="Cell">
                <select id="filenameComparatorId">
                    <option value="iContains">Sub String (case insensitive)</option>
                    <option value="Contains">Sub String</option>
                    <option value="iStartsWith">Starts With (case insensitive)</option>
                    <option value="StartsWith">Starts With</option>
                    <option value="iEndsWith">Ends With (case insensitive)</option>
                    <option value="EndsWith">Ends With</option>
                    <option value="Equals">Equals</option>
                    <option value="NotEquals">Not Equals</option>
                </select>
            </div>
        </div>
        <div class="Row">
            <div class="Cell">
                <input id="imageIdCheckboxId" type="checkbox">
            </div>
            <div class="Cell">
                <label>Image Id</label>
            </div>
            <div class="Cell">
                <input id="imageIdId" type="text">
            </div>
            <div class="Cell">
                <select id="imageIdComparatorId">
                    <option value="iContains">Sub String (case insensitive)</option>
                    <option value="Contains">Sub String</option>
                    <option value="iStartsWith">Starts With (case insensitive)</option>
                    <option value="StartsWith">Starts With</option>
                    <option value="iEndsWith">Ends With (case insensitive)</option>
                    <option value="EndsWith">Ends With</option>
                    <option value="Equals">Equals</option>
                    <option value="NotEquals">Not Equals</option>
                </select>
            </div>
        </div>

        <div class="Heading">
            <div class="Cell">
                <p><input id="GeneralQueryResetButtonId" type="Button" value="Reset"></input></p>
            </div>
        </div>

    </div>


</div>

<!--
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
</div>


<div class="Table niceBoxNoBorder">
    <div class="Title niceBoxHeader">
        Mission Constraints
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

</div>


</div>
         -->
