<html>
<head>
  <meta http-equiv='Content-Type' content='text/html; charset=UTF-8'/>
  <meta name='layout' content='singleColumn'/>
  <title>OMAR <g:meta name="app.version"/>: Login</title>
  <style type='text/css' media='screen'>
  #login {
    margin: 15px 0px;
    padding: 0px;
    text-align: center;
  }

  #login .inner {
    width: 260px;
    margin: 0px auto;
    text-align: left;
    padding: 10px;
    border-top: 1px dashed #499ede;
    border-bottom: 1px dashed #499ede;
    background-color: #EEF;
  }

  #login .inner .fheader {
    padding: 4px;
    margin: 3px 0px 3px 0;
    color: #2e3741;
    font-size: 14px;
    font-weight: bold;
  }

  #login .inner .cssform p {
    clear: left;
    margin: 0;
    padding: 5px 0 8px 0;
    padding-left: 105px;
    border-top: 1px dashed gray;
    margin-bottom: 10px;
    height: 1%;
  }

  #login .inner .cssform input[type='text'] {
    width: 120px;
  }

  #login .inner .cssform label {
    font-weight: bold;
    float: left;
    margin-left: -105px;
    width: 100px;
  }

  #login .inner .login_message {
    color: red;
  }

  #login .inner .text_ {
    width: 120px;
  }

  #login .inner .chk {
    height: 12px;
  }
  </style>
</head>
<body onload="init();">

<content tag="top">
    <div align="center">
      <g:link class="home" uri="/"><img src="${resource(plugin: 'omar', dir: 'images', file: 'omarLogo.png')}" alt="OMAR Logo"/></g:link>
    </div>
</content>

<content tag="center">
  <div align="center"><a href="${createLink(view: "login", action: "about")}">About OMAR</a></div>
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
<g:javascript>
  function init()
  {
    document.forms['loginForm'].elements['j_username'].focus();
  }
</g:javascript>
</body>
</html>
