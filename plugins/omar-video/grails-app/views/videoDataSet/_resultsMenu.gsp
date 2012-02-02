<%--
  Created by IntelliJ IDEA.
  User: sbortman
  Date: 1/31/12
  Time: 3:12 PM
  To change this template use File | Settings | File Templates.
--%>

<g:form name="exportForm" method="post">
</g:form>

<div id="resultsMenu" class="yuimenubar yuimenubarnav">
  <div class="bd">
    <ul class="first-of-type">
      <li class="yuimenubaritem first-of-type">
        <a class="yuimenubaritemlabel" id="homeMenu" href="${createLink(controller: 'home', action: 'index')}"
           title="OMAR™ Home">&nbsp;&nbsp;&nbsp;&nbsp;OMAR™ Home</a>
      </li>
      <li class="yuimenubaritem first-of-type">
        <a class="yuimenubaritemlabel" id="Search" href="#searchMenu" title="Search">Search</a>

        <div id="searchMenu" class="yuimenu">
          <div class="bd">
            <ul>
              <li class="yuimenuitem">
                <a class="yuimenuitemlabel" href="${createLink(action: 'search')}" title="New Search">New</a>
              </li>
              <li class="yuimenuitem">
                <a class="yuimenuitemlabel" href="${createLink(action: "search", params: params)}"
                   title="Edit Search">Edit</a>
              </li>
            </ul>
          </div>
        </div>
      </li>
      <li class="yuimenubaritem first-of-type">
        <a class="yuimenubaritemlabel" href="#exportMenu">Export</a>

        <div id="exportMenu" class="yuimenu">
          <div class="bd">
            <ul>
              <li class="yuimenuitem">
                <a class="yuimenuitemlabel" href="javascript:exportAs('csv')" title="Export Csv">Csv File</a>
              </li>
              <li class="yuimenuitem">
                <a class="yuimenuitemlabel" href="javascript:exportAs('shp')" title="Export Shape">Shape File</a>
              </li>
            </ul>
          </div>
        </div>
      </li>
    </ul>
  </div>
</div>
