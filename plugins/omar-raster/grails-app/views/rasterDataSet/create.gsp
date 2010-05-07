<%@ page import="org.ossim.omar.Repository" %>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="main5"/>
  <title>Create RasterDataSet</title>
</head>
<body>
<div class="nav">
  <span class="menuButton">
	<g:link class="home" controller="home">Home</g:link>
  </span>
  <span class="menuButton"><g:link class="list" action="list">RasterDataSet List</g:link></span>
</div>
<div class="body">
  <h1>Create RasterDataSet</h1>
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
</body>
</html>
