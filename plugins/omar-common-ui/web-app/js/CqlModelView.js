
OMAR.models.CqlColumnDef = Backbone.Model.extend({
    idAttribute:"name",
    defaults:{
        name:"",
        label:"",
        type:"" // string, date, numeric, geom
    },
    initialize:function(params){

    }
});


OMAR.models.CqlVideoColumnDefCollection = Backbone.Collection.extend({
    model:OMAR.models.CqlColumnDef,
    initialize:function(params){
        this.add([
            {name:"id", label:"Record id", type:"id"}
            ,{name:"filename", label:"Filename", type:"string"}
            ,{name:"width", label:"Width", type:"numeric"}
            ,{name:"height", label:"Height", type:"numeric"}
            ,{name:"ground_geom", label:"Ground Geom", type:"geometry"}
            ,{name:"start_date", label:"Start Date", type:"datetime"}
            ,{name:"end_date", label:"End Date", type:"datetime"}
            ,{name:"index_id", label:"Index Id", type:"string"}
            ]);
    }
});

OMAR.models.CqlRasterColumnDefCollection = Backbone.Collection.extend({
    model:OMAR.models.CqlColumnDef,
    initialize:function(params){
        this.add([
            {name:"id", label:"Record id", type:"id"}
            ,{name:"be_number", label:"BE Number", type:"string"}
            ,{name:"title", label:"Image ID", type:"string"}
            ,{name:"class_name", label:"Class Name", type:"string"}
            ,{name:"country_code", label:"Country", type:"string"}
            ,{name:"file_type", label:"Class Name", type:"string"}
            ,{name:"filename", label:"Filename", type:"string"}
            ,{name:"image_id", label:"IID", type:"string"}
            ,{name:"mission", label:"Mission", type:"string"}
            ,{name:"niirs", label:"NIIRS", type:"numeric"}
            ,{name:"sensor_id", label:"Sensor", type:"string"}
            ,{name:"target_id", label:"Target Id", type:"string"}
            ,{name:"image_representation", label:"Image Representation", type:"string"}
            ,{name:"azimuth_angle", label:"Azimuth Angle", type:"numeric"}
            ,{name:"grazing_angle", label:"Grazing Angle", type:"numeric"}
            ,{name:"acquisition_date", label:"Acquisition Date", type:"datetime"}
            ,{name:"ground_geom", label:"Ground Geom", type:"geometry"}
            ,{name:"entry_id", label:"Entry Id", type:"string"}
            ,{name:"exclude_policy", label:"Exclude Policy", type:"string"}
            ,{name:"width", label:"Width", type:"numeric"}
            ,{name:"height", label:"Height", type:"numeric"}
            ,{name:"number_of_bands", label:"Number of Bands", type:"numeric"}
            ,{name:"number_of_res_levels", label:"Number of Res Levels", type:"numeric"}
            ,{name:"gsd_unit", label:"GSD Unit", type:"numeric"}
            ,{name:"gsdx", label:"GSD X", type:"numeric"}
            ,{name:"gsdy", label:"GSD Y", type:"numeric"}
            ,{name:"bit_depth", label:"Bit Depth", type:"numeric"}
            ,{name:"data_type", label:"Data Type", type:"string"}
            ,{name:"index_id", label:"Index Id", type:"string"}
            ,{name:"height", label:"Height", type:"numeric"}
            ,{name:"product_id", label:"Product Id", type:"string"}
            ,{name:"image_category", label:"Image Category", type:"string"}
            ,{name:"security_classification", label:"Security Classification", type:"string"}
            ,{name:"security_code", label:"Security Code", type:"string"}
            ,{name:"isorce", label:"Isource", type:"string"}
            ,{name:"organization", label:"Organization", type:"string"}
            ,{name:"description", label:"Description", type:"string"}
            ,{name:"organization", label:"Organization", type:"string"}
            ,{name:"wac_code", label:"Wac Code", type:"string"}
            ,{name:"sun_elevation", label:"Sun Elevation", type:"numeric"}
            ,{name:"sun_azimuth", label:"Sun Azimuth", type:"numeric"}
            ,{name:"cloud_cover", label:"Cloud Cover", type:"numeric"}
           // ,{name:"style_id", label:"Style Id", type:"numeric"}
            //,{name:"keep_forever", label:"Keep Forever", type:"numeric"}
           // ,{name:"valid_model", label:"Valid Model", type:"string"}
            ,{name:"access_date", label:"Access Date", type:"datetime"}
            ,{name:"ingest_date", label:"Ingest Date", type:"datetime"}
            ,{name:"receive_date", label:"Recieve Date", type:"datetime"}
            ,{name:"file_type", label:"File Type", type:"string"}
        ]);
    }
});

