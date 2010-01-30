var Dom = YAHOO.util.Dom;
var Event = YAHOO.util.Event;

Event.onDOMReady( function()
{
  var layout2 = new YAHOO.widget.Layout( {
    units: [
      { position: 'top', height: 25, body: 'header' },
      { position: 'bottom', height: 25, body: 'footer' },
      { position: 'center', body: 'main' }
    ]
  } );

  layout2.on( 'render', function()
  {
    var el = layout2.getUnitByPosition( 'center' ).get( 'wrap' );


    var layout1 = new YAHOO.widget.Layout( el, {
      parent: layout2,
      units: [
        { position: 'top', height: 70, body: 'top1', header: 'Top', gutter: '5px', collapse: true, resize: true },
        { position: 'right', header: 'Right', width: 300, resize: true, gutter: '5px', /*footer: 'Footer',*/ collapse: true, scroll: true, body: 'right1', animate: true },
        { position: 'bottom', header: 'Bottom', height: 100, resize: true, body: 'bottom1', gutter: '5px', collapse: true },
        { position: 'left', header: 'Left', width: 200, resize: true, body: 'left1', gutter: '5px', collapse: true, /*close: true,collapseSize: 50,*/  scroll: true, animate: true },
        { position: 'center', body: 'center1' }
      ]
    } );

    layout1.on( 'render', function()
    {
      /*
       layout.getUnitByPosition( 'left' ).on( 'close', function()
       {
       closeLeft( );
       } );
       */
    } );

    layout1.on( 'resize', function()
    {
      var c = layout1.getUnitByPosition( 'center' );
      var mapWidth = c.get( 'width' );
      var mapHeight = c.get( 'height' );
      
      resized( mapWidth, mapHeight );
    } );

    layout1.render( );
    init( );

    /*
     Event.on( 'tLeft', 'click', function( ev )
     {
     Event.stopEvent( ev );
     layout.getUnitByPosition( 'left' ).toggle( );
     } );

     Event.on( 'tRight', 'click', function( ev )
     {
     Event.stopEvent( ev );
     layout.getUnitByPosition( 'right' ).toggle( );
     } );

     Event.on( 'padRight', 'click', function( ev )
     {
     Event.stopEvent( ev );
     var pad = prompt( 'CSS gutter to apply: ("2px" or "2px 4px" or any combination of the 4 sides)', layout.getUnitByPosition( 'right' ).get( 'gutter' ) );
     layout.getUnitByPosition( 'right' ).set( 'gutter', pad );
     } );

     var closeLeft = function()
     {
     var a = document.createElement( 'a' );
     a.href = '#';
     a.innerHTML = 'Add Left Unit';
     Dom.get( 'closeLeft' ).parentNode.appendChild( a );

     Dom.setStyle( 'tLeft', 'display', 'none' );
     Dom.setStyle( 'closeLeft', 'display', 'none' );
     Event.on( a, 'click', function( ev )
     {
     Event.stopEvent( ev );
     Dom.setStyle( 'tLeft', 'display', 'inline' );
     Dom.setStyle( 'closeLeft', 'display', 'inline' );
     a.parentNode.removeChild( a );
     layout.addUnit( layout.get( 'units' )[3] );
     layout.getUnitByPosition( 'left' ).on( 'close', function()
     {
     closeLeft( );
     } );
     } );
     };


     Event.on( 'closeLeft', 'click', function( ev )
     {
     Event.stopEvent( ev );
     layout.getUnitByPosition( 'left' ).close( );
     } );
     */

  } );


  layout2.render( );


} );