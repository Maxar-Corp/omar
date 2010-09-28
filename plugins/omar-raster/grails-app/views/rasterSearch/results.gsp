<body class="yui-skin-sam">
<div class="nav">
  <span class="menuButton"><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></span>
  <span class="menuButton"><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]"/></g:link></span>
</div>
<div class="body">
  <h1><g:message code="default.list.label" args="[entityName]"/></h1>
  <g:if test="${flash.message}">
    <div class="message">${flash.message}</div>
  </g:if>

  <div id="dynamicdata"></div>
  <%--

  <g:javascript plugin='richui' src='yui/yahoo-dom-event/yahoo-dom-event.js'/>
  <g:javascript plugin='richui' src='yui/connection/connection-min.js'/>
  <g:javascript plugin='richui' src='yui/json/json-min.js'/>
  <g:javascript plugin='richui' src='yui/element/element-min.js'/>
  <g:javascript plugin='richui' src='yui/paginator/paginator-min.js'/>
  <g:javascript plugin='richui' src='yui/datasource/datasource-min.js'/>
  <g:javascript plugin='richui' src='yui/datatable/datatable-min.js'/>
  --%>

  <script type="text/javascript" src="${scratch.bundle(contentType: 'text/javascript', files: [
      resource(plugin: 'richui', dir: 'js/yui/yahoo-dom-event', file: 'yahoo-dom-event.js'),
      resource(plugin: 'richui', dir: 'js/yui/connection', file: 'connection-min.js'),
      resource(plugin: 'richui', dir: 'js/yui/json', file: 'json-min.js'),
      resource(plugin: 'richui', dir: 'js/yui/element', file: 'element-min.js'),
      resource(plugin: 'richui', dir: 'js/yui/paginator', file: 'paginator-min.js'),
      resource(plugin: 'richui', dir: 'js/yui/datasource', file: 'datasource-min.js'),
      resource(plugin: 'richui', dir: 'js/yui/datatable', file: 'datatable-min.js')
  ])}"></script>

  <g:javascript>
    YAHOO.example.DynamicData = function()
    {
      // Column definitions
      var myColumnDefs = [
          {'key': 'id','label': 'Id','sortable': true,'resizeable': true},
          {'key': 'name','label': 'Name','sortable': true,'resizeable': true},
          {'key': 'country','label': 'Country','sortable': true,'resizeable': true},
          {'key': 'population','label': 'Population','sortable': true,'resizeable': true},
          {'key': 'capital','label': 'Capital','sortable': true,'resizeable': true},
          {'key': 'latitude','label': 'Latitude','sortable': true,'resizeable': true},
          {'key': 'longitude','label': 'Longitude','sortable': true,'resizeable': true}
      ];

      // Custom parser
      var stringToDate = function( sData )
      {
        var array = sData.split( "-" );
        return new Date( array[1] + " " + array[0] + ", " + array[2] );
      };

      // DataSource instance
      var myDataSource = new YAHOO.util.DataSource( "${createLink(action: 'query.json')}?" );
      myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSON;
      myDataSource.responseSchema = {
        resultsList: "results",
        fields: [
          {key:"id", parser:"number"},
          {key:"name"},
          {key:"country"},
          {key:"population", parser:"number"},
          {key:"capital"},
          {key:"latitude", parser:"number"},
          {key:"longitude", parser:"number"}
        ],
        metaFields: {
          totalRecords: "totalRecords" // Access to value in the server response
        }
      };

      // DataTable configuration
      var myConfigs = {
        // Initial request for first page of data
        initialRequest: "max=${params.max ?: 10}&offset=${params.offset ?: 0}&sort=${params.sort ?: "id"}&order=${params.order ?: "asc"}&",
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

          if (this.customQueryString != null) {
              query += '&' + this.customQueryString;
          }


          // tack on any user filters
          for (filterBy in this.userFilters) {
              query += '&filterBy=' + filterBy + '&filterOn=' + this.userFilters[filterBy];
          }

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

      return {
        ds: myDataSource,
        dt: myDataTable
      };

    }();
  </g:javascript>

</body>
</html>