OMAR.models.CqlModel = Backbone.Model.extend({
    idAttribute:"id",
    defaults:{
        id:"",
        name:"",
        cql:"",
        conditions:""
    },
    initialize:function(params){
        if(params)
        {
            if(!params.cql&&params.conditions)
            {
                this.attributes.cql = this.toCql();
            }
        }
    },
    toCql:function (errors)
    {
        var thisPtr = this;
        function getQuery(condition, errors)
        {
            var op = [' ', condition.operator, ' '].join('');

            var e = [];
            var elen = condition.expressions.length;
            for (var i = 0; i < elen; i++)
            {
                var expr = condition.expressions[i];
                var errorMessage = {expression:expr,message:""};
                //
                //alert(expr.colval + ", " + expr.opval);

                var val   = expr.val;
                var opval = expr.opval?expr.opval.toUpperCase():expr.opval;
                var fullExpression = "";
                var colType = expr.coltype;

                if(opval == "IS NULL" || opval == "IS NOT NULL")
                {
                    fullExpression = expr.colval + " " + opval;
                }
                else
                {
                    //var colDef = this.columnDefs.get(expr.colval);
                    switch(colType)
                    {
                        case "id":
                            if(!expr.val){
                                errorMessage.message =  "No value present, please input value";
                                errors.push(errorMessage);
                            }
                            else if(val.split(" ").size()>1)
                            {
                                errorMessage.message =  "ID values should be separated by commas with no spaces";
                                errors.push(errorMessage);
                            }
                            else
                            {
                                var idSplit = val.split(",");
                                if(idSplit.size() > 0)
                                {
                                    var idx = 0;
                                    for(idx = 0; idx < idSplit.size(); ++idx)
                                    {
                                        if(!OMAR.isInteger(idSplit[idx]))
                                        {
                                            errorMessage.message =  "ID values should be integers.  Non integer detected, please fix.";
                                            errors.push(errorMessage);
                                        }
                                    }
                                }
                            }
                            fullExpression = expr.colval +" " + opval+ "(" +val + ")";
                            break;
                        case "raw_cql":
                            if(!expr.val){
                                errorMessage.message =  "No value present, please input value";
                                errors.push(errorMessage);
                            }
                            // no validation here
                            fullExpression = val;
                            break;
                        case "geometry":
                            if(!expr.val){
                                errorMessage.message =  "No value present, please input value";
                                errors.push(errorMessage);
                            }
                            switch(expr.opval.toLowerCase())
                            {
                                case "bbox":
                                    var splitVal = expr.val.split(",");
                                    if(splitVal.size() != 4)
                                    {
                                        errorMessage.message =  "Must have 4 values separated by commas in WMS BBOX format";
                                        errors.push(errorMessage);
                                    }
                                    var minLon = splitVal[0].trim();
                                    var minLat = splitVal[1].trim();
                                    var maxLon = splitVal[2].trim();
                                    var maxLat = splitVal[3].trim();
                                    if(!OMAR.isFloat(minLon)||!OMAR.isFloat(minLat)||
                                        !OMAR.isFloat(maxLon)||!OMAR.isFloat(maxLat))
                                    {
                                        errorMessage.message = "Please enter a numeric value. A non numeric value was detected";
                                        errors.push(errorMessage);
                                    }
                                    fullExpression = "BBOX("+expr.colval + "," + val+")";
                                    break;
                                case "dwithin":
                                    var splitVal = expr.val.split(",");

                                    if(splitVal.size() != 4)
                                    {
                                        errorMessage.message =  "Must have 4 values separated by commas in DWITHIN statement.\n";
                                        errorMessage.message += "Example: <lat>,<lon>,<distance>,<units>, where <units> can be m (meters) or km (kilometers)</units>"
                                        errors.push(errorMessage);
                                    }
                                    var lat = splitVal[0].trim();
                                    var lon = splitVal[1].trim();
                                    var distance = splitVal[2].trim();
                                    var unit = splitVal[3].trim().toLowerCase();
                                    switch(unit)
                                    {
                                        case "m":
                                        case "km":
                                        case "dd":
                                            break;
                                        default:
                                            errorMessage.message = "Unit must be either m (meters), km (Kilometers) or dd (decimal degrees)";
                                            break;
                                    }
                                    if(!OMAR.isFloat(lat)||!OMAR.isFloat(lon)||!OMAR.isFloat(distance))
                                    {
                                        errorMessage.message = "Please enter a numeric value for lat, lon, and distance. A non numeric value was detected";
                                        errors.push(errorMessage);
                                    }
                                    // until geottols fixes support for unit conversion they use the units of the
                                    // column for distance.  Leave a bogus meters as unit argument and convert
                                    // passed in units to degrees for now
                                    //
                                    var degrees = 0;
                                    if(unit == "dd")
                                    {
                                        degrees = distance;
                                    }
                                    else
                                    {
                                        var inchesPerUnit = OpenLayers.INCHES_PER_UNIT[unit];
                                        var inches = inchesPerUnit*distance;
                                        degrees = inches*(1.0/OpenLayers.INCHES_PER_UNIT["degrees"]);
                                    }
                                    fullExpression = "DWITHIN("+expr.colval + ",POINT(" +lon +" " +
                                                     lat+")," + degrees + "," + "meters)";
                                    break;
                            }
                            break;
                        case "string":
                            if(!expr.val){
                                errorMessage.message =  "No value present, please input value";
                                errors.push(errorMessage);
                            }
                            switch(expr.opval.toLowerCase())
                            {
                                // we need to add support for case insensitive
                                // title ilike'%rect%'
                                // we will add this type of syntax later
                                //
                                case "contains":
                                    opval = "LIKE";
                                    val = " '%"+val+"%'";
                                    break;
                                case "icontains":
                                    opval = "ILIKE";
                                    val = " '%"+val+"%'";
                                    break;
                                case "startswith":
                                    opval = "LIKE";
                                    val = " '"+val+"%'";
                                    break;
                                case "istartswith":
                                    opval = "ILIKE";
                                    val = " '"+val+"%'";
                                    break;
                                case "endswith":
                                    opval = "LIKE";
                                    val = " '%"+val+"'";
                                    break;
                                case "iendswith":
                                    opval = "ILIKE";
                                    val = " '%"+val+"'";
                                    break;
                                case "in":
                                    var splitVal = val.split(",");
                                    val = "('"+splitVal.join("','") + "')";

                                    break;
                                default:
                                    val = "'"+val+"'";
                                    break;
                            }
                            fullExpression = expr.colval + " " + opval + val;
                            break;
                        case "numeric":
                            if(!expr.val){
                                errorMessage.message =  "No value present, please input value";
                                errors.push(errorMessage);
                            }
                            switch(expr.opval.toLowerCase())
                            {
                                case "in":
                                    var valSplit = val.split(",");
                                    if(OMAR.isFloatArray(valSplit))
                                    {
                                        fullExpression = expr.colval + " " + opval + "(" + valSplit.join(",")+")";
                                    }
                                    else
                                    {
                                        errorMessage.message = "Should have a list of numeric values separated";
                                        errorMessage.message += "\nby commas. example: 1,3,4";
                                        errors.push(errorMessage);
                                    }
                                    break;
                                default:
                                    if(!OMAR.isFloat(val))
                                    {
                                        errorMessage.message = "Value is not a number, please fix the value";
                                        errors.push(errorMessage);
                                    }
                                    fullExpression = expr.colval + " " + opval + " " + val;
                                    break;
                            }
                            break;
                        case "datetime":
                            switch(expr.opval)
                            {
                                case "today":
                                    var startDate = new OMAR.models.Date();
                                    startDate.clearTime();
                                    var endDate   =  new OMAR.models.Date({date:startDate.date});
                                    endDate.add(new OMAR.models.Duration("P24H"));
                                    fullExpression = expr.colval + " >= '" +
                                        startDate.toISOString()+"' AND " + expr.colval + " < '" +
                                        endDate.toISOString()+"'";
                                    break;
                                case "before":
                                case "after":
                                case "during":
                                    if(!OMAR.isIso8601(val))
                                    {
                                        errorMessage.message = "Value is not formatted for an ISO8601 date time and or period";
                                        errors.push(errorMessage);
                                    }
                                    fullExpression = expr.colval + " " + opval + " " + val;
                                    break;
                                default:
                                    if(!expr.val){
                                        errorMessage.message = "No value present, please input value";
                                        errors.push(errorMessage);

                                    }
                                    else if(val.toLowerCase() == "now")
                                    {
                                        val = (new OMAR.models.Date()).toISOString();
                                        fullExpression = expr.colval + " " + opval + " '" + val+"'";
                                    }
                                    else
                                    {
                                        if(!OMAR.isIso8601(val))
                                        {
                                            errorMessage.message = "Value is not a standard date format.  Please re-enter date.";
                                            errors.push(errorMessage);
                                            fullExpression = expr.colval + " " + opval + " '" + val+"'";
                                        }
                                        else if(OMAR.isInteger(val))
                                        {
                                            switch(expr.opval)
                                            {
                                                case "<":
                                                case "<=":
                                                    fullExpression = expr.colval + " " + opval + " '" + val+"-01-01T00:00:00.000Z'";
                                                    break;
                                                case ">":
                                                case ">=":
                                                    fullExpression = expr.colval + " " + opval + " '" + val+"-12-31T23:59:59.999Z'";
                                                    break;

                                            }
                                        }
                                        else
                                        {
                                            fullExpression = expr.colval + " " + opval + " '" + val+"'";
                                        }
                                    }
                                    break;
                            }
                            break;
                    }
                }
                if(errors.size()<1)
                {
                    //e.push(expr.colval + " " + opval + " " + val);
                    e.push(fullExpression);
                }
                else
                {
                    e = [];
                }
            }

            var n = [];
            var nlen = condition.nestedexpressions.length;

            for (var k = 0; k < nlen; k++) {
                var nestexpr = condition.nestedexpressions[k];
                var result = getQuery(nestexpr, errors);
                if(result)
                {
                    n.push(result);
                }
            }

            var q = [];
            if (e.length > 0)
                q.push(e.join(op));
            if (n.length > 0)
                q.push(n.join(op));

            return ['(', q.join(op), ')'].join('');
        }

        if(!errors)
        {
            errors = [];
        }
        var result = ""
        var condition = this.get("conditions");
        if(condition)
        {
            result = getQuery(condition, errors);
            if(result == "()")
            {
                result = "";
            }
        }
        return result;
    }
});

