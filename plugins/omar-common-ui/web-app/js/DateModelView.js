OMAR.models.Date = Backbone.Model.extend(
    {
        date:null,
        initialize:function(options)
        {
            this.date = new Date();
            this.attributes = {};
            if(options)
            {
                if(typeof options == "string")
                {
                    this.setISO8601(options);
                }
                else if(options instanceof Date)
                {
                    this.date = new Date(options.getTime());
                }
                if(options.date)
                {
                    if(typeof options.date == "string"){
                        this.setISO8601(options.date);
                    }
                    else if(options.date instanceof Date)
                    {
                        this.date = new Date(options.date.getTime());
                    }
                }
            }
        },
        clearTime:function(){
            this.date.setUTCHours(0);
            this.date.setUTCMinutes(0);
            this.date.setUTCSeconds(0);
            this.date.setUTCMilliseconds(0);
        },
        toISOString:function(){
            function pad(n, w){
                if(w == null || w == 2)
                {
                    return n<10 ? '0'+n : n;

                }
                else if(w == 3)
                {
                    if(n < 10)
                    {
                        return "00"+n;
                    }
                    else if(n <= 99)
                    {
                        return "0"+n;
                    }
                }

                return ""+n;
            }
            var milliFractionString = (this.date.getUTCMilliseconds()/1000).toString();
            if(milliFractionString.charAt(1) == '.')
            {
                milliFractionString = milliFractionString.substring(1, milliFractionString.length);
            }
            return (this.date.getUTCFullYear()+'-'
                + pad(this.date.getUTCMonth()+1)+'-'
                + pad(this.date.getUTCDate())+'T'
                + pad(this.date.getUTCHours())+':'
                + pad(this.date.getUTCMinutes())+':'
                + pad(this.date.getUTCSeconds())
                + milliFractionString
                + 'Z');
        },
        setISO8601:function(dString)
        {
            var regexp = /(\d\d\d\d)(-)?(\d\d)(-)?(\d\d)(T)?(\d\d)(:)?(\d\d)(:)?(\d\d)(\.\d+)?(Z|([+-])(\d\d)(:)?(\d\d))/;

            if (dString.toString().match(new RegExp(regexp))) {
                var d = dString.match(new RegExp(regexp));
                var offset = 0;

                this.date.setUTCDate(1);
                this.date.setUTCFullYear(parseInt(d[1],10));
                this.date.setUTCMonth(parseInt(d[3],10) - 1);
                this.date.setUTCDate(parseInt(d[5],10));
                this.date.setUTCHours(parseInt(d[7],10));
                this.date.setUTCMinutes(parseInt(d[9],10));
                this.date.setUTCSeconds(parseInt(d[11],10));
                if (d[12])
                    this.date.setUTCMilliseconds(parseFloat(d[12]) * 1000);
                else
                    this.date.setUTCMilliseconds(0);
                if (d[13] != 'Z') {
                    offset = (d[15] * 60) + parseInt(d[17],10);
                    offset *= ((d[14] == '-') ? -1 : 1);
                    this.date.setTime(this.date.getTime() - offset * 60 * 1000);
                }
            }
            else {
                this.date.setTime(Date.parse(dString));
            }
            return this;
        },
        subtract:function(duration)
        {
            var millis = duration.computePartialMillis();
            var millisDate = this.date.getTime();
            this.date.setTime(millisDate-millis);
            // now handle years
            var yearsForMonth = Math.floor(duration.months / 12);
            var monthRemainder = duration.months - yearsForMonth*12;
            this.date.setFullYear(this.date.getFullYear() -
                (duration.years + yearsForMonth));
            var month = this.date.getMonth();
            var newMonth = month - monthRemainder;
            var newYear  = this.date.getFullYear();
            if(newMonth < 0)
            {
                newYear--;
                newMonth+=12;
            }

            this.date.setFullYear(newYear, newMonth);
            return this;
        },
        add:function(duration)
        {
            var millis = duration.computePartialMillis();
            var millisDate = this.date.getTime();
            this.date.setTime(millisDate+millis);
            var yearsForMonth = Math.floor(duration.months / 12);
            var monthsToAdd = duration.months - yearsForMonth;
            this.date.setYear(this.date.getFullYear() + (duration.years + yearsForMonth));

            var newMonth   = this.date.getMonth();
            var newYear = this.date.getFullYear();

            newMonth += monthsToAdd;
            newMonthRemainder = (newMonth%12);
            newYear += newMonthRemainder;
            newMonth -= (newMonthRemainder*12);

            this.date.setFullYear(newYear, newMonth);
            return this;
        }
    }
);

