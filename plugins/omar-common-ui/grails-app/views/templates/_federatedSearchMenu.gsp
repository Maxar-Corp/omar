<ul id="jMenu">

  <li><a class="fNiv">OMAR™</a><!-- Do not forget the "fNiv" class for the first level links !! -->
    <ul>
      <li class="arrow"></li>
      <li><a href="${createLink(controller: 'login', action: 'about')}">About</a></li>
      <li><a href="${createLink(controller: 'home', action: 'index')}">Home</a></li>
      <li><a href="${createLink(controller: 'logout')}">Logoout</a></li>
    </ul>
  </li>
  
  <li><a class="fNiv">Export</a>
    <ul>
      <li class="arrow"></li>
      <li><a href="javascript:generateKmlQuery()">KML Query</a></li>
    </ul>
  </li>
  
  <li><a class="fNiv">Search</a>
    <ul>
      <li class="arrow"></li>
      <li><a href="javascript:refreshFootprints()">Update Footprints</a></li>
      <li><a href="javascript:search()">Search</a></li>
    </ul>
  </li>

   <li><a class="fNiv">Map Tools</a>
    <ul>
      <li class="arrow"></li>
      <li><a>Set Measurement Unit Type</a>
        <ul>
          <li><a>Kilometers</a></li>
          <li><a>Meters</a></li>
          <li><a>Feet</a></li>
          <li><a>Miles</a></li>
          <li><a>Yards</a></li>
          <li><a>Nautical Miles</a></li>
        </ul>
      </li>
    </ul>
  </li>
  
</ul>