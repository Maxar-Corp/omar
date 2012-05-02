
/*******************************************************************************
 * This notice must be untouched at all times.
 *
 * CSS Sandpaper: smooths out differences between CSS implementations.
 *
 * This javascript library contains routines to implement the CSS transform,
 * box-shadow and gradient in IE.  It also provides a common syntax for other
 * browsers that support vendor-specific methods.
 *
 * Written by: Zoltan Hawryluk. Version 1.0 beta 1 completed on March 8, 2010.
 *
 * Some routines are based on code from CSS Gradients via Canvas v1.2
 * by Weston Ruter <http://weston.ruter.net/projects/css-gradients-via-canvas/>
 *
 * Requires sylvester.js by James Coglan http://sylvester.jcoglan.com/
 *
 * cssSandpaper.js v.1.0 beta 1 available at http://www.useragentman.com/
 *
 * released under the MIT License:
 *   http://www.opensource.org/licenses/mit-license.php
 *
 ******************************************************************************/
//if (!document.querySelectorAll) {
//    document.querySelectorAll = cssQuery;
//}

var cssSandpaper = new function(){
    var me = this;
    
    var styleNodes, styleSheets = new Array();
    
    var ruleSetRe = /[^\{]*{[^\}]*}/g;
    var ruleSplitRe = /[\{\}]/g;
    
    var reGradient = /gradient\([\s\S]*\)/g;
    var reHSL = /hsl\([\s\S]*\)/g;
    
    // This regexp from the article 
    // http://james.padolsey.com/javascript/javascript-comment-removal-revisted/
    var reMultiLineComment = /\/\/.+?(?=\n|\r|$)|\/\*[\s\S]+?\*\//g;
    
    var reAtRule = /@[^\{\};]*;|@[^\{\};]*\{[^\}]*\}/g;
    
    var reFunctionSpaces = /\(\s*/g
    
    
    var ruleLists = new Array();
    var styleNode;
    
    var tempObj;
    var body;
    
    
    me.init = function(reinit){
   
        if (EventHelpers.hasPageLoadHappened(arguments) && !reinit) {
            return;
        }
		
        body = document.body;
        
        tempObj = document.createElement('div');
    }   
    
    me.setTransform = function(obj, transformString){
        var property = CSS3Helpers.findProperty(obj, 'transform');
        
        if (property == "filter") {
            var matrix = CSS3Helpers.getTransformationMatrix(transformString);
            CSS3Helpers.setMatrixFilter(obj, matrix)
        } else if (obj.style[property] != null) {
            obj.style[property] = transformString;
        }
    }
    

    /**
	 * Convert a hyphenated string to camelized text.  For example, the string "font-type" will be converted
	 * to "fontType".
	 * 
	 * @param {Object} s - the string that needs to be camelized.
	 * @return {String} - the camelized text.
	 */
	me.camelize = function (s) {
		var r="";
		
		for (var i=0; i<s.length; i++) {
			if (s.substring(i, i+1) == '-') {
				i++;
				r+= s.substring(i, i+1).toUpperCase();
			} else {
				r+= s.substring(i, i+1);
			}
		}
		
		return r;
	}


    me.getProperties = function (obj, objName)
	{
		var result = ""
		
		if (!obj) {
			return result;
		}
		
		for (var i in obj)
		{
			try {
				result += objName + "." + i.toString() + " = " + obj[i] + ", ";
			} catch (ex) {
				// nothing
			}
		}
		return result
	}
        
    
}

