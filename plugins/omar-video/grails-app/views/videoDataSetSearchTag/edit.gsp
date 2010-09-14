
<%@ page import="org.ossim.omar.VideoDataSetSearchTag" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'videoDataSetSearchTag.label', default: 'VideoDataSetSearchTag')}" />
        <title>Edit VideoDataSetSearchTag</title>
    </head>
    <body>
    <content tag="content">
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLink(uri: '/')}">OMAR Home</a></span>
            <span class="menuButton"><g:link class="list" action="list">List Search Tags</g:link></span>
            <span class="menuButton"><g:link class="create" action="create">Create Search Tag</g:link></span>
        </div>
        <div class="body">
            <h1><g:message code="default.edit.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <g:hasErrors bean="${videoDataSetSearchTagInstance}">
            <div class="errors">
                <g:renderErrors bean="${videoDataSetSearchTagInstance}" as="list" />
            </div>
            </g:hasErrors>
            <g:form method="post" >
                <g:hiddenField name="id" value="${videoDataSetSearchTagInstance?.id}" />
                <g:hiddenField name="version" value="${videoDataSetSearchTagInstance?.version}" />
                <div class="dialog">
                    <table>
                        <tbody>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="name"><g:message code="videoDataSetSearchTag.name.label" default="Name" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: videoDataSetSearchTagInstance, field: 'name', 'errors')}">
                                    <g:textField name="name" value="${videoDataSetSearchTagInstance?.name}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="description"><g:message code="videoDataSetSearchTag.description.label" default="Description" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: videoDataSetSearchTagInstance, field: 'description', 'errors')}">
                                    <g:textField name="description" value="${videoDataSetSearchTagInstance?.description}" />
                                </td>
                            </tr>
                        
                        </tbody>
                    </table>
                </div>
                <div class="buttons">
                    <span class="button"><g:actionSubmit class="save" action="update" value="${message(code: 'default.button.update.label', default: 'Update')}" /></span>
                    <span class="button"><g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" /></span>
                </div>
            </g:form>
        </div>
      </content>
    </body>
</html>
