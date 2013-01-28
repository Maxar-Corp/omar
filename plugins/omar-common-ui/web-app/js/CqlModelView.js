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
        this.cqlBtnConditionEl = $(this.el).find("#cqlBtnCondition");
        this.cqlBtnQueryEl = $(this.el).find("#cqlBtnQuery");

        $(this.cqlBtnConditionEl).click(this.cqlBtnConditionClicked.bind(this));
        $(this.cqlBtnQueryEl).click(this.cqlBtnQueryClicked.bind(this));

        this.rootCondition = '<table><tr><td class="seperator" ><img src="../images/res/remove.gif" alt="Remove" class="remove" /><select><option value="and">And</option><option value="or">Or</option></select></td>';
        this.rootCondition += '<td><div class="querystmts"></div><div><img class="add" src="../images/res/add.gif" alt="Add" /> <button class="addroot">+()</button></div>';
        this.rootCondition += '</td></tr></table>';

        this.statement = '<div><img src="../images/res/remove.gif" alt="Remove" class="remove" />'

        this.rasterStatment = '<select class="col">';
        this.rasterStatment += '<option value="filename">Filename</option>';
        this.rasterStatment += '<option value="image_id">IID</option>';
        this.rasterStatment += '<option value="be_number">BE</option>';
        this.rasterStatment += '<option value="target_id">Target ID</option>';
        this.rasterStatment += '<option value="product_id">Product ID</option>';
        this.rasterStatment += '<option value="sensor_id">Sensor</option>';
        this.rasterStatment += '<option value="mission_id">Mission</option>';
        this.rasterStatment += '</select>';
        this.rasterStatment += '<select class="op">';
        this.rasterStatment += '<option value="contains">contains</option>';
        this.rasterStatment += '<option value="g8">differs from field</option>';
        this.rasterStatment += '</select>'
        this.rasterStatment += '<input type="text" /></div>';

        this.statement += this.rasterStatment;
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

        // Add the default staement segment to the root condition
        elem.find('td >.querystmts').append(this.statement);

        // Add the head class to the first statement
        elem.find('td >.querystmts div >.remove').addClass('head');

        // Handle click for adding new statement segment
        // When a new statement is added add a condition to handle remove click.
        elem.find('td div >.add').click(function () {
            $(this).parent().siblings('.querystmts').append(thisPtr.statement);
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