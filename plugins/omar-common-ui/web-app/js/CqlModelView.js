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

OMAR.models.CqlRasterColumnDefCollection = Backbone.Collection.extend({
    model:OMAR.models.CqlColumnDef,
    initialize:function(params){
        this.add([
            {name:"be_number", label:"BE Number", type:"string"}
           ,{name:"class_name", label:"Class Name", type:"string"}
           ,{name:"country_code", label:"Country Code", type:"string"}
           ,{name:"file_type", label:"Class Name", type:"string"}
           ,{name:"filename", label:"Filename", type:"string"}
           ,{name:"image_id", label:"Image Id", type:"string"}
           ,{name:"mission", label:"Mission", type:"string"}
           ,{name:"niirs", label:"NIIRS", type:"numeric"}
           ,{name:"sensor_id", label:"Sensor Id", type:"string"}
           ,{name:"target_id", label:"Target Id", type:"string"}
           ,{name:"entry_id", label:"Entry Id", type:"string"}
           ,{name:"exclude_policy", label:"Exclude Policy", type:"string"}
           ,{name:"width", label:"Width", type:"numeric"}
           ,{name:"height", label:"Height", type:"numeric"}
           ,{name:"number_of_bands", label:"Number of Bands", type:"numeric"}
           ,{name:"number_of_res_levels", label:"Number of Res Levels", type:"numeric"}
           ,{name:"gsd_unit", label:"GSD Unit", type:"numeric"}
           ,{name:"gsd_x", label:"GSD X", type:"numeric"}
           ,{name:"gsd_y", label:"GSD Y", type:"numeric"}
           ,{name:"bit_depth", label:"Bit Depth", type:"numeric"}
           ,{name:"data_type", label:"Data Type", type:"string"}
           ,{name:"index_id", label:"Index Id", type:"string"}
           ,{name:"height", label:"Height", type:"numeric"}
           ,{name:"product_id", label:"Product Id", type:"string"}
           ,{name:"image_category", label:"Image Category", type:"string"}
           ,{name:"image_representation", label:"Image Representation", type:"string"}
           ,{name:"azimuth_angle", label:"Azimuth Angle", type:"numeric"}
           ,{name:"grazing_angle", label:"Grazing Angle", type:"numeric"}
           ,{name:"security_classification", label:"Security Classification", type:"string"}
           ,{name:"security_code", label:"Security Code", type:"string"}
           ,{name:"title", label:"Title", type:"string"}
           ,{name:"isorce", label:"Isource", type:"string"}
           ,{name:"organization", label:"Organization", type:"string"}
           ,{name:"description", label:"Description", type:"string"}
           ,{name:"organization", label:"Organization", type:"string"}
           ,{name:"wac_code", label:"Wac Code", type:"string"}
           ,{name:"sun_elevation", label:"Sun Elevation", type:"numeric"}
           ,{name:"sun_azimuth", label:"Sun Azimuth", type:"numeric"}
           ,{name:"cloud_cover", label:"Cloud Cover", type:"numeric"}
           ,{name:"style_id", label:"Style Id", type:"numeric"}
           ,{name:"keep_forever", label:"Keep Forever", type:"numeric"}
           ,{name:"ground_geom", label:"Ground Geom", type:"string"}
           ,{name:"acquisition_date", label:"Acquisition Date", type:"datetime"}
           ,{name:"valid_model", label:"Valid Model", type:"string"}
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
        function getQuery(condition, errors)
        {
            var op = [' ', condition.operator, ' '].join('');

            var e = [];
            var elen = condition.expressions.length;
            var error = false;
            for (var i = 0; ((i < elen)&&(!error)); i++) {
                var expr = condition.expressions[i];

                //
                //alert(expr.colval + ", " + expr.opval);

                var val = expr.val;
                var opval = expr.opval;
                var fullExpression = "";
                if(!error)
                {
                    var colType = expr.coltype;
                    //var colDef = this.columnDefs.get(expr.colval);
                    switch(colType)
                    {
                        case "string":
                            if(!expr.val){
                                errors.push({expression:expr,
                                             message:"No value present, please input value"});
                                //alert("No value present, please input value");
                               // $("#"+expr.valId).focus();
                            }
                            switch(expr.opval)
                            {
                                case "contains":
                                    opval = "like";
                                    val = "'%"+val+"%'";
                                    break;
                                case "startswith":
                                    opval = "like";
                                    val = "'"+val+"%'";
                                    break;
                                case "endswith":
                                    opval = "like";
                                    val = "'%"+val+"'";
                                    break;
                                default:
                                    val = "'"+val+"'";
                                    break;
                            }
                            fullExpression = expr.colval + " " + opval + " " + val;
                            break;
                        case "numeric":
                            if(!expr.val){
                               // alert("No value present, please input value");
                               // $("#"+expr.valId).focus();
                                errors.push({expression:expr,
                                    message:"No value present, please input value"});
                            }
                            if(!OMAR.isFloat(val))
                            {
                                errors.push({expression:expr,
                                    message:"Value is not a number, please fix the value"});
                            }
                            fullExpression = expr.colval + " " + opval + " " + val;
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
                                default:
                                    if(!expr.val){
                                        errors.push({expression:expr,
                                            message:"No value present, please input value"});
                                    }
                                    fullExpression = expr.colval + " " + opval + " " + val;
                                    break;
                            }
                            break;
                    }
                }
                if(!errors.size())
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
            for (var k = 0; ((k < nlen)&&!error); k++) {
                var nestexpr = condition.nestedexpressions[k];
                var result = this.getQuery(nestexpr);
                if(result)
                {
                    n.push(result);
                }
                else
                {
                    error = true;
                }
            }

            var q = [];
            if (e.length > 0)
                q.push(e.join(op));
            if (n.length > 0)
                q.push(n.join(op));

            if(error)
            {
                return "";
            }
            return ['(', q.join(op), ')'].join('');
        }

        if(!errors)
        {
            errors = [];
        }
        return getQuery(this.get("conditions"), errors);
    }
});

OMAR.models.CqlCollectionModel = Backbone.Collection.extend({
    url:"",
    model:OMAR.models.CqlModel,
    initialize:function(params){
    }
});


OMAR.views.CqlView = Backbone.View.extend({
    el:"#cqlId",
    initialize:function(params){
        this.setElement(this.el);
        this.model = new OMAR.models.CqlModel();
        this.idIncrement = 0;
        this.cqlEnabledCheckboxEl = $(this.el).find("#cqlEnabledCheckbox");
        this.cqlBtnResetEl = $(this.el).find("#cqlBtnReset");
        this.cqlBtnQueryEl = $(this.el).find("#cqlBtnQuery");
        this.cqlBtnValidateEl = $(this.el).find("#cqlBtnValidate");
        this.cqlSelectNamedQueriesEl = $(this.el).find("#cqlSelectNamedQueries");
        this.columnDefs = new OMAR.models.CqlRasterColumnDefCollection();

        $(this.cqlBtnResetEl).click(this.cqlBtnResetClicked.bind(this));
        $(this.cqlBtnQueryEl).click(this.cqlBtnQueryClicked.bind(this));
        $(this.cqlBtnValidateEl).click(this.cqlBtnValidateClicked.bind(this));
        $(this.cqlEnabledCheckboxEl).click(this.cqlEnabledCheckboxClicked.bind(this));
        this.rootCondition = '<table><tr><td class="seperator" ><img class="remove" src="'+params.resourceImages.remove +'" alt="Remove" /><select><option value="and">And</option><option value="or">Or</option></select></td>';
        this.rootCondition += '<td><div class="querystmts"></div><div><img class="add" src="'+params.resourceImages.add +'" alt="Add" /> <button class="addroot">+()</button></div>';
        this.rootCondition += '</td></tr></table>';
        this.statement = '<div><img class="remove" src="'+params.resourceImages.remove +'" alt="Remove" />'

        $(this.cqlSelectNamedQueriesEl).append("<option value=''></option>");
        $(this.cqlSelectNamedQueriesEl).append("<option value='today'>Today</option>");
        $(this.cqlSelectNamedQueriesEl).change(this.cqlSelectNamedQueriesChanged.bind(this));
    },
    getStatement:function(colId, opId){
        var idx    = 0;
        var baseOpId = colId+opId+"COLOPTION_";
        var result = "<select id='"+colId+"' class='col'>";

        for(idx = 0; idx < this.columnDefs.size();++idx)
        {
            var name  = this.columnDefs.at(idx).get("name");
            var label = this.columnDefs.at(idx).get("label");
            var html = "<option id='"+baseOpId+idx+"' value='"+name+"'>"+label+"</option>";
            result += html;
        }
        result+="</select>";
        result+= "<select class='op' id='"+opId+"'>";
 //       result+= "<option value='<' >Less Than</option>";
        result+= "</select>";
        var textField = "<input id='"+ colId+opId+"TEXTFIELD' type='text'/>";
        return (this.statement+result+textField);
    },
    cqlSelectNamedQueriesChanged:function(){
        alert($(cqlSelectNamedQueries).val());
        this.clearQuery(false);
    },
    clearQuery:function(conditions){
        $('.cqlQuery').html("");
        if(!conditions)
        {
            this.addQueryRoot('.cqlQuery', true)
        }
    },
    getTypesForColumn : function(col){

    },
    getCondition: function (rootsel) {
        //Get the columns from table (to find a clean way to do it later) //tbody>tr>td
        var elem = $(rootsel).children().children().children();
        //elem 0 is for operator, elem 1 is for expressions

        var q = {};
        var expressions = [];
        var nestedexpressions = [];

        var operator = $(elem[0]).find(':selected').val();
        q.operator = operator;

        // Get all the expressions in a condition
        var expressionelem = $(elem[1]).find('> .querystmts div');
        for (var i = 0; i < expressionelem.length; i++) {
            var col = $(expressionelem[i]).find('.col :selected');
            var op = $(expressionelem[i]).find('.op :selected');
            var textInput = $(expressionelem[i]).find(':text');
            expressions[i] =  {
                "coltype":this.columnDefs.get(col.val()).get("type")
                ,"colval": col.val()
                ,"colid" : $(col).attr('id')
                ,"coldisp" : col.text()
                ,"opval" : op.val()
                ,"opid" : $(op).attr("id")
                ,"opdisp" : op.text()
                ,"val" : textInput.val()
                ,"valId" : $(textInput).attr("id")
            };
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
    addQueryRoot:function (sel, isroot)
    {
        var thisPtr = this;

        $(sel).append(thisPtr.rootCondition);
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
        ++thisPtr.idIncrement;
        var opId = "op"+thisPtr.idIncrement;
        var colId = "col"+thisPtr.idIncrement;

        // Add the default staement segment to the root condition
        var appendedStatement = elem.find('td >.querystmts').append(this.getStatement(colId, opId));
        $(appendedStatement).find("#"+colId).change($.proxy(thisPtr.columnSelectorChanged, thisPtr, colId, opId)) ;
        // Add the head class to the first statement
        this.columnSelectorChanged(colId, opId);
        elem.find('td >.querystmts div >.remove').addClass('head');

        // Handle click for adding new statement segment
        // When a new statement is added add a condition to handle remove click.
        elem.find('td div >.add').click(function () {
            ++thisPtr.idIncrement;
            var opId2    = "op"+thisPtr.idIncrement;
            var colId2   = "col"+thisPtr.idIncrement;
            var appendedStatement = $(this).parent().siblings('.querystmts').append(thisPtr.getStatement(colId2, opId2));
            var colEl = $(appendedStatement).find("#"+colId2);
            thisPtr.columnSelectorChanged(colId2, opId2);
            $(colEl).change($.proxy(thisPtr.columnSelectorChanged, thisPtr, colId2, opId2)) ;

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
    },
    columnSelectorChanged:function(col, op){
       //alert($("#"+col)[0]);
        var columnName = $("#"+col).val();
        var opEl = $("#"+op);
        var row = this.columnDefs.get(columnName);
        if(row)
        {
            var baseOpId = col+op+"OPOPTION_";
            switch(row.get("type"))
            {
                case "string":
                    $(opEl).empty();
                   /* $(opEl).append("<option id='"+baseOpId+"0' value='<' >Less Than</option>");
                    $(opEl).append("<option id='"+baseOpId+"1' value='>' >Greater Than</option>");
                    $(opEl).append("<option id='"+baseOpId+"3' value='like' >Like</option>");
                    */
                    $(opEl).append("<option id='"+baseOpId+"0' value='contains' >Contains</option>");
                    $(opEl).append("<option id='"+baseOpId+"1' value='=' >Equal</option>");
                    $(opEl).append("<option id='"+baseOpId+"2' value='startswith' >Starts With</option>");
                    $(opEl).append("<option id='"+baseOpId+"3' value='endswith' >Ends With</option>");
                    break;
                case "numeric":
                    $(opEl).empty();
                    $(opEl).append("<option id='"+baseOpId+"0' value='<' >Less Than</option>");
                    $(opEl).append("<option id='"+baseOpId+"1' value='<=' >Less Than Equal</option>");
                    $(opEl).append("<option id='"+baseOpId+"2' value='>' >Greater Than</option>");
                    $(opEl).append("<option id='"+baseOpId+"3' value='>=' >Greater Than Equal</option>");
                    $(opEl).append("<option id='"+baseOpId+"4' value='=' >Equal</option>");
                    break;
                case "datetime":
                    $(opEl).empty();
                    $(opEl).append("<option id='"+baseOpId+"0' value='today' >Today</option>");
                    $(opEl).append("<option id='"+baseOpId+"1' value='<' >Less Than</option>");
                    $(opEl).append("<option id='"+baseOpId+"2' value='<=' >Less Than Equal</option>");
                    $(opEl).append("<option id='"+baseOpId+"3' value='>' >Greater Than</option>");
                    $(opEl).append("<option id='"+baseOpId+"4' value='>=' >Greater Than Equal</option>");
                    $(opEl).append("<option id='"+baseOpId+"5' value='=' >Equal</option>");
                    break;
            }
        }
//        var op = $(this.el).find("#"+op);
//        $(op).empty();
    },
    cqlBtnValidateClicked:function(){
        var con = this.getCondition('.cqlQuery >table');
        var errors = [];
        this.model.set({conditions:con});
        var k = this.model.toCql(errors);
        if(errors.size())
        {
            alert("Number of errors: " + errors.size() + ".  First error:\n"+ errors[0].message);
            $("#"+errors[0].expression.valId).focus();
        }
        else
        {
            alert("Cql is valid!");
        }
    },
    cqlBtnResetClicked:function(){
      this.clearQuery();
    },
    cqlBtnQueryClicked:function(){
        var con = this.getCondition('.cqlQuery >table');
        this.model.set({conditions:con});
        var errors = [];
        var k = this.model.toCql(errors);
        if(errors.size())
        {
            alert(errors[0].message);
            $("#"+errors[0].expression.valId).focus();
        }
        if(k)
        {
            alert(k);
        }
    },
    cqlEnabledCheckboxClicked:function(){
    },
    toCondition:function(){
        var con = this.getCondition('.cqlQuery >table');
        if(con)
        {
            alert(con);
        }
    },
    toCql:function(){
        if($(this.cqlEnabledCheckboxEl).attr("checked") != "checked")
        {
            return "";
        }
        var con = this.getCondition('.cqlQuery >table');
        this.model.set({conditions:con});
        var k = this.model.toCql();

        if(!k) return "";

        return k;
    },
    render:function(){
        this.addQueryRoot('.cqlQuery', true);
    }
});