<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="main8"/>
  <g:set var="entityName" value="${message(code: 'rasterEntry.label', default: 'RasterEntry')}"/>
  <title><g:message code="default.list.label" args="[entityName]"/></title>


  <link rel="stylesheet" type="text/css" href="${resource(plugin: 'richui', dir: 'js/yui/paginator/assets/skins/sam', file: 'paginator.css')}"/>
  <link rel="stylesheet" type="text/css" href="${resource(plugin: 'richui', dir: 'js/yui/datatable/assets/skins/sam', file: 'datatable.css')}"/>
  <link rel="stylesheet" type="text/css" href="${resource(plugin: 'richui', dir: 'js/yui/button/assets/skins/sam', file: 'button.css')}"/>

  <%--
  <link rel="stylesheet" type="text/css" href="${omar.bundle(contentType: 'text/css', files: [
      resource(plugin: 'richui', dir: 'js/yui/fonts', file: 'fonts-min.css'),
      resource(plugin: 'richui', dir: 'js/yui/paginator/assets/skins/sam', file: 'paginator.css'),
      resource(plugin: 'richui', dir: 'js/yui/datatable/assets/skins/sam', file: 'datatable.css')
  ])}"/>
  --%>

</head>
<body class="yui-skin-sam">
<content tag="north">
  <div class="nav">
    <span class="menuButton"><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></span>
    <span class="menuButton"><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]"/></g:link></span>
  </div>
</content>
<content tag="south">
  <div id="radiobuttonsfromjavascript"></div>
</content>
<content tag="center">
  <div class="body">
  %{--<h1><g:message code="default.list.label" args="[entityName]"/></h1>--}%
    <g:if test="${flash.message}">
      <div class="message">${flash.message}</div>
    </g:if>

    <div id="dynamicdata"></div>
  </div>
</content>

<%--
<g:javascript plugin='richui' src='yui/yahoo-dom-event/yahoo-dom-event.js'/>
<g:javascript plugin='richui' src='yui/connection/connection-min.js'/>
<g:javascript plugin='richui' src='yui/json/json-min.js'/>
<g:javascript plugin='richui' src='yui/element/element-min.js'/>
<g:javascript plugin='richui' src='yui/paginator/paginator-min.js'/>
<g:javascript plugin='richui' src='yui/datasource/datasource-min.js'/>
<g:javascript plugin='richui' src='yui/datatable/datatable-min.js'/>
--%>

<omar:bundle contentType="javascript" files="${[
    [plugin: 'richui', dir: 'js/yui/connection', file: 'connection-min.js'],
    [plugin: 'richui', dir: 'js/yui/json', file: 'json-min.js'],
    [plugin: 'richui', dir: 'js/yui/paginator', file: 'paginator-min.js'],
    [plugin: 'richui', dir: 'js/yui/datasource', file: 'datasource-min.js'],
    [plugin: 'richui', dir: 'js/yui/datatable', file: 'datatable-min.js'],
    [plugin: 'richui', dir: 'js/yui/button', file: 'button-min.js']
]}"/>

<g:javascript>
    YAHOO.widget.DataTable.Formatter.thumbnail = function( elLiner, oRecord, oColumn, oData )
    {
      elLiner.innerHTML = "<a href='" + oData.href + "'><img src='" + oData.url + "'></a>";
    };

    YAHOO.widget.DataTable.Formatter.link = function( elLiner, oRecord, oColumn, oData )
    {
      elLiner.innerHTML = "<a href='" + oData.href + "'>" + oData.label + "</a>";
    };

    YAHOO.example.DynamicData = function()
    {
      // Column definitions
      var myColumnDefs = ${myColumnDefs};

      // Custom parser
      var stringToDate = function( sData )
      {
        var array = sData.split( "-" );
        return new Date( array[1] + " " + array[0] + ", " + array[2] );
      };

      // DataSource instance
      var myDataSource = new YAHOO.util.DataSource(
              "${createLink(action: 'query.json')}?" );
        
      myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSON;
      myDataSource.responseSchema = {
        resultsList: "results",
        fields: ${fields},
        metaFields: {
          totalRecords: "totalRecords" // Access to value in the server response
        }
      };

      // DataTable configuration
      var myConfigs = {
        // Initial request for first page of data
        initialRequest: "${initialRequest}",
        // Enables dynamic server-driven data
        dynamicData: true,
        // Sets UI initial sort arrow
        sortedBy : {key:"id", dir:YAHOO.widget.DataTable.CLASS_ASC},
        // Enables pagination
        paginator: new YAHOO.widget.Paginator( { rowsPerPage: ${params.max} } ),

        // Change query string to match service
        generateRequest: function(state) {

          var query = "offset=" + state.pagination.recordOffset +
                 "&max=" + state.pagination.rowsPerPage +
                 "&sort=" + state.sortedBy.key +
                 "&order=" + ((state.sortedBy.dir === YAHOO.widget.DataTable.CLASS_ASC) ? "asc" : "desc");

          if ("${initialRequest}" != null) {
              query += '&' + "${initialRequest}";
          }

/*
          if (this.customQueryString != null) {
              query += '&' + this.customQueryString;
              alert(query);
          }

          // tack on any user filters
          for (filterBy in this.userFilters) {
              query += '&filterBy=' + filterBy + '&filterOn=' + this.userFilters[filterBy];
          }
*/

          return query;
        }
      };

      // DataTable instance
      var myDataTable = new YAHOO.widget.DataTable( "dynamicdata", myColumnDefs, myDataSource, myConfigs );
      // Update totalRecords on the fly with value from server
      myDataTable.handleDataReturnPayload = function( oRequest, oResponse, oPayload )
      {
        oPayload.totalRecords = oResponse.meta.totalRecords;
        return oPayload;
      }

      var oButtonGroup3 = new YAHOO.widget.ButtonGroup({
                                      id:  "buttongroup3",
                                      name:  "radiofield3",
                                      container:  "radiobuttonsfromjavascript" });

      oButtonGroup3.addButtons([

          { label: "Image", value: "image", checked: true },
          { label: "Metadata", value: "metadata" },
          { label: "File", value: "file" },
          { label: "Links", value: "links" }

      ]);


      var showGroup = function ( group )
      {
         for ( var i in myColumnDefs )
         {
            var column = myDataTable.getColumn(myColumnDefs[i].key);

            if (column)
            {
              myDataTable.hideColumn(column);

              if( group == myColumnDefs[i].group )
              {
                myDataTable.showColumn(column);
              }
            }
          }

          myDataTable.showColumn(myDataTable.getColumn("id"));
          myDataTable.showColumn(myDataTable.getColumn("thumbnail"));          
      };

      // "checkedButtonChange" event handler for each ButtonGroup instance
      var onCheckedButtonChange = function ( p_oEvent )
      {
          if( p_oEvent.newValue )
          {
            showGroup( p_oEvent.newValue.get("value") );
          }
      };

      showGroup("image");
      myDataTable.showColumn(myDataTable.getColumn("id"));
      myDataTable.showColumn(myDataTable.getColumn("thumbnail"));

      oButtonGroup3.on("checkedButtonChange", onCheckedButtonChange);
        
      return {
        ds: myDataSource,
        dt: myDataTable
      };
    }();
</g:javascript>

</body>
</html>
