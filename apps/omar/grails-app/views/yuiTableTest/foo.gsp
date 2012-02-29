<%--
  Created by IntelliJ IDEA.
  User: sbortman
  Date: Aug 11, 2009
  Time: 9:08:47 AM
  To change this template use File | Settings | File Templates.
--%>

<%@ page import="org.ossim.omar.raster.RasterEntrySearchTag" contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <title>Simple GSP page</title>
  <gui:resources components="['dataTable','datePicker', 'dialog', 'tabView']"/>

  <style type="text/css">
  div.niceBox {
    margin-top: 3px;
    margin-bottom: 8px;
    background-color: #CCFFBF;
    border: 1px solid #2A8400;
  }

  div.niceBoxHd {
    font-size: 87%;
    font-weight: bold;
    padding: 2px;
    color: white;
    background-color: #2AD400;
  }

  div.niceBoxBody {
    font-size: 87%;
    padding: 3px;
  }
  </style>
</head>
<body class="yui-skin-sam">
<gui:tabView>
  <gui:tab label="Image" active="true">
    <gui:dataTable id="image"
            draggableColumns="true"
            columnDefs="${imageColumnDefs}"
            controller="yuiTableTest" action="dataAsJSON"
            paginate="true"
            rowsPerPage="${max}"
            params="${queryParams?.toMap2()}"
            paginatorConfig="[
              template:'{PreviousPageLink} {PageLinks} {NextPageLink} {CurrentPageReport}',
              pageReportTemplate:'{totalRecords} total records'
            ]"/>

  </gui:tab>
<%--
  <gui:tab label="Metadata" active="true">
    <gui:dataTable id="metdata"
            draggableColumns="true"
            columnDefs="${metadataColumnDefs}"
            controller="yuiTableTest" action="dataAsJSON"
            paginate="true"
            rowsPerPage="${max}"
            params="${queryParams?.toMap2()}"
            paginatorConfig="[
              template:'{PreviousPageLink} {PageLinks} {NextPageLink} {CurrentPageReport}',
              pageReportTemplate:'{totalRecords} total records'
            ]"/>
  </gui:tab>
  <gui:tab label="File">
    <gui:dataTable id="file"
            draggableColumns="true"
            columnDefs="${fileColumnDefs}"
            controller="yuiTableTest" action="dataAsJSON"
            paginate="true"
            rowsPerPage="${max}"
            params="${queryParams?.toMap2()}"
            paginatorConfig="[
              template:'{PreviousPageLink} {PageLinks} {NextPageLink} {CurrentPageReport}',
              pageReportTemplate:'{totalRecords} total records'
            ]"/>
  </gui:tab>
  <gui:tab label="Links">
    <gui:dataTable id="links"
            draggableColumns="true"
            columnDefs="${linkColumnDefs}"
            controller="yuiTableTest" action="dataAsJSON"
            paginate="true"
            rowsPerPage="${max}"
            params="${queryParams?.toMap2()}"
            paginatorConfig="[
              template:'{PreviousPageLink} {PageLinks} {NextPageLink} {CurrentPageReport}',
              pageReportTemplate:'{totalRecords} total records'
            ]"/>
  </gui:tab>
--%>
</gui:tabView>
<g:form>
  <div class="niceBox">
    <div class="niceBoxHd">Geospatial Criteria</div>
    <div class="niceBoxBody">
      <fieldset>
        <legend></legend>
        <ul>
          <li><label for="aoiMinLon">Min Lon:</label></li>
          <li><input type="text" id="aoiMinLon" name="aoiMinLon" value="${queryParams?.aoiMinLon}"/></li>
          <li><label for="aoiMinLat">Min Lat:</label></li>
          <li><input type="text" id="aoiMinLat" name="aoiMinLat" value="${queryParams?.aoiMinLat}"/></li>
          <li><label for="aoiMaxLon">Max Lon:</label></li>
          <li><input type="text" id="aoiMaxLon" name="aoiMaxLon" value="${queryParams?.aoiMaxLon}"/></li>
          <li><label for="aoiMaxLat">Max Lat:</label></li>
          <li><input type="text" id="aoiMaxLat" name="aoiMaxLat" value="${queryParams?.aoiMaxLat}"/></li>
          <%--
          <li><br/></li>
          <li><button onclick="">Set</button><input type="button" onclick="clearAOI()" value="Clear AOI"></li>
          --%>
        </ul>
      </fieldset>
    </div>
  </div>
  <div class="niceBox">
    <div class="niceBoxHd">Temporal Criteria</div>
    <div class="niceBoxBody">
      <fieldset>
        <legend></legend>
        <ul>
          <li><label for="startDate">Start Date:</label></li>
          <li><gui:datePicker id="startDate" name="startDate"
                  close="true" includeTime="true" formatString="MM/dd/yyyy HH:mm:ss"
                  value="${queryParams?.startDate}"/></li>
          <%--
          <li><input type="text" id="startDate" name="startDate" value="${fieldValue(bean: queryParams, field: 'startDate')}"/><button id="showStartDate" onclick="return false;">Set</button></li>
          --%>
          <li><label for="endDate">End Date:</label></li>
          <%--
          <li><input type="text" id="endDate" value=""/><button id="showEndate" onclick="return false;">Set</button></li>
          <gui:dialog
                  title="Set the Ending Date"
                  triggers="[show:[id:'showEndate', on:'click']]"
                  modal="true">
            <gui:datePicker includeTime="true" formatString="yyyy/MM/dd HH:mm:ss"/>
          </gui:dialog>
          <li><br/></li>
          <li><button onclick="">Set</button><button onclick="">Clear</button></li>
          --%>
          <li>
            <%--
            <gui:datePicker id="endDate" name="endDate"
                    close="true" includeTime="true" formatString="MM/dd/yyyy HH:mm:ss"
                    value="${queryParams?.endDate}"/>
            --%>
            <g:datePicker name="endDate" value="${queryParams?.endDate}"/>
          </li>
        </ul>
      </fieldset>
    </div>
  </div>
  <div class="niceBox">
    <div class="niceBoxHd">Metadata Criteria</div>
    <div class="niceBoxBody">
      <fieldset>
        <legend></legend>
        <ul>
          <li>
            <g:select id="searchTag.id" name='searchTag.id' value="${queryParams?.searchTag?.id}"
                    noSelection="${['null':'Select One...']}"
                    from='${RasterEntrySearchTag.list()}'
                    optionKey="id" optionValue="description"/>
          </li>
          <li>
            <input type="text" id="searchTagValue" name="searchTagValue" value="${fieldValue(bean: queryParams, field: 'searchTagValue')}"/>
          </li>
        </ul>
      </fieldset>
    </div>
  </div>
  <br/>
  <fieldset>
    <ul><li><g:submitButton name="search" value="Search"/></li></ul>
  </fieldset>

</g:form>
</body>
</html>
