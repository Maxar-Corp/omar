<head>
  <title><g:message code='spring.security.ui.forgotPassword.title'/>
  </title>
  <meta name='layout' content='generatedViews'/>
</head>

<body>
<content tag="content">
  <div class="nav">
    <ul>
      <li><a class="home" href="${createLink( uri: '/' )}">Home</a></li>
    </ul>
  </div>

  <div class="body">

    <g:if test="${flash.message}">
      <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${command}">
      <div class="errors">
        <g:renderErrors bean="${command}" as="list"/>
      </div>
    </g:hasErrors>

    <p/>

    <g:form action='forgotPassword' name="forgotPasswordForm"
            autocomplete='off'>

      <g:if test='${emailSent}'>
        <br/>

        <h1>
          <g:message code='spring.security.ui.forgotPassword.sent'/>
        </h1>
      </g:if>

      <g:else>

        <br/>

        <h1>
          <g:message code='spring.security.ui.forgotPassword.description'/>
        </h1>

        <div class="dialog">

          <table>
            <tr class="prop">
              <td valign="top" class="name"><label for="username"><g:message
                  code='spring.security.ui.forgotPassword.username'/></label></td>
              <td><g:textField name="username" size="25"/>
              </td>
            </tr>
          </table>
        </div>

        <div class="buttons">
          <span class="button">
            <g:submitButton name='Reset' class="save" form='forgotPasswordForm'
                            value="${message( code: 'spring.security.ui.forgotPassword.submit' )}"/>
          </span>
        </div>
      </g:else>

    </g:form>
  </div>
</content>
</body>
