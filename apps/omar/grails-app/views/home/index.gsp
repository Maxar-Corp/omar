<html>
<head>
  <title>Welcome to OMAR 2.0</title>
  <meta name="layout" content="main"/>
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
        <img src="${createLinkTo(dir: '/images', file: 'discover.gif')}" alt="">
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

<div>
  <h1 style="font-size:150%">Browse:</h1>
  <table>
    <tr>
      <td width="120px">
        <img src="${createLinkTo(dir: '/images', file: 'globe_128.png')}" width="96" height="96" alt="">
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

<div>
  <h1 style="font-size:150%">KML Queries:</h1>
  <table>
    <tr>
      <td width="120px">
        <img src="${createLinkTo(dir: '/images', file: 'GoogleEarth_1.png')}" width="96" height="96" alt="">
      </td>
      <td>
        <ol>
          <li><g:link controller="kmlQuery" action="topImages">Top Images</g:link></li>
          <li><g:link controller="kmlQuery" action="topVideos">Top Videos</g:link></li>
          <li><g:link controller="kmlQuery" action="imageFootprints">Image footprints</g:link></li>
          <li><g:link controller="kmlQuery" action="videoFootprints">Video footprints</g:link></li>
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
          <img src="${createLinkTo(dir: '/images', file: 'logout.png')}" width="96" height="96" alt="">
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
          <img src="${createLinkTo(dir: '/images', file: 'use.gif')}" alt="">
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
          <img src="${createLinkTo(dir: '/images', file: 'extend.gif')}" alt="">
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

</body>
</html>