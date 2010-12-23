<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="generatedViews"/>
  <title>OMAR: Edit User ${person.id}</title>
</head>
<body>
<content tag="content">
  <div class="nav">
    <span class="menuButton"><g:link class="home" uri="/">OMAR™ Home</g:link></span>
  </div>
  <div class="body">
    <h1>Edit ${loggedInUserInfo(field: 'username')}'s Preferences</h1>
    <g:if test="${flash.message}">
      <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${person}">
      <div class="errors">
        <g:renderErrors bean="${person}" as="list"/>
      </div>
    </g:hasErrors>
    <g:form method="post">
      <input type="hidden" name="id" value="${person?.id}"/>
      <div class="dialog">
        <table>
          <tbody>
          <tr class="prop">
            <td valign="top" class="name">
              <label for="userRealName">Full Name:</label>
            </td>
            <td valign="top" class="value ${hasErrors(bean: person, field: 'userRealName', 'errors')}">
              <input type="text" id="userRealName" name="userRealName" value="${person?.userRealName?.encodeAsHTML()}"/>
            </td>
          </tr>
          <tr class="prop">
            <td valign="top" class="name">
              <label for="passwd">Password:</label>
            </td>
            <td valign="top" class="value ${hasErrors(bean: person, field: 'passwd', 'errors')}">
              <input type="password" id="passwd" name="passwd" value="${person?.passwd?.encodeAsHTML()}"/>
            </td>
          </tr>
          <tr class="prop">
            <td valign="top" class="name">
              <label for="verifypasswd">Verify Password:</label>
            </td>
            <td valign="top" class="value ${hasErrors(bean: person, field: 'passwd', 'errors')}">
              <input type="password" id="verifypasswd" name="verifypasswd" value="${person?.passwd?.encodeAsHTML()}"/>
            </td>
          </tr>
          <tr class="prop">
            <td valign="top" class="name">
              <label for="description">Description:</label>
            </td>
            <td valign="top" class="value ${hasErrors(bean: person, field: 'description', 'errors')}">
              <input type="text" id="description" name="description" value="${person?.description?.encodeAsHTML()}"/>
            </td>
          </tr>
          <tr class="prop">
            <td valign="top" class="name">
              <label for="email">Email:</label>
            </td>
            <td valign="top" class="value ${hasErrors(bean: person, field: 'email', 'errors')}">
              <input type="text" id="email" name="email" value="${person?.email?.encodeAsHTML()}"/>
            </td>
          </tr>
          <tr class="prop">
            <td valign="top" class="name">
              <label for="emailShow">Show Email:</label>
            </td>
            <td valign="top" class="value ${hasErrors(bean: person, field: 'emailShow', 'errors')}">
              <g:checkBox name="emailShow" value="${person?.emailShow}"/>
            </td>
          </tr>
          </tbody>
        </table>
      </div>
      <div class="buttons">
        <span class="button"><g:actionSubmit class="save" value="Update"/></span>
      </div>
    </g:form>
  </div>
</content>
</body>
</html>