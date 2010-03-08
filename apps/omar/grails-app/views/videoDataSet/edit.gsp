<%@ page import="org.ossim.omar.Repository" %>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="main"/>
  <title>Edit VideoDataSet</title>
</head>
<body>
<div class="nav">
  <span class="menuButton"><a class="home" href="${createLinkTo(dir: '')}">Home</a></span>
  <span class="menuButton"><g:link class="list" action="list">VideoDataSet List</g:link></span>
  <span class="menuButton"><g:link class="create" action="create">New VideoDataSet</g:link></span>
</div>
<div class="body">
  <h1>Edit VideoDataSet</h1>
  <g:if test="${flash.message}">
    <div class="message">${flash.message}</div>
  </g:if>
  <g:hasErrors bean="${videoDataSet}">
    <div class="errors">
      <g:renderErrors bean="${videoDataSet}" as="list"/>
    </div>
  </g:hasErrors>
  <g:form method="post">
    <input type="hidden" name="id" value="${videoDataSet?.id}"/>
    <div class="dialog">
      <table>
        <tbody>

          <tr class="prop">
            <td valign="top" class="name">
              <label for="width">Width:</label>
            </td>
            <td valign="top" class="value ${hasErrors(bean: videoDataSet, field: 'width', 'errors')}">
              <input type="text" id="width" name="width" value="${fieldValue(bean: videoDataSet, field: 'width')}"/>
            </td>
          </tr>

          <tr class="prop">
            <td valign="top" class="name">
              <label for="height">Height:</label>
            </td>
            <td valign="top" class="value ${hasErrors(bean: videoDataSet, field: 'height', 'errors')}">
              <input type="text" id="height" name="height" value="${fieldValue(bean: videoDataSet, field: 'height')}"/>
            </td>
          </tr>

<%--
          <tr class="prop">
            <td valign="top" class="name">
              <label for="height">Ground Geom:</label>
            </td>
            <td valign="top" class="value ${hasErrors(bean: videoDataSet, field: 'groundGeom', 'errors')}">
              <input type="text" id="groundGeom" name="height" value="${fieldValue(bean: videoDataSet, field: 'groundGeom')}"/>
            </td>
          </tr>

          <tr class="prop">
            <td valign="top" class="name">
              <label for="startDate">Start Date:</label>
            </td>
            <td valign="top" class="value ${hasErrors(bean: videoDataSet, field: 'startDate', 'errors')}">
              <g:datePicker name="startDate" value="${videoDataSet?.startDate}"></g:datePicker>
            </td>
          </tr>

          <tr class="prop">
            <td valign="top" class="name">
              <label for="endDate">End Date:</label>
            </td>
            <td valign="top" class="value ${hasErrors(bean: videoDataSet, field: 'endDate', 'errors')}">
              <g:datePicker name="endDate" value="${videoDataSet?.endDate}"></g:datePicker>
            </td>
          </tr>
--%>
        
          <tr class="prop">
            <td valign="top" class="name">
              <label for="fileObjects">File Objects:</label>
            </td>
            <td valign="top" class="value ${hasErrors(bean: videoDataSet, field: 'fileObjects', 'errors')}">

              <ul>
                <g:each var="f" in="${videoDataSet?.fileObjects?}">
                  <li><g:link controller="videoFile" action="show" id="${f.id}">${f?.encodeAsHTML()}</g:link></li>
                </g:each>
              </ul>
              <g:link controller="videoFile" params="['videoDataSet.id':videoDataSet?.id]" action="create">Add VideoFile</g:link>

            </td>
          </tr>

          <tr class="prop">
            <td valign="top" class="name">
              <label for="repository">Repository:</label>
            </td>
            <td valign="top" class="value ${hasErrors(bean: videoDataSet, field: 'repository', 'errors')}">
              <g:select optionKey="id" from="${Repository.list()}" name="repository.id" value="${videoDataSet?.repository?.id}"></g:select>
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
</body>
</html>
