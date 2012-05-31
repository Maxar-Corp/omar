<%@ page import="org.ossim.omar.core.Repository" %>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="generatedViews"/>
  <title>OMAR: Create Raster Set</title>
</head>
<body>
<content tag="content">
  <div class="nav">
      <ul>
          <li class="menuButton"><g:link class="home" uri="/">OMAR™ Home</g:link></li>
          <li class="menuButton"><g:link class="list" action="list">Raster Set List</g:link></li>
      </ul>
  </div>
  <div class="body">
    <h1>OMAR: Create Raster Set</h1>
    <g:if test="${flash.message}">
      <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${rasterDataSet}">
      <div class="errors">
        <g:renderErrors bean="${rasterDataSet}" as="list"/>
      </div>
    </g:hasErrors>
    <g:form action="save" method="post">
      <div class="dialog">
        <table>
          <tbody>
          <tr class="prop">
            <td valign="top" class="name">
              <label for="repository">Repository:</label>
            </td>
            <td valign="top" class="value ${hasErrors(bean: rasterDataSet, field: 'repository', 'errors')}">
              <g:select optionKey="id" from="${Repository.list()}" name="repository.id" value="${rasterDataSet?.repository?.id}"></g:select>
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