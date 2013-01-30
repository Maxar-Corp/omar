<ul id="federatedSearchMenuId" class="jMenu">
  <li><a class="fNiv">OMAR™</a>
    <ul>
      <li class="arrow"></li>
      <li><a href="${createLink(controller: 'login', action: 'about')}">About</a></li>
      <li><a href="${createLink(controller: 'home', action: 'index')}">Home</a></li>
      <li><a href="${createLink(controller: 'logout')}">Logout</a></li>
    </ul>
  </li>
  
  <li><a class="fNiv">Export</a>
    <ul>
      <li class="arrow"></li>
      <li><a href="javascript:generateKmlQuery()">KML Query</a></li>
      <li><a href="javascript:generateKmlQuery()">GeoJSON</a></li>
      <li><a href="javascript:generateKmlQuery()">GML2</a></li>
      <li><a href="javascript:generateKmlQuery()">CSV</a></li>
    </ul>
  </li>
  
  <li><a class="fNiv">Search</a>
    <ul>
      <li class="arrow"></li>
      <li><a href="javascript:refreshFootprints()">Update Footprints</a></li>
      <li><a href="javascript:mapView.menuView.search()">Search</a></li>
    </ul>
  </li>
</ul>