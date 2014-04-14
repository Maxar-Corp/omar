//Helps in creating a structure will help later.
function makeStruct( names )
{
    var names = names.split( ' ' );
    var count = names.length;

    function constructor()
    {
        for ( var i = 0; i < count; i++ )
        {
            this[names[i]] = arguments[i];
        }
    }

    return constructor;
}

ConditionBuilderModel = Backbone.Model.extend( {} );

ConditionBuilderView = Backbone.View.extend( {
    initialize: function ()
    {
        // Initialize rootcondition - start
        this.rootcondition = '<table><tr><td class="seperator" ><div class="remove" alt="Remove" class="remove"></div><select><option value="and">And</option><option value="or">Or</option></select></td>';
        this.rootcondition += '<td><div class="querystmts"></div><div><div class="add" alt="Add"></div> <button class="addroot">+()</button></div>';
        this.rootcondition += '</td></tr></table>';
        // Initialize rootcondition - end

        // Initalize statement - start
        this.statement = '<div class="querystmt"><div class="remove" alt="Remove" class="remove"></div>';

        // Initialize columns dropdown - start
        var columns = this.model.get( 'columns' );
        this.statement += '<select class="col">';
        for ( var i = 0; i < columns.length; i++ )
        {
            this.statement += '<option value="' + columns[i].field + '">' + columns[i].title + '</option>';
        }
        this.statement += '</select>';
        // Initialize columns dropdown - end

        // Initialize operators dropdown start
        var operators = this.model.get( 'operators' );
        this.statement += '<select class="op">';
        for ( var i = 0; i < operators.length; i++ )
        {
            this.statement += '<option value="' + operators[i].field + '">' + operators[i].title + '</option>';
        }
        this.statement += '</select>';
        // Initialize operators dropdown start

        this.statement += '<input type="text" /></div>';
        // Initalize statement - end

        this.render();
    },
    render: function ()
    {
        this.addqueryroot( '.query', true );
    },
    addqueryroot: function ( sel, isroot )
    {
        $( sel ).append( this.rootcondition );
        var q = $( sel ).find( 'table' );
        var l = q.length;
        var elem = q;
        if ( l > 1 )
        {
            elem = $( q[l - 1] );
        }

        //If root element remove the close image
        if ( isroot )
        {
            elem.find( 'td > .remove' ).detach();
        }
        else
        {
            elem.find( 'td > .remove' ).click( function ( e )
            {
                // td>tr>tbody>table
                $( e.target ).parent().parent().parent().parent().detach();
            } );
        }

        // Add the default staement segment to the root condition
        elem.find( 'td > .querystmts' ).append( this.statement );

        // Add the head class to the first statement
        //elem.find( 'td >.querystmts div >.remove' ).addClass( 'head' );

        // Handle click for adding new statement segment
        // When a new statement is added add a condition to handle remove click.
        elem.find( 'td div > .add' ).click( {view: this}, function ( e )
        {
            $( e.target ).parent().siblings( '.querystmts' ).append( e.data.view.statement );
            var stmts = $( this ).parent().siblings( '.querystmts' ).find( 'div >.remove' ); //.filter( ':not(.head)' );
            stmts.unbind( 'click' );
            stmts.click( function ()
            {
                $( this ).parent().detach();
            } );
        } );

        // Handle click to add new root condition
        elem.find( 'td div > .addroot' ).click( {view: this}, function ( e )
        {
            e.data.view.addqueryroot( $( e.target ).parent(), false );
        } );
    },
    //Recursive method to iterate over the condition tree and generate the query
    getQuery: function ( condition )
    {
        var op = [' ', condition.operator, ' '].join( '' );

        var e = [];
        var elen = condition.expressions.length;
        for ( var i = 0; i < elen; i++ )
        {
            var expr = condition.expressions[i];
            e.push( expr.colval + " " + expr.opval + " " + expr.val );
        }

        var n = [];
        var nlen = condition.nestedexpressions.length;
        for ( var k = 0; k < nlen; k++ )
        {
            var nestexpr = condition.nestedexpressions[k];
            var result = this.getQuery( nestexpr );
            n.push( result );
        }

        var q = [];
        if ( e.length > 0 )
        {
            q.push( e.join( op ) );
        }

        if ( n.length > 0 )
        {
            q.push( n.join( op ) );
        }

        return ['(', q.join( op ), ')'].join( ' ' );
    },
    //Recursive method to parse the condition and generate the query. Takes the selector for the root condition
    getCondition: function ( rootsel )
    {
        //Get the columns from table (to find a clean way to do it later) //tbody>tr>td
        var elem = $( rootsel ).children().children().children();
        //elem 0 is for operator, elem 1 is for expressions

        var q = {};
        var expressions = [];
        var nestedexpressions = [];

        var operator = $( elem[0] ).find( ':selected' ).val();
        q.operator = operator;

        // Get all the expressions in a condition
        var expressionelem = $( elem[1] ).find( '> .querystmts .querystmt' );
        for ( var i = 0; i < expressionelem.length; i++ )
        {
            expressions[i] = {};
            var col = $( expressionelem[i] ).find( '.col :selected' );
            var op = $( expressionelem[i] ).find( '.op :selected' );
            expressions[i].colval = col.val();
            expressions[i].coldisp = col.text();
            expressions[i].opval = op.val();
            expressions[i].opdisp = op.text();
            expressions[i].val = $( expressionelem[i] ).find( ':text' ).val();
        }
        q.expressions = expressions;

        // Get all the nested expressions
        if ( $( elem[1] ).find( '> div > table' ).length != 0 )
        {
            var len = $( elem[1] ).find( '> div > table' ).length;

            for ( var k = 0; k < len; k++ )
            {
                nestedexpressions[k] = this.getCondition( $( elem[1] ).find( '> div > table' )[k] );
            }
        }
        q.nestedexpressions = nestedexpressions;

        return q;
    }
} );


