/**
 * Created with IntelliJ IDEA.
 * User: gpotts
 * Date: 2/8/13
 * Time: 10:40 AM
 * To change this template use File | Settings | File Templates.
 */
$.fn.dataTableExt.oPagination.input = {
    "fnInit": function ( oSettings, nPaging, fnCallbackDraw )
    {
        var nDropDown = document.createElement( 'select' );
        var nFirst = document.createElement( 'button' );
        var nPrevious = document.createElement( 'button' );
        var nNext = document.createElement( 'button' );
        var nLast = document.createElement( 'button' );
        var nInput = document.createElement( 'input' );
        var nPage = document.createElement( 'span' );
        var nOf = document.createElement( 'span' );
        var nDropDownId = "";
        var nFirstId = "";
        var nPagingId = "";
        var nPreviousId = "";
        var nNextId = "";
        var nLastId = "";
        var nInputId = "";
        var nPageId = "";
        var nOfId = "";
        nFirst.innerHTML = oSettings.oLanguage.oPaginate.sFirst;
        nPrevious.innerHTML = oSettings.oLanguage.oPaginate.sPrevious;
        nNext.innerHTML = oSettings.oLanguage.oPaginate.sNext;
        nLast.innerHTML = oSettings.oLanguage.oPaginate.sLast;

        nFirst.className    = "paginate_button first";
        nPrevious.className = "paginate_button previous";
        nNext.className     = "paginate_button next";
        nLast.className     = "paginate_button last";
        nOf.className       = "paginate_of";
        nPage.className     = "paginate_page";

        nDropDownId    = oSettings.sTableId+'_lengthMenu';
        nFirstId    = oSettings.sTableId+'_first';
        nPagingId   = oSettings.sTableId+'_paginate';
        nPreviousId = oSettings.sTableId+"_previous";
        nNextId  = oSettings.sTableId+"_next";
        nLastId  = oSettings.sTableId+"_last";
        nInputId = oSettings.sTableId+"_input";
        nPageId = oSettings.sTableId+"_page";
        nOfId = oSettings.sTableId+"_of";
        nPaging.setAttribute( 'id', nPagingId );
        nDropDown.setAttribute( 'id', nDropDownId );
        nFirst.setAttribute( 'id',  nFirstId);
        nPrevious.setAttribute( 'id', nPreviousId );
        nNext.setAttribute( 'id', nNextId );
        nLast.setAttribute( 'id', nLastId );
        nInput.setAttribute( 'id', nInputId );
        nPage.setAttribute( 'id', nPageId );
        nOf.setAttribute( 'id', nOfId );

        nInput.type = "text";
        nInput.style.width = "15px";
        nInput.style.display = "inline";
        nPage.innerHTML = "Page ";

        nPaging.appendChild( nDropDown );
        nPaging.appendChild( nFirst );
        nPaging.appendChild( nPrevious );
        nPaging.appendChild( nPage );
        nPaging.appendChild( nInput );
        nPaging.appendChild( nOf );
        nPaging.appendChild( nNext );
        nPaging.appendChild( nLast );

        function adjustPage(currentValue)
        {
            var iCurrentPage = Math.ceil(oSettings._iDisplayStart / oSettings._iDisplayLength) +1;

            if(currentValue != iCurrentPage)
            {
                var iNewStart = oSettings._iDisplayLength * (currentValue - 1);
                if ( iNewStart > oSettings.fnRecordsDisplay() )
                {
                    /* Display overrun */
                    oSettings._iDisplayStart = (Math.ceil((oSettings.fnRecordsDisplay()-1) /
                        oSettings._iDisplayLength)-1) * oSettings._iDisplayLength;
                    fnCallbackDraw( oSettings );
                    return;
                }

                oSettings._iDisplayStart = iNewStart;
                oSettings._iDisplayEnd = iNewStart + oSettings._iDisplayLength;
                fnCallbackDraw( oSettings );
            }
        }

        $(oSettings.aLengthMenu).each(function(idx, v){
            $(nDropDown).append("<option value='"+v+"'>"+v+"</option>");
        });
        $(nDropDown).val(oSettings._iDisplayLength);

        $(nDropDown).change(function(){
            oSettings._iDisplayLength = parseInt($(this).val());
            fnCallbackDraw( oSettings );
        });
        $(nFirst).click( function () {
            $(nInput).val(1);
            adjustPage($(nInput).val());
            //oSettings.oApi._fnPageChange( oSettings, "first" );
            // fnCallbackDraw( oSettings );
        } );

        $(nPrevious).click( function() {
            var value = parseInt($(nInput).val());

            if($(nInput).val()>1)
            {
                --value;
                $(nInput).val(value);
                adjustPage(value);
            }
        } );

        $(nNext).click( function() {
            var iPages = Math.ceil((oSettings.fnRecordsDisplay()) / oSettings._iDisplayLength);
            var value = parseInt($(nInput).val());
            if(value<iPages)
            {
                ++value;
                $(nInput).val(value);
                adjustPage(value);
            }
        } );

        $(nLast).click( function() {
            var iPages = Math.ceil((oSettings.fnRecordsDisplay()) / oSettings._iDisplayLength);
            var value = parseInt($(nInput).val());
            if(value!=iPages)
            {
                $(nInput).val(iPages);
                adjustPage(iPages);
            }
        } );

        $(nInput).change( function (e) {
            adjustPage(this.value);
        });

        $(nInput).keyup( function (e) {
            var iPages = Math.ceil((oSettings.fnRecordsDisplay()) / oSettings._iDisplayLength);

            if ( e.which == 38 || e.which == 39 )
            {
                if(this.value != iPages)
                {
                    this.value++;
                    adjustPage(this.value);
                }
            }
            else if ( (e.which == 37 || e.which == 40) && this.value > 1 )
            {
                if(this.value >1)
                {
                    this.value--;
                    adjustPage(this.value);
                }
            }
            if(this.value == 13)
            {
                adjustPage(this.value);
            }
            if ( this.value == "" || this.value.match(/[^0-9]/) )
            {
                return;
            }

        } );

        /* Take the brutal approach to cancelling text selection */
        $('button', nPaging).bind( 'mousedown', function () { return false; } );
        $('button', nPaging).bind( 'selectstart', function () { return false; } );
        $('span', nPaging).bind( 'mousedown', function () { return false; } );
        $('span', nPaging).bind( 'selectstart', function () { return false; } );
    },


    "fnUpdate": function ( oSettings, fnCallbackDraw )
    {
        if ( !oSettings.aanFeatures.p )
        {
            return;
        }
        var iPages = Math.ceil((oSettings.fnRecordsDisplay()) / oSettings._iDisplayLength);
        var iCurrentPage = Math.ceil(oSettings._iDisplayStart / oSettings._iDisplayLength)+1;
        if(iCurrentPage > iPages)
        {
            iCurrentPage = iPages;
        }
        /* Loop over each instance of the pager */
        var an = oSettings.aanFeatures.p;
        for ( var i=0, iLen=an.length ; i<iLen ; i++ )
        {
            var spans = an[i].getElementsByTagName('span');
            var inputs = an[i].getElementsByTagName('input');
            spans[1].innerHTML = " of "+iPages;
            inputs[0].value = iCurrentPage;
        }
        var nFirstId    = "#"+oSettings.sTableId+'_first';
        var nPreviousId = "#"+oSettings.sTableId+"_previous";
        var nNextId  = "#"+oSettings.sTableId+"_next";
        var nLastId  = "#"+oSettings.sTableId+"_last";
        if(iCurrentPage == 1)
        {
            $(nFirstId).attr('disabled', 'disabled');
            $(nPreviousId).attr('disabled', 'disabled');
        }
        else
        {
            $(nFirstId).removeAttr('disabled');
            $(nPreviousId).removeAttr('disabled');
        }
        if(iCurrentPage == iPages)
        {
            $(nNextId).attr('disabled', 'disabled');
            $(nLastId).attr('disabled', 'disabled');
        }
        else
        {
            $(nNextId).removeAttr('disabled');
            $(nLastId).removeAttr('disabled');
        }
    }
};