OMAR.models.Duration = Backbone.Model.extend(
    {
        years:0,
        months:0,
        weeks:0,
        days:0,
        hours:0,
        minutes:0,
        seconds:0,
        valuesSetFlag:false,
        initialize:function(options)
        {
            if(options)
            {
                if(typeof options == "string")
                {
                    this.setFromISOString(options);
                }
                else
                {
                    this.set(options);
                    this.setValueSetFlag();
                }
            }
        },
        setValueSetFlag:function()
        {
            this.valuesSetFlag = false;
            if(this.years>0||this.months>0||this.weeks>0||this.days>0||
                this.hours>0||this.minutes>0||this.seconds>0)
            {
                this.valuesSetFlag = true;
            }
        },
        toISOString:function()
        {
            var result = "";
            if(!this.valuesSetFlag) return result;
            result = "P";
            if(this.years > 0)
            {
                result += this.years + "Y";
            }
            if(this.months)
            {
                result += this.months + "M";
            }
            if(this.weeks)
            {
                result += this.weeks + "W";
            }
            if(this.days)
            {
                result += this.days + "D";
            }

            if(this.hours > 0 || this.minutes > 0 || this.seconds >0)
            {
                result += "T";
                if(this.hours>0)
                {
                    result += this.hours + "H"
                }
                if(this.minutes>0)
                {
                    result += this.minutes + "M"
                }
                if(this.seconds>0)
                {
                    result += this.seconds + "S"
                }
            }

            return result;
        },
        clear:function(){
            this.years=0;
            this.months=0;
            this.weeks=0;
            this.days=0;
            this.hours=0;
            this.minutes=0;
            this.seconds=0;
            this.valuesSetFlag=false;
        },
        computePartialMillis:function(){
            // we will not do months and years for this will be handled by someone else
            // we will only do the ones that we directly know how to convert to millis
            return (this.seconds*1000 +
                this.minutes*60000+
                this.hours*3600000+
                this.weeks*604800000+
                this.days*86400000);
        },
        setFromISOString:function(s){
            var idx = 0;
            var isTimePart = false;
            this.clear();
            if(s.charAt(idx) == 'P')
            {
                var idx2 = 1;
                var value = 0;
                for(idx = 1; idx < s.length;++idx)
                {
                    if(s.charAt(idx) > '9')
                    {
                        if(s.charAt(idx) == 'T')
                        {
                            idx2 = idx+1;
                            isTimePart = true;
                        }
                        else if(idx > idx2)
                        {
                            value = parseInt(s.substring(idx2, idx));
                            switch(s.charAt(idx))
                            {
                                case 'Y':
                                {
                                    this.years = value;
                                    break;
                                }
                                case 'M':
                                {
                                    if(isTimePart)
                                    {
                                        this.minutes = value;
                                    }
                                    else
                                    {
                                        this.months = value;
                                    }
                                    break;
                                }
                                case 'W':
                                {
                                    this.weeks = value;
                                    break;
                                }
                                case 'D':
                                {
                                    this.days = value;
                                    break;
                                }
                                case 'H':
                                {
                                    this.hours = value;
                                    break;
                                }
                                case 'S':
                                {
                                    this.seconds = value;
                                    break;
                                }
                                default:
                                {
                                    //this.clear();
                                    //throw "Badly formed ISO Duration";
                                    this.setValueSetFlag();
                                    return this;
                                }
                            }
                            idx2 = idx +1;
                        }
                    }
                }
            }

            this.setValueSetFlag();
            return this;
        }
    }
);

