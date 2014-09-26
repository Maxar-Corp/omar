modules = {
application {
        resource url:'js/application.js'
    }
  singleColumn {
    resource url: [dir: 'css', file: 'main.css']
    resource url: [dir: 'css', file: 'omar-2.0.css']

    resource url: [plugin: 'yui', dir: 'js/yui/reset-fonts-grids', file: 'reset-fonts-grids.css']
    resource url: [plugin: 'yui', dir: 'js/yui/assets/skins/sam', file: 'skin.css']

    resource url: [dir: 'js', file: 'application.js']
    resource url: [plugin: 'yui', dir: 'js/yui/yahoo-dom-event', file: 'yahoo-dom-event.js']
    resource url: [dir: 'js', file: 'datechooser.js']
    resource url: [plugin: 'yui', dir: 'js/yui/calendar', file: 'calendar-min.js']
    resource url: [plugin: 'yui', dir: 'js/yui/element', file: 'element-min.js']
    resource url: [plugin: 'yui', dir: 'js/yui/tabview', file: 'tabview-min.js']
  }
  searchPageLayout {

    resource url: [dir: 'css', file: 'main.css']
    resource url: [dir: 'css', file: 'omar-2.0.css']

    resource url: [plugin: 'yui', dir: "js/yui/reset-fonts-grids", file: "reset-fonts-grids.css"]
    resource url: [plugin: 'yui', dir: "js/yui/assets/skins/sam", file: "skin.css"]

    resource url: [plugin: 'yui', dir: "js/yui/yahoo-dom-event", file: "yahoo-dom-event.js"]
    resource url: [plugin: 'yui', dir: "js/yui/element", file: "element-min.js"]
    resource url: [plugin: 'yui', dir: "js/yui/dragdrop", file: "dragdrop-min.js"]
    resource url: [plugin: 'yui', dir: "js/yui/resize", file: "resize-min.js"]
    resource url: [plugin: 'yui', dir: "js/yui/animation", file: "animation-min.js"]
    resource url: [plugin: 'yui', dir: "js/yui/layout", file: "layout-min.js"]
    resource url: [plugin: 'yui', dir: 'js/yui/container/', file: 'container_core-min.js']
    resource url: [plugin: 'yui', dir: 'js/yui/menu/', file: 'menu-min.js']
    resource url: [plugin: 'yui', dir: 'js/yui/tabview/', file: 'tabview-min.js']
    resource url: [plugin: 'yui', dir: 'js/yui/calendar', file: 'calendar-min.js']

    resource url: [plugin: 'openlayers', dir: 'css', file: 'mapwidget2.css']
    resource url: [plugin: 'openlayers', dir: 'js/theme/default', file: 'style.css']
    resource url: [plugin: 'openlayers', dir: 'js', file: 'OpenLayers.js']


    resource url: [plugin: "omar-core", dir: "js/prototype", file: "prototype.js"]
    resource url: [plugin: 'omar-core', dir: 'js', file: 'omar.js']
    resource url: [plugin: 'omar-core', dir: 'js', file: 'coord.js']

    //resource url: [plugin: "omar-core", dir: "js/prototype", file: "prototype.js"]

    resource url: [plugin: 'omar-common-ui', dir: 'js', file: 'OMAR.js']
    resource url: [dir: 'js', file: 'application.js']
    resource url: [dir: 'js', file: 'datechooser.js']

    resource url: [dir: 'css', file: 'searchPageLayout.css']
    resource url: [dir: 'js', file: 'searchPageLayout.js']

  }

  resultsPageLayout {
    resource url: [dir: 'css', file: 'main.css']
    resource url: [dir: 'css', file: 'omar-2.0.css']

    resource url: [plugin: 'yui', dir: 'js/yui/reset-fonts-grids', file: 'reset-fonts-grids.css']
    resource url: [plugin: 'yui', dir: 'js/yui/assets/skins/sam', file: 'skin.css']

    resource url: [plugin: 'yui', dir: 'js/yui/yahoo-dom-event', file: 'yahoo-dom-event.js']
    resource url: [plugin: 'yui', dir: 'js/yui/calendar', file: 'calendar-min.js']
    resource url: [plugin: 'yui', dir: 'js/yui/element', file: 'element-min.js']
    resource url: [plugin: 'yui', dir: 'js/yui/tabview/', file: 'tabview-min.js']
    resource url: [plugin: 'yui', dir: 'js/yui/container/', file: 'container_core-min.js']
    resource url: [plugin: 'yui', dir: 'js/yui/menu/', file: 'menu-min.js']
    resource url: [plugin: 'yui', dir: 'js/yui/dragdrop', file: 'dragdrop-min.js']
    resource url: [plugin: 'yui', dir: "js/yui/resize", file: "resize-min.js"]
    resource url: [plugin: 'yui', dir: "js/yui/animation", file: "animation-min.js"]
    resource url: [plugin: 'yui', dir: "js/yui/layout", file: "layout-min.js"]

    resource url: [plugin: "omar-core", dir: "js/prototype", file: "prototype.js"]

    resource url: [dir: 'js', file: 'application.js']
    resource url: [dir: 'js', file: 'datechooser.js']
    resource url: [dir: 'js', file: 'resultsPageLayout.js']
  }

  rasterViewsStatic {
    resource url: [dir: 'css', file: 'main.css']
    resource url: [dir: 'css', file: 'omar-2.0.css']
    resource url: [plugin: 'yui', dir: 'js/yui/reset-fonts-grids', file: 'reset-fonts-grids.css']
    resource url: [plugin: 'yui', dir: 'js/yui/assets/skins/sam', file: 'skin.css']

    resource url: [dir: 'js', file: 'application.js']
    resource url: [plugin: 'yui', dir: 'js/yui/yahoo-dom-event', file: 'yahoo-dom-event.js']
    resource url: [dir: 'js', file: 'datechooser.js']
    resource url: [plugin: 'yui', dir: 'js/yui/calendar', file: 'calendar-min.js']
    resource url: [plugin: 'yui', dir: 'js/yui/element', file: 'element-min.js']
    resource url: [plugin: 'yui', dir: 'js/yui/tabview/', file: 'tabview-min.js']
    resource url: [plugin: 'yui', dir: 'js/yui/container/', file: 'container_core-min.js']
    resource url: [plugin: 'yui', dir: 'js/yui/menu/', file: 'menu-min.js']
    resource url: [plugin: 'yui', dir: 'js/yui/dragdrop', file: 'dragdrop-min.js']
    resource url: [plugin: 'yui', dir: 'js/yui/slider', file: 'slider-min.js']

    resource url: [plugin: 'openlayers', dir: 'css', file: 'mapwidget2.css']
    resource url: [plugin: 'openlayers', dir: 'js/theme/default', file: 'style.css']

    resource url: [plugin: 'openlayers', dir: 'js', file: 'OpenLayers.js']

    resource url: [plugin: 'omar-core', dir: 'js', file: 'coordinateConversion.js']
    resource url: [plugin: 'omar-core', dir: 'js', file: 'mapwidget.js']
  }

  multiLayerLayout {
    resource url: [dir: 'css', file: 'main.css']
    resource url: [dir: 'css', file: 'omar-2.0.css']

    resource url: [plugin: 'yui', dir: "js/yui/reset-fonts-grids", file: "reset-fonts-grids.css"]
    resource url: [plugin: 'yui', dir: "js/yui/assets/skins/sam", file: "skin.css"]

    resource url: [plugin: 'yui', dir: "js/yui/yahoo-dom-event", file: "yahoo-dom-event.js"]
    resource url: [plugin: 'yui', dir: "js/yui/element", file: "element-min.js"]
    //resource url: [plugin: 'yui', dir: "js/yui/dragdrop", file: "dragdrop-min.js"]
    //resource url: [plugin: 'yui', dir: "js/yui/resize", file: "resize-min.js"]
    //resource url: [plugin: 'yui', dir: "js/yui/animation", file: "animation-min.js"]
    resource url: [plugin: 'yui', dir: "js/yui/layout", file: "layout-min.js"]
    resource url: [plugin: 'yui', dir: 'js/yui/container/', file: 'container_core-min.js']
    resource url: [plugin: 'yui', dir: 'js/yui/menu/', file: 'menu-min.js']
    //resource url: [plugin: 'yui', dir: 'js/yui/tabview/', file: 'tabview-min.js']
    //resource url: [plugin: 'yui', dir: 'js/yui/calendar', file: 'calendar-min.js']


    resource url: [plugin: 'openlayers', dir: 'css', file: 'mapwidget2.css']
    resource url: [plugin: 'openlayers', dir: 'js/theme/default', file: 'style.css']
    resource url: [plugin: 'openlayers', dir: 'js', file: 'OpenLayers.js']


    resource url: [plugin: 'omar-core', dir: 'js', file: 'mapwidget.js']
    resource url: [plugin: 'omar-core', dir: 'js', file: 'coord.js']

    resource url: [dir: 'js', file: 'application.js']
    //resource url: [dir: 'js', file: 'datechooser.js']

    resource url: [dir: 'css', file: 'multiLayerLayout.css']
    resource url: [dir: 'js', file: 'multiLayerLayout.js']


  }

  groundSpacePageLayout {
    resource url: [dir: 'css', file: 'main.css']
    resource url: [dir: 'css', file: 'omar-2.0.css']
    resource url: [plugin: 'omar-core', dir: 'js', file: 'spin-min.js']


    resource url: [plugin: 'yui', dir: "js/yui/reset-fonts-grids", file: "reset-fonts-grids.css"]
    resource url: [plugin: 'yui', dir: "js/yui/assets/skins/sam", file: "skin.css"]

    resource url: [plugin: 'yui', dir: "js/yui/yahoo-dom-event", file: "yahoo-dom-event.js"]
    resource url: [plugin: 'yui', dir: "js/yui/element", file: "element-min.js"]
    resource url: [plugin: 'yui', dir: "js/yui/dragdrop", file: "dragdrop-min.js"]
    resource url: [plugin: 'yui', dir: "js/yui/resize", file: "resize-min.js"]
    //resource url: [plugin: 'yui', dir: "js/yui/animation", file: "animation-min.js"]
    resource url: [plugin: 'yui', dir: "js/yui/layout", file: "layout-min.js"]
    resource url: [plugin: 'yui', dir: 'js/yui/container/', file: 'container_core-min.js']
    resource url: [plugin: 'yui', dir: 'js/yui/menu/', file: 'menu-min.js']
    resource url: [plugin: 'yui', dir: 'js/yui/tabview/', file: 'tabview-min.js']
    //resource url: [plugin: 'yui', dir: 'js/yui/calendar', file: 'calendar-min.js']
    resource url: [plugin: 'yui', dir: 'js/yui/slider', file: 'slider-min.js']
    resource url: [plugin: 'yui', dir: 'js/yui/json', file: 'json-min.js']


    resource url: [plugin: 'openlayers', dir: 'css', file: 'mapwidget2.css']
    resource url: [plugin: 'openlayers', dir: 'js/theme/default', file: 'style.css']
    resource url: [plugin: 'openlayers', dir: 'js', file: 'OpenLayers.js']

    resource url: [plugin: 'omar-common-ui', dir: 'js', file: 'OMAR.js']

    resource url: [plugin: 'omar-core', dir: 'js', file: 'mapwidget.js']
    resource url: [plugin: 'omar-core', dir: 'js', file: 'coord.js']

    resource url: [dir: 'js', file: 'application.js']
    resource url: [dir: 'js', file: 'matrix.js']
    // resource url: [dir: 'js', file: 'OpenLayersImageManipulator.js']
    //resource url: [dir: 'js', file: 'datechooser.js']

    resource url: [dir: 'css', file: 'groundSpacePageLayout.css']
    resource url: [dir: 'js', file: 'groundSpacePageLayout.js']
    resource url: [dir: 'js', file: 'groundSpaceLogic.js']

  }

  imageSpacePageLayout {

    resource url: [dir: 'css', file: 'main.css']
    resource url: [dir: 'css', file: 'omar-2.0.css']
    resource url: [plugin: 'omar-core', dir: 'js', file: 'spin-min.js']

    resource url: [plugin: 'yui', dir: "js/yui/reset-fonts-grids", file: "reset-fonts-grids.css"]
    resource url: [plugin: 'yui', dir: "js/yui/assets/skins/sam", file: "skin.css"]

    resource url: [plugin: 'yui', dir: "js/yui/yahoo-dom-event", file: "yahoo-dom-event.js"]
    resource url: [plugin: 'yui', dir: "js/yui/element", file: "element-min.js"]
    resource url: [plugin: 'yui', dir: "js/yui/dragdrop", file: "dragdrop-min.js"]
    resource url: [plugin: 'yui', dir: "js/yui/resize", file: "resize-min.js"]
    resource url: [plugin: 'yui', dir: "js/yui/animation", file: "animation-min.js"]
    resource url: [plugin: 'yui', dir: "js/yui/layout", file: "layout-min.js"]
    resource url: [plugin: 'yui', dir: 'js/yui/container', file: 'container_core-min.js']
    resource url: [plugin: 'yui', dir: 'js/yui/menu', file: 'menu-min.js']
    resource url: [plugin: 'yui', dir: 'js/yui/tabview', file: 'tabview-min.js']
    //resource url: [plugin: 'yui', dir: 'js/yui/calendar', file: 'calendar-min.js']
    resource url: [plugin: 'yui', dir: 'js/yui/slider', file: 'slider-min.js']
    resource url: [plugin: 'yui', dir: 'js/yui/json', file: 'json-min.js']


    resource url: [plugin: 'openlayers', dir: 'css', file: 'mapwidget2.css']
    resource url: [plugin: 'openlayers', dir: 'js/theme/default', file: 'style.css']
    resource url: [plugin: 'openlayers', dir: 'js', file: 'OpenLayers.js']


    resource url: [plugin: 'omar-common-ui', dir: 'js', file: 'OMAR.js']
    //resource url: [plugin: 'omar-core', dir: 'js', file: 'mapwidget.js']
    resource url: [plugin: 'omar-core', dir: 'js', file: 'coord.js']

    resource url: [dir: 'js/cssSandpaper', file: 'EventHelpers.js']
    resource url: [dir: 'js/cssSandpaper', file: 'cssQuery-p.js']
    resource url: [dir: 'js/cssSandpaper/jcoglan.com', file: 'sylvester.js']
    resource url: [dir: 'js/cssSandpaper', file: 'cssSandpaper-lite.js']
    resource url: [dir: 'js', file: 'application.js']
    resource url: [dir: 'js', file: 'matrix.js']
    resource url: [plugin: 'omar-core', dir: 'js', file: 'coord.js']

    resource url: [dir: 'js', file: 'OpenLayersImageManipulator.js']
    //resource url: [dir: 'js', file: 'datechooser.js']

    resource url: [dir: 'css', file: 'imageSpacePageLayout.css']
    resource url: [dir: 'js', file: 'imageSpaceLogic.js']
    resource url: [dir: 'js', file: 'imageSpacePageLayout.js']
  }


  mapWidgetLayout {
    resource url: [plugin: 'yui', dir: "js/yui/reset-fonts-grids", file: "reset-fonts-grids.css"]
    resource url: [plugin: 'yui', dir: "js/yui/assets/skins/sam", file: "skin.css"]

    resource url: [plugin: 'yui', dir: "js/yui/yahoo-dom-event", file: "yahoo-dom-event.js"]
    resource url: [plugin: 'yui', dir: "js/yui/element", file: "element-min.js"]
    resource url: [plugin: 'yui', dir: "js/yui/dragdrop", file: "dragdrop-min.js"]
    resource url: [plugin: 'yui', dir: "js/yui/resize", file: "resize-min.js"]
    resource url: [plugin: 'yui', dir: "js/yui/animation", file: "animation-min.js"]
    resource url: [plugin: 'yui', dir: "js/yui/layout", file: "layout-min.js"]

    resource url: [dir: 'css', file: 'mapWidgetLayout.css']
    resource url: [dir: 'js', file: 'mapWidgetLayout.js']
  }

  showVideoPageLayout {
    resource url: [dir: 'css', file: 'main.css']
    resource url: [dir: 'css', file: 'omar-2.0.css']

    resource url: [plugin: 'yui', dir: "js/yui/reset-fonts-grids", file: "reset-fonts-grids.css"]
    resource url: [plugin: 'yui', dir: "js/yui/assets/skins/sam", file: "skin.css"]

    resource url: [plugin: 'yui', dir: "js/yui/yahoo-dom-event", file: "yahoo-dom-event.js"]
    resource url: [plugin: 'yui', dir: "js/yui/element", file: "element-min.js"]
    resource url: [plugin: 'yui', dir: "js/yui/resize", file: "resize-min.js"]
    resource url: [plugin: 'yui', dir: "js/yui/layout", file: "layout-min.js"]
    resource url: [plugin: 'yui', dir: 'js/yui/container/', file: 'container_core-min.js']
    resource url: [plugin: 'yui', dir: 'js/yui/menu/', file: 'menu-min.js']

    resource url: [dir: 'css', file: 'showVideoPageLayout.css']
    resource url: [dir: 'js', file: 'showVideoPageLayout.js']
  }

  scriptsPageLayout {
    resource url: [plugin: "omar-core", dir: "js/prototype", file: "prototype.js"]
    resource url: [plugin: 'yui', dir: 'js/yui/json', file: 'json-min.js']
  }

  product {
    resource url: [plugin: "omar-core", dir: "js/prototype", file: "prototype.js"]
    resource url: [plugin: 'omar-common-ui', dir: 'js', file: 'OMAR.js']
    resource url: [plugin: 'omar-core', dir: 'js', file: 'jquery.js']
    resource url: [plugin: 'omar-common-ui', dir: 'js/jQuery', file: 'jquery-ui.js']
    resource url: [plugin: 'omar-common-ui', dir: 'js/jQuery', file: 'jquery.layout-latest.js']
    resource url: [plugin: 'omar-common-ui', dir: 'js', file: 'json2.js']
    resource url: [plugin: 'omar-common-ui', dir: 'js/underscore', file: 'underscore-1.4.2.js']
    resource url: [plugin: 'omar-common-ui', dir: 'js', file: 'backbone-min.js']
    resource url: [plugin: 'omar-common-ui', dir: 'css', file: 'jMenu.jquery.css']
    resource url: [plugin: 'omar-common-ui', dir: 'css/jQuery', file: 'layout-default-latest.css']
    resource url: [plugin: 'omar-common-ui', dir: 'css/jQuery', file: 'layout-default-latest.css']
    resource url: [plugin: 'omar-common-ui', dir: 'css/jQuery/smoothness', file: 'jquery-ui-1.9.2.custom.css']
    resource url: [plugin: 'omar-common-ui', dir: 'css/jQuery', file: 'jquery-ui-timepicker-addon.css']
    resource url: [plugin: 'omar-common-ui', dir: 'js', file: 'jMenu.jquery.js']
    resource url: [dir: 'js', file: 'spin-min.js', plugin: 'omar-core']
    //resource url: [dir: 'css', file: 'admin.css']
    //resource url: [dir: 'js', file: 'admin.js']
    resource url: [dir: "js", file: "productPage.js"]
  }

  job {
    resource url: [plugin: "omar-core", dir: "js/prototype", file: "prototype.js"]
    resource url: [plugin: 'omar-common-ui', dir: 'js', file: 'OMAR.js']
    resource url: [plugin: 'omar-core', dir: 'js', file: 'jquery.js']
    resource url: [plugin: 'omar-common-ui', dir: 'js/jQuery', file: 'jquery-ui.js']
    resource url: [plugin: 'omar-common-ui', dir: 'js/jQuery', file: 'jquery.layout-latest.js']
    resource url: [plugin: 'omar-common-ui', dir: 'js', file: 'json2.js']
    resource url: [plugin: 'omar-common-ui', dir: 'js/underscore', file: 'underscore-1.4.2.js']
    resource url: [plugin: 'omar-common-ui', dir: 'js', file: 'backbone-min.js']
    resource url: [plugin: 'omar-common-ui', dir: 'css', file: 'jMenu.jquery.css']
    resource url: [plugin: 'omar-common-ui', dir: 'css/jQuery', file: 'layout-default-latest.css']
    resource url: [plugin: 'omar-common-ui', dir: 'css/jQuery', file: 'layout-default-latest.css']
    resource url: [plugin: 'omar-common-ui', dir: 'css/jQuery/smoothness', file: 'jquery-ui-1.9.2.custom.css']
    resource url: [plugin: 'omar-common-ui', dir: 'css/jQuery', file: 'jquery-ui-timepicker-addon.css']
    resource url: [plugin: 'omar-common-ui', dir: 'js', file: 'jMenu.jquery.js']
    resource url: [dir: 'js', file: 'spin-min.js', plugin: 'omar-core']
    //resource url: [dir: 'css', file: 'admin.css']
    //resource url: [dir: 'js', file: 'admin.js']
    

    resource url: [plugin: 'omar-chipper', dir: 'js/jquery-easyui/themes', file: 'icon.css']
    resource url: [plugin: 'omar-chipper', dir: 'js/jquery-easyui/themes/default', file: 'easyui.css']
    resource url: [plugin: 'omar-chipper', dir: 'js/jquery-easyui', file: 'jquery.easyui.min.js']    

    resource url: [dir: "js", file: "jobPage.js"]
  }

}
