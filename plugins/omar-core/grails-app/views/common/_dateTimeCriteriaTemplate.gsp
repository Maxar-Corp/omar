<div class="niceBox">
  <div class="niceBoxHd">Date/Time Criteria:</div>

  <div class="niceBoxBody">
    <p><b>Start Date/Time:</b></p>

    <p><g:textField id="startDateInput" name="startDateInput" style="width:60px"
                    title="Click to enter start date in MM/DD/YYYY."/> <g:textField id="startDate_hour"
                                                                                    name="startDate_hour"
                                                                                    style="width:20px" value="00"
                                                                                    title="Enter start hour in zulu time."/>:<g:textField
        id="startDate_minute" name="startDate_minute" style="width:20px" value="00"
        title="Enter start minute in zulu time."/> Zulu</p>

    <p>

    <div class="datechooser yui-skin-sam" id="startDateContainer"></div></p>

    <p><b>End Date/Time:</b></p>

    <p><g:textField id="endDateInput" name="endDateInput" style="width:60px"
                    title="Click to enter end date in MM/DD/YYYY."/> <g:textField id="endDate_hour" name="endDate_hour"
                                                                                  style="width:20px" value="00"
                                                                                  title="Enter end hour in zulu time."/>:<g:textField
        id="endDate_minute" name="endDate_minute" style="width:20px" value="00"
        title="Enter end minute in zulu time."/> Zulu</p>

    <p>

    <div class="datechooser yui-skin-sam" id="endDateContainer"></div></p>

    <p>&nbsp;</p>

    <p><span id="linkbutton3" class="yui-button yui-link-button" title="Click to clear date and time."><span
        class="first-child"><a href="javascript:clearDateTime();">Clear Date/Time</a></span></span></p>
  </div>
</div>

<input type="hidden" id="startDate" name="startDate" value="date.struct"/>
<input type="hidden" id="startDate_month" name="startDate_month"/>
<input type="hidden" id="startDate_day" name="startDate_day"/>
<input type="hidden" id="startDate_year" name="startDate_year"/>
<input type="hidden" id="startDate_timezone" name="startDate_timezone" value="UTC"/>

<input type="hidden" id="endDate" name="endDate" value="date.struct"/>
<input type="hidden" id="endDate_month" name="endDate_month"/>
<input type="hidden" id="endDate_day" name="endDate_day"/>
<input type="hidden" id="endDate_year" name="endDate_year"/>
<input type="hidden" id="endDate_timezone" name="endDate_timezone" value="UTC"/>