<html>
<head>
  <title>Welcome to OMAR 2.0</title>
  <meta name="layout" content="main4"/>
</head>
<body>
<h1 style="margin-left:20px;font-size:200%;">Welcome to OMAR</h1>
<p style="margin-left:20px;width:80%">Please select an action from the following options:
</p>

<div>
  <h1 style="font-size:150%">Search:</h1>
  <table>
    <tr>
      <td width="120px">
        <img src="${resource(dir: 'images', file: 'discover.gif')}" alt="">
      </td>
      <td>
        <ol>
          <li><g:link controller="rasterEntry" action="search">Imagery</g:link></li>
          <li><g:link controller="videoDataSet" action="search">Video</g:link></li>
        </ol>
      </td>
    </tr>
  </table>
</div>
<g:if test="${grailsApplication.config.views?.home?.browseEnabled}">
  <div>
    <h1 style="font-size:150%">Browse:</h1>
    <table>
      <tr>
        <td width="120px">
          <img src="${resource(dir: 'images', file: 'globe_128.png')}" width="96" height="96" alt="">
        </td>
        <td>
          <ol>
            <li><g:link controller="rasterEntry" action="index">Imagery</g:link></li>
            <li><g:link controller="videoDataSet" action="index">Video</g:link></li>
          </ol>
        </td>
      </tr>
    </table>
  </div>
</g:if>

<div>
  <h1 style="font-size:150%">KML Network Links:</h1>
  <table>
    <tr>
      <td width="120px">
        <img src="${resource(dir: 'images', file: 'GoogleEarth_1.png')}" width="96 " height="96" alt="">
      </td>
      <td>
        <ol>
          <li>
            <g:form name="imageView" url="[controller:'kmlQuery', action:'topImages']">
              <g:textField name="maximages" size="2" value="${grailsApplication.config.kml.defaultImages}"/>
              <a href="javascript:submitImageView();">Most Recent Images for View</a>
            </g:form>
          </li>
          <li>
            <g:form name="videoView" url="[controller:'kmlQuery', action:'topVideos']">
              <g:textField name="maxvideos" size="2" value="${grailsApplication.config.kml.defaultVideos}"/>
              <a href="javascript:submitVideoView();">Most Recent Videos for View</a>
            </g:form>
          </li>
          <li>
            <g:form name="imageFootprints" url="[controller:'kmlQuery', action:'imageFootprints']">
              <g:textField name="imagedays" size="2" value="${grailsApplication.config.kml.daysCoverage}"/>
              <a href="javascript:submitImageCoverage();">Most Recent Days Imagery Coverage</a>
            </g:form>
          </li>
          <li>
            <g:form name="videoFootprints" url="[controller:'kmlQuery', action:'videoFootprints']">
              <g:textField name="videodays" size="2" value="${grailsApplication.config.kml.daysCoverage}"/>
              <a href="javascript:submitVideoCoverage();">Most Recent Days Video Coverage</a>
            </g:form>
          </li>
        </ol>
      </td>
    </tr>
  </table>
</div>

<div>
  <h1 style="font-size:150%">Report:</h1>
  <table>
    <tr>
      <td width="120px">
        <img src="${resource(dir: 'images', file: 'report.png')}" width="96 " height="96" alt="">
      </td>
      <td>
        <ol>
          <li>
            <g:link plugin="omar-core" controller="report" action="create">Create Report</g:link>
          </li>
          <g:ifAllGranted role="ROLE_ADMIN">
            <li>
              <g:link plugin="omar-core" controller="report" action="list">List Reports</g:link>
            </li>
          </g:ifAllGranted>
        </ol>
      </td>
    </tr>
  </table>
</div>

<div>
  <h1 style="font-size:150%">Logout</h1>
  <table>
    <tr>
      <td>
        <a href="${createLink(controller: "logout")}">
          <img src="${resource(dir: 'images', file: 'logout.png')}" width="96" height="96" alt="">
        </a>
      </td>
    </tr>
  </table>
</div>

<g:ifAllGranted role="ROLE_ADMIN">
  <div>
    <h1 style="font-size:150%">User Management:</h1>
    <table>
      <tr>
        <td width="120px">
          <img src="${resource(dir: 'images', file: 'use.gif')}" alt="">
        </td>
        <td>
          <ol>
            <li><g:link controller="user" action="index">User</g:link></li>
            <li><g:link controller="role" action="index">Roles</g:link></li>
            <li><g:link controller="requestmap" action="index">Permissions</g:link></li>
          </ol>
        </td>
      </tr>
    </table>
  </div>

  <div>
    <h1 style="font-size:150%">Edit Tables:</h1>
    <table>
      <tr>
        <td width="120px">
          <img src="${resource(dir: 'images', file: 'extend.gif')}" alt="">
        </td>
        <td>
          <ol>
            <g:each var="c" in="${editableControllers}">
              <li><g:link controller="${c.path}">${c.name}</g:link></li>
            </g:each>
          </ol>
        </td>
      </tr>
    </table>
  </div>
</g:ifAllGranted>
<g:javascript>
  function submitImageView()
  {
    document.imageView.submit();
  }

  function submitVideoView()
  {
    document.videoView.submit();
  }

  function submitImageCoverage()
  {
    document.imageFootprints.submit();
  }

  function submitVideoCoverage()
  {
    document.videoFootprints.submit();
  }
</g:javascript>
</body>
</html>