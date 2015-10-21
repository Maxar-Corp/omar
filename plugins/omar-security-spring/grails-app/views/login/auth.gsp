<html>
<head>
    <meta http-equiv='Content-Type' content='text/html; charset=UTF-8'/>
    <meta name='layout' content='loginPageLayout'/>
    <title>OMAR <g:meta name="app.version"/>: Login</title>
    <asset:stylesheet src="loginPage.css"/>
</head>

<body class=" yui-skin-sam">

<content tag="top">
    <div align="center">
        <g:link class="home" uri="/">
            <asset:image src="omarLogo.png" alt="OMAR Logo"/>
        </g:link>
    </div>
</content>

<content tag="center">
    <div align="center"><a href="${createLink( view: "login", action: "about" )}">About OMAR</a></div>

    <div id='login'>
        <div class='inner'>
            <g:if test='${flash.message}'>
                <div class='login_message'>${flash.message}</div>
            </g:if>
            <div class='fheader'>Please Login...</div>

            <form action='${request.contextPath}/j_spring_security_check' method='POST' id='loginForm' class='cssform'>
                <p>
                    <label for='j_username'>Login ID</label>
                    <input autocorrect='off' type='text' class='text_' name='j_username' id='j_username'/>
                </p>

                <p>
                    <label for='j_password'>Password</label>
                    <input type='password' class='text_' name='j_password' id='j_password'/>
                </p>

                <p>
                    <label for='remember_me'>Remember me</label>
                    <input type='checkbox' class='chk' id='remember_me' name='_spring_security_remember_me'/>
                </p>

                <p>
                    <input type='submit' value='Login'/>
                </p>
            </form>
        </div>
        <g:if test="${grailsApplication.config.login.registration.enabled}">
            <g:link controller="register" action="index">Click here to register</g:link>
            <br>
        </g:if>
        <g:link controller="register" action="forgotPassword">Forgot Password</g:link>
    </div>
</content>
<asset:javascript src="loginPage.js"/>
</body>
</html>