var MatrixGenerator = new function(){
    var me = this;
    var reUnit = /[a-z]+$/;
    me.identity = $M([[1, 0, 0], [0, 1, 0], [0, 0, 1]]);
    
    
    function degreesToRadians(degrees){
        return (degrees - 360) * Math.PI / 180;
    }
    
    function getRadianScalar(angleStr){
    
        var num = parseFloat(angleStr);
        var unit = angleStr.match(reUnit);
		
		
		if (angleStr.trim() == '0') {
			num = 0;
			unit = 'rad';
		}
        
        if (unit.length != 1 || num == 0) {
            return 0;
        }
        
        
        unit = unit[0];
        
        
        var rad;
        switch (unit) {
            case "deg":
                rad = degreesToRadians(num);
                break;
            case "rad":
                rad = num;
                break;
            default:
                throw "Not an angle: " + angleStr;
        }
        return rad;
    }
    
    me.prettyPrint = function(m){
        return StringHelpers.sprintf('| %s %s %s | - | %s %s %s | - |%s %s %s|', m.e(1, 1), m.e(1, 2), m.e(1, 3), m.e(2, 1), m.e(2, 2), m.e(2, 3), m.e(3, 1), m.e(3, 2), m.e(3, 3))
    }
    
    me.rotate = function(angleStr){
        var num = getRadianScalar(angleStr);
        return Matrix.RotationZ(num);
    }
    
    me.scale = function(sx, sy){
        sx = parseFloat(sx)
        
        if (!sy) {
            sy = sx;
        } else {
            sy = parseFloat(sy)
        }
        
        
        return $M([[sx, 0, 0], [0, sy, 0], [0, 0, 1]]);
    }
    
    me.scaleX = function(sx){
        return me.scale(sx, 1);
    }
    
    me.scaleY = function(sy){
        return me.scale(1, sy);
    }
    
    me.skew = function(ax, ay){
        var xRad = getRadianScalar(ax);
        var yRad;
        
        if (ay != null) {
            yRad = getRadianScalar(ay)
        } else {
            yRad = xRad
        }
		
		if (xRad != null && yRad != null) {
			
			return $M([[1, Math.tan(xRad), 0], [Math.tan(yRad), 1, 0], [0, 0, 1]]);
		} else {
			return null;
		}
    }
    
    me.skewX = function(ax){
    
        return me.skew(ax, "0");
    }
    
    me.skewY = function(ay){
        return me.skew("0", ay);
    }
    
    me.translate = function(tx, ty){
    
        var TX = parseInt(tx);
        var TY = parseInt(ty)
        
        //jslog.debug(StringHelpers.sprintf('translate %f %f', TX, TY));
        
        return $M([[1, 0, TX], [0, 1, TY], [0, 0, 1]]);
    }
    
    me.translateX = function(tx){
        return me.translate(tx, 0);
    }
    
    me.translateY = function(ty){
        return me.translate(0, ty);
    }
    
    
    me.matrix = function(a, b, c, d, e, f){
    
        // for now, e and f are ignored
        return $M([[a, c, parseInt(e)], [b, d, parseInt(f)], [0, 0, 1]])
    }
}

