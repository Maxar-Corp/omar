modules = {
  jeasyui {
/*
<r:external plugin="omar-chipper" dir="js/jquery-easyui" file="jquery.min.js"/>
*/
    dependsOn( 'jquery' )
/*
<r:external plugin="omar-chipper" dir="js/jquery-easyui/themes" file="icon.css"/>
<r:external plugin="omar-chipper" dir="js/jquery-easyui/themes/default" file="easyui.css"/>
<r:external plugin="omar-chipper" dir="js/jquery-easyui" file="jquery.easyui.min.js"/>
*/
    resource url: [plugin: 'omar-chipper', dir: 'js/jquery-easyui/themes', file: 'icon.css']
    resource url: [plugin: 'omar-chipper', dir: 'js/jquery-easyui/themes/default', file: 'easyui.css']
    resource url: [plugin: 'omar-chipper', dir: 'js/jquery-easyui', file: 'jquery.easyui.min.js']
  }
  chipperOpenLayers {
/*
<r:external plugin="omar-chipper" dir="js/openlayers/theme/default" file="style.css"/>
<r:external plugin="omar-chipper" dir="js/openlayers" file="OpenLayers.light.js"/>
*/
    resource url: [plugin: 'omar-chipper', dir: 'js/openlayers/theme/default', file: 'style.css']
    resource url: [plugin: 'omar-chipper', dir: 'js/openlayers', file: 'OpenLayers.light.js']
  }

  chipperUnderscore {
    resource url: [plugin: 'omar-chipper', dir: 'js/backbone', file: 'underscore-min.js']
  }
  chipperBackbone {
    dependsOn( 'chipperUnderscore' )
    resource url: [plugin: 'omar-chipper', dir: 'js/backbone', file: 'backbone-min.js']
  }

}