OMAR.models.Repeating = Backbone.Model.extend(
    {
        n:null,
        initialize:function(options)
        {
            this.n = 0;
        }
    }
);
OMAR.models.Interval = Backbone.Model.extend(
    {
        a:null,
        b:null,
        initialize:function(options){
            if(options)
            {
                if(typeof options == "string")
                {
                    this.setFromISOString(options);
                }
                else
                {
                }
            }
        },
        getStartEndDate:function()
        {
            var result = new Array(2);
            result[0] = null;
            result[1] = null;
            if(this.a == null)
            {
                if(this.b instanceof OMAR.Duration)
                {
                    result[0] = new OMAR.models.Date();
                    result[1] = new OMAR.models.Date(result[0]);
                    result[1].add(this.b);
                }
                else if(this.b instanceof OMAR.Date)
                {
                    result[0] = new OMAR.models.Date();
                    result[1] = new OMAR.models.Date(this.b);
                }
            }
            else if(this.b == null)
            {
                if(this.a instanceof OMAR.Duration)
                {
                    result[1] = new OMAR.models.Date();
                    result[0] = new OMAR.models.Date(result[1]);
                    result[0].subtract(this.a);
                }
                else if(this.a instanceof OMAR.models.Date)
                {
                    result[0] = new OMAR.models.Date(this.a);
                    result[1] = new OMAR.models.Date();
                }
            }
            else if(this.a instanceof OMAR.models.Date &&
                this.b instanceof OMAR.models.Date)
            {
                result[0] = this.a;
                result[1] = this.b;
            }
            else if(this.a instanceof OMAR.Duration)
            {
                if(this.b instanceof OMAR.models.Date)
                {
                    var date = new OMAR.models.Date(this.b);
                    result[0] = date.subtract(this.a);
                    result[1] = new OMAR.models.Date(this.b);
                }
            }
            else if(this.a instanceof OMAR.models.Date)
            {
                if(this.b instanceof OMAR.Duration)
                {
                    result[0] = new OMAR.models.Date(this.a);
                    result[1] = new OMAR.models.Date(this.a);
                    result[1].add(this.b);
                }
            }
            return result;
        },
        setFromISOString:function(s)
        {
            if(s.search("/") < 0)
            {
                this.a = new OMAR.models.Date(s);
                this.b = new OMAR.models.Date(this.a);

            }
            else
            {
                var valueArray = s.split("/");
                if(valueArray.length > 1)
                {
                    if(valueArray[0] != "")
                    {
                        if(valueArray[0].charAt(0) == 'P')
                        {
                            this.a = new OMAR.models.Duration(valueArray[0]);
                        }
                        else
                        {
                            this.a = new OMAR.models.Date(valueArray[0]);
                        }
                    }
                    if(valueArray[1] != "")
                    {
                        if(valueArray[1].charAt(0) == 'P')
                        {
                            this.b = new OMAR.models.Duration(valueArray[1]);
                        }
                        else
                        {
                            this.b = new OMAR.models.Date(valueArray[1]);
                        }
                    }
                }
            }

            return this;
        }
    }
);

OMAR.models.ISO8601=Backbone.Model.extend(
    {
        defaults: {
            "date":  ""
        },
        initialize:function(options)
        {

        },
        validate:function(attrs)
        {
            if((attrs.date != null)&&(attrs.date!=""))
            {
                if(!OMAR.isIso8601(attrs.date))
                {
                    return "Date is invalid";
                }
            }
        },
        convertToDate:function(){
            var intervals = convertToIntervals();
            if(intervals.length>0)
            {
                return intervals.getStartEndDate()[0];
            }
            return null;
        },
        convertToIntervals:function(){
            var intervals = new Array();
            var value = this.get("date");
            if(OMAR.isIso8601(value))
            {
                var splitIntervals = value.split(",");
                for(idx = 0; idx < splitIntervals.length;++idx)
                {
                    interval =  new OMAR.models.Interval(splitIntervals[idx]);
                    this.dateIntervals.push(interval);
                }
            }
            return intervals;
        }
        /*
         setFromISOString:function(s)
         {
         this.dateIntervals = new Array();
         splitIntervals = s.split(",");
         var idx = 0;
         for(idx = 0; idx < splitIntervals.length;++idx)
         {
         interval =  new OMAR.Interval(splitIntervals[idx]);
         this.dateIntervals.push(interval);
         }
         return this;
         }
         */
    }
);


