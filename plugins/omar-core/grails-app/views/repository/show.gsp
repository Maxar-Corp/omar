<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="generatedViews"/>
  <title>OMAR: Show Repository ${fieldValue( bean: repository, field: 'id' )}</title>
</head>

<body>
<content tag="content">
  <div class="nav">
    <ul>
      <li class="menuButton"><g:link class="home" uri="/">OMAR™ Home</g:link></li>
      <li class="menuButton"><g:link class="list" action="list">Repository List</g:link></li>
      <sec:ifAllGranted roles="ROLE_ADMIN">
        <li class="menuButton"><g:link class="create" action="create">Create Repository</g:link></li>
        <li class="menuButton"><g:link controller="RunScript" action="scripts">Scripts</g:link></li>
      </sec:ifAllGranted>
    </ul>
  </div>

  <div class="body">
    <h1>OMAR: Show Repository ${fieldValue( bean: repository, field: 'id' )}</h1>
    <g:if test="${flash.message}">
      <div class="message">${flash.message}</div>
    </g:if>
    <div class="dialog">
      <table>
        <tbody>
        <tr class="prop">
          <td valign="top" class="name">Id:</td>
          <td valign="top" class="value">${fieldValue( bean: repository, field: 'id' )}</td>
        </tr>
        <tr class="prop">
          <td valign="top" class="name">Base Dir:</td>
          <td valign="top" class="value">${fieldValue( bean: repository, field: 'baseDir' )}</td>
        </tr>
        <tr class="prop">
          <td valign="top" class="name">Scan Start Date:</td>
          <td valign="top" class="value">
            <g:formatDate date="${repository?.scanStartDate}"/>
          </td>
        </tr>
        <tr class="prop">
          <td valign="top" class="name">Scan End Date:</td>
          <td valign="top" class="value">
            <g:formatDate date="${repository?.scanEndDate}"/>
          </td>
        </tr>
        </tbody>
      </table>
    </div>

    <div class="buttons">
      <g:form>
        <input type="hidden" name="id" value="${repository?.id}"/>
        <sec:ifAllGranted roles="ROLE_ADMIN">
          <span class="button"><g:actionSubmit class="edit" value="Edit"/></span>
          <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');"
                                               value="Delete"/></span>
          <span class="menuButton"><g:link action="runStager" id="${repository?.id}">Run Stager</g:link></span>
        </sec:ifAllGranted>
      </g:form>
    </div>
  </div>
</content>
</body>
</html>
