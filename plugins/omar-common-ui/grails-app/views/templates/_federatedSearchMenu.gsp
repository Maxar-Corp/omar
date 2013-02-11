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
        <li id="ExportKmlQueryId"><a>KML Query</a></li>
        <li id="ExportKmlId"><a>KML</a></li>
        <li id="ExportGeoJsonId"><a>GeoJSON</a></li>
        <li id="ExportGml2Id"><a>GML2</a></li>
        <li id="ExportCsvId"><a>CSV</a></li>
    </ul>
  </li>
  
  <li><a class="fNiv">Search</a>
    <ul>
      <li class="arrow"></li>
      <li id="SearchRasterId"><a>Search</a></li>
    </ul>
  </li>
</ul>