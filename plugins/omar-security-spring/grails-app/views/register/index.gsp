<head>
  <title><g:message code='spring.security.ui.register.title'/></title>
  <meta name="layout" content="generatedViews"/>

</head>

<body>
<content tag="content">
  <p/>

  <div class="nav">
    <ul><li><a class="home" href="${createLink( uri: '/' )}">Home</a></li></ul>
  </div>

  <div class="body">

    <g:form action='register' name='registerForm'>

      <g:if test='${emailSent}'>
        <br/>

        <h1>Account created:</h1>
        <g:message code='spring.security.ui.register.sent'/>
      </g:if>
      <g:else>
        <h1>Create a new account:</h1>
        <g:if test="${flash.message}">
          <div class="message">${flash.message}</div>
        </g:if>
        <g:hasErrors bean="${command}">
          <div class="errors">
            <g:renderErrors bean="${command}" as="list"/>
          </div>
        </g:hasErrors>

        <br/>

        <div class="dialog">

          <table>
            <tbody>

            <tr class="prop">
              <td valign="top" class="name">
                <label for="username"><g:message code="user.username.label" default="Username"/></label>
              </td>
              <td valign="top" class="value ${hasErrors( bean: command, field: 'username', 'errors' )}">

                <g:textField name='username' labelCode='user.username.label' bean="${command}"
                             size='40' labelCodeDefault='Username' value="${command.username}"/>
              </td>
            </tr>

            <tr class="prop">
              <td valign="top" class="name">
                <label for="userRealName"><g:message code="user.userRealName.label" default="Full Name"/></label>
              </td>
              <td valign="top" class="value ${hasErrors( bean: command, field: 'userRealName', 'errors' )}">

                <g:textField name='userRealName' labelCode='user.userRealName.label' bean="${command}"
                             size='40' labelCodeDefault='Full Name' value="${command.userRealName}"/>
              </td>
            </tr>

            <tr class="prop">
              <td valign="top" class="name">
                <label for="organization"><g:message code="user.organization.label" default="Organization"/></label>
              </td>
              <td valign="top" class="value ${hasErrors( bean: command, field: 'organization', 'errors' )}">
                <g:textField name='organization' bean="${command}" value="${command.organization}"
                             size='40' labelCode='user.organization.label' labelCodeDefault='Organization'/>
              </td>
            </tr>

            <tr class="prop">
              <td valign="top" class="name">
                <label for="phoneNumber"><g:message code="user.phoneNumber.label" default="Phone Number"/></label>
              </td>
              <td valign="top" class="value ${hasErrors( bean: command, field: 'phoneNumber', 'errors' )}">

                <g:textField name='phoneNumber' labelCode='user.phoneNumber.label' bean="${command}"
                             size='40' labelCodeDefault='Phone Number' value="${command.phoneNumber}"/>
              </td>
            </tr>

            <tr class="prop">
              <td valign="top" class="name">
                <label for="email"><g:message code="user.email.label" default="E-Mail"/></label>
              </td>
              <td valign="top" class="value ${hasErrors( bean: command, field: 'email', 'errors' )}">
                <g:textField name='email' bean="${command}" value="${command.email}"
                             size='40' labelCode='user.email.label' labelCodeDefault='E-mail'/>
              </td>
            </tr>

            <tr class="prop">
              <td valign="top" class="name">
                <label for="email2"><g:message code="user.email2.label" default="E-mail (again)"/></label>
              </td>
              <td valign="top" class="value ${hasErrors( bean: command, field: 'email2', 'errors' )}">
                <g:textField name='email2' labelCode='user.email2.label' bean="${command}"
                             size='40' labelCodeDefault='E-mail (again)' value="${command.email2}"/>
              </td>
            </tr>


            <tr class="prop">
              <td valign="top" class="name">
                <label for="password"><g:message code="user.password.label" default="Password"/></label>
              </td>
              <td valign="top" class="value ${hasErrors( bean: command, field: 'password', 'errors' )}">
                <g:passwordField name='password' labelCode='user.password.label' bean="${command}"
                                 size='40' labelCodeDefault='Password' value="${command.password}"/>
              </td>
            </tr>

            <tr class="prop">
              <td valign="top" class="name">
                <label for="password2"><g:message code="user.password2.label" default="Password (again)"/></label>
              </td>
              <td valign="top" class="value ${hasErrors( bean: command, field: 'password2', 'errors' )}">
                <g:passwordField name='password2' labelCode='user.password2.label' bean="${command}"
                                 size='40' labelCodeDefault='Password (again)' value="${command.password2}"/>
              </td>
            </tr>

            </tbody>
          </table>

        </div>

        <div class="buttons">
          <span class="button">
            <g:submitButton name='Create' class="save"
                            value='${message( code: 'spring.security.ui.register.submit', default: 'Create' )}'/>
          </span>
        </div>

      </g:else>

    </g:form>
  </div>
</content>
</body>
