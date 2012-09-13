function SpinControl(){
    this.spinnerOpts = {
        lines: 13, // The number of lines to draw
        length: 24, // The length of each line
        width: 8, // The line thickness
        radius: 18, // The radius of the inner circle
        corners: 1, // Corner roundness (0..1)
        rotate: 0, // The rotation offset
        color: '#FFFFFF', // #rgb or #rrggbb
        speed: 1, // Rounds per second
        trail: 100, // Afterglow percentage
        shadow: true, // Whether to render a shadow
        hwaccel: false, // Whether to use hardware acceleration
        className: 'spinner-control', // The CSS class to assign to the spinner
        zIndex: 2e9, // The z-index (defaults to 2000000000)
        top: 'auto', // Top position relative to parent in px
        left: 'auto' // Left position relative to parent in px
    };
    this.spinner;
    this.targetDiv;
    this.counter = 0;
    this.initializeWithDiv = function (divEl)
    {
        counter = 0;
        this.targetDiv = divEl
    }
    this.stop = function ()
    {
        if(this.spinner) this.spinner.stop();

    }
    this.start = function ()
    {
        if(this.spinner)
        {
            this.spinner = this.spinner.spin(this.targetDiv);
        }
        else if(this.targetDiv)
        {
            this.spinner = new Spinner(this.spinnerOpts).spin(this.targetDiv);
        }
    }
    this.increaseCounter = function(){
        counter++;
        this.start();
    }
    this.decreaseCounter = function(){
        --counter;
        if(counter < 1)
        {
            this.stop();
            counter = 0;
        }
    }
}
