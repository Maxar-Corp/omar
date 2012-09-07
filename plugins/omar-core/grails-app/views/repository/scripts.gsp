<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="generatedViews"/>
  <title>OMAR: Scripts</title>
</head>
<body>
<content tag="content">
  <div class="nav">
      <ul>
          <li class="menuButton"><g:link class="home" uri="/">OMAR™ Home</g:link></li>
          <li class="menuButton"><g:link class="create" action="create">Create Repository</g:link></li>
          <li class="menuButton"><g:link class="list" action="list">Repository List</g:link></li>
      </ul>
  </div>
  <div class="body">
    <h1>OMAR: Staging Scripts</h1>
    <g:if test="${flash.message}">
      <div class="message">${flash.message}</div>
    </g:if>
    
    <g:form controller="RunScript" action="indexFiles" method="post">
      omarRunScript.sh indexFiles <g:textField name="path" value="${myValue}" />  <span class="button"><input type="submit" value="Index Files" /></span>
    </g:form>

    <p>&nbsp;<p>
    
    <g:form controller="RunScript" action="removeRaster" method="post">
      omarRunScript.sh removeRaster <g:textField name="path" value="${myValue}" />  <span class="button"><input type="submit" value="Remove Raster" /></span>
    </g:form>

    <p>&nbsp;<p>

    <g:form controller="RunScript" action="stageRaster" method="post">
      omarRunScript.sh stageRaster <g:textField name="path" value="${myValue}" />  <span class="button"><input type="submit" value="Stage Raster" /></span>
    </g:form>

    <p>&nbsp;<p>

    <g:form controller="RunScript" action="synchFiles" method="post">
      omarRunScript.sh synchFiles <span class="button"><input type="submit" value="Synch Files" /></span>
    </g:form>

    <p>&nbsp;<p>
    
    <g:form controller="RunScript" action="clearCache" method="post">
      omarRunScript.sh clearCache <span class="button"><input type="submit" value="Clear Cache" /></span>
    </g:form>
  </div>
</content>
</body>
</html>