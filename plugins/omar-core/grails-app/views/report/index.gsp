<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="generatedViews"/>
</head>
<body>
<content tag="content">
    <omar:logout/>
    <div class="nav">
      <ul>
          <li class="menuButton"><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>

      </ul>
  </div>
  <div class="body">
    <h1><g:message code="default.create.label" args="[entityName]"/></h1>
    <g:if test="${flash.message}">
      <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${reportInstance}">
      <div class="errors">
        <g:renderErrors bean="${reportInstance}" as="list"/>
      </div>
    </g:hasErrors>
    <g:form action="sendMail">
      <div class="dialog">
        <table>
          <tbody>

          <tr class="prop">
            <td valign="top" class="name">
              <label for="emailTo">To</label>
            </td>
            <td valign="top" class="value ${hasErrors(bean: reportInstance, field: 'emailTo', 'errors')}">
              <g:textField name="emailTo" value="${reportInstance?.emailTo}"/>
            </td>
          </tr>
          <tr class="prop">
            <td valign="top" class="name">
              <label for="subject">Subject</label>
            </td>
            <td valign="top" class="value ${hasErrors(bean: reportInstance, field: 'subject', 'errors')}">
              <g:textField name="subject" value="${reportInstance?.subject}"/>
            </td>
          </tr>
          <tr class="prop">
            <td valign="top" class="name">
              <label for="name">Your name</label>
            </td>
            <td valign="top" class="value ${hasErrors(bean: reportInstance, field: 'name', 'errors')}">
              <g:textField name="name" value="${reportInstance?.name}"/>
            </td>
          </tr>
          <tr>
            <td valign="top" class="name">
              <label for="emailFrom">From</label>
            </td>
            <td valign="top" class="value ${hasErrors(bean: reportInstance, field: 'emailFrom', 'errors')}">
              <g:textField name="emailFrom" value="${reportInstance?.emailFrom}"/>
            </td>
          </tr>

          <tr class="prop">
            <td valign="top" class="name">
              <label for="phone">Phone</label>
            </td>
            <td valign="top" class="value ${hasErrors(bean: reportInstance, field: 'phone', 'errors')}">
              <g:textField name="phone" value="${reportInstance?.phone}"/>
            </td>
          </tr>

          <tr class="prop">
            <td valign="top" class="name">
              <label for="report">Report</label>
            </td>
            <td valign="top" class="value ${hasErrors(bean: reportInstance, field: 'report', 'errors')}">
              <g:textArea name="report" cols="40" rows="5" value="${reportInstance?.report}"/>
            </td>
          </tr>

          <%--
          <tr class="prop">
            <td valign="top" class="name">
              <label for="status"><g:message code="report.status.label" default="Status"/></label>
            </td>
            <td valign="top" class="value ${hasErrors(bean: reportInstance, field: 'status', 'errors')}">
              <g:textField name="status" value="${reportInstance?.status}"/>
            </td>
          </tr>
          --%>

          <g:hiddenField name="status" value="NEW"/>

          </tbody>
        </table>
      </div>
      <div class="buttons">
        <span class="button"><g:submitButton name="sendEmail" class="save" value="Send email"/></span>
      </div>
    </g:form>
  </div>
</content>
</body>
</html>
