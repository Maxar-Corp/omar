<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="generatedViews"/>
  <title>OMAR: Create Repository</title>
</head>
<body>
<content tag="content">
  <div class="nav">
      <ul>
          <li class="menuButton"><g:link class="home" uri="/">OMAR™ Home</g:link></li>
          <li class="menuButton"><g:link class="list" action="list">Repository List</g:link></li>
          <li class="menuButton"><g:link controller="RunScript" action="scripts">Scripts</g:link></li>
      </ul>
  </div>
  <div class="body">
    <h1>OMAR: Create Repository</h1>
    <g:if test="${flash.message}">
      <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${repository}">
      <div class="errors">
        <g:renderErrors bean="${repository}" as="list"/>
      </div>
    </g:hasErrors>
    <g:form action="save" method="post">
      <div class="dialog">
        <table>
          <tbody>
          <tr class="prop">
            <td valign="top" class="name">
              <label for="baseDir">Base Dir:</label>
            </td>
            <td valign="top" class="value ${hasErrors(bean: repository, field: 'baseDir', 'errors')}">
              <input type="text" id="baseDir" name="baseDir" value="${fieldValue(bean: repository, field: 'baseDir')}" size="128"/>
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