var CSS3Helpers = new function(){
    var me = this;
    
    
    var reTransformListSplitter = /[a-zA-Z]+\([^\)]*\)\s*/g;
    
    var reLeftBracket = /\(/g;
    var reRightBracket = /\)/g;
    var reComma = /,/g;
    
    var reSpaces = /\s+/g
    
    var reFilterNameSplitter = /progid:([^\(]*)/g;
    
    var reLinearGradient
    
    var canvas;
    
    var cache = new Array();
    
    
    me.supports = function(cssProperty){
        if (CSS3Helpers.findProperty(document.body, cssProperty) != null) {
            return true;
        } else {
            return false;
        }
    }
    
    me.getCanvas = function(){
    
        if (canvas) {
            return canvas;
        } else {
            canvas = document.createElement('canvas');
            return canvas;
        }
    }
    
    me.getTransformationMatrix = function(CSS3TransformProperty, doThrowIfError){
    
        var transforms = CSS3TransformProperty.match(reTransformListSplitter);
		
		/*
		 * Do a check here to see if there is anything in the transformation
		 * besides legit transforms
		 */
		if (doThrowIfError) {
			var checkString = transforms.join(" ").replace(/\s*/g, ' ');
			var normalizedCSSProp = CSS3TransformProperty.replace(/\s*/g, ' ');
			
			if (checkString != normalizedCSSProp) {
				throw ("An invalid transform was given.")	
			}
		}
		
		
        var resultantMatrix = MatrixGenerator.identity;
        
        for (var j = 0; j < transforms.length; j++) {
        
            var transform = transforms[j];
			
            transform = transform.replace(reLeftBracket, '("').replace(reComma, '", "').replace(reRightBracket, '")');
            
            
            try {
                var matrix = eval('MatrixGenerator.' + transform);
				
				
                //jslog.debug( transform + ': ' + MatrixGenerator.prettyPrint(matrix))
                resultantMatrix = resultantMatrix.x(matrix);
            } 
            catch (ex) {
            	
				if (doThrowIfError) {
					var method = transform.split('(')[0];

					var funcCall = transform.replace(/\"/g, '');

					if (MatrixGenerator[method]  == undefined) {
						throw "Error: invalid tranform function: " + funcCall;
					} else {
						throw "Error: Invalid or missing parameters in function call: " + funcCall;

					}
				}
                // do nothing;
            }
        }
        
        return resultantMatrix;
        
    }


     
    function swapIndices(array, index1, index2){
        var tmp = array[index1];
        array[index1] = array[index2];
        array[index2] = tmp;
    }
    
      
    function normalizePercentage(s){
        if (s.substring(s.length - 1, s.length) == '%') {
            return parseFloat(s) / 100 + "";
        } else {
            return s;
        }
        
    }
    
    
     
    
    me.getBracketedSubstring = function(s, header){
        var gradientIndex = s.indexOf(header + '(')
        
        if (gradientIndex != -1) {
            var substring = s.substring(gradientIndex);
            
            var openBrackets = 1;
            for (var i = header.length + 1; i < 100 || i < substring.length; i++) {
                var c = substring.substring(i, i + 1);
                switch (c) {
                    case "(":
                        openBrackets++;
                        break;
                    case ")":
                        openBrackets--;
                        break;
                }
                
                if (openBrackets == 0) {
                    break;
                }
                
            }
            
            return substring.substring(gradientIndex + header.length + 1, i);
        }
        
        
    }
    
    
    me.setMatrixFilter = function(obj, matrix){
	
	
		if (!hasIETransformWorkaround(obj)) {
			addIETransformWorkaround(obj)
		}
		var container = obj.parentNode;
		//container.xTransform = degrees;
		
		var filter = obj.filters.item('DXImageTransform.Microsoft.Matrix');
		//jslog.debug(MatrixGenerator.prettyPrint(matrix))
		filter.M11 = matrix.e(1, 1);
		filter.M12 = matrix.e(1, 2);
		filter.M21 = matrix.e(2, 1);
		filter.M22 = matrix.e(2, 2);
		
		
		// Now, adjust the margins of the parent object
		var offsets = me.getIEMatrixOffsets(obj, matrix, container.xOriginalWidth, container.xOriginalHeight);
		container.style.marginLeft = offsets.x;
		container.style.marginTop = offsets.y;
		container.style.marginRight = 0;
		container.style.marginBottom = 0;
	}
	
	me.getTransformedDimensions = function (obj, matrix) {
		var r = {};
		
		if (hasIETransformWorkaround(obj)) {
			r.width = obj.offsetWidth;
			r.height = obj.offsetHeight;
		} else {
			var pts = [
				matrix.x($V([0, 0, 1]))	,
				matrix.x($V([0, obj.offsetHeight, 1])),
				matrix.x($V([obj.offsetWidth, 0, 1])),
				matrix.x($V([obj.offsetWidth, obj.offsetHeight, 1]))
			];
			var maxX = 0, maxY =0, minX=0, minY=0;
			
			for (var i = 0; i < pts.length; i++) {
				var pt = pts[i];
				var x = pt.e(1), y = pt.e(2);
				var minX = Math.min(minX, x);
				var maxX = Math.max(maxX, x);
				var minY = Math.min(minY, y);
				var maxY = Math.max(maxY, y);
			}
			
			
				r.width = maxX - minX;
				r.height = maxY - minY;
				 
		}
		
		return r;
	}
	
	me.getIEMatrixOffsets = function (obj, matrix, width, height) {
        var r = {};
		
		var originalWidth = parseFloat(width);
		var originalHeight = parseFloat(height);
		
		
        var offset;
        if (CSSHelpers.getComputedStyle(obj, 'display') == 'inline') {
            offset = 0;
        } else {
            offset = 13; // This works ... don't know why.
        }
		var transformedDimensions = me.getTransformedDimensions(obj, matrix);
        
        r.x = (((originalWidth - transformedDimensions.width) / 2) - offset + matrix.e(1, 3)) + 'px';
        r.y  = (((originalHeight - transformedDimensions.height) / 2) - offset + matrix.e(2, 3)) + 'px';
        
		return r;
    }
    
    function hasIETransformWorkaround(obj){
    
        return CSSHelpers.isMemberOfClass(obj.parentNode, 'IETransformContainer');
    }
    
    function addIETransformWorkaround(obj){
        if (!hasIETransformWorkaround(obj)) {
            var parentNode = obj.parentNode;
            var filter;
            
            // This is the container to offset the strange rotation behavior
            var container = document.createElement('div');
            CSSHelpers.addClass(container, 'IETransformContainer');
            
            
            container.style.width = obj.offsetWidth + 'px';
            container.style.height = obj.offsetHeight + 'px';
            
            container.xOriginalWidth = obj.offsetWidth;
            container.xOriginalHeight = obj.offsetHeight;
            container.style.position = 'absolute'
            container.style.zIndex = obj.currentStyle.zIndex;
            
            
            var horizPaddingFactor = 0; //parseInt(obj.currentStyle.paddingLeft); 
            var vertPaddingFactor = 0; //parseInt(obj.currentStyle.paddingTop);
            if (obj.currentStyle.display == 'block') {
                container.style.left = obj.offsetLeft + 13 - horizPaddingFactor + "px";
                container.style.top = obj.offsetTop + 13 + -vertPaddingFactor + 'px';
            } else {
                container.style.left = obj.offsetLeft + "px";
                container.style.top = obj.offsetTop + 'px';
                
            }
            //container.style.float = obj.currentStyle.float;
            
            
            obj.style.top = "auto";
            obj.style.left = "auto"
            obj.style.bottom = "auto";
            obj.style.right = "auto";
            // This is what we need in order to insert to keep the document
            // flow ok
            var replacement = obj.cloneNode(true);
            replacement.style.visibility = 'hidden';
            
            obj.replaceNode(replacement);
            
            // now, wrap container around the original node ... 
            
            obj.style.position = 'absolute';
            container.appendChild(obj);
            parentNode.insertBefore(container, replacement);
            container.style.backgroundColor = 'transparent';
            
            container.style.padding = '0';
            
            filter = me.addFilter(obj, 'DXImageTransform.Microsoft.Matrix', "M11=1, M12=0, M21=0, M22=1, sizingMethod='auto expand'")
            var bgImage = obj.currentStyle.backgroundImage.split("\"")[1];
            /*
            
             
            
             if (bgImage) {
            
             
            
             var alphaFilter = me.addFilter(obj, "DXImageTransform.Microsoft.AlphaImageLoader", "src='" + bgImage + "', sizingMethod='scale'");
            
             
            
             alert(bgImage)
            
             
            
             alphaFilter.src = bgImage;
            
             
            
             sizingMethod = 'scale';
            
             
            
             obj.style.background = 'none';
            
             
            
             obj.style.backgroundImage = 'none';
            
             
            
             }
            
             
            
             */
            
        }
        
    }
    
    me.addFilter = function(obj, filterName, filterValue){
        // now ... insert the filter so we can exploit its wonders
        var filter;
        try {
            //alert("addFilter: " + obj.className + ", " + filterName + ", " + filterValue);
            filter = obj.filters.item(filterName);
        } 
        catch (ex) {
            // dang! We have to go through all of them and make sure filter
            // is set right before we add the new one.
            
            var filterList = new MSFilterList(obj)
            
            filterList.fixFilterStyle();
            
            var comma = ", ";
            
            if (obj.filters.length == 0) {
                comma = "";
            }
            
            obj.style.filter += StringHelpers.sprintf("%sprogid:%s(%s)", comma, filterName, filterValue);
            filter = obj.filters.item(filterName);
            
        }
        
        return filter;
    }
    
    
    function degreesToRadians(degrees){
        return (degrees - 360) * Math.PI / 180;
    }
    
    me.findProperty = function(obj, type){
        capType = type.capitalize();
        
        var r = cache[type]
        if (!r) {
        
        
            var style = obj.style;
            
            
            var properties = [type, 'Moz' + capType, 'Webkit' + capType, 'O' + capType, 'filter'];
            for (var i = 0; i < properties.length; i++) {
                if (style[properties[i]] != null) {
                    r = properties[i];
                    break;
                }
            }
            
            if (r == 'filter' && document.body.filters == undefined) {
                r = null;
            }
            cache[type] = r;
        }
        return r;
    }
    
    /*
     * "A point is a pair of space-separated values. The syntax supports numbers,
     *  percentages or the keywords top, bottom, left and right for point values."
     *  This keywords and percentages into pixel equivalents
     */
    me.parseCoordinate = function(value, max){
        //Convert keywords
        switch (value) {
            case 'top':
            case 'left':
                return 0;
            case 'bottom':
            case 'right':
                return max;
            case 'center':
                return max / 2;
        }
        
        //Convert percentage
        if (value.indexOf('%') != -1) 
            value = parseFloat(value.substr(0, value.length - 1)) / 100 * max;
        //Convert bare number (a pixel value)
        else 
            value = parseFloat(value);
        if (isNaN(value)) 
            throw Error("Unable to parse coordinate: " + value);
        return value;
    }
      
}

function MSFilterList(node){
    var me = this;
    
    me.list = new Array();
    me.node = node;
    
    var reFilterListSplitter = /[\s\S]*\([\s\S]*\)/g;
    
    var styleAttr = node.style;
    
    function init(){
    
        var filterCalls = styleAttr.filter.match(reFilterListSplitter);
        
        if (filterCalls != null) {
        
            for (var i = 0; i < filterCalls.length; i++) {
                var call = filterCalls[i];
                
                me.list.push(new MSFilter(node, call));
                
            }
        }
        
        
    }
    
    me.toString = function(){
        var sb = new StringBuffer();
        
        for (var i = 0; i < me.list.length; i++) {
        
            sb.append(me.list[i].toString());
            if (i < me.list.length - 1) {
                sb.append(',')
            }
        }
        return sb.toString();
    }
    
    
    me.fixFilterStyle = function(){
    
        try {
            me.node.style.filter = me.toString();
        } 
        catch (ex) {
            // do nothing.
        }
        
    }
    
    init();
}

function MSFilter(node, filterCall){
    var me = this;
    
    me.node = node;
    me.filterCall = filterCall;
    
    var reFilterNameSplitter = /progid:([^\(]*)/g;
    var reParameterName = /([a-zA-Z0-9]+\s*)=/g;
    
    
    function init(){
        me.name = me.filterCall.match(reFilterNameSplitter)[0].replace('progid:', '');
        
        //This may not be the best way to do this.
        var parameterString = filterCall.split('(')[1].replace(')', '');
        me.parameters = parameterString.match(reParameterName);
        
        for (var i = 0; i < me.parameters.length; i++) {
            me.parameters[i] = me.parameters[i].replace('=', '');
        }
        
    }
    
    me.toString = function(){
    
        var sb = new StringBuffer();
        
        sb.append(StringHelpers.sprintf('progid:%s(', me.name));
        
        for (var i = 0; i < me.parameters.length; i++) {
            var param = me.parameters[i];
            var filterObj = me.node.filters.item(me.name);
            var paramValue = filterObj[param];
            if (typeof(paramValue) == 'string') {
                sb.append(StringHelpers.sprintf('%s="%s"', param, filterObj[param]));
            } else {
                sb.append(StringHelpers.sprintf('%s=%s', param, filterObj[param]));
            }
            
            if (i != me.parameters.length - 1) {
                sb.append(', ')
            }
        }
        sb.append(')');
        
        return sb.toString();
    }
    
    
    
    init();
}

var implementation = new function(){
    this.NONE = 0;
    
    // Native Support.
    this.NATIVE = 1;
    
    // Vendor specific prefix implementations
    this.MOZILLA = 2;
    this.WEBKIT = 3;
    this.IE = 4;
    this.OPERA = 5;
    
    // Non CSS3 Workarounds 
    this.CANVAS_WORKAROUND = 6;
    this.FILTER_WORKAROUND = 7;
    this.HEX_WORKAROUND = 8;
}

var colorType = new function () {
	this.BACKGROUND = 0;
	this.FOREGROUND = 1;
}

/*
 * Extra helper routines
 */
if (!window.StringHelpers) {
StringHelpers = new function(){
    var me = this;
    
    // used by the String.prototype.trim()			
    me.initWhitespaceRe = /^\s\s*/;
    me.endWhitespaceRe = /\s\s*$/;
    me.whitespaceRe = /\s/;
    
    /*******************************************************************************
     * Function sprintf(format_string,arguments...) Javascript emulation of the C
     * printf function (modifiers and argument types "p" and "n" are not supported
     * due to language restrictions)
     *
     * Copyright 2003 K&L Productions. All rights reserved
     * http://www.klproductions.com
     *
     * Terms of use: This function can be used free of charge IF this header is not
     * modified and remains with the function code.
     *
     * Legal: Use this code at your own risk. K&L Productions assumes NO
     * resposibility for anything.
     ******************************************************************************/
    me.sprintf = function(fstring){
        var pad = function(str, ch, len){
            var ps = '';
            for (var i = 0; i < Math.abs(len); i++) 
                ps += ch;
            return len > 0 ? str + ps : ps + str;
        }
        var processFlags = function(flags, width, rs, arg){
            var pn = function(flags, arg, rs){
                if (arg >= 0) {
                    if (flags.indexOf(' ') >= 0) 
                        rs = ' ' + rs;
                    else if (flags.indexOf('+') >= 0) 
                        rs = '+' + rs;
                } else 
                    rs = '-' + rs;
                return rs;
            }
            var iWidth = parseInt(width, 10);
            if (width.charAt(0) == '0') {
                var ec = 0;
                if (flags.indexOf(' ') >= 0 || flags.indexOf('+') >= 0) 
                    ec++;
                if (rs.length < (iWidth - ec)) 
                    rs = pad(rs, '0', rs.length - (iWidth - ec));
                return pn(flags, arg, rs);
            }
            rs = pn(flags, arg, rs);
            if (rs.length < iWidth) {
                if (flags.indexOf('-') < 0) 
                    rs = pad(rs, ' ', rs.length - iWidth);
                else 
                    rs = pad(rs, ' ', iWidth - rs.length);
            }
            return rs;
        }
        var converters = new Array();
        converters['c'] = function(flags, width, precision, arg){
            if (typeof(arg) == 'number') 
                return String.fromCharCode(arg);
            if (typeof(arg) == 'string') 
                return arg.charAt(0);
            return '';
        }
        converters['d'] = function(flags, width, precision, arg){
            return converters['i'](flags, width, precision, arg);
        }
        converters['u'] = function(flags, width, precision, arg){
            return converters['i'](flags, width, precision, Math.abs(arg));
        }
        converters['i'] = function(flags, width, precision, arg){
            var iPrecision = parseInt(precision);
            var rs = ((Math.abs(arg)).toString().split('.'))[0];
            if (rs.length < iPrecision) 
                rs = pad(rs, ' ', iPrecision - rs.length);
            return processFlags(flags, width, rs, arg);
        }
        converters['E'] = function(flags, width, precision, arg){
            return (converters['e'](flags, width, precision, arg)).toUpperCase();
        }
        converters['e'] = function(flags, width, precision, arg){
            iPrecision = parseInt(precision);
            if (isNaN(iPrecision)) 
                iPrecision = 6;
            rs = (Math.abs(arg)).toExponential(iPrecision);
            if (rs.indexOf('.') < 0 && flags.indexOf('#') >= 0) 
                rs = rs.replace(/^(.*)(e.*)$/, '$1.$2');
            return processFlags(flags, width, rs, arg);
        }
        converters['f'] = function(flags, width, precision, arg){
            iPrecision = parseInt(precision);
            if (isNaN(iPrecision)) 
                iPrecision = 6;
            rs = (Math.abs(arg)).toFixed(iPrecision);
            if (rs.indexOf('.') < 0 && flags.indexOf('#') >= 0) 
                rs = rs + '.';
            return processFlags(flags, width, rs, arg);
        }
        converters['G'] = function(flags, width, precision, arg){
            return (converters['g'](flags, width, precision, arg)).toUpperCase();
        }
        converters['g'] = function(flags, width, precision, arg){
            iPrecision = parseInt(precision);
            absArg = Math.abs(arg);
            rse = absArg.toExponential();
            rsf = absArg.toFixed(6);
            if (!isNaN(iPrecision)) {
                rsep = absArg.toExponential(iPrecision);
                rse = rsep.length < rse.length ? rsep : rse;
                rsfp = absArg.toFixed(iPrecision);
                rsf = rsfp.length < rsf.length ? rsfp : rsf;
            }
            if (rse.indexOf('.') < 0 && flags.indexOf('#') >= 0) 
                rse = rse.replace(/^(.*)(e.*)$/, '$1.$2');
            if (rsf.indexOf('.') < 0 && flags.indexOf('#') >= 0) 
                rsf = rsf + '.';
            rs = rse.length < rsf.length ? rse : rsf;
            return processFlags(flags, width, rs, arg);
        }
        converters['o'] = function(flags, width, precision, arg){
            var iPrecision = parseInt(precision);
            var rs = Math.round(Math.abs(arg)).toString(8);
            if (rs.length < iPrecision) 
                rs = pad(rs, ' ', iPrecision - rs.length);
            if (flags.indexOf('#') >= 0) 
                rs = '0' + rs;
            return processFlags(flags, width, rs, arg);
        }
        converters['X'] = function(flags, width, precision, arg){
            return (converters['x'](flags, width, precision, arg)).toUpperCase();
        }
        converters['x'] = function(flags, width, precision, arg){
            var iPrecision = parseInt(precision);
            arg = Math.abs(arg);
            var rs = Math.round(arg).toString(16);
            if (rs.length < iPrecision) 
                rs = pad(rs, ' ', iPrecision - rs.length);
            if (flags.indexOf('#') >= 0) 
                rs = '0x' + rs;
            return processFlags(flags, width, rs, arg);
        }
        converters['s'] = function(flags, width, precision, arg){
            var iPrecision = parseInt(precision);
            var rs = arg;
            if (rs.length > iPrecision) 
                rs = rs.substring(0, iPrecision);
            return processFlags(flags, width, rs, 0);
        }
        farr = fstring.split('%');
        retstr = farr[0];
        fpRE = /^([-+ #]*)(\d*)\.?(\d*)([cdieEfFgGosuxX])(.*)$/;
        for (var i = 1; i < farr.length; i++) {
            fps = fpRE.exec(farr[i]);
            if (!fps) 
                continue;
            if (arguments[i] != null) 
                retstr += converters[fps[4]](fps[1], fps[2], fps[3], arguments[i]);
            retstr += fps[5];
        }
        return retstr;
    }
    
    /**
     * Take out the first comment inside a block of HTML
     *
     * @param {String} s - an HTML block
     * @return {String} s - the HTML block uncommented.
     */
    me.uncommentHTML = function(s){
        if (s.indexOf('-->') != -1 && s.indexOf('<!--') != -1) {
            return s.replace("<!--", "").replace("-->", "");
        } else {
            return s;
        }
    }
}
}

if (!window.XMLHelpers) {

XMLHelpers = new function(){

    var me = this;
    
    /**
     * Wrapper for XMLHttpRequest Object.  Grabbing data (XML and/or text) from a URL.
     * Grabbing data from a URL. Input is one parameter, url. It returns a request
     * object. Based on code from
     * http://www.xml.com/pub/a/2005/02/09/xml-http-request.html.  IE caching problem
     * fix from Wikipedia article http://en.wikipedia.org/wiki/XMLHttpRequest
     *
     * @param {String} url - the URL to retrieve
     * @param {Function} processReqChange - the function/method to call at key events of the URL retrieval.
     * @param {String} method - (optional) "GET" or "POST" (default "GET")
     * @param {String} data - (optional) the CGI data to pass.  Default null.
     * @param {boolean} isAsync - (optional) is this call asyncronous.  Default true.
     *
     * @return {Object} a XML request object.
     */
    me.getXMLHttpRequest = function(url, processReqChange) //, method, data, isAsync)
    {
        var argv = me.getXMLHttpRequest.arguments;
        var argc = me.getXMLHttpRequest.arguments.length;
        var httpMethod = (argc > 2) ? argv[2] : 'GET';
        var data = (argc > 3) ? argv[3] : "";
        var isAsync = (argc > 4) ? argv[4] : true;
        
        var req;
        // branch for native XMLHttpRequest object
        if (window.XMLHttpRequest) {
            req = new XMLHttpRequest();
            // branch for IE/Windows ActiveX version
        } else if (window.ActiveXObject) {
            try {
                req = new ActiveXObject('Msxml2.XMLHTTP');
            } 
            catch (ex) {
                req = new ActiveXObject("Microsoft.XMLHTTP");
            }
            // the browser doesn't support XML HttpRequest. Return null;
        } else {
            return null;
        }
        
        if (isAsync) {
            req.onreadystatechange = processReqChange;
        }
        
        if (httpMethod == "GET" && data != "") {
            url += "?" + data;
        }
        
        req.open(httpMethod, url, isAsync);
        
        //Fixes IE Caching problem
        req.setRequestHeader("If-Modified-Since", "Sat, 1 Jan 2000 00:00:00 GMT");
        req.send(data);
        
        return req;
    }
}
}


if (!window.CSSHelpers) {
CSSHelpers = new function(){
    var me = this;
    
    var blankRe = new RegExp('\\s');
    
	/*
	 * getComputedStyle: code from http://blog.stchur.com/2006/06/21/css-computed-style/
	 */
	me.getComputedStyle = function(elem, style)
	{
	  var computedStyle;
	  if (typeof elem.currentStyle != 'undefined')
	    { computedStyle = elem.currentStyle; }
	  else
	    { computedStyle = document.defaultView.getComputedStyle(elem, null); }
	
	  return computedStyle[style];
	}
	
	
    /**
     * Determines if an HTML object is a member of a specific class.
     * @param {Object} obj - an HTML object.
     * @param {Object} className - the CSS class name.
     */
    me.isMemberOfClass = function(obj, className){
    
        if (blankRe.test(className)) 
            return false;
        
        var re = new RegExp(getClassReString(className), "g");
        
        return (re.test(obj.className));
        
        
    }
    
    /**
     * Make an HTML object be a member of a certain class.
     *
     * @param {Object} obj - an HTML object
     * @param {String} className - a CSS class name.
     */
    me.addClass = function(obj, className){
        if (blankRe.test(className)) {
            return;
        }
        
        // only add class if the object is not a member of it yet.
        if (!me.isMemberOfClass(obj, className)) {
            obj.className += " " + className;
        }
    }
    
    /**
     * Make an HTML object *not* be a member of a certain class.
     *
     * @param {Object} obj - an HTML object
     * @param {Object} className - a CSS class name.
     */
    me.removeClass = function(obj, className){
    
        if (blankRe.test(className)) {
            return;
        }
        
        
        var re = new RegExp(getClassReString(className), "g");
        
        var oldClassName = obj.className;
        
        
        if (obj.className) {
            obj.className = oldClassName.replace(re, '');
        }
        
        
    }
	
	function getClassReString(className) {
		return '\\s'+className+'\\s|^' + className + '\\s|\\s' + className + '$|' + '^' + className +'$';
	}
	
	/**
	 * Given an HTML element, find all child nodes of a specific class.
	 * 
	 * With ideas from Jonathan Snook 
	 * (http://snook.ca/archives/javascript/your_favourite_1/)
	 * Since this was presented within a post on this site, it is for the 
	 * public domain according to the site's copyright statement.
	 * 
	 * @param {Object} obj - an HTML element.  If you want to search a whole document, set
	 * 		this to the document object.
	 * @param {String} className - the class name of the objects to return
	 * @return {Array} - the list of objects of class cls. 
	 */
	me.getElementsByClassName = function (obj, className)
	{
		//if (obj.getElementsByClassName) {
         //   alert("ABOUT TO TEST");
         //   alert("obj.getElementsByClassName(className) ========== " + obj.getElementsByClassName);
		//	return DOMHelpers.nodeListToArray(obj.getElementsByClassName(className))
		//}
		//else {
			var a = [];
			var re = new RegExp(getClassReString(className));
			var els = DOMHelpers.getAllDescendants(obj);
			for (var i = 0, j = els.length; i < j; i++) {
				if (re.test(els[i].className)) {
					a.push(els[i]);
					
				}
			}
			return a;
		//}
	}
    
    /**
     * Generates a regular expression string that can be used to detect a class name
     * in a tag's class attribute.  It is used by a few methods, so I
     * centralized it.
     *
     * @param {String} className - a name of a CSS class.
     */
    function getClassReString(className){
        return '\\s' + className + '\\s|^' + className + '\\s|\\s' + className + '$|' + '^' + className + '$';
    }
    
    
}
}


/* 
 * Adding trim method to String Object.  Ideas from
 * http://www.faqts.com/knowledge_base/view.phtml/aid/1678/fid/1 and
 * http://blog.stevenlevithan.com/archives/faster-trim-javascript
 */
String.prototype.trim = function(){
    var str = this;
    
    // The first method is faster on long strings than the second and 
    // vice-versa.
    if (this.length > 6000) {
        str = this.replace(StringHelpers.initWhitespaceRe, '');
        var i = str.length;
        while (StringHelpers.whitespaceRe.test(str.charAt(--i))) 
            ;
        return str.slice(0, i + 1);
    } else {
        return this.replace(StringHelpers.initWhitespaceRe, '').replace(StringHelpers.endWhitespaceRe, '');
    }
    
    
};

if (!window.DOMHelpers) {

DOMHelpers = new function () {
	var me = this;
	
	/**
	 * Returns all children of an element. Needed if it is necessary to do
	 * the equivalent of getElementsByTagName('*') for IE5 for Windows.
	 * 
	 * @param {Object} e - an HTML object.
	 */
	me.getAllDescendants = function(obj) {
		return obj.all ? obj.all : obj.getElementsByTagName('*');
	}
	
	/******
	* Converts a DOM live node list to a static/dead array.  Good when you don't
	* want the thing you are iterating in a for loop changing as the DOM changes.
	* 
	* @param {Object} nodeList - a node list (like one returned by document.getElementsByTagName)
	* @return {Array} - an array of nodes.
	* 
	*******/
	me.nodeListToArray = function (nodeList) 
	{ 
	    var ary = []; 
	    for(var i=0, len = nodeList.length; i < len; i++) 
	    { 
	        ary.push(nodeList[i]); 
	    } 
	    return ary; 
	} 
}
}

//+ Jonas Raoni Soares Silva
//@ http://jsfromhell.com/string/capitalize [v1.0]

String.prototype.capitalize = function(){ //v1.0
    return this.charAt(0).toUpperCase() + this.substr(1);
    
};


/*
 *  stringBuffer.js - ideas from
 *  http://www.multitask.com.au/people/dion/archives/000354.html
 */
function StringBuffer(){
    var me = this;
    
    var buffer = [];
    
    
    me.append = function(string){
        buffer.push(string);
        return me;
    }
    
    me.appendBuffer = function(bufferToAppend){
        buffer = buffer.concat(bufferToAppend);
    }
    
    me.toString = function(){
        return buffer.join("");
    }
    
    me.getLength = function(){
        return buffer.length;
    }
    
    me.flush = function(){
        buffer.length = 0;
    }
    
}
