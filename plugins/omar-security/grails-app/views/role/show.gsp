<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="generatedViews"/>
  <title>OMAR: Show Role ${authority.id}</title>
</head>
<body>
<content tag="content">
  <div class="nav">
    <span class="menuButton"><g:link class="home" uri="/">OMAR™ Home</g:link></span>
	<span class="menuButton"><g:link class="list" action="list">Role List</g:link></span>
	<span class="menuButton"><g:link class="create" action="create">Create Role</g:link></span>
  </div>
  <div class="body">
    <h1>OMAR: Show Role ${authority.id}</h1>
    <g:if test="${flash.message}">
      <div class="message">${flash.message}</div>
    </g:if>
    <div class="dialog">
      <table>
        <tbody>
        <tr class="prop">
          <td valign="top" class="name">Id:</td>
		  <td valign="top" class="value">${authority.id}</td>
		</tr>
        <tr class="prop">
		  <td valign="top" class="name">Role Name:</td>
		  <td valign="top" class="value">${authority.authority.substring(5).toLowerCase()}</td>
		</tr>
        <tr class="prop">
		  <td valign="top" class="name">Description:</td>
		  <td valign="top" class="value">${authority.description}</td>
		</tr>
        <tr class="prop">
		  <td valign="top" class="name">People:</td>
		  <td valign="top" class="value">${authority.people}</td>
		</tr>
        </tbody>
      </table>
    </div>
    <div class="buttons">
      <g:form>
        <input type="hidden" name="id" value="${authority?.id}"/>
		<span class="button"><g:actionSubmit class="edit" value="Edit"/></span>
		<span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
	  </g:form>
    </div>
  </div>
</content>
</body>
</html>