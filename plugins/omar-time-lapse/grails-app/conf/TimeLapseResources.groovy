modules = 
{
	timeLapse 
	{
		resource url: [plugin: 'yui', dir: "js/yui/reset-fonts-grids", file: "reset-fonts-grids.css"]
		resource url: [plugin: 'yui', dir: "js/yui/assets/skins/sam", file: "skin.css"]
		resource url: [plugin: 'yui', dir: "js/yui/yahoo-dom-event", file: "yahoo-dom-event.js"]
		resource url: [plugin: 'yui', dir: "js/yui/element", file: "element-min.js"]
		resource url: [plugin: 'yui', dir: 'js/yui/container/', file: 'container_core-min.js']
		resource url: [plugin: 'yui', dir: 'js/yui/menu/', file: 'menu-min.js']

		resource url: [plugin: 'yui', dir: "js/yui/resize", file: "resize-min.js"]
		resource url: [plugin: 'yui', dir: "js/yui/layout", file: "layout-min.js"]
		resource url: [dir: 'css', file: 'loginPageLayout.css']

//		resource url: [dir: 'css', file: 'OpenLayers_v2_12.css', plugin: 'openlayers']
//		resource url: [dir: 'js/theme/default', file: 'style.css', plugin: 'openlayers']

		resource url: [dir: 'css/jQuery/cupertino', file: 'jquery-ui.css', plugin: 'omar-common-ui']
		resource url: [dir: 'css', file: 'timeLapse.css']

//		resource url: [dir: 'js', file: 'OpenLayers_v2_12.js', plugin: 'openlayers']

		resource url: [dir: 'js', file: 'jquery.js', plugin: 'omar-core']
		resource url: [dir: 'js/jQuery', file: 'jquery-ui.js', plugin: 'omar-common-ui']
		resource url: [dir: 'js', file: 'coord.js', plugin: 'omar-core']
		resource url: [dir: 'js', file: 'spin-min.js', plugin: 'omar-core']
		resource url: [dir: 'js', file: 'timeLapse.js']
	}
}
