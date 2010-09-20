<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="main1"/>
  <title>OMAR: Video Set List</title>
</head>

<body class="yui-skin-sam">
<content tag="header">
  <div class="nav">
    <span class="menuButton"><g:link class="home" uri="/">OMAR™ Home</g:link></span>
    <g:ifAllGranted role="ROLE_ADMIN">
      <span class="menuButton"><g:link class="create" action="create">Create Video Set</g:link></span>
    </g:ifAllGranted>
    <span class="menuButton"><g:link action="search">Search Video</g:link></span>
    <span class="menuButton"><a href="javascript:toggleThumbnails()">Toggle Thumbnails</a></span>
    <span class="menuButton"><a href="javascript:toggleDetails()">Toggle Details</a></span>
  </div>
</content>
<content tag="content">
  <div id="dt"></div>

  <script type="text/javascript">

    // Set up DataSource
    var myDataSource = new YAHOO.util.DataSource('${createLink(action: 'listData')}?');
    myDataSource.responseType   = YAHOO.util.DataSource.TYPE_JSON;
    myDataSource.responseSchema = {
      resultsList : 'records',
      fields      : ['id','thumbnail','width','height','startDate','endDate','minLon','minLat','maxLon','maxLat','filename'],
      metaFields  : {
      totalRecords: 'totalRecords'
      }
    };

    // A custom function to translates sorting and pagination values
    // into a query string the server will accept
    var buildQueryString = function (state,dt) {
      return "offset=" + state.pagination.recordOffset +
              "&max=" + state.pagination.rowsPerPage +
              "&sort=" + state.sorting.key +
              "&order=" + ((state.sorting.dir === YAHOO.widget.DataTable.CLASS_ASC) ? "asc" : "desc");
    };

    // Custom function to handle pagination requests
    var handlePagination = function (state,dt) {
      var sortedBy  = dt.get('sortedBy');

      // Define the new state
      var newState = {
        startIndex: state.recordOffset,
        sorting: {
          key: sortedBy.key,
          dir: ((sortedBy.dir === YAHOO.widget.DataTable.CLASS_DESC) ? YAHOO.widget.DataTable.CLASS_DESC : YAHOO.widget.DataTable.CLASS_ASC)
        },
        pagination : { // Pagination values
          recordOffset: state.recordOffset, // Default to first page when sorting
          rowsPerPage: dt.get("paginator").getRowsPerPage(),
        }
      };

      // Create callback object for the request
      var oCallback = {
        success: dt.onDataReturnSetRows,
        failure: dt.onDataReturnSetRows,
        scope: dt,
        argument: newState // Pass in new state as data payload for callback function to use
        };

      // Send the request
      dt.getDataSource().sendRequest(buildQueryString(newState), oCallback);
    };

    this.myCustomFormatter = function(elLiner, oRecord, oColumn, oData) {
      elLiner.innerHTML = "<a href='../videoStreaming/show/" + oData + "'><img src='../thumbnail/frame/" + oData + "?size=128'></a>";
    };

    YAHOO.widget.DataTable.Formatter.myCustom = this.myCustomFormatter;


      this.test = function(elLiner, oRecord, oColumn, oData) {
      elLiner.innerHTML = "dfd";
    };
    YAHOO.widget.Paginator.ui.MyCustomControl = this.foo;

    var myColumnDefs = [
      {key: "thumbnail", label: "Thumbnail", sortable: false, width: 128, formatter:"myCustom"},
      {key: "id", label: "Id", sortable: true},
      {key: "width", label: "Width", sortable: true},
      {key: "height", label: "Height", sortable: true},
      {key: "startDate", label: "Start Date", sortable: false},
      {key: "endDate", label: "End Date", sortable: false},
      {key: "minLon", label: "Min Lon", sortable: false},
      {key: "minLat", label: "Min Lat", sortable: false},
      {key: "maxLon", label: "Max Lon", sortable: false},
      {key: "maxLat", label: "Max Lat", sortable: false},
      {key: "filename", label: "Filename", sortable: false}
    ];

    var myPaginator = new YAHOO.widget.Paginator({
      containers: "paging",
      rowsPerPage: 10,
      template : "{FirstPageLink} {PreviousPageLink} <strong>{PageLinks}</strong> {NextPageLink} {LastPageLink} {CurrentPageReport} <input name='foopert' value='1' size='14' type='text' onchange='foo(this.value)'>"

    });

    var myTableConfig = {
      initialRequest         : 'max=10&offset=0&sort=id&order=asc',
      generateRequest        : buildQueryString,
      paginator              : myPaginator,
      paginationEventHandler : handlePagination,
      sortedBy               : {key: "id", dir: YAHOO.widget.DataTable.CLASS_ASC},
    };

    function foo(frm)
    {
      if (frm >= '1' && frm <= myPaginator.getTotalPages())
      {
        var frmInt = parseInt(frm);
        myPaginator.setPage(frmInt, false);
      }
      else
      {
        alert("ERROR: Invalid Input.\nAcceptable inputs are 1 through " + myPaginator.getTotalPages() + ".");
      }
    }

    var myDataTable = new YAHOO.widget.DataTable('dt', myColumnDefs, myDataSource, myTableConfig);

    // Override function for custom server-side sorting
    myDataTable.sortColumn = function(oColumn) {
      // Default ascending
      var sDir = "asc";

      // If already sorted, sort in opposite direction
      if(oColumn.key === this.get("sortedBy").key) {
        sDir = (this.get("sortedBy").dir === YAHOO.widget.DataTable.CLASS_ASC) ? "desc" : "asc";
      }

      // Define the new state
      var newState = {
        startIndex: 0,
        sorting: { // Sort values
          key: oColumn.key,
          dir: (sDir === "desc") ? YAHOO.widget.DataTable.CLASS_DESC : YAHOO.widget.DataTable.CLASS_ASC
        },
        pagination : { // Pagination values
          recordOffset: 0, // Default to first page when sorting
          rowsPerPage: this.get("paginator").getRowsPerPage()}
      };

      // Create callback object for the request
      var oCallback = {
        success: this.onDataReturnSetRows,
        failure: this.onDataReturnSetRows,
        scope: this,
        argument: newState // Pass in new state as data payload for callback function to use
      };

      // Send the request
      this.getDataSource().sendRequest(buildQueryString(newState), oCallback);
    };

    var tn = "on";
    function toggleThumbnails()
    {
      if(tn == "on")
      {
        myDataTable.hideColumn(0);
        tn = "off";
      }
      else
      {
        myDataTable.showColumn(0);
        tn = "on";
      }
    }

    var td = "on";
    function toggleDetails()
    {
      if(td == "on")
      {
        myDataTable.hideColumn(2);
        myDataTable.hideColumn(3);
        myDataTable.hideColumn(6);
        myDataTable.hideColumn(7);
        myDataTable.hideColumn(8);
        myDataTable.hideColumn(9);
        td = "off";
      }
      else
      {
        myDataTable.showColumn(2);
        myDataTable.showColumn(3);
        myDataTable.showColumn(6);
        myDataTable.showColumn(7);
        myDataTable.showColumn(8);
        myDataTable.showColumn(9);
        td = "on";
      }
    }
  </script>
</content>

<content tag="footer">
  <div id="paging"></div>
</content>

</body>

</html>