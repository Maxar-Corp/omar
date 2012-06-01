<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="generatedViews"/>
  <title>OMAR: Show Raster Entry ${fieldValue(bean: rasterEntry, field: 'id')}</title>
</head>
<body>
<content tag="content">
    <omar:logout/>
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
    <h1>OMAR: Show Raster Entry ${fieldValue(bean: rasterEntry, field: 'id')}</h1>
    <g:if test="${flash.message}">
      <div class="message">${flash.message}</div>
    </g:if>
    <div class="dialog">
      <table>
        <tbody>
        <tr class="prop">
          <td valign="top" class="name">Id:</td>
          <td valign="top" class="value">${fieldValue(bean: rasterEntry, field: 'id')}</td>
        </tr>
        <tr class="prop">
          <td valign="top" class="name">Entry Id:</td>
          <td valign="top" class="value">${fieldValue(bean: rasterEntry, field: 'entryId')}</td>
        </tr>
        <tr class="prop">
          <td valign="top" class="name">Width:</td>
          <td valign="top" class="value">${fieldValue(bean: rasterEntry, field: 'width')}</td>
        </tr>
        <tr class="prop">
          <td valign="top" class="name">Height:</td>
          <td valign="top" class="value">${fieldValue(bean: rasterEntry, field: 'height')}</td>
        </tr>
        <tr class="prop">
          <td valign="top" class="name">Number Of Bands:</td>
          <td valign="top" class="value">${fieldValue(bean: rasterEntry, field: 'numberOfBands')}</td>
        </tr>
        <tr class="prop">
          <td valign="top" class="name">Number Of Resolution Levels:</td>
          <td valign="top" class="value">${fieldValue(bean: rasterEntry, field: 'numberOfResLevels')}</td>
        </tr>
        <tr class="prop">
          <td valign="top" class="name">Meters Per Pixel:</td>
          <td valign="top" class="value">${rasterEntry.getMetersPerPixel()}</td>
        </tr>
        <tr class="prop">
          <td valign="top" class="name">Bit Depth:</td>
          <td valign="top" class="value">${fieldValue(bean: rasterEntry, field: 'bitDepth')}</td>
        </tr>
        <tr class="prop">
          <td valign="top" class="name">Data Type:</td>
          <td valign="top" class="value">${fieldValue(bean: rasterEntry, field: 'dataType')}</td>
        </tr>
        <tr class="prop">
          <td valign="top" class="name">Image ID:</td>
          <td valign="top" class="value">${fieldValue(bean: rasterEntry, field: 'imageId')}</td>
        </tr>
        <tr class="prop">
          <td valign="top" class="name">Target ID:</td>
          <td valign="top" class="value">${fieldValue(bean: rasterEntry, field: 'targetId')}</td>
        </tr>
        <tr class="prop">
          <td valign="top" class="name">Sensor ID:</td>
          <td valign="top" class="value">${fieldValue(bean: rasterEntry, field: 'sensorId')}</td>
        </tr>
        <tr class="prop">
          <td valign="top" class="name">Mission ID:</td>
          <td valign="top" class="value">${fieldValue(bean: rasterEntry, field: 'missionId')}</td>
        </tr>
        <tr class="prop">
          <td valign="top" class="name">Image Category:</td>
          <td valign="top" class="value">${fieldValue(bean: rasterEntry, field: 'imageCategory')}</td>
        </tr>
        <tr class="prop">
          <td valign="top" class="name">Azimuth Angle:</td>
          <td valign="top" class="value">${fieldValue(bean: rasterEntry, field: 'azimuthAngle')}</td>
        </tr>
        <tr class="prop">
          <td valign="top" class="name">Grazing Angle:</td>
          <td valign="top" class="value">${fieldValue(bean: rasterEntry, field: 'grazingAngle')}</td>
        </tr>
        <tr class="prop">
          <td valign="top" class="name">Security Classification:</td>
          <td valign="top" class="value">${fieldValue(bean: rasterEntry, field: 'securityClassification')}</td>
        </tr>
        <tr class="prop">
          <td valign="top" class="name">Title:</td>
          <td valign="top" class="value">${fieldValue(bean: rasterEntry, field: 'title')}</td>
        </tr>
        <tr class="prop">
          <td valign="top" class="name">Organization:</td>
          <td valign="top" class="value">${fieldValue(bean: rasterEntry, field: 'organization')}</td>
        </tr>
        <tr class="prop">
          <td valign="top" class="name">Description:</td>
          <td valign="top" class="value">${fieldValue(bean: rasterEntry, field: 'description')}</td>
        </tr>
        <tr class="prop">
          <td valign="top" class="name">NIIRS:</td>
          <td valign="top" class="value">${fieldValue(bean: rasterEntry, field: 'niirs')}</td>
        </tr>
        <tr class="prop">
          <td valign="top" class="name">Ground Geometry:</td>
          <td valign="top" class="value">${fieldValue(bean: rasterEntry, field: 'groundGeom')}</td>
        </tr>
        <tr class="prop">
          <td valign="top" class="name">Acquisition Date:</td>
          <td valign="top" class="value">${fieldValue(bean: rasterEntry, field: 'acquisitionDate')}</td>
        </tr>
        <%--
        <tr class="prop">
          <td valign="top" class="name">Ground Geom:</td>
          <td valign="top" class="value">${fieldValue(bean: rasterEntry, field: 'groundGeom')}</td>
        </tr>
        <tr class="prop">
          <td valign="top" class="name">Acquisition Date:</td>
          <td valign="top" class="value">${fieldValue(bean: rasterEntry, field: 'acquisitionDate')}</td>
        </tr>
        <tr class="prop">
          <td valign="top" class="name">Metadata Tags:</td>
          <td valign="top" style="text-align:left;" class="value">
            <g:link controller="metadataTag" action="list" params="${[rasterEntryId: rasterEntry.id]}">Show MetadataTags</g:link>
          </td>
        </tr>
        --%>
        <tr class="prop">
          <td valign="top" class="name">File Objects:</td>
          <td valign="top" style="text-align:left;" class="value">
            <g:if test="${rasterEntry.fileObjects}">
              <g:link controller="rasterEntryFile" action="list" params="${[rasterEntryId: rasterEntry.id]}">Show Raster Files</g:link>
            </g:if>
          </td>
        </tr>
        <tr class="prop">
          <td valign="top" class="name">Raster Data Set:</td>
          <td valign="top" class="value"><g:link controller="rasterDataSet" action="show" id="${rasterEntry?.rasterDataSet?.id}">${rasterEntry?.rasterDataSet?.encodeAsHTML()}</g:link></td>
        </tr>
        </tbody>
      </table>
    </div>
    <div class="buttons">
      <g:form>
        <input type="hidden" name="id" value="${rasterEntry?.id}"/>
        <sec:ifAllGranted roles="ROLE_ADMIN">
          <span class="button"><g:actionSubmit class="edit" value="Edit"/></span>
          <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
        </sec:ifAllGranted>
        <span class="menuButton">
          <a href="${createLink(controller: 'thumbnail', action: 'show', id: rasterEntry.id, params: [size: 512])}" >Show Thumbnail</a>
        </span>
      </g:form>
    </div>
  </div>
</content>
</body>
</html>
