function CoordinateConversion()
{
	this.ddToDms = function(dd, position)
	{
		var degreesAbs = Math.abs(dd);
		var degrees = Math.floor(degreesAbs);
		
		var minutesAbs = Math.abs((degreesAbs - degrees) * 60);
		var minutes = Math.floor(minutesAbs);
		
		var seconds = Math.abs((minutesAbs - minutes) * 60);
        seconds = seconds.toFixed(2);
		
		var dms;
        if(minutes < 10 && seconds < 10)
		{
			dms = degrees + "°0" + minutes + "'0" + seconds + '"';
		}
		else if (minutes < 10)
		{
			dms = degrees + "°0" + minutes + "'" + seconds + '"';
		}
		else if (seconds < 10)
		{
			dms = degrees + "°" + minutes + "'0" + seconds + '"';
		}
		else
		{
			dms = degrees + "°" + minutes + "'" + seconds + '"';
		}
		
		if(position == "lat" && dd > 0)
		{
			dms = dms + " N";
		}
		else if(position == "lat" && dd < 0)
		{
			dms = dms + " S";
		}
		else if(position == "lon" && dd > 0)
		{
			dms = dms + " E";
		}
		else if(position == "lon" && dd < 0)
		{
			dms = dms + " W";
		}
		
		return dms;
	};
	
	this.dmsToDd = function(degrees, minutes, seconds, position)
	{
		var dd = Math.abs(degrees) + Math.abs(minutes / 60) + Math.abs(seconds / 3600);
		
		if (position == "S" || position == "s" || position == "W" || position == "w")
		{
			dd = -dd;
		}
		
		return dd;
	};
	
	// degrees to radians
	this.degToRad = function(degrees)
	{
		return (degrees * (Math.PI / 180));
	};

	// radians to degrees
	this.radToDeg = function(radians)
	{
		return (radians * (180 / Math.PI));
	};







    // Decimal Degrees to Military Grid Reference System
    this.ddToMgrs = function(lat, lon)
    {
        var K0 = 0.9996; // scale factor
        var A1 = 6378137.0 * K0;
        var B1 = 6356752.3142 * K0;
        K0 = 0;
        var N0 = 0;
        var E0 = 500000;

        var N1 = (A1 - B1) / (A1 + B1); // n
        var N2 = N1 * N1;
        var N3 = N2 * N1;
        var E2 = ((A1 * A1) - (B1 * B1)) / (A1 * A1); // e^2

        var latRad = lat * (Math.PI / 180.0);
        var lonRad = lon * (Math.PI / 180.0);

        var latRadSin = Math.sin(latRad);

        var latRadCos = Math.cos(latRad);
        var latRadCos2 = latRadCos * latRadCos;
        var latRadCos3 = latRadCos2 * latRadCos;

        var latRadTan = latRadSin / latRadCos;
        var latRadTan2 = latRadTan * latRadTan;

        var K3 = latRad - K0;
        var K4 = latRad + K0;

        var Merid = Math.floor((lon) / 6) * 6 + 3;
        if ((lat >= 72) && (lon >= 0))
        {
            if (lon < 9)
            {
                Merid=3;
            }

            else if (lon<21)
            {
                Merid=15;
            }

            else if (lon<33)
            {
                Merid=27;
            }

            else if (lon<42)
            {
                Merid=39;
            }
        }

        if((lat >= 56) && (lat < 64))
        {
            if ((lon >= 3) && (lon < 12))
            {
                Merid=9;
            }
        }

        var L0 = Merid * (Math.PI / 180.0); // Long of True Origin (3,9,15 etc)

        // Arc of Meridian
        var J3 = K3 *(1 + N1 + 1.25 * (N2 + N3));
        var J4 = Math.sin(K3) * Math.cos(K4) * (3 * (N1 + N2 + 0.875 * N3));
        var J5 = Math.sin(2 * K3) * Math.cos(2 * K4) * (1.875 * (N2 + N3));
        var J6 = Math.sin(3 * K3) * Math.cos(3 * K4) * 35 / 24 * N3;
        var M = (J3 - J4 + J5 - J6) * B1;

        var Temp = 1 - E2 * latRadSin * latRadSin;
        var V = A1 / Math.sqrt(Temp);
        var R = V * (1 - E2) / Temp;
        var H2 = V / R - 1.0;

        var P = lonRad - L0;
        var P2 = P * P;
        var P4 = P2 * P2;
        J3 = M + N0;
        J4 = V / 2 * latRadSin * latRadCos;
        J5 = V / 24 * latRadSin * (latRadCos3) * (5 - (latRadTan2) + 9 * H2);
        J6 = V / 720 * latRadSin * latRadCos3 * latRadCos2 * (61 - 58 * (latRadTan2) + latRadTan2 * latRadTan2);
        var North = J3 + P2 * J4 + P4 * J5 + P4* P2 * J6;

        var Area = "UTM2";
        var South = (lat < 0);

        if (((Area=='UTM1') || (Area=='UTM2')) && South)
        {
            North = North + 10000000.0; // UTM S hemisphere
        }

        var J7 = V * latRadCos;
        var J8 = V / 6 * latRadCos3 * (V / R - latRadTan2);
        var J9 = V / 120 * latRadCos3 * latRadCos2;
        J9 = J9 * (5 - 18 * latRadTan2 + latRadTan2 * latRadTan2 + 14 * H2 - 58 * latRadTan2 * H2);

        var East = E0 + P * J7 + P2 * P * J8 + P4 * P * J9;
        var IEast = Math.round(East);
        var INorth = Math.round(North);
        var EastStr = '' + Math.abs(IEast);
        var NorthStr = '' + Math.abs(INorth);

        while (EastStr.length<7)
        {
            EastStr = '0' + EastStr;
        }

        while (NorthStr.length<7)
        {
            NorthStr = '0' + NorthStr;
        }

        var GR100km = eval(EastStr.substring(1,2) + NorthStr.substring(1, 2));
        var GRremainder = EastStr.substring(2,7) + '' + NorthStr.substring(2, 7);

        var LonZone = (Merid - 3) / 6 + 31;

        var GR;

        if (LonZone % 1 != 0)
        {
            GR = 'non-UTM central meridian';
        }

        else
        {
            if (IEast < 100000 || lat < -80 || IEast > 899999 || lat >= 84)
            {
                GR = 'outside UTM grid area';
            }

            else
            {
                var Letters = 'ABCDEFGHJKLMNPQRSTUVWXYZ'
                var Pos = Math.round(lat / 8 - 0.5) + 10 + 2;
                var LatZone = Letters.substring(Pos, Pos + 1);

                if (LatZone > 'X')
                {
                    LatZone = 'X';
                }

                Pos = Math.round(Math.abs(INorth) / 100000 - 0.5);

                while (Pos > 19)
                {
                   Pos = Pos - 20;
                }

                if (LonZone % 2 == 0)
                {
                    Pos = Pos + 5;
                    if (Pos>19)
                    {
                        Pos = Pos - 20;
                    }
                }

                var N100km = Letters.substring(Pos, Pos + 1);
                Pos = GR100km / 10 - 1;
                P = LonZone;
                while (P > 3)
                {
                    P = P - 3;
                }

                Pos = Pos + ((P - 1) * 8);
                var E100km = Letters.substring(Pos, Pos + 1);
                GR = LonZone + "" + LatZone + "" + E100km + "" + N100km + "" + GRremainder + "";
            }
        }
        return(GR);
    };
















	this.mgrsToUtm = function(mgrs)
    {

      var mgrsRegExp = /^(\d{1,2})\s?([C-X])\s?([A-Z])\s?([A-Z])\s?(\d{0,5})\s?(\d{0,5})\s?/

        if (mgrs.match(mgrsRegExp))
        {
            var zone = RegExp.$1;
            var zdl = RegExp.$2;
            var c1 = RegExp.$3;
            var c2 = RegExp.$4;
            var E = RegExp.$5;
            var N = RegExp.$6;

            var u={};
            var ok=MGRStoUTM(zone,zdl,c1,c2,E,N,u);

            var northing = null;

            if(u.N<0)
            {
                northing = u.N+10000000;
            }
            else
            {
                northing = u.N;
            }
            var s=(u.N<0)?"S":"N";

            if(u.N<0)
            {
                u.N+=10000000;
            }

            latlon = new Array(2);
            var x, y, southhemi;


            x = u.E;

            y = u.N;

            if ((u.zone < 1) || (60 < u.zone))
            {
                alert ("The UTM zone you entered is out of range.  " +
                       "Please enter a number in the range [1, 60].");
                return false;
            }

            var mgrsRegExp = /^(\d{1,2})([a-zA-Z])([a-zA-Z])([a-zA-Z])(\d{10})?/

            if(($("centerMgrs").value.match(mgrsRegExp)))
            {
                var lonZone = parseInt(RegExp.$1, 10);
                var latZone = RegExp.$2;
                var easting = RegExp.$3;
                var northing = RegExp.$4;
                var remainder = parseInt(RegExp.$5, 10);
            }

         


            if (latZone == "C" || latZone == "D" || latZone == "E" || latZone == "F" || latZone == "G" || latZone == "H"
                    || latZone == "J" || latZone == "K" || latZone == "L" || latZone == "M")
            {
                southhemi = true;
            }
            else
            {
                southhemi = false;
            }

            UTMXYToLatLon (x, y, u.zone, southhemi, latlon);

            var lat = ( RadToDeg (latlon[1]));
            var lon = ( RadToDeg (latlon[0]));

            return lon + " " + lat;
        }

        else
        {
            alert("Error: Not a valid MGRS String.");
        }
    };

    var UTMzdlChars="CDEFGHJKLMNPQRSTUVWXX";

    function UTMzdl(latDeg)
    {
        if(-80<=latDeg&&latDeg<=84)
            return UTMzdlChars.charAt(Math.floor((latDeg+80)/8));
        else
        {
            alert("No zdl: UTM is not valid for Lat "+latDeg);  //Not normally reached
            return "";
        }
    }

    function UTMNtoZDL(N)
    {
        var latDeg=N*82.82/(92*100000);
        //alert("latdeg="+latDeg);
        return UTMzdl(latDeg);
    }

    function ZoneLatToN(d,E)
    {
        var c= [[ 0,      0.000,833978.557,   0.000,0.0000000000],
                [ 8, 884297.851,830743.842,1205.908,0.0072920001],
                [16,1768935.376,821099.997,2318.642,0.0144411176],
                [24,2654226.538,805227.189,3252.171,0.0213074192],
                [32,3540435.693,783423.227,3934.216,0.0277567819],
                [40,4427757.219,756099.648,4311.838,0.0336635858],
                [48,5316300.224,723775.915,4355.565,0.0389131714],
                [56,6206079.587,687071.439,4061.740,0.0434040145],
                [64,7097014.163,646695.227,3452.887,0.0470495860],
                [72,7988932.503,603433.054,2576.040,0.0497798833],
                [80,8881585.816,558132.215,1499.140,0.0515426300],
                [84,9328093.831,534994.655, 911.352,0.0520499036]];    //    special value for N Hemisphere

        var d1=(d==84)?11:Math.abs(d)/8;

        var N0=c[d1][1];
        var E1=c[d1][2];
        var k =c[d1][4];

        var E10=E1-500000;

        var x=(E-500000.)/E10;

        var N=N0+k*x*(E-500000.)/(1+Math.sqrt(1-k*k*x*x));

        return(d>0)?N:-N;
    }

    function UTMtoZDL(E,N)
    {
        var d;

        if(N<=ZoneLatToN(84,E))
        {
            for(d=72;d>=-72;d-=8)
            {
                if(ZoneLatToN(d,E)<N)return UTMzdl(d+4);
            }
        }

        alert("Invalid Northing: cannot compute ZDL");

        return "";
    }

    function MGRSdigits(EorN,u,n)
    {
        var v=(EorN+1e7+1e-4)/10000000+"0000000000000000";
        u.x100=Number(v.substr(2,2));
        u.r100=v.substr(4,n);
        //alert(v+"\n"+u.x100+"\n"+u.r100);
    }

    var MGRSchars="ABCDEFGHJKLMNPQRSTUVWXYZ";

    function MGRSletters(zone,E100,N100,u)
    {
        //alert("Zone "+zone+"\nE100="+E100+"\nN100="+N100);

        var j=E100-1;
        var e8=8*(zone-1)+j;
        u.c1=MGRSchars.charAt(e8%24);
        u.c2=MGRSchars.charAt((N100+100+((zone%2)?0:5))%20);    //NB advance 2nd letter by 5 for odd zones
    }

    function UTMtoMGRS(zone,E,N,d,u)
    {
        if(zone<1||zone>60||Math.round(zone)!=zone)
        {
            alert("Invalid Zone");
            return false;
        }

        u.zone=zone;

        var zdl=UTMtoZDL(E,N);

        if(zdl.length!=1)
        {
            //alert("Invalid UTM");
            return false;
        }

        u.zdl=zdl;

        var e={},n={};

        MGRSdigits(E,e,d);
        MGRSdigits(N,n,d);

        var E100=e.x100,N100=n.x100;

        MGRSletters(zone,E100,N100,u);

        u.E=e.r100;
        u.N=n.r100;

        return true;
    }

    function zdlMedianLat(zdl)
    {
        if(zdl=="X")
        {
            return 78;            //not 76; X is 72 to 84
        }
        var i=UTMzdlChars.indexOf(zdl);
        if(i<0)
        {
            alert('Invalid zone designation letter "'+zdl+'"');
            quit();
        }
        return -76+8*i
    }

    function MGRStoUTM(zone,zdl,c1,c2,er,nr,u)
    {
        //alert("MGRStoUTM "+c1+" "+c2+" "+er+" "+nr);

        if(zone<1||zone>60||Math.round(zone)!=zone)
        {
            alert("Invalid Zone");
            return false;
        }

        u.zone=zone;

        var n1=MGRSchars.indexOf(c1);
        var n2=MGRSchars.indexOf(c2);

        if(n1<0||n2<0)
        {
            alert("Invalid MGRS square characters");
            return false;
        }

        var E0=1+n1%8;
        var N0=(20+n2-((zone%2)?0:5))%20;

        var approxN=zdlMedianLat(zdl)*100/90; // approx median northing of zdl in units of 100km

        N0+=Math.round((approxN-N0)/20)*20;   // add a multiple of 2000km to get the MGRS square closest
                                              // to approxN (letters repeat every 20*100km=2000km)

        d=er.length;

        if(nr.length!=d)
        {
            alert("MGRS Easting and Northing must have\nthe same number of digits");
            return false;
        }

        u.E=E0*100000+Number(er)*Math.pow(10,5-d);
        u.N=N0*100000+Number(nr)*Math.pow(10,5-d);

        return true;
    }

    var pi = 3.14159265358979;

    /* Ellipsoid model constants (actual values here are for WGS84) */
    var sm_a = 6378137.0;
    var sm_b = 6356752.314;
    var sm_EccSquared = 6.69437999013e-03;

    var UTMScaleFactor = 0.9996;

    function DegToRad (deg)
    {
        return (deg / 180.0 * pi)
    }

    function RadToDeg (rad)
    {
        return (rad / pi * 180.0)
    }

    function ArcLengthOfMeridian (phi)
    {
        var alpha, beta, gamma, delta, epsilon, n;
        var result;

        /* Precalculate n */
        n = (sm_a - sm_b) / (sm_a + sm_b);

        /* Precalculate alpha */
        alpha = ((sm_a + sm_b) / 2.0)
           * (1.0 + (Math.pow (n, 2.0) / 4.0) + (Math.pow (n, 4.0) / 64.0));

        /* Precalculate beta */
        beta = (-3.0 * n / 2.0) + (9.0 * Math.pow (n, 3.0) / 16.0)
           + (-3.0 * Math.pow (n, 5.0) / 32.0);

        /* Precalculate gamma */
        gamma = (15.0 * Math.pow (n, 2.0) / 16.0)
            + (-15.0 * Math.pow (n, 4.0) / 32.0);

        /* Precalculate delta */
        delta = (-35.0 * Math.pow (n, 3.0) / 48.0)
            + (105.0 * Math.pow (n, 5.0) / 256.0);

        /* Precalculate epsilon */
        epsilon = (315.0 * Math.pow (n, 4.0) / 512.0);

        /* Now calculate the sum of the series and return */
        result = alpha
            * (phi + (beta * Math.sin (2.0 * phi))
            + (gamma * Math.sin (4.0 * phi))
            + (delta * Math.sin (6.0 * phi))
            + (epsilon * Math.sin (8.0 * phi)));

        return result;
    }

    function UTMCentralMeridian (zone)
    {
        var cmeridian;

        cmeridian = DegToRad (-183.0 + (zone * 6.0));

        return cmeridian;
    }

    function FootpointLatitude (y)
    {
        var y_, alpha_, beta_, gamma_, delta_, epsilon_, n;
        var result;

        /* Precalculate n (Eq. 10.18) */
        n = (sm_a - sm_b) / (sm_a + sm_b);

        /* Precalculate alpha_ (Eq. 10.22) */
        /* (Same as alpha in Eq. 10.17) */
        alpha_ = ((sm_a + sm_b) / 2.0)
            * (1 + (Math.pow (n, 2.0) / 4) + (Math.pow (n, 4.0) / 64));

        /* Precalculate y_ (Eq. 10.23) */
        y_ = y / alpha_;

        /* Precalculate beta_ (Eq. 10.22) */
        beta_ = (3.0 * n / 2.0) + (-27.0 * Math.pow (n, 3.0) / 32.0)
            + (269.0 * Math.pow (n, 5.0) / 512.0);

        /* Precalculate gamma_ (Eq. 10.22) */
        gamma_ = (21.0 * Math.pow (n, 2.0) / 16.0)
            + (-55.0 * Math.pow (n, 4.0) / 32.0);

        /* Precalculate delta_ (Eq. 10.22) */
        delta_ = (151.0 * Math.pow (n, 3.0) / 96.0)
            + (-417.0 * Math.pow (n, 5.0) / 128.0);

        /* Precalculate epsilon_ (Eq. 10.22) */
        epsilon_ = (1097.0 * Math.pow (n, 4.0) / 512.0);

        /* Now calculate the sum of the series (Eq. 10.21) */
        result = y_ + (beta_ * Math.sin (2.0 * y_))
            + (gamma_ * Math.sin (4.0 * y_))
            + (delta_ * Math.sin (6.0 * y_))
            + (epsilon_ * Math.sin (8.0 * y_));

        return result;
    }

    function MapLatLonToXY (phi, lambda, lambda0, xy)
    {
        var N, nu2, ep2, t, t2, l;
        var l3coef, l4coef, l5coef, l6coef, l7coef, l8coef;
        var tmp;

        /* Precalculate ep2 */
        ep2 = (Math.pow (sm_a, 2.0) - Math.pow (sm_b, 2.0)) / Math.pow (sm_b, 2.0);

        /* Precalculate nu2 */
        nu2 = ep2 * Math.pow (Math.cos (phi), 2.0);

        /* Precalculate N */
        N = Math.pow (sm_a, 2.0) / (sm_b * Math.sqrt (1 + nu2));

        /* Precalculate t */
        t = Math.tan (phi);
        t2 = t * t;
        tmp = (t2 * t2 * t2) - Math.pow (t, 6.0);

        /* Precalculate l */
        l = lambda - lambda0;

        /* Precalculate coefficients for l**n in the equations below
           so a normal human being can read the expressions for easting
           and northing
           -- l**1 and l**2 have coefficients of 1.0 */
        l3coef = 1.0 - t2 + nu2;

        l4coef = 5.0 - t2 + 9 * nu2 + 4.0 * (nu2 * nu2);

        l5coef = 5.0 - 18.0 * t2 + (t2 * t2) + 14.0 * nu2
            - 58.0 * t2 * nu2;

        l6coef = 61.0 - 58.0 * t2 + (t2 * t2) + 270.0 * nu2
            - 330.0 * t2 * nu2;

        l7coef = 61.0 - 479.0 * t2 + 179.0 * (t2 * t2) - (t2 * t2 * t2);

        l8coef = 1385.0 - 3111.0 * t2 + 543.0 * (t2 * t2) - (t2 * t2 * t2);

        /* Calculate easting (x) */
        xy[0] = N * Math.cos (phi) * l
            + (N / 6.0 * Math.pow (Math.cos (phi), 3.0) * l3coef * Math.pow (l, 3.0))
            + (N / 120.0 * Math.pow (Math.cos (phi), 5.0) * l5coef * Math.pow (l, 5.0))
            + (N / 5040.0 * Math.pow (Math.cos (phi), 7.0) * l7coef * Math.pow (l, 7.0));

        /* Calculate northing (y) */
        xy[1] = ArcLengthOfMeridian (phi)
            + (t / 2.0 * N * Math.pow (Math.cos (phi), 2.0) * Math.pow (l, 2.0))
            + (t / 24.0 * N * Math.pow (Math.cos (phi), 4.0) * l4coef * Math.pow (l, 4.0))
            + (t / 720.0 * N * Math.pow (Math.cos (phi), 6.0) * l6coef * Math.pow (l, 6.0))
            + (t / 40320.0 * N * Math.pow (Math.cos (phi), 8.0) * l8coef * Math.pow (l, 8.0));

        return true;
    }

    function MapXYToLatLon (x, y, lambda0, philambda)
    {
        var phif, Nf, Nfpow, nuf2, ep2, tf, tf2, tf4, cf;
        var x1frac, x2frac, x3frac, x4frac, x5frac, x6frac, x7frac, x8frac;
        var x2poly, x3poly, x4poly, x5poly, x6poly, x7poly, x8poly;

        /* Get the value of phif, the footpoint latitude. */
        phif = FootpointLatitude (y);

        /* Precalculate ep2 */
        ep2 = (Math.pow (sm_a, 2.0) - Math.pow (sm_b, 2.0))
              / Math.pow (sm_b, 2.0);

        /* Precalculate cos (phif) */
        cf = Math.cos (phif);

        /* Precalculate nuf2 */
        nuf2 = ep2 * Math.pow (cf, 2.0);

        /* Precalculate Nf and initialize Nfpow */
        Nf = Math.pow (sm_a, 2.0) / (sm_b * Math.sqrt (1 + nuf2));
        Nfpow = Nf;

        /* Precalculate tf */
        tf = Math.tan (phif);
        tf2 = tf * tf;
        tf4 = tf2 * tf2;

        /* Precalculate fractional coefficients for x**n in the equations
           below to simplify the expressions for latitude and longitude. */
        x1frac = 1.0 / (Nfpow * cf);

        Nfpow *= Nf;   /* now equals Nf**2) */
        x2frac = tf / (2.0 * Nfpow);

        Nfpow *= Nf;   /* now equals Nf**3) */
        x3frac = 1.0 / (6.0 * Nfpow * cf);

        Nfpow *= Nf;   /* now equals Nf**4) */
        x4frac = tf / (24.0 * Nfpow);

        Nfpow *= Nf;   /* now equals Nf**5) */
        x5frac = 1.0 / (120.0 * Nfpow * cf);

        Nfpow *= Nf;   /* now equals Nf**6) */
        x6frac = tf / (720.0 * Nfpow);

        Nfpow *= Nf;   /* now equals Nf**7) */
        x7frac = 1.0 / (5040.0 * Nfpow * cf);

        Nfpow *= Nf;   /* now equals Nf**8) */
        x8frac = tf / (40320.0 * Nfpow);

        /* Precalculate polynomial coefficients for x**n.
           -- x**1 does not have a polynomial coefficient. */
        x2poly = -1.0 - nuf2;

        x3poly = -1.0 - 2 * tf2 - nuf2;

        x4poly = 5.0 + 3.0 * tf2 + 6.0 * nuf2 - 6.0 * tf2 * nuf2
            - 3.0 * (nuf2 *nuf2) - 9.0 * tf2 * (nuf2 * nuf2);

        x5poly = 5.0 + 28.0 * tf2 + 24.0 * tf4 + 6.0 * nuf2 + 8.0 * tf2 * nuf2;

        x6poly = -61.0 - 90.0 * tf2 - 45.0 * tf4 - 107.0 * nuf2
            + 162.0 * tf2 * nuf2;

        x7poly = -61.0 - 662.0 * tf2 - 1320.0 * tf4 - 720.0 * (tf4 * tf2);

        x8poly = 1385.0 + 3633.0 * tf2 + 4095.0 * tf4 + 1575 * (tf4 * tf2);

        /* Calculate latitude */
        philambda[0] = phif + x2frac * x2poly * (x * x)
            + x4frac * x4poly * Math.pow (x, 4.0)
            + x6frac * x6poly * Math.pow (x, 6.0)
            + x8frac * x8poly * Math.pow (x, 8.0);

        /* Calculate longitude */
        philambda[1] = lambda0 + x1frac * x
            + x3frac * x3poly * Math.pow (x, 3.0)
            + x5frac * x5poly * Math.pow (x, 5.0)
            + x7frac * x7poly * Math.pow (x, 7.0);

        return true;
    }

    function LatLonToUTMXY (lat, lon, zone, xy)
    {
        MapLatLonToXY (lat, lon, UTMCentralMeridian (zone), xy);

        /* Adjust easting and northing for UTM system. */
        xy[0] = xy[0] * UTMScaleFactor + 500000.0;
        xy[1] = xy[1] * UTMScaleFactor;
        if (xy[1] < 0.0)
            xy[1] = xy[1] + 10000000.0;

        return zone;
    }

    function UTMXYToLatLon (x, y, zone, southhemi, latlon)
    {
        var cmeridian;

        x -= 500000.0;
        x /= UTMScaleFactor;

        /* If in southern hemisphere, adjust y accordingly. */
        if (southhemi)
        y -= 10000000.0;

        y /= UTMScaleFactor;

        cmeridian = UTMCentralMeridian (zone);
        MapXYToLatLon (x, y, cmeridian, latlon);

        return true;
    }
}