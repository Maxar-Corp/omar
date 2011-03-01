<html>
<head>
  <meta content="yes" name="apple-mobile-web-app-capable"/>
  <meta content="minimum-scale=1.0, width=device-width, user-scalable=no" name="viewport"/>
  <link rel="stylesheet" href="${resource(dir: 'css', file: 'iwebkit.css')}"/>
  <title>Welcome to OMAR</title>
</head>

<body>

<div id="topbar">
  <div id="title">OMAR</div>

  <div id="rightbutton">
    <a href="${createLink(controller: 'logout')}">Logout</a>
  </div>
</div>

<div id="content">
  <ul class="pageitem">
    <li class="textbox"><span class="header">Search:</span></li>
    <li class="menu">
      <g:link controller="rasterEntry" action="search_mobile">
        <img alt="list" src="${resource(dir: 'images', file: 'discover.gif')}"/>
        <span class="name">Raster Search</span>
        <span class="arrow"></span>
      </g:link>
    </li>
    <li class="menu">
      <g:link controller="videoDataSet" action="search_mobile">
        <img alt="list" src="${resource(dir: 'images', file: 'discover.gif')}"/>
        <span class="name">Video Search</span>
        <span class="arrow"></span>
      </g:link>
    </li>
  </ul>

  <ul class="pageitem">
    <li class="textbox"><span class="header">List:</span></li>
    <li class="menu">
      <g:link controller="rasterEntry" action="list_mobile">
        <img alt="list" src="${resource(dir: 'images', file: 'globe_128.png')}"/>
        <span class="name">Raster List</span>
        <span class="arrow"></span>
      </g:link>
    </li>
    <li class="menu">
      <g:link controller="videoDataSet" action="list_mobile">
        <img alt="list" src="${resource(dir: 'images', file: 'globe_128.png')}"/>
        <span class="name">Video List</span>
        <span class="arrow"></span>
      </g:link>
    </li>
  </ul>

  <ul class="pageitem">
    <li class="textbox"><span class="header">User Feedback:</span></li>
    <li class="menu">
      <g:link controller="report" action="create">
        <img alt="list" src="${resource(dir: 'images', file: 'report.png')}"/>
        <span class="name">Submit Feedback</span>
        <span class="arrow"></span>
      </g:link>
    </li>
    <sec:ifAllGranted roles="ROLE_ADMIN">
      <li class="menu">
        <g:link controller="report" action="list">
          <img alt="list" src="${resource(dir: 'images', file: 'report.png')}"/>
          <span class="name">View Feedback</span>
          <span class="arrow"></span>
        </g:link>
      </li>
    </sec:ifAllGranted>
  </ul>

  <sec:ifAllGranted roles="ROLE_ADMIN">
    <ul class="pageitem">
      <li class="textbox"><span class="header">User Management:</span></li>
      <li class="menu">
        <g:link controller="user" action="index">
          <img alt="list" src="${resource(dir: 'images', file: 'use.gif')}"/>
          <span class="name">User</span>
          <span class="arrow"></span>
        </g:link>
      </li>
      <li class="menu">
        <g:link controller="role" action="index">
          <img alt="list" src="${resource(dir: 'images', file: 'use.gif')}"/>
          <span class="name">Roles</span>
          <span class="arrow"></span>
        </g:link>
      </li>
      <li class="menu">
        <g:link controller="requestmap" action="index">
          <img alt="list" src="${resource(dir: 'images', file: 'use.gif')}"/>
          <span class="name">Permissions</span>
          <span class="arrow"></span>
        </g:link>
      </li>
    </ul>

    <ul class="pageitem">
      <li class="textbox"><span class="header">Edit Tables:</span></li>
      <g:each var="c" in="${editableControllers}">
        <li class="menu">
          <g:link controller="${c.path}">
            <img alt="list" src="${resource(dir: 'images', file: 'extend.gif')}"/>
            <span class="name">${c.name}</span>
            <span class="arrow"></span>
          </g:link>
        </li>
      </g:each>
    </ul>
  </sec:ifAllGranted>
</div>

<div id="footer">
  <a href="http://www.radiantblue.com">Powered by RadiantBlue Technologies</a>
</div>

</body>

</html>
