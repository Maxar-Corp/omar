modules = {
	federationRasterSearch{
		resource url: [plugin: 'omar-common-ui', dir: 'js', file:'OMAR.js']
      	resource url: [plugin: 'omar-common-ui', dir: 'js', file: 'json2.js']
        resource url: [plugin: 'omar-common-ui', dir: 'js/underscore', file:'underscore-1.4.2.js']
        resource url: [plugin: 'omar-common-ui', dir: 'js', file: 'backbone-min.js']
        resource url: [plugin: 'omar-common-ui', dir: 'js', file: 'BboxModelView.js']
        resource url: [plugin: 'omar-ogc', dir: 'js', file: 'WfsModel.js']


        resource url: [dir: 'js', file: 'rasterSearch.js']

        // css resources for jquery
		resource url: [plugin: 'omar-common-ui', dir: 'css/jQuery', file: 'layout-default-latest.css']
		resource url: [plugin: 'omar-common-ui', dir: 'css/jQuery/smoothness', file: 'jquery-ui-1.9.2.custom.css']
		resource url: [plugin: 'omar-common-ui', dir: 'css/jQuery', file: 'jquery-ui-timepicker-addon.css']

		resource url: [plugin: 'omar-common-ui', dir: 'js/jQuery', file: 'jquery-ui-timepicker-addon.js']
 
 
     }
}