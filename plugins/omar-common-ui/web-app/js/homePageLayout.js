/**
 * Created by IntelliJ IDEA.
 * User: sbortman
 * Date: 1/30/12
 * Time: 10:48 AM
 * To change this template use File | Settings | File Templates.
 */


(function ()
{
    var Dom = YAHOO.util.Dom,
            Event = YAHOO.util.Event;

    Event.onDOMReady( function ()
    {
        var layout = new YAHOO.widget.Layout( {
            units:[
                { position:'top', height:75, body:'top1' },
                { position:'bottom', height:25, body:'bottom1' },
                { position:'center', body:'center1', scroll:true }
            ]
        } );
        layout.on( 'render', function ()
        {
            Dom.setStyle( document.body, 'visibility', 'visible' );
        } );
        layout.render();
    } );
})();