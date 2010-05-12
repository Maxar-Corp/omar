<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="main"/>
  <g:set var="entityName" value="${message(code: 'city.label', default: 'City')}"/>
  <title><g:message code="default.create.label" args="[entityName]"/></title>
  <link rel="stylesheet" href="${resource(dir: 'css', file: 'formLayout.css')}"/>
</head>
<body>
<div class="nav">
  <span class="menuButton"><g:link class="home" uri="/">Home</g:link></span>
  <span class="menuButton"><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]"/></g:link></span>
</div>
<div class="body">
  <h1><g:message code="default.create.label" args="[entityName]"/></h1>
  <g:if test="${flash.message}">
    <div class="message">${flash.message}</div>
  </g:if>
  <g:hasErrors bean="${cityInstance}">
    <div class="errors">
      <g:renderErrors bean="${cityInstance}" as="list"/>
    </div>
  </g:hasErrors>
  <g:form action="save" method="post">
    <div class="formLayout">
      <bean:withBean beanName="cityInstance">
        <bean:field property="name"/>
        <bean:field property="population"/>
        <bean:field property="capital"/>
        <bean:field property="latitude"/>
        <bean:field property="longitude"/>
        <bean:select property="country" from="${Country.list().sort { it.name }}" optionKey="id" optionValue="name"/>
      </bean:withBean>
    </div>
    <div class="buttons">
      <span class="button"><g:submitButton name="create" class="save" value="${message(code: 'default.button.create.label', default: 'Create')}"/></span>
    </div>
  </g:form>
</div>
</body>
</html>
