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
        <li id="ExportKmlQueryId" title="Will take a default action.  Fixes the BBOX query if use spatial is true and floats the bbox if not"><a>KML Query</a></li>
        <li id="ExportKmlQueryFloatBboxId" title="Bbox is not fixed even if you provide one"><a>KML Query Float Bbox</a></li>
        <li id="ExportKmlId" title="Outputs the currently selected items on the results or if none are selected it will do the currently listed items in the results page"><a>KML</a></li>
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