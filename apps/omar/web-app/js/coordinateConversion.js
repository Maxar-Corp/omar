function CoordinateConversion()
{

this.ddToDms = function(decimalDegrees, hemisphere, secondsPrecision)
{
    this.degreesAbs = Math.abs(decimalDegrees);
    this.degrees = Math.floor(this.degreesAbs);

    this.minutesAbs = Math.abs((this.degreesAbs - this.degrees) * 60);
    this.minutes = Math.floor(this.minutesAbs);

    this.seconds = Math.abs((this.minutesAbs - this.minutes) * 60);
    if(!secondsPrecision)
    {
        this.seconds = this.seconds.toFixed(3);
    }
    else
    {
        this.seconds = this.seconds.toFixed(secondsPrecision);
    }

    if(this.minutes < 10 && this.seconds < 10)
    {
        this.dms = this.degrees + " 0" + this.minutes + " 0" + this.seconds;
    }
    else if (this.minutes < 10)
    {
        this.dms = this.degrees + " 0" + this.minutes + " " + this.seconds;
    }
    else if (this.seconds < 10)
    {
        this.dms = this.degrees + " " + this.minutes + " 0" + this.seconds;
    }
    else
    {
        this.dms = this.degrees + " " + this.minutes + " " + this.seconds;
    }

    if(hemisphere == "latitude" && decimalDegrees > 0)
    {
        this.dms = this.dms + " N";
    }
    else if(hemisphere == "latitude" && decimalDegrees < 0)
    {
        this.dms = this.dms + " S";
    }
    else if(hemisphere == "longitude" && decimalDegrees > 0)
    {
        this.dms = this.dms + " E";
    }
    else if(hemisphere == "longitude" && decimalDegrees < 0)
    {
        this.dms = this.dms + " W";
    }
    else
    {
        this.dms;
    }

    return this.dms;
};

this.dmsToDd = function(degrees, minutes, seconds)
{
    this.dd = Math.abs(degrees) + Math.abs(minutes / 60) + Math.abs(seconds / 3600);

    if ( degrees < 0 )
    {
        this.dd = -this.dd;
    }
    else
    {
        this.dd;
    }

    return this.dd;
};

}