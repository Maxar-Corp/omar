<div id="menu1" class="yuimenubar yuimenubarnav">
  <div class="bd">
    <ul class="first-of-type">
      <li class="yuimenubaritem first-of-type">
        <a class="yuimenubaritemlabel" id="homeMenu"
           href="${createLink( controller: 'home', action: 'index' )}"
           title="OMAR™ Home">&nbsp;&nbsp;&nbsp;&nbsp;OMAR™ Home</a>
      </li>

      <li class="yuimenubaritem first-of-type">
        <a class="yuimenubaritemlabel" href="#exportMenu">Export</a>

        <div id="exportMenu" class="yuimenu">
          <div class="bd">
            <ul>
              <li class="yuimenuitem">
                <a class="yuimenuitemlabel" href="javascript:generateKml()"
                   title="Export KML.  If no selection box is present the view bounds will float in google earth and if you hit refresh it will use the current google viewport to query the latest imagery.  If you specify a selection then it will be fixed to that location">KML Query</a>
              </li>
            </ul>
          </div>
        </div>
      </li>

      <li class="yuimenubaritem first-of-type"><a class="yuimenubaritemlabel" href="#viewMenu">View</a>

        <div id="viewMenu" class="yuimenu">
          <div class="bd">
            <ul>
              <li class="yuimenuitem">
                <a class="yuimenuitemlabel" href="javascript:updateOmarFilters();"
                   title="Refresh the footprints">Refresh Footprints</a>
              </li>
              <li class="yuimenuitem">
                <a class="yuimenuitemlabel" href="javascript:search();"
                   title="Execute the search for the specified criteria">Search</a>
              </li>
            </ul>
          </div>
        </div>
      </li>

    </ul>
  </div>
</div>