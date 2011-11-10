/*
 * This code is based on the following blog entry http://blog.davglass.com/files/yui/cal2/
 * with some modifications.
 *
 *
 *
 */
function DateChooser()
{
    var cal;
    var over_cal = false;
    var displayContainer;
    var inputId;
    var structId;
    var format;
    var locale;
    var firstDayOfWeek;
    var focusCallback = null;
    var blurCallback = null;
    var changeCallback = null;
    var navigator = false;

    this.setDisplayContainer = function( container )
    {
        displayContainer = container;
    }

    this.setInputId = function( id )
    {
        inputId = id;
    }

    this.setStructId = function( id )
    {
        structId = id;
    }

    this.setFormat = function( dateFormat )
    {
        format = dateFormat;
    }

    this.setLocale = function( dateLocale )
    {
        locale = dateLocale;
    }

    this.setFirstDayOfWeek = function( day )
    {
        firstDayOfWeek = day;
    }

    this.setFocusCallback = function( callback )
    {
        focusCallback = callback;
    }

    this.setBlurCallback = function( callback )
    {
        blurCallback = callback;
    }

    this.setChangeCallback = function( callback )
    {
        changeCallback = callback;
    }

    this.setNavigator = function( nav )
    {
        navigator = nav;
    }

    this.init = function()
    {
        cal = new YAHOO.widget.Calendar( "cal" + inputId, displayContainer, { LOCALE_WEEKDAYS:"short", MULTI_SELECT: false, navigator: navigator} );

        if ( format == "dd.MM.yyyy" )
        {
            //Correct formats for Germany: dd.MM.yyyy, dd.MM, MM.yyyy
            cal.cfg.setProperty( "DATE_FIELD_DELIMITER", "." );
            cal.cfg.setProperty( "MDY_DAY_POSITION", 1 );
            cal.cfg.setProperty( "MDY_MONTH_POSITION", 2 );
            cal.cfg.setProperty( "MDY_YEAR_POSITION", 3 );
            cal.cfg.setProperty( "MD_DAY_POSITION", 1 );
            cal.cfg.setProperty( "MD_MONTH_POSITION", 2 );
        }
        else if ( format == "yyyy-MM-dd" )
        {
            //Correct ISO date formats : yyyy-MM-dd, MM-dd, yyyy-MM
            cal.cfg.setProperty( "DATE_FIELD_DELIMITER", "-" );
            cal.cfg.setProperty( "MDY_DAY_POSITION", 3 );
            cal.cfg.setProperty( "MDY_MONTH_POSITION", 2 );
            cal.cfg.setProperty( "MDY_YEAR_POSITION", 1 );
            cal.cfg.setProperty( "MD_DAY_POSITION", 2 );
            cal.cfg.setProperty( "MD_MONTH_POSITION", 1 );
            cal.cfg.setProperty( "MY_MONTH_POSITION", 2 );
            cal.cfg.setProperty( "MY_YEAR_POSITION", 1 );
        }
        else if ( format == "dd-MM-yyyy" )
        {
            cal.cfg.setProperty( "DATE_FIELD_DELIMITER", "-" );
            cal.cfg.setProperty( "MDY_DAY_POSITION", 1 );
            cal.cfg.setProperty( "MDY_MONTH_POSITION", 2 );
            cal.cfg.setProperty( "MDY_YEAR_POSITION", 3 );
            cal.cfg.setProperty( "MD_DAY_POSITION", 1 );
            cal.cfg.setProperty( "MD_MONTH_POSITION", 2 );
        }
        else if ( format == "dd/MM/yyyy" )
        {
            cal.cfg.setProperty( "DATE_FIELD_DELIMITER", "/" );
            cal.cfg.setProperty( "MDY_DAY_POSITION", 1 );
            cal.cfg.setProperty( "MDY_MONTH_POSITION", 2 );
            cal.cfg.setProperty( "MDY_YEAR_POSITION", 3 );
            cal.cfg.setProperty( "MD_DAY_POSITION", 1 );
            cal.cfg.setProperty( "MD_MONTH_POSITION", 2 );
        }

        if ( locale == "de" )
        {
            //Date labels for German locale
            cal.cfg.setProperty( "MONTHS_SHORT", ["Jan", "Feb", "M\u00E4r", "Apr", "Mai", "Jun", "Jul", "Aug", "Sep", "Okt", "Nov", "Dez"] );
            cal.cfg.setProperty( "MONTHS_LONG", ["Januar", "Februar", "M\u00E4rz", "April", "Mai", "Juni", "Juli", "August", "September", "Oktober", "November", "Dezember"] );
            cal.cfg.setProperty( "WEEKDAYS_1CHAR", ["S", "M", "D", "M", "D", "F", "S"] );
            cal.cfg.setProperty( "WEEKDAYS_SHORT", ["So", "Mo", "Di", "Mi", "Do", "Fr", "Sa"] );
            cal.cfg.setProperty( "WEEKDAYS_MEDIUM", ["Son", "Mon", "Die", "Mit", "Don", "Fre", "Sam"] );
            cal.cfg.setProperty( "WEEKDAYS_LONG", ["Sonntag", "Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag", "Samstag"] );
            cal.cfg.setProperty( "START_WEEKDAY", 1 );
        }

        if ( locale == "it" )
        {
            //Date labels for Italian locale
            cal.cfg.setProperty( "MONTHS_SHORT", ["Gen", "Feb", "Mar", "Apr", "Mag", "Giu", "Lug", "Ago", "Set", "Ott", "Nov", "Dic"] );
            cal.cfg.setProperty( "MONTHS_LONG", ["Gennaio", "Febbraio", "Marzo", "Aprile", "Maggio", "Giugno", "Luglio", "Agosto", "Settembre", "Ottobre", "Novembre", "Dicembre"] );
            cal.cfg.setProperty( "WEEKDAYS_1CHAR", ["D", "L", "M", "M", "G", "V", "S"] );
            cal.cfg.setProperty( "WEEKDAYS_SHORT", ["Do", "Lu", "Ma", "Me", "Gi", "Ve", "Sa"] );
            cal.cfg.setProperty( "WEEKDAYS_MEDIUM", ["Dom", "Lun", "Mar", "Mer", "Gio", "Ven", "Sab"] );
            cal.cfg.setProperty( "WEEKDAYS_LONG", ["Domenica", "Lunedi", "Martedi", "Mercoledi", "Giovedi", "Venerdi", "Sabato"] );
        }

        if ( firstDayOfWeek != null )
        {
            cal.cfg.setProperty( "START_WEEKDAY", firstDayOfWeek );
        }

        cal.selectEvent.subscribe( getDate, cal, true );
        cal.renderEvent.subscribe( setupListeners, cal, true );

        YAHOO.util.Event.addListener( inputId, 'focus', focus );
        //YAHOO.util.Event.addListener(inputId, 'focus', showCal);
        YAHOO.util.Event.addListener( inputId, 'blur', blur );
        //YAHOO.util.Event.addListener(inputId, 'blur', hideCal);
        YAHOO.util.Event.addListener( inputId, 'change', change );
        //YAHOO.util.Event.addListener(inputId, 'change', dateChange);


        cal.render();
        hideCal();
    }

    function setupListeners()
    {
        YAHOO.util.Event.addListener( displayContainer, 'mouseover', overCal );
        YAHOO.util.Event.addListener( displayContainer, 'mouseout', outCal );
    }

    function focus()
    {
        showCal();

        if ( focusCallback != null )
        {
            eval( focusCallback );
        }
    }

    function blur()
    {
        hideCal();

        if ( blurCallback != null )
        {
            eval( blurCallback );
        }
    }

    function change()
    {
        dateChange();

        if ( changeCallback != null )
        {
            eval( changeCallback );
        }
    }

    function getDate()
    {
        var calDate = this.getSelectedDates()[0];

        var day = calDate.getDate();
        var month = (calDate.getMonth() + 1);
        var year = calDate.getFullYear();

        if ( day < 10 )
        {
            day = '0' + day;
        }
        if ( month < 10 )
        {
            month = '0' + month;
        }

        if ( format == "dd.MM.yyyy" )
        {
            calDate = day + '.' + month + '.' + year;
        }
        else if ( format == "MM/dd/yyyy" )
        {
            calDate = month + '/' + day + '/' + year;
        }
        else if ( format == "dd-MM-yyyy" )
        {
            calDate = day + '-' + month + '-' + year;
        }
        else if ( format == "dd/MM/yyyy" )
        {
            calDate = day + '/' + month + '/' + year;
        }
        else if ( format == "yyyy-MM-dd" )
        {
            calDate = year + '-' + month + '-' + day;
        }

        updateStruct( day, month, year );

        YAHOO.util.Dom.get( inputId ).value = calDate;

        over_cal = false;
        hideCal();

        if ( changeCallback != null )
        {
            eval( changeCallback );
        }
    }

    function updateStruct( day, month, year )
    {
        YAHOO.util.Dom.get( structId + "_day" ).value = day;
        YAHOO.util.Dom.get( structId + "_month" ).value = month;
        YAHOO.util.Dom.get( structId + "_year" ).value = year;
    }

    function dateChange()
    {
        var date = YAHOO.util.Dom.get( inputId ).value;

        if ( date )
        {
            var day;
            var month;
            var year;

            if ( date == "" )
            {
                day = "";
                month = "";
                year = "";
            }
            else
            {
                if ( format == "dd.MM.yyyy" )
                {
                    var date = date.split( '.' );

                    if ( date.length == 3 )
                    {
                        day = date[0];
                        month = date[1];
                        year = date[2];
                    }
                }
                else if ( format == "MM/dd/yyyy" )
                {
                    var date = date.split( '/' );

                    if ( date.length == 3 )
                    {
                        day = date[1];
                        month = date[0];
                        year = date[2];
                    }
                } else if ( format == "dd-MM-yyyy" )
                {
                    var date = date.split( '-' );

                    if ( date.length == 3 )
                    {
                        day = date[0];
                        month = date[1];
                        year = date[2];
                    }
                } else if ( format == "dd/MM/yyyy" )
                {
                    var date = date.split( '/' );

                    if ( date.length == 3 )
                    {
                        day = date[0];
                        month = date[1];
                        year = date[2];
                    }
                } else if ( format == "yyyy-MM-dd" )
                {
                    var date = date.split( '-' );

                    if ( date.length == 3 )
                    {
                        day = date[2];
                        month = date[1];
                        year = date[0];
                    }
                }
            }

            updateStruct( day, month, year );
        }
        else
        {
            updateStruct( "", "", "" );
        }
    }

    function showCal()
    {
        var xy = YAHOO.util.Dom.getXY( inputId );
        var date = YAHOO.util.Dom.get( inputId ).value;

        if ( date )
        {
            var tmpDate = new Date();

            if ( format == "dd.MM.yyyy" )
            {
                var date = date.split( '.' );

                if ( date.length == 3 )
                {
                    tmpDate = new Date( date[2], date[1] - 1, date[0] );
                }
            }
            else if ( format == "MM/dd/yyyy" )
            {
                var date = date.split( '/' );

                if ( date.length == 3 )
                {
                    tmpDate = new Date( date[2], date[0] - 1, date[1] );
                }
            }
            else if ( format == "yyyy-MM-dd" )
            {
                var date = date.split( '-' );

                if ( date.length == 3 )
                {
                    tmpDate = new Date( date[0], date[1] - 1, date[2] );
                }
            }
            else if ( format == "dd-MM-yyyy" )
            {
                var date = date.split( '-' );

                if ( date.length == 3 )
                {
                    tmpDate = new Date( date[2], date[1] - 1, date[0] );
                }
            }
            else if ( format == "dd/MM/yyyy" )
            {
                var date = date.split( '/' );

                if ( date.length == 3 )
                {
                    tmpDate = new Date( date[2], date[1] - 1, date[0] );
                }
            }

            //if date is not valid
            if ( isNaN( tmpDate.getYear() ) ) tmpDate = null;

            cal.cfg.setProperty( "PAGEDATE", tmpDate );
            cal.select( tmpDate );
            cal.render();
        }

        YAHOO.util.Dom.setStyle( displayContainer, 'display', 'block' );
        xy[1] = xy[1] + 20;
        YAHOO.util.Dom.setXY( displayContainer, xy );
    }

    function hideCal()
    {
        if ( !over_cal )
        {
            YAHOO.util.Dom.setStyle( displayContainer, 'display', 'none' );
        }
    }

    function overCal()
    {
        over_cal = true;
    }

    function outCal()
    {
        over_cal = false;
    }
}