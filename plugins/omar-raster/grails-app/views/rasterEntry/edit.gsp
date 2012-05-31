<%@ page import="org.ossim.omar.raster.RasterDataSet" %>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="generatedViews"/>
  <title>OMAR: Edit Raster Entry ${fieldValue(bean: rasterEntry, field: 'id')}</title>
</head>
<body>
<content tag="content">
  <div class="nav">
      <ul>
          <li class="menuButton"><g:link class="home" uri="/">OMAR™ Home</g:link></li>
          <li class="menuButton"><g:link class="list" action="list">Raster Entry List</g:link></li>
          <sec:ifAllGranted roles="ROLE_ADMIN">
              <li class="menuButton"><g:link class="create" action="create">Create Raster Entry</g:link></li>
          </sec:ifAllGranted>
      </ul>
  </div>
  <div class="body">
    <h1>OMAR: Edit Raster Entry ${fieldValue(bean: rasterEntry, field: 'id')}</h1>
    <g:if test="${flash.message}">
      <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${rasterEntry}">
      <div class="errors">
        <g:renderErrors bean="${rasterEntry}" as="list"/>
      </div>
    </g:hasErrors>
    <g:form method="post">
      <input type="hidden" name="id" value="${rasterEntry?.id}"/>
      <div class="dialog">
        <table>
          <tbody>
          <tr class="prop">
            <td valign="top" class="name">
              <label for="entryId">Entry Id:</label>
            </td>
            <td valign="top" class="value ${hasErrors(bean: rasterEntry, field: 'entryId', 'errors')}">
              <input type="text" id="entryId" name="entryId" value="${fieldValue(bean: rasterEntry, field: 'entryId')}"/>
            </td>
          </tr>
          <tr class="prop">
            <td valign="top" class="name">
              <label for="width">Width:</label>
            </td>
            <td valign="top" class="value ${hasErrors(bean: rasterEntry, field: 'width', 'errors')}">
              <input type="text" id="width" name="width" value="${fieldValue(bean: rasterEntry, field: 'width')}"/>
            </td>
          </tr>
          <tr class="prop">
            <td valign="top" class="name">
              <label for="height">Height:</label>
            </td>
            <td valign="top" class="value ${hasErrors(bean: rasterEntry, field: 'height', 'errors')}">
              <input type="text" id="height" name="height" value="${fieldValue(bean: rasterEntry, field: 'height')}"/>
            </td>
          </tr>
          <tr class="prop">
            <td valign="top" class="name">
              <label for="numberOfBands">Number Of Bands:</label>
            </td>
            <td valign="top" class="value ${hasErrors(bean: rasterEntry, field: 'numberOfBands', 'errors')}">
              <input type="text" id="numberOfBands" name="numberOfBands" value="${fieldValue(bean: rasterEntry, field: 'numberOfBands')}"/>
            </td>
          </tr>
          <tr class="prop">
            <td valign="top" class="name">
              <label for="bitDepth">Bit Depth:</label>
            </td>
            <td valign="top" class="value ${hasErrors(bean: rasterEntry, field: 'bitDepth', 'errors')}">
              <input type="text" id="bitDepth" name="bitDepth" value="${fieldValue(bean: rasterEntry, field: 'bitDepth')}"/>
            </td>
          </tr>
          <tr class="prop">
            <td valign="top" class="name">
              <label for="dataType">Data Type:</label>
            </td>
            <td valign="top" class="value ${hasErrors(bean: rasterEntry, field: 'dataType', 'errors')}">
              <input type="text" id="dataType" name="dataType" value="${fieldValue(bean: rasterEntry, field: 'dataType')}"/>
            </td>
          </tr>
          <%--
          <tr class="prop">
            <td valign="top" class="name">
              <label for="groundGeom">Ground Geom:</label>
            </td>
            <td valign="top" class="value ${hasErrors(bean: rasterEntry, field: 'groundGeom', 'errors')}">
              <input type="text" id="groundGeom" name="groundGeom" value="${fieldValue(bean: rasterEntry, field: 'groundGeom')}"/>
            </td>
          </tr>
          <tr class="prop">
            <td valign="top" class="name">
              <label for="acquisitionDate">Acquisition Date:</label>
            </td>
            <td valign="top" class="value ${hasErrors(bean: rasterEntry, field: 'acquisitionDate', 'errors')}">
              <input type="text" id="acquisitionDate" name="acquisitionDate" value="${fieldValue(bean: rasterEntry, field: 'acquisitionDate')}"/>
            </td>
          </tr>
          <tr class="prop">
            <td valign="top" class="name">
              <label for="srs">Srs:</label>
            </td>
            <td valign="top" class="value ${hasErrors(bean:rasterEntry,field:'srs','errors')}">
              <input type="text" id="srs" name="srs" value="${fieldValue(bean:rasterEntry,field:'srs')}"/>
            </td>
          </tr>
          <tr class="prop">
            <td valign="top" class="name">
              <label for="metadataTags">Metadata Tags:</label>
            </td>
            <td valign="top" class="value ${hasErrors(bean: rasterEntry, field: 'metadataTags', 'errors')}">
              <ul>
                <g:each var="m" in="${rasterEntry?.metadataTags?}">
                  <li><g:link controller="metadataTag" action="show" id="${m.id}">${m}</g:link></li>
                </g:each>
              </ul>
              <g:link controller="metadataTag" params="[" rasterEntry.id":rasterEntry?.id]" action="create">Add MetadataTag</g:link>
            </td>
          </tr>
          --%>
          <tr class="prop">
            <td valign="top" class="name">
              <label for="fileObjects">File Objects:</label>
            </td>
            <td valign="top" class="value ${hasErrors(bean: rasterEntry, field: 'fileObjects', 'errors')}">
              <ul>
                <g:each var="f" in="${rasterEntry?.fileObjects?}">
                  <li><g:link controller="rasterEntryFile" action="show" id="${f.id}">${f}</g:link></li>
                </g:each>
              </ul>
              <g:link controller="rasterEntryFile" params="[" rasterEntry.id":rasterEntry?.id]" action="create">Add RasterEntryFile</g:link>
            </td>
          </tr>
          <tr class="prop">
            <td valign="top" class="name">
              <label for="rasterDataSet">Raster Data Set:</label>
            </td>
            <td valign="top" class="value ${hasErrors(bean: rasterEntry, field: 'rasterDataSet', 'errors')}">
              <g:select optionKey="id" from="${RasterDataSet.list()}" name="rasterDataSet.id" value="${rasterEntry?.rasterDataSet?.id}"></g:select>
            </td>
          </tr>
          </tbody>
        </table>
      </div>
      <div class="buttons">
        <span class="button"><g:actionSubmit class="save" value="Update"/></span>
        <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
      </div>
    </g:form>
  </div>
</content>
</body>
</html>