OMAR.models.CqlCollectionModel = Backbone.Collection.extend({
    url:"",
    model:OMAR.models.CqlModel,
    initialize:function(params){
    }
});

OMAR.models.ExpressionModel = Backbone.Model.extend({
   defaults:{
       coltype:""
       ,colval: ""
       ,colid :""
       ,coldisp :""
       ,opval : ""
       ,opid : ""
       ,opdisp : ""
       ,val : ""
       ,valId : ""
   }
});



OMAR.views.CqlView = Backbone.View.extend({
    el:"#cqlId",
    initialize:function(params){
        if(params)
        {
            this.wfsTypeNameModel = params.wfsTypeNameModel;
        }
        this.setElement(this.el);
        this.model = new OMAR.models.CqlModel();
        this.idIncrement = 0;
        this.cqlBtnResetEl = $(this.el).find("#cqlBtnReset");
        this.cqlBtnShowEl = $(this.el).find("#cqlBtnQuery");
        this.cqlBtnValidateEl = $(this.el).find("#cqlBtnValidate");
        this.cqlSelectNamedQueriesEl = $(this.el).find("#cqlSelectNamedQueries");
        this.columnDefs = new OMAR.models.CqlRasterColumnDefCollection();

        $(this.cqlBtnResetEl).click(this.cqlBtnResetClicked.bind(this));
        $(this.cqlBtnShowEl).click(this.cqlBtnShowClicked.bind(this));
        $(this.cqlBtnValidateEl).click(this.cqlBtnValidateClicked.bind(this));
        this.resourceImages = params.resourceImages
        this.rootCondition = '<table><tr><td class="seperator" ><img class="remove" src="'+params.resourceImages.remove +'" alt="Remove" /><select><option value="and">And</option><option value="or">Or</option></select></td>';
        this.rootCondition += '<td><div class="querystmts"></div><div><img class="add" src="'+params.resourceImages.add +'" alt="Add" /> <button class="addroot">+()</button></div>';
        this.rootCondition += '</td></tr></table>';
        this.statement = '<div><img class="remove" src="'+params.resourceImages.remove +'" alt="Remove" />'

        $(this.cqlSelectNamedQueriesEl).append("<option value=''></option>");
       // $(this.cqlSelectNamedQueriesEl).append("<option value='today'>Today</option>");

        $(this.cqlSelectNamedQueriesEl).change(this.cqlSelectNamedQueriesChanged.bind(this));

        this.wfsTypeNameModel.bind("change", this.wfsTypeNameModelChanged, this);
    },
    wfsTypeNameModelChanged:function(){
        var typeName = this.wfsTypeNameModel.get("typeName");

        if(typeName == "raster_entry")
        {
            this.columnDefs = new OMAR.models.CqlRasterColumnDefCollection();
        }
        else if(typeName == "video_data_set")
        {
            this.columnDefs = new OMAR.models.CqlVideoColumnDefCollection();
        }

        this.clearQuery();
    },
    getRootCondition:function(condition){
        var result = "";

        result  = '<table><tr><td class="seperator" ><img class="remove" src="'+this.resourceImages.remove +'" alt="Remove" />';
        if(condition)
        {
            result += ("<select>");
            var options = "";
            switch(condition.operator.toLowerCase())
            {
                case "or":
                    result += '<option value="and">And</option><option selected="selected" value="or">Or</option>';
                    break;
                case "and":
                    result += '<option selected="selected" value="and">And</option><option value="or">Or</option>';
                    break;
            }
        }
        else
        {
            result += "<select>";
            result += '<option value="and">And</option>';
            result += '<option value="or">Or</option>';
        }
        result += "</select>";
        result +='</td>';
        result += '<td><div class="querystmts"></div><div><img class="add" src="'+this.resourceImages.add +'" alt="Add" /> <button class="addroot">+()</button></div>';
        result += '</td></tr></table>';

        return result;
    },
    getStatement:function(colId, opId, expression){
        var idx    = 0;
        var baseOpId = colId+opId+"_COLOPTION_";
        var result = "";
        result =  "<select id='"+colId+"' class='col'>";

        var rawCql = {name:"raw_cql", label:"Raw CQL", type:"raw_cql"};
        var html = "<option id='"+baseOpId+idx+"' value='"+rawCql.name+"'>"+rawCql.label+"</option>";
        result += html;

        for(idx = 0; idx < this.columnDefs.size();++idx)
        {
            var name  = this.columnDefs.at(idx).get("name");
            var label = this.columnDefs.at(idx).get("label");
            html = "<option id='"+baseOpId+idx+"' value='"+name+"'>"+label+"</option>";
            result += html;
        }
        result+="</select>";
        if(expression)
        {
            result+="<select class='op' id='"+opId+"'>";
            result+=this.getOptionElements(expression.coltype, colId, opId);
        }
        else
        {
            result+="<select class='op' id='"+opId+"'>";
        }
        //       result+= "<option value='<' >Less Than</option>";
        result+= "</select>";
        var textField = "<input id='"+ colId+opId+"_TEXTFIELD' type='text'/>";
        return (this.statement+result+textField);
    },
    cqlSelectNamedQueriesChanged:function(){
        if(!$(cqlSelectNamedQueries).val())
        {
            this.clearQuery();
        }
        else
        {
            this.clearQuery();
            // alert($(cqlSelectNamedQueries).val());
            // var testCondition = '{"operator":"or","expressions":[{"coltype": "datetime", "colval": "acquisition_date", "colid": "col1op1_COLOPTION_40", "coldisp": "Acquisition Date", "opval": "<", "opid": "col1op1OPOPTION_0", "opdisp":"Today", "val": "now", "valId": "col1op1_TEXTFIELD"}],"nestedexpressions":"[]"}';
          //  var testConditionNested = '{"operator":"and","expressions":[{"coltype": "datetime", "colval": "acquisition_date", "colid": "col1op1_COLOPTION_40", "coldisp": "Acquisition Date", "opval": "today", "opid": "col1op1OPOPTION_0", "opdisp": "Today", "val": "now", "valId": "col1op1_TEXTFIELD"}, {"coltype": "numeric", "colval": "bit_depth", "colid": "col3op3_COLOPTION_19", "coldisp": "Bit Depth", "opval": "<", "opid": "col3op3OPOPTION_0", "opdisp": "Less Than", "val": "16", "valId": "col3op3_TEXTFIELD"}],"nestedexpressions":[{"operator": "and", "expressions": [{"coltype": "datetime", "colval": "ingest_date", "colid": "col2op2_COLOPTION_43", "coldisp": "Ingest Date", "opval": "today", "opid": "col2op2OPOPTION_0", "opdisp": "Today", "val": "", "valId": "col2op2_TEXTFIELD"}, {"coltype": "numeric", "colval": "grazing_angle", "colid": "col4op4_COLOPTION_26", "coldisp": "Grazing Angle", "opval": ">", "opid": "col4op4OPOPTION_2", "opdisp": "Greater Than", "val": "45", "valId": "col4op4_TEXTFIELD"}], "nestedexpressions": []}]}';


          //  this.clearQuery(JSON.parse(testConditionNested));
        }
    },
    clearQuery:function(conditions){
        $('.cqlQuery').html("");
        if(!conditions)
        {
            this.addQueryRoot('.cqlQuery', true);
        }
        else
        {
           this.setCondition(conditions, '.cqlQuery');
        }
        this.model.set("conditions", this.getCondition())
    },
    getTypesForColumn : function(col){

    },
    getCondition: function (sel) {
        var rootsel = sel;
        if(!sel) rootsel = '.cqlQuery >table';
        //Get the columns from table (to find a clean way to do it later) //tbody>tr>td
        var elem = $(rootsel).children().children().children();
        //elem 0 is for operator, elem 1 is for expressions

        var q                 = new Object();
        var expressions       = new Array();
        var nestedexpressions = new Array();

        var operator = $(elem[0]).find(':selected').val();
        q.operator = operator;

        // Get all the expressions in a condition
        var expressionelem = $(elem[1]).find('> .querystmts div');
        for (var i = 0; i < expressionelem.length; i++) {
            var col = $(expressionelem[i]).find('.col :selected');
            var op = $(expressionelem[i]).find('.op :selected');
            var textInput = $(expressionelem[i]).find(':text');
            var expressionModel = {
                coltype:col.val()=="raw_cql"?"raw_cql":this.columnDefs.get(col.val()).get("type")
                ,colval: $(col).val()
                ,colid : $(col).attr("id")
                ,coldisp : $(col).text()
                ,opval : $(op).val()
                ,opid : $(op).attr("id")
                ,opdisp : $(op).text()
                ,val : $(textInput).val()
                ,valId : $(textInput).attr("id")
            };
            expressions[i] = expressionModel;
        }
        q.expressions = expressions;

        // Get all the nested expressions
        if ($(elem[1]).find('table').length != 0) {
            var len = $(elem[1]).find('table').length;

            for (var k = 0; k < len; k++) {
                nestedexpressions[k] = this.getCondition($(elem[1]).find('table')[k]);
            }
        }
        q.nestedexpressions = nestedexpressions;
        return q;
    },
    setCondition:function(cond, sel){
        var thisPtr = this;
         if(!cond) clearQuery();
        thisPtr.idIncrement = 0;
        if(!sel)
        {
            sel = '.cqlQuery';
        }
        thisPtr.addQueryRoot(sel, true, cond);
    },
    addQueryRoot:function (sel, isroot, cond)
    {
        var thisPtr = this;
        $(sel).append(thisPtr.getRootCondition(cond));
        var q = $(sel).find('table');
        var l = q.length;
        var elem = q;
        if (l > 1) {
            elem = $(q[l - 1]);
        }
        //If root element remove the close image
        if (isroot) {
            elem.find('td >.remove').detach();
        }
        else {
            elem.find('td >.remove').click(function () {
                // td>tr>tbody>table
                $(this).parent().parent().parent().parent().detach();
            });
        }

        if(cond)
        {
            elem.find('td div > .addroot').click(function () {
                thisPtr.addQueryRoot($(this).parent(), false);
            });

            // lets comment this out for now.  without, it allows deleting
            // of all statments
            //
            //elem.find('td >.querystmts div >.remove').addClass('head');
            elem.find('td div >.add').click(function () {
                ++thisPtr.idIncrement;
                var opId2    = "op"+thisPtr.idIncrement;
                var colId2   = "col"+thisPtr.idIncrement;
                var appendedStatement = $(this).parent().siblings('.querystmts').append(thisPtr.getStatement(colId2, opId2));
                var colEl = $(appendedStatement).find("#"+colId2);
                thisPtr.columnSelectorChanged(colId2, opId2);
                $(colEl).change($.proxy(thisPtr.columnSelectorChanged, thisPtr, colId2, opId2)) ;

                var stmts = $(this).parent().siblings('.querystmts').find('div >.remove').filter(':not(.head)');
               // var stmts = $(elem).siblings('.querystmts').find('div >.remove').filter(':not(.head)');
                stmts.unbind('click');
                stmts.click(function () {
                    $(this).parent().detach();
                });
            });

            for(idx = 0; idx < cond.expressions.size();++idx)
            {
                ++thisPtr.idIncrement;
                var opId    = "op"  + thisPtr.idIncrement;
                var colId   = "col" + thisPtr.idIncrement;
                q = $(sel).find('table');
                l = q.length;
                elem = q;
                if (l > 1) {
                    elem = $(q[l - 1]);
                }
                var appendedStatement = elem.find('td >.querystmts').append(this.getStatement(colId, opId));
                $(appendedStatement).find("#"+colId).change($.proxy(thisPtr.columnSelectorChanged, thisPtr, colId, opId)) ;
                $(appendedStatement).find("#"+opId).change($.proxy(thisPtr.opSelectorChanged, thisPtr, colId, opId)) ;

               // if(idx == 0)
               //{
                   // elem.find('td >.querystmts div >.remove').addClass('head');
               // }
                var colEl = $(appendedStatement).find("#"+colId);
                var opEl = $(appendedStatement).find("#"+opId);
                $(colEl).change($.proxy(thisPtr.columnSelectorChanged, thisPtr, colId, opId)) ;
                $(opEl).change($.proxy(thisPtr.opSelectorChanged, thisPtr, colId, opId)) ;
                $(colEl).val(cond.expressions[idx].colval);
                //alert($("#"+colId +' option[value='+ cond.expressions[idx].colval +']').size());
                thisPtr.columnSelectorChanged(colId, opId);
                $(opEl).val(cond.expressions[idx].opval);
                this.opSelectorChanged(colId, opId);
                $("#"+colId+opId+"_TEXTFIELD").val(cond.expressions[idx].val);
                $(colEl).change($.proxy(thisPtr.columnSelectorChanged, thisPtr, colId, opId)) ;

                var stmts = $(elem.find('td div >.add')).parent().siblings('.querystmts').find('div >.remove').filter(':not(.head)');
                stmts.unbind('click');
                stmts.click(function () {
                        $(this).parent().detach();
                });

            }
            var elemRoot =  elem.find('td div > .addroot');
            if(cond.nestedexpressions)
            {
                var exprIdx = 0;
                for(exprIdx = 0;exprIdx<cond.nestedexpressions.size();++exprIdx)
                {
                    thisPtr.addQueryRoot(elemRoot.parent(), false, cond.nestedexpressions[exprIdx]);
                }
            }
         }
        else
        {
            ++thisPtr.idIncrement;
            var opId  = "op" + thisPtr.idIncrement;
            var colId = "col"+ thisPtr.idIncrement;

            // Add the default statement segment to the root condition
            var appendedStatement = elem.find('td >.querystmts').append(this.getStatement(colId, opId));
            $(appendedStatement).find("#"+colId).change($.proxy(thisPtr.columnSelectorChanged, thisPtr, colId, opId)) ;
            $(appendedStatement).find("#"+opId).change($.proxy(thisPtr.opSelectorChanged, thisPtr, colId, opId)) ;
            // Add the head class to the first statement
            this.columnSelectorChanged(colId, opId);
            //elem.find('td >.querystmts div >.remove').addClass('head');
            var stmts = $(elem.find('td div >.add')).parent().siblings('.querystmts').find('div >.remove').filter(':not(.head)');
            stmts.unbind('click');
            stmts.click(function () {
                $(this).parent().detach();
            });

            // Handle click for adding new statement segment
            // When a new statement is added add a condition to handle remove click.
            elem.find('td div >.add').click(function () {
                ++thisPtr.idIncrement;
                var opId2    = "op"+thisPtr.idIncrement;
                var colId2   = "col"+thisPtr.idIncrement;
                var appendedStatement = $(this).parent().siblings('.querystmts').append(thisPtr.getStatement(colId2, opId2));
                var colEl = $(appendedStatement).find("#"+colId2);
                var opEl = $(appendedStatement).find("#"+opId2);
                thisPtr.columnSelectorChanged(colId2, opId2);
                $(colEl).change($.proxy(thisPtr.columnSelectorChanged, thisPtr, colId2, opId2)) ;
                $(opEl).change($.proxy(thisPtr.opSelectorChanged, thisPtr, colId2, opId2)) ;

                var stmts = $(this).parent().siblings('.querystmts').find('div >.remove').filter(':not(.head)');
                stmts.unbind('click');
                stmts.click(function () {
                    $(this).parent().detach();
                });
            });
            // Handle click to add new root condition
            elem.find('td div > .addroot').click(function () {
                thisPtr.addQueryRoot($(this).parent(), false);
            });
        }
    },
    getOptionElements : function(colType, colId, opId){
        var result = "";
        var baseOpId = colId+opId+"OPOPTION_";

        switch(colType)
        {
            case "id":
                result+="<option id='"+baseOpId+"0' value='in' >in</option>";
                break;
            case "string":
                result+="<option id='"+baseOpId+"0' value='CONTAINS' >Contains</option>";
                result+="<option id='"+baseOpId+"1' value='iCONTAINS' >Contains (no case)</option>";
                result+="<option id='"+baseOpId+"2' value='STARTSWITH' >Starts With</option>";
                result+="<option id='"+baseOpId+"3' value='iSTARTSWITH' >Starts With (no case)</option>";
                result+="<option id='"+baseOpId+"4' value='ENDSWITH' >Ends With</option>";
                result+="<option id='"+baseOpId+"5' value='iENDSWITH' >Ends With (no case)</option>";
                result+="<option id='"+baseOpId+"6' value='is null' >IS Null</option>";
                result+="<option id='"+baseOpId+"7' value='is not null' >IS NOT Null</option>";
                result+="<option id='"+baseOpId+"8' value='=' >Equal</option>";
                result+="<option id='"+baseOpId+"9' value='in' >In</option>";
                break;
            case "numeric":
                result+="<option id='"+baseOpId+"0' value='<' >Less Than</option>";
                result+="<option id='"+baseOpId+"1' value='<=' >Less Than Equal</option>";
                result+="<option id='"+baseOpId+"2' value='>' >Greater Than</option>";
                result+="<option id='"+baseOpId+"3' value='>=' >Greater Than Equal</option>";
                result+="<option id='"+baseOpId+"4' value='=' >Equal</option>";
                result+="<option id='"+baseOpId+"5' value='is null' >IS Null</option>";
                result+="<option id='"+baseOpId+"6' value='is not null' >IS NOT Null</option>";
                result+="<option id='"+baseOpId+"6' value='in' >In</option>";
                break;
            case "datetime":
                result+="<option id='"+baseOpId+"0' value='today' >Today</option>";
                result+="<option id='"+baseOpId+"0' value='after' >After</option>";
                result+="<option id='"+baseOpId+"0' value='before' >Before</option>";
                result+="<option id='"+baseOpId+"0' value='during' >During</option>";
                result+="<option id='"+baseOpId+"1' value='<' >Less Than</option>";
                result+="<option id='"+baseOpId+"2' value='<=' >Less Than Equal</option>";
                result+="<option id='"+baseOpId+"3' value='>' >Greater Than</option>";
                result+="<option id='"+baseOpId+"4' value='>=' >Greater Than Equal</option>";
                result+="<option id='"+baseOpId+"5' value='=' >Equal</option>";
                result+="<option id='"+baseOpId+"6' value='is null' >IS Null</option>";
                result+="<option id='"+baseOpId+"7' value='is not null' >IS NOT Null</option>";
                break;
            case "geometry":
                result+="<option id='"+baseOpId+"0' value='BBOX' >BBOX</option>";
                result+="<option id='"+baseOpId+"0' value='DWITHIN' >Within</option>";
                result+="<option id='"+baseOpId+"1' value='is null' >IS Null</option>";
                result+="<option id='"+baseOpId+"2' value='is not null' >IS NOT Null</option>";
                break;
            case "raw_cql":
                result+="<option id='"+baseOpId+"0' value='RAW_CQL' >Raw CQL</option>";
                break;
        }

        return result;
    },
    columnSelectorChanged:function(colId, opId){
        //alert($("#"+col)[0]);
        var columnName = $("#"+colId).val();
        var opEl = $("#"+opId);
        var row = this.columnDefs.get(columnName);
        var textEl = $("#"+colId+opId+"_TEXTFIELD");
        $(opEl).empty();
        if(row)
        {
            $(opEl).append(this.getOptionElements(row.get("type"), colId, opId));
            $(opEl).show();
            $(textEl).val("");
            this.opSelectorChanged(colId, opId);
        }
        else if(columnName == "raw_cql")
        {
            $(textEl).show();
            $(opEl).hide();
            $(textEl).val("");
        }

    },
    opSelectorChanged:function(colId, opId){
        var columnName = $("#"+colId).val();
        var row = this.columnDefs.get(columnName);
        var opName = $("#"+opId).val().toLowerCase();
        var textEl = $("#"+opId).parent().find(":text");
        var title = "";
        if(opName)
        {
            switch(opName)
            {
                case "today":
                case "is null":
                case "is not null":
                    $(textEl).hide();
                    break;
                case "bbox":
                    $(textEl).val("<minLon>,<minLat>,<maxLon>,<maxLat>");
                    $(textEl).show();
                    $(textEl).select();
                    //$(textEl).attr("title", "This is a formatted WMS style query string");
                    break;
                case "dwithin":
                    $(textEl).val("<lat>,<lon>,<distance>,m");
                    //$(textEl).attr("title", "This is a string of the form center lat lon followed by distance then unit (m,km, or dd)");
                    break;
                default:
                    $(textEl).show();
                    //$(textEl).attr("title", "");
                    break;
            }
        }
    },

    cqlBtnValidateClicked:function(){
        var con = this.getCondition('.cqlQuery >table');
        var errors = [];
        this.model.set({conditions:con});
        var k = this.model.toCql(errors);
        if(errors.size())
        {
            alert("Number of errors: " + errors.size() + ".  First error:\n"+ errors[0].message);
            var el = $("#"+errors[0].expression.valId);
            $(el).select();
        }
        else
        {
            alert("Cql is valid!");
        }
       // var jsonCond = JSON.stringify(con);
       // if (window.console) window.console.log(jsonCond);
        //alert(jsonCond.replace("\\\"", '"'));
        //alert(jsonCond);//JSON.stringify(con));
    },
    cqlBtnResetClicked:function(){
        this.clearQuery();
    },
    cqlBtnShowClicked:function(){
        var con = this.getCondition('.cqlQuery >table');
        this.model.set({conditions:con});
        var errors = [];
        var k = this.model.toCql(errors);
        if(errors.size())
        {
            alert(errors[0].message);
            $("#"+errors[0].expression.valId).focus();
        }
        else if(k)
        {
            alert(k);
        }
    },
    toCondition:function(){
        var con = this.getCondition('.cqlQuery >table');
        if(con)
        {
            alert(con);
        }

        return con;
    },
    toCql:function(){
        var con = this.getCondition('.cqlQuery >table');
        this.model.set({conditions:con});
        var errors = [];
        var k = this.model.toCql(errors);
        if(!k||errors.size()>0)
        {
            return "";
        }
        return k;
    },
    render:function(){
        this.addQueryRoot('.cqlQuery', true);
    }
});

