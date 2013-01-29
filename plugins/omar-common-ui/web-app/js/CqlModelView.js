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
           ,{name:"acquisition_date", label:"Acquisition Date", type:"string"}
           ,{name:"valid_model", label:"Valid Model", type:"string"}
           ,{name:"access_date", label:"Access Date", type:"string"}
           ,{name:"ingest_date", label:"Ingest Date", type:"string"}
           ,{name:"receive_date", label:"Recieve Date", type:"string"}
           ,{name:"file_type", label:"File Type", type:"string"}
           ,{name:"other_tags_xml", label:"Other Tags XML", type:"string"}
        ]);
    }
});

OMAR.models.CqlModel = Backbone.Model.extend({
    idAttribute:"id",
    defaults:{
        id:"",
        name:"",
        cql:"",
        cqlState:""
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
        this.idIncrement = 0;
        this.cqlBtnConditionEl = $(this.el).find("#cqlBtnCondition");
        this.cqlBtnQueryEl = $(this.el).find("#cqlBtnQuery");
        this.columnDefs = new OMAR.models.CqlRasterColumnDefCollection();

        $(this.cqlBtnConditionEl).click(this.cqlBtnConditionClicked.bind(this));
        $(this.cqlBtnQueryEl).click(this.cqlBtnQueryClicked.bind(this));

        this.rootCondition = '<table><tr><td class="seperator" ><img src="../images/res/remove.gif" alt="Remove" class="remove" /><select><option value="and">And</option><option value="or">Or</option></select></td>';
        this.rootCondition += '<td><div class="querystmts"></div><div><img class="add" src="../images/res/add.gif" alt="Add" /> <button class="addroot">+()</button></div>';
        this.rootCondition += '</td></tr></table>';

        this.statement = '<div><img src="../images/res/remove.gif" alt="Remove" class="remove" />'
    },
    getStatement:function(colId, opId){
        var idx    = 0;
        var result = "<select id='"+colId+"' class='col'>";

        for(idx = 0; idx < this.columnDefs.size();++idx)
        {
            var name  = this.columnDefs.at(idx).get("name");
            var label = this.columnDefs.at(idx).get("label");
            var html = "<option value='"+name+"'>"+label+"</option>";
            result += html;
        }
        result+="</select>";
        result+= "<select class='op' id='"+opId+"'>";
 //       result+= "<option value='<' >Less Than</option>";
        result+= "</select>";
        return (this.statement+result+"<input type='text'/>");
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
            expressions[i] = {};
            var col = $(expressionelem[i]).find('.col :selected');
            var op = $(expressionelem[i]).find('.op :selected');
            expressions[i].colval = col.val();
            expressions[i].coldisp = col.text();
            expressions[i].opval = op.val();
            expressions[i].opdisp = op.text();
            expressions[i].val = $(expressionelem[i]).find(':text').val();
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
    getQuery:function (condition)
    {
        var op = [' ', condition.operator, ' '].join('');

        var e = [];
        var elen = condition.expressions.length;
        for (var i = 0; i < elen; i++) {
            var expr = condition.expressions[i];

           // alert("WE CAN FORMAT AND VALIDATE HERE!" + this.columnDefs.get(expr.colval));
            // add tests for ops HERE!!
            //

            e.push(expr.colval + " " + expr.opval + " " + expr.val);
        }

        var n = [];
        var nlen = condition.nestedexpressions.length;
        for (var k = 0; k < nlen; k++) {
            var nestexpr = condition.nestedexpressions[k];
            var result = this.getQuery(nestexpr);
            n.push(result);
        }

        var q = [];
        if (e.length > 0)
            q.push(e.join(op));
        if (n.length > 0)
            q.push(n.join(op));

        return ['(', q.join(op), ')'].join(' ');
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
            this.columnSelectorChanged(colId2, opId2);
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
        var op = $("#"+op);
        var row = this.columnDefs.get(columnName);
        if(row)
        {
            switch(row.get("type"))
            {
                case "string":
                    $(op).empty();
                    $(op).append("<option value='<' >Less Than</option>");
                    $(op).append("<option value='>' >Greater Than</option>");
                    $(op).append("<option value='=' >Equal</option>");
                    $(op).append("<option value='like' >Like</option>");
                    break;
                case "numeric":
                    $(op).empty();
                    $(op).append("<option value='<' >Less Than</option>");
                    $(op).append("<option value='>' >Greater Than</option>");
                    $(op).append("<option value='=' >Equal</option>");
                    break;
            }
        }
//        var op = $(this.el).find("#"+op);
//        $(op).empty();
    },
    cqlBtnConditionClicked:function(){
        var query = {};
        query = this.getCondition('.cqlQuery > table');
        //var l = JSON.stringify(query,null,4);
        var l = JSON.stringify(query);
        alert(l);

    },
    cqlBtnQueryClicked:function(){
        var con = this.getCondition('.cqlQuery >table');
        var k = this.getQuery(con);
        alert(k);
    },
    render:function(){
        this.addQueryRoot('.cqlQuery', true);
    }
});