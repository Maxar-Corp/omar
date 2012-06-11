<%@ page import="org.ossim.omar.core.Repository; org.ossim.omar.core.Repository" %>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="generatedViews"/>
  <title>OMAR: Create Video Data Set</title>
</head>
<body>
<content tag="content">
  <div class="nav">
    <ul>
    <li><g:link class="home" uri="/">OMAR™ Home</g:link></li>
    <li><g:link class="list" action="list">Video Data Set List</g:link></li>
    </ul>
  </div>
  <div class="body">
    <h1>OMAR: Create Video Data Set</h1>
    <g:if test="${flash.message}">
      <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${videoDataSet}">
      <div class="errors">
        <g:renderErrors bean="${videoDataSet}" as="list"/>
      </div>
    </g:hasErrors>
    <g:form action="save" method="post">
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
              <label for="groundGeom">Ground Geom:</label>
            </td>
            <td valign="top" class="value ${hasErrors(bean: videoDataSet, field: 'groundGeom', 'errors')}">
              <input type="text" id="groundGeom" name="groundGeom" value="${fieldValue(bean: videoDataSet, field: 'groundGeom')}">
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
              <label for="repository">Repository:</label>
            </td>
            <td valign="top" class="value ${hasErrors(bean: videoDataSet, field: 'repository', 'errors')}">
              <g:select optionKey="id" from="${org.ossim.omar.core.Repository.list()}" name="repository.id" value="${videoDataSet?.repository?.id}"></g:select>
            </td>
          </tr>
          </tbody>
        </table>
      </div>
      <div class="buttons">
        <span class="button"><input class="save" type="submit" value="Create"/></span>
      </div>
    </g:form>
  </div>
</content>
</body>
</html>
