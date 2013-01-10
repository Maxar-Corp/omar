<ul id="FederatedSearchMenu" class="ui-menu">
    <li><a href="#">OMAR™</a><ul>
        <li><a href="${createLink(controller: 'login', action: 'about')}">About</a></li>
        <li><a href="${createLink(controller: 'home', action: 'index')}">Home</a></li>
        <li><a href="${createLink(controller: 'logout')}">Log Out</a></li></ul>
    </li>

    <li><a href="#">Export</a><ul>
        <li><a href="javaScript:generateKmlQuery()">Kml Query</a></li></ul>
    </li>

    <li><a href="#">View</a><ul>
        <li><a href="javaScript:refreshFootprints()">Refresh Footprints</a></li>
        <li><a href="javaScript:search()">Search</a></li></ul>
    </li>
</ul>