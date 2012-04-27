function OmarPoint(px,py){

    this.x = px;
    this.y = py;

    this.sub = function(pt)
    {
        return new OmarPoint(this.x - pt.x,
                             this.y - pt.y);
    }
   this.add = function(pt)
    {
        return new OmarPoint(this.x + pt.x,
                             this.y + pt.y);
    }
    this.toString = function()
    {
        return ("x:"+this.x + ",y:" +this.y);
    }
    this.toJSON = function()
    {
        return ("{x:" + this.x + ",\ny:" + this.y + "}");
    }
}

function OmarAffineParams(){
    this.rotate    = 0.0;
    this.pivot     = new OmarPoint(0.0,0.0);
    this.translate = new OmarPoint(0.0,0.0);
    this.scale     = new OmarPoint(1.0,1.0);

    this.toMatrix = function()
    {
        var result = new OmarMatrix3x3();
        var trans     = new OmarMatrix3x3();
        var negPivot  = new OmarMatrix3x3();
        var pivot     = new OmarMatrix3x3();
        var rotate    = new OmarMatrix3x3();
        var scale     = new OmarMatrix3x3();
        trans.makeTranslate(this.translate.x, this.translate.y);
        negPivot.makeTranslate(-this.pivot.x, 
                                -this.pivot.y);
        pivot.makeTranslate(this.pivot.x, 
                            this.pivot.y);
        rotate.makeRotateZ(this.rotate);
        scale.makeScale(this.scale.x, this.scale.y);
        result.copy(scale.transform(pivot.transform(rotate.transform(negPivot.transform(trans)))));
        return result;
    }
}

