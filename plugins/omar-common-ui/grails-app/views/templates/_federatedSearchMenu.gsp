<ul id="federatedSearchMenuId" class="jMenu">
    <li><a class="fNiv">OMAR™</a>
        <ul>
            <li class="arrow"></li>
            <li><a href="${createLink( controller: 'home', action: 'index' )}">Home</a></li>
            <li><a href="${createLink( controller: 'login', action: 'about' )}">About</a></li>
            <li><a href="${createLink( controller: 'logout' )}">Logout</a></li>
        </ul>
    </li>

    <li><a class="fNiv">Export</a>
        <ul>
            <li class="arrow"></li>
            <li id="ExportKmlQueryId"
                title="Will take a default action.  Fixes the BBOX query to your spatial BBOX defintion.  If use spatial is unselected and no BBOX is present then the BBOX constraint will float with the google viewport."><a>KML Query</a>
            </li>
            <li id="ExportKmlQueryFloatBboxId"
                title="Forces spatial contraint for BBOX to change with the google earth viewport."><a>KML Query Float Bbox</a>
            </li>
            <li id="ExportKmlId"
                title="The current selection or if no selection the current page of results will be output as KML"><a>KML</a>
            </li>
            <li id="ExportGeoJsonId"
                title="The current selection or if no selection the current page of results will be output as GeoJSON"><a>GeoJSON</a>
            </li>
            <li id="ExportGml2Id"
                title="The current selection or if no selection the current page of results will be output as GML2"><a>GML2</a>
            </li>
            <li id="ExportCsvId"
                title="The current selection or if no selection the current page of results will be output as CSV"><a>CSV</a>
            </li>
            <li id="TimeLapseId" title="The current selection is used for time lapse output"><a>Timelapse</a></li>
            <sec:ifAnyGranted roles="ROLE_ADMIN,ROLE_DOWNLOAD">
                <li id="ExportGeoCellId"
                    title="The current selection will be output as GCL"><a>GeoCell Project Package</a></li>
                <li id="DownloadId"
                    title="The current selection will download the mainfile and all associated files"><a>Download</a>
                </li>
            </sec:ifAnyGranted>
            <li id="CreateProductId"
                title="Uses the current selection to define a output product."><a>Create Product</a></li>
        </ul>
    </li>

    <li><a class="fNiv">Search</a>
        <ul>
            <li class="arrow"></li>
            <li name="Search" id="SearchId"><a>Search</a></li>
            <li name="ClearSelectedRows" id="ClearSelectedRowsId"><a>Clear Selected Rows</a></li>
        </ul>
    </li>
</ul>