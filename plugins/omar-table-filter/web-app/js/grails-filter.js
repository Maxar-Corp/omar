// LIKE,EQUALS,GREATER,LESS,BETWEEN

var numeric = new Array( "EQUALS", "GREATER", "LESS", "BETWEEN" );
var boolean = new Array( "EQUALS" );
var date = new Array( "EQUALS", "GREATER", "LESS", "BETWEEN" );
var text = new Array( "LIKE", "EQUALS" );

var temp = "<option value='\EQUALS\'>EQUALS</option>"

/**
 * Called to update field values when criteria changes.
 */
function criteriaChanged()
{

    if ( $( 'filterCriteria' ).value == "BETWEEN" )
    {
        $( 'filterValue2' ).style.display = "";
    }
    else
    {

        $( 'filterValue2' ).style.display = "none";
        $( 'filterValue2' ).value = "";

    }

}

/**
 * Called to update criteria, when field changes
 * @param obj
 */
function fieldChanged( obj )
{

    var type = obj.options[obj.selectedIndex].getAttribute( 'fieldtype' );

    var options;

    switch ( type )
    {
    case "numeric":
        options = parseOptions( numeric );
        break;
    case "text":
        options = parseOptions( text );
        break;
    case "date":
        options = parseOptions( date );
        break;
    case "boolean":
        options = parseOptions( boolean );
        break;
    default:
        options = "<option value=''>-Choose operator-</option>"
    }

    Element.update( 'filterCriteria', options );

    if ( type == "boolean" && $( 'filterValue' ).type == "text" )
    {

        Element.replace( 'filterValue', "<select id=\'filterValue\' name=\'filterValue\'><option></option><option value=\'TRUE\'>TRUE</option><option value=\'FALSE\'>FALSE</option></select>" );

    }
    else if ( $( 'filterValue' ).type == "select-one" )
    {

        Element.replace( 'filterValue', "<input type=\'text\' id=\'filterValue\' name=\'filterValue\'/>" )
    }

    $('filterValue').value = '';
}

/**
 * Called when the filter is initialized.
 */
function filterInitialized()
{
    $( 'filterError' ).update( '' );
    $( 'filterBusy' ).style.display = "";
}

/**
 * Called when filter action finishes.
 */
function filterFinished()
{
    $( 'filterBusy' ).style.display = "none";
}

/**
 * Parses list and creates options for select tag.
 * @param list
 */
function parseOptions( list )
{
    var options = "";
    list.each(
            function ( i )
            {
                options += "<option value=\'" + i + "\'>" + i + "</option>";
            }
    )
    return options;
}