<head>
<title><g:message code='spring.security.ui.resetPassword.title' />
</title>
<meta name='layout' content='generatedViews' />
</head>

<body>
<content tag="content">
    <p />

    <div class="nav">
        <span class="menuButton"><a class="home"
            href="${createLink(uri: '/')}">Home</a> </span>
    </div>

    <div class="body">

        <g:if test="${flash.message}">
            <div class="message">
                ${flash.message}
            </div>
        </g:if>
        <g:hasErrors bean="${command}">
            <div class="errors">
                <g:renderErrors bean="${command}" as="list" />
            </div>
        </g:hasErrors>

        <div class="dialog">

            <g:form action='resetPassword' name='resetPasswordForm'
                autocomplete='off'>

                <g:hiddenField name='t' value='${token}' />

                <div class="sign-in">
                    <br />
                    <h1>
                        <g:message code='spring.security.ui.resetPassword.description' />
                    </h1>

                    <table>

                        <tr class="prop">
                            <td valign="top" class="name"><label for="password"><g:message
                                        code="resetPasswordCommand.password.label" default="Password" />
                            </label>
                            </td>
                            <td valign="top"
                                class="value ${hasErrors(bean: command, field: 'password', 'errors')}">
                                <g:passwordField name='password'
                                    labelCode='resetPasswordCommand.password.label'
                                    bean="${command}" size='40' labelCodeDefault='Password'
                                    value="${command.password}" />
                            </td>
                        </tr>

                        <tr class="prop">
                            <td valign="top" class="name"><label for="password2"><g:message
                                        code="resetPasswordCommand.password2.label"
                                        default="Password (again)" /> </label>
                            </td>
                            <td valign="top"
                                class="value ${hasErrors(bean: command, field: 'password2', 'errors')}">
                                <g:passwordField name='password2'
                                    labelCode='resetPasswordCommand.password2.label'
                                    bean="${command}" size='40' labelCodeDefault='Password (again)'
                                    value="${command.password2}" />
                            </td>
                        </tr>



                    </table>

                    <div class="buttons">
                        <span class="button"> <g:submitButton name='reset'
                                class='save' form='resetPasswordForm'
                                value="${message(code:'spring.security.ui.resetPassword.submit')}" />
                        </span>
                    </div>
                </div>
            </g:form>
        </div>
    </div>
</content>
</body>
