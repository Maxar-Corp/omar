s<html>
<head>
  <title>Welcome to OMAR <g:meta name="app.version"/></title>
  <meta name="layout" content="homeLayout"/>
</head>
<body class="yui-skin-sam">
<content tag="top">
  <div align="center">
    <g:link class="home" uri="/"><img src="${resource(dir: 'images', file: 'omarLogo.png')}" alt="OMAR Logo"/></g:link>
  </div>

</content>

<content tag="center">
  <g:if test="${flash.message}">
    <div class="message">${flash.message}</div>
  </g:if>
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
              <li><g:link controller="rasterEntry" action="results">Imagery</g:link></li>
              <li><g:link controller="videoDataSet" action="results">Video</g:link></li>
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

  <sec:ifNotGranted roles="ROLE_ADMIN">
    <div>
      <h1 style="font-size:150%">User Preferences:</h1>
      <table>
        <tr>
          <td width="120px">
            <img src="${resource(dir: 'images', file: 'use.gif')}" alt="">
          </td>
          <td>
            <ol>
              <li><g:link controller="userPreferences" action="editProfile" id="${user?.id}">Edit Profile</g:link></li>
              <li><g:link controller="userPreferences" action="changePassword" id="${user?.id}">Change Password</g:link></li>
            </ol>
          </td>
        </tr>
      </table>
    </div>
  </sec:ifNotGranted>
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
              <g:link plugin="omar-core" controller="report" action="create">User Feedback</g:link>
            </li>
              <li>
                <g:link plugin="omar-core" controller="report" action="list">View Feedback</g:link>
              </li>
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

  <sec:ifAllGranted roles="ROLE_ADMIN">
    <div>
      <h1 style="font-size:150%">User Management:</h1>
      <table>
        <tr>
          <td width="120px">
            <img src="${resource(dir: 'images', file: 'use.gif')}" alt="">
          </td>
          <td>
            <ol>
              <li><g:link controller="secUser" action="index">User</g:link></li>
              <li><g:link controller="secRole" action="index">Roles</g:link></li>
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
    </div
  </sec:ifAllGranted>
</content>

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