function OmarMatrix3x3(){
     this.m00 = 1.0;
     this.m01 = 0.0;
     this.m02 = 0.0;
     this.m10 = 0.0;
     this.m11 = 1.0;
     this.m12 = 0.0;
     this.m20 = 0.0;
     this.m21 = 0.0;
     this.m22 = 1.0;


     this.create = function(v00, v01, v02,
                            v10, v11, v12,
                            v20, v21, v22)
     {
        result = new OmarMatrix3x3();

        result.m00 = v00;
        result.m01 = v01;
        result.m02 = v02;
        result.m10 = v10;
        result.m11 = v11;
        result.m12 = v12;
        result.m20 = v20;
        result.m21 = v21;
        result.m22 = v22;

        return result;
     }
     this.copy = function(m)
     {
        this.m00 = m.m00;
        this.m01 = m.m01;
        this.m02 = m.m02;
        this.m10 = m.m10;
        this.m11 = m.m11;
        this.m12 = m.m12;
        this.m20 = m.m20;
        this.m21 = m.m21;
        this.m22 = m.m22;

        return this;
     }
     this.assign = function(v00, v01, v02,
                            v10, v11, v12,
                            v20, v21, v22)
     {
        this.m00 = v00;
        this.m01 = v01;
        this.m02 = v02;
        this.m10 = v10;
        this.m11 = v11;
        this.m12 = v12;
        this.m20 = v20;
        this.m21 = v21;
        this.m22 = v22;

        return this;
     }
     this.makeRotateX = function(angle)
     {
        RAD_PER_DEG = Math.PI/180.0;
        Cosine = Math.cos(angle*RAD_PER_DEG);
        Sine   = Math.sin(angle*RAD_PER_DEG);

        this.assign(1.0, 0.0, 0.0,
                    0.0, Cosine, Sine,
                    0, -Sine, Cosine);

        return this;
     }
     this.makeRotateY = function(angle)
     {
        RAD_PER_DEG = Math.PI/180.0;
        Cosine = Math.cos(angle*RAD_PER_DEG);
        Sine   = Math.sin(angle*RAD_PER_DEG);

        this.assign(Cosine, 0.0, -Sine,
                    0.0, 1.0, 0.0,
                    Sine, 0.0, Cosine);
        return this;
     }

     this.makeRotateZ = function(angle)
     {
        RAD_PER_DEG = Math.PI/180.0;
        Cosine = Math.cos(angle*RAD_PER_DEG);
        Sine   = Math.sin(angle*RAD_PER_DEG);

        this.assign(Cosine, Sine, 0.0,
                    -Sine, Cosine, 0.0,
                    0.0, 0.0, 1.0);

        return this;
     }

     this.makeScale = function(x,y)
     {
        if(x instanceof OmarPoint)
        {
            this.m00 = x.x;
            this.m11 = x.y;
        }
        else
        {
            if(x) this.m00 = x;
            if(y) this.m11 = y;
        }
 
        return this;
     }
     this.makeTranslate = function(dx, dy)
     {
        if(dx instanceof OmarPoint)
        {
            this.m02 = dx.x;
            this.m12 = dx.y;
        }
        else
        {
            if(dx) this.m02 = dx;
            if(dy) this.m12 = dy;
        }
 
        return this;
     }
     this.transform = function(v)
     {
        if(v instanceof OmarMatrix3x3)
        {
            var result = new OmarMatrix3x3();

            result.m00 = this.m00*v.m00 + this.m01*v.m10 + this.m02*v.m20;
            result.m01 = this.m00*v.m01 + this.m01*v.m11 + this.m02*v.m21;
            result.m02 = this.m00*v.m02 + this.m01*v.m12 + this.m02*v.m22;

            result.m10 = this.m10*v.m00 + this.m11*v.m10 + this.m12*v.m20;
            result.m11 = this.m10*v.m01 + this.m11*v.m11 + this.m12*v.m21;
            result.m12 = this.m10*v.m02 + this.m11*v.m12 + this.m12*v.m22;

            result.m20 = this.m20*v.m00 + this.m21*v.m10 + this.m22*v.m20;
            result.m21 = this.m20*v.m01 + this.m21*v.m11 + this.m22*v.m21;
            result.m22 = this.m20*v.m02 + this.m21*v.m12 + this.m22*v.m22;

            return result;
        }
        else
        {
            return new OmarPoint(v.x*this.m00 + v.y*this.m01 + this.m02,
                                 v.x*this.m10 + v.y*this.m11 + this.m12);
        }
        return null;
     }
     this.inv = function()
     {
        divisor = this.det();
        if(Math.abs(divisor) < 0.000000001) divisor = 1.0;

        result     = new OmarMatrix3x3();

        result.m00 = this.m22*this.m11 - this.m21*this.m12;
        result.m01 = -(this.m22*this.m01-this.m21*this.m02);
        result.m02 = this.m12*this.m01-this.m11*this.m02;
        result.m10 = -(this.m22*this.m10-this.m20*this.m12);
        result.m11 = this.m22*this.m00-this.m20*this.m02;
        result.m12 = -(this.m12*this.m00-this.m10*this.m02);
        result.m20 = this.m21*this.m10-this.m20*this.m11;
        result.m21 = -(this.m21*this.m00-this.m20*this.m01);
        result.m22 = this.m11*this.m00-this.m10*this.m01;

        result.m00/=divisor;
        result.m01/=divisor;
        result.m02/=divisor;
        result.m10/=divisor;
        result.m11/=divisor;
        result.m12/=divisor;
        result.m20/=divisor;
        result.m21/=divisor;
        result.m22/=divisor;

        return result;
     }
     this.det = function()
     {
        return (this.m00*(this.m22*this.m11 -this.m21*this.m12)-
                this.m10*(this.m22*this.m01-this.m21*this.m02) -
                this.m20*(this.m12*this.m01-this.m11*this.m02));
     }
     this.toString = function()
     {
         return (String(this.m00) + ", " + this.m01 + ", " +this.m02 + "\n"+
                 this.m10 + ", " + this.m11 + ", " + this.m12 + "\n"+
                 this.m20 + ", " + this.m21 + ", " + this.m22);
     }

}

