<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="main"/>
  <title>Show VideoFile</title>
</head>
<body>
<content tag="content">
<div class="nav">
  <span class="menuButton"><g:link class="home" uri="/">OMAR Home</g:link></span>
  <span class="menuButton"><g:link class="list" action="list">VideoFile List</g:link></span>
  <g:ifAllGranted role="ROLE_ADMIN">
    <span class="menuButton"><g:link class="create" action="create">New VideoFile</g:link></span>
  </g:ifAllGranted>
</div>
<div class="body">
  <h1>Show VideoFile</h1>
  <g:if test="${flash.message}">
    <div class="message">${flash.message}</div>
  </g:if>
  <div class="dialog">
    <table>
      <tbody>

        <tr class="prop">
          <td valign="top" class="name">Id:</td>

          <td valign="top" class="value">${fieldValue(bean: videoFile, field: 'id')}</td>

        </tr>

        <tr class="prop">
          <td valign="top" class="name">Name:</td>

          <td valign="top" class="value">${fieldValue(bean: videoFile, field: 'name')}</td>

        </tr>

        <tr class="prop">
          <td valign="top" class="name">Type:</td>

          <td valign="top" class="value">${fieldValue(bean: videoFile, field: 'type')}</td>

        </tr>

        <tr class="prop">
          <td valign="top" class="name">Video Data Set:</td>

          <td valign="top" class="value"><g:link controller="videoDataSet" action="show" id="${videoFile?.videoDataSet?.id}">${videoFile?.videoDataSet?.encodeAsHTML()}</g:link></td>

        </tr>

      </tbody>
    </table>
  </div>
  <div class="buttons">
    <g:form>
      <input type="hidden" name="id" value="${videoFile?.id}"/>
      <g:ifAllGranted role="ROLE_ADMIN">
        <span class="button"><g:actionSubmit class="edit" value="Edit"/></span>
        <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
      </g:ifAllGranted>
    </g:form>
  </div>
</div>
  </content>
</body>
</html>
