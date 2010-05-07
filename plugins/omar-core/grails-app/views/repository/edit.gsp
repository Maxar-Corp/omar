<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="main5"/>
  <title>Edit Repository</title>
</head>
<body>
<div class="nav">
  <span class="menuButton"><a class="home" href="${resource(dir: '')}">Home</a></span>
  <span class="menuButton"><g:link class="list" action="list">Repository List</g:link></span>
  <span class="menuButton"><g:link class="create" action="create">New Repository</g:link></span>
</div>
<div class="body">
  <h1>Edit Repository</h1>
  <g:if test="${flash.message}">
    <div class="message">${flash.message}</div>
  </g:if>
  <g:hasErrors bean="${repository}">
    <div class="errors">
      <g:renderErrors bean="${repository}" as="list"/>
    </div>
  </g:hasErrors>
  <g:form method="post">
    <input type="hidden" name="id" value="${repository?.id}"/>
    <div class="dialog">
      <table>
        <tbody>

          <tr class="prop">
            <td valign="top" class="name">
              <label for="baseDir">Base Dir:</label>
            </td>
            <td valign="top" class="value ${hasErrors(bean: repository, field: 'baseDir', 'errors')}">
              <input type="text" id="baseDir" name="baseDir" value="${fieldValue(bean: repository, field: 'baseDir')}" size="128"/>
            </td>
          </tr>

          <%--
          <tr class="prop">
            <td valign="top" class="name">
              <label for="rasterDataSets">Raster Data Sets:</label>
            </td>
            <td valign="top" class="value ${hasErrors(bean: repository, field: 'rasterDataSets', 'errors')}">

              <ul>
                <g:each var="r" in="${repository?.rasterDataSets?}">
                  <li><g:link controller="rasterDataSet" action="show" id="${r.id}">${r?.encodeAsHTML()}</g:link></li>
                </g:each>
              </ul>
              <g:link controller="rasterDataSet" params="['repository.id':repository?.id]" action="create">Add RasterDataSet</g:link>

            </td>
          </tr>

          <tr class="prop">
            <td valign="top" class="name">
              <label for="videoDataSets">Video Data Sets:</label>
            </td>
            <td valign="top" class="value ${hasErrors(bean: repository, field: 'videoDataSets', 'errors')}">

              <ul>
                <g:each var="v" in="${repository?.videoDataSets?}">
                  <li><g:link controller="videoDataSet" action="show" id="${v.id}">${v?.encodeAsHTML()}</g:link></li>
                </g:each>
              </ul>
              <g:link controller="videoDataSet" params="['repository.id':repository?.id]" action="create">Add VideoDataSet</g:link>

            </td>
          </tr>
          --%>
        </tbody>
      </table>
    </div>
    <div class="buttons">
      <span class="button"><g:actionSubmit class="save" value="Update"/></span>
      <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
    </div>
  </g:form>
</div>
</body>
</html>
