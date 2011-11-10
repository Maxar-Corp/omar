<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>

  <meta http-equiv="content-type" content="text/html; charset=utf-8">
  <title>Server-side Pagination and Sorting for Dynamic Data</title>

  <style type="text/css">
    /*
    margin and padding on body element
    can introduce errors in determining
    element position and are not recommended;
    we turn them off as a foundation for YUI
    CSS treatments.
    */
  body {
    margin: 0;
    padding: 0;
  }
  </style>


  <link rel="stylesheet" type="text/css"
        href="${resource(plugin: 'yui', dir: 'js/yui/fonts', file: 'fonts-min.css')}"/>
  <link rel="stylesheet" type="text/css"
        href="${resource(plugin: 'yui', dir: 'js/yui/paginator/assets/skins/sam', file: 'paginator.css')}"/>
  <link rel="stylesheet" type="text/css"
        href="${resource(plugin: 'yui', dir: 'js/yui/datatable/assets/skins/sam', file: 'datatable.css')}"/>


  <g:javascript plugin="yui" src="js/yui/yahoo-dom-event/yahoo-dom-event.js"/>
  <g:javascript plugin="yui" src="js/yui/connection/connection-min.js"/>
  <g:javascript plugin="yui" src="js/yui/json/json-min.js"/>
  <g:javascript plugin="yui" src="js/yui/element/element-min.js"/>
  <g:javascript plugin="yui" src="js/yui/paginator/paginator-min.js"/>
  <g:javascript plugin="yui" src="js/yui/datasource/datasource-min.js"/>
  <g:javascript plugin="yui" src="js/yui/datatable/datatable-min.js"/>


  <!--there is no custom header content for this example-->

</head>

<body class=" yui-skin-sam">

<h1>Server-side Pagination and Sorting for Dynamic Data</h1>

<div class="exampleIntro">
  <p>This example enables server-side sorting and pagination for data that is
  dynamic in nature.</p>

</div>

<!--BEGIN SOURCE CODE FOR EXAMPLE =============================== -->

<div id="dynamicdata"></div>

<script type="text/javascript">
  YAHOO.example.DynamicData = function()
  {
    // Column definitions
    var myColumnDefs = [
      // sortable:true enables sorting
      {
        key:"id",
        label:"ID",
        sortable:true
      },
      {
        key:"name",
        label:"Name",
        sortable:true
      },
      {
        key:"date",
        label:"Date",
        sortable:true,
        formatter:"date"
      },
      {
        key:"price",
        label:"Price",
        sortable:true
      },
      {
        key:"number",
        label:"Number",
        sortable:true
      }
    ];

    // Custom parser
    var stringToDate = function( sData )
    {
      var array = sData.split( "-" );
      return new Date( array[1] + " " + array[0] + ", " + array[2] );
    };

    // DataSource instance
    var myDataSource = new YAHOO.util.DataSource( "/omar-2.0/yuiTableTest/getData?" );
    myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSON;
    myDataSource.responseSchema = {
      resultsList: "records",
      fields: [
        {
          key:"id",
          parser:"number"
        },
        {
          key:"name"
        },
        {
          key:"date",
          parser:stringToDate
        },
        {
          key:"price",
          parser:"number"
        },
        {
          key:"number",
          parser:"number"
        }
      ],
      metaFields: {
        totalRecords: "totalRecords" // Access to value in the server response
      }
    };

    // DataTable configuration
    var myConfigs = {
      initialRequest: "sort=id&dir=asc&startIndex=0&results=25", // Initial request for first page of data
      dynamicData: true, // Enables dynamic server-driven data
      sortedBy : {key:"id", dir:YAHOO.widget.DataTable.CLASS_ASC}, // Sets UI initial sort arrow
      paginator: new YAHOO.widget.Paginator( { rowsPerPage:25 } ) // Enables pagination
    };

    // DataTable instance
    var myDataTable = new YAHOO.widget.DataTable( "dynamicdata", myColumnDefs, myDataSource, myConfigs );
    // Update totalRecords on the fly wit
    // h value from server
    myDataTable.handleDataReturnPayload = function( oRequest, oResponse, oPayload )
    {
      oPayload.totalRecords = oResponse.meta.totalRecords;
      return oPayload;
    }

    return {
      ds: myDataSource,
      dt: myDataTable
    };

  }();
</script>
</body>
</html>