OMAR.models.SimpleDateRangeModel=Backbone.Model.extend({
    defaults:{
      startDate:"",
      endDate:""
    },
    initialize:function(params)
    {
    },
    validate:function(attrs){
        if(attrs.startDate&&!OMAR.isIso8601(attrs.startDate))
        {
            return "Start date is not an ISO date standard format";
        }
        if(attrs.endDate&&!OMAR.isIso8601(attrs.endDate))
        {
            return "Start date is not an ISO date standard format";
        }
    },
    toCql:function(columnNameStartDate, columnNameEndDate){
        var result = "";
        var startDate = this.get("startDate");
        var endDate = this.get("endDate");

        if(columnNameStartDate&&columnNameEndDate)
        {
            if(startDate&&endDate)
            {
                result += "((";
                result += ("'"+startDate+"'<=" +columnNameEndDate);
                result += ")AND(";
                result += (columnNameStartDate +"<=" +"'"+endDate+"'");
                result += "))";
            }
            else if(startDate!="")
            {
                result +="(";
                result +=(columnNameStartDate +">='"+startDate+"'");
                result +=")";
            }
            else if(endDate!="")
            {
                result +="(";
                result +=(columnNameStartDate +"<='"+endDate+"'");
                result +=")";
            }
        }
        else if(columnNameStartDate)
        {
            if((startDate != "")&&(endDate!=""))
            {
                result +="((";
                result +=(columnNameStartDate +">='"+startDate+"'");
                result +=")AND(";
                result +=(columnNameStartDate +"<='"+endDate+"'");
                result += "))";
            }
            else if(startDate!="")
            {
                result +="(";
                result +=(columnNameStartDate +">='"+startDate+"'");
                result +=")";
            }
            else if(endDate!="")
            {
                result +="(";
                result +=(columnNameStartDate +"<='"+endDate+"'");
                result +=")";
            }
        }
        return result;
    }
});

OMAR.views.SimpleDateRangeView=Backbone.View.extend({
    el:"#dateTimeId",
    initialize:function(params){
        this.model = new OMAR.models.SimpleDateRangeModel();
        this.setElement(this.el);
        this.startDateTimeEl = $("#startDateTime");
        this.endDateTimeEl = $("#endDateTime");
        this.startDateTimeEl.datetimepicker({
            dateFormat: "yy-mm-dd",
            timeFormat: "HH:mm:ss.lz",
            separator: 'T',
            showSecond: true,
            showMillisec: true,
            showOn: "button",
            //buttonImage: "images/calendar.gif",
            buttonText:"...",
            buttonImageOnly: false
        });

        this.endDateTimeEl.datetimepicker({
            dateFormat: "yy-mm-dd",
            timeFormat: "HH:mm:ss.lz",
            separator: 'T',
            showSecond: true,
            showMillisec: true,
            showOn: "button",
           // buttonImage: "images/calendar.gif",
            buttonText:"...",
            buttonImageOnly: false
        });
    },
    render:function(){
    },
    events:{
        "change #startDateTime" : "startDateTimeChange",
        "change #endDateTime"   : "endDateTimeChange"
    },
    startDateTimeChange:function(){
        var v = this.startDateTimeEl.val();
        if(!v ||(v==""))
        {
            this.model.set("startDate", "");
        }
        else if(OMAR.isIso8601(v))
        {
            this.model.set("startDate", v);
        }
    },
    endDateTimeChange:function(){
        var v = this.endDateTimeEl.val();
        if(!v ||(v==""))
        {
            this.model.set("endDate", "");
        }
        else if(OMAR.isIso8601(v))
        {
            this.model.set("endDate", v);
        }
    }
})