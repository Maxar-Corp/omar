modules = {
  jeasyui {
/*
<r:external plugin="omar-chipper" dir="js/jquery-easyui" file="jquery.min.js"/>
*/
 //   dependsOn( 'jquery' )

    resource url: [plugin: 'omar-core', dir: 'js', file: 'jquery.js']
    resource url: [plugin: 'omar-common-ui', dir: 'js/jQuery', file: 'jquery-ui.js']
    resource url: [plugin: 'omar-common-ui', dir: 'js/jQuery', file: 'jquery.layout-latest.js']

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
    //resource url: [plugin: 'omar-chipper', dir: 'js/openlayers', file: 'OpenLayers.js']
    resource url: [plugin: 'omar-chipper', dir: 'js/openlayers', file: 'OpenLayers.js']

  }

  chipperUnderscore {
    resource url: [plugin: 'omar-chipper', dir: 'js/backbone', file: 'underscore-min.js']
  }
  chipperBackbone {
    dependsOn( 'chipperUnderscore' )
    resource url: [plugin: 'omar-chipper', dir: 'js/backbone', file: 'backbone-min.js']
  }

  spinner {
    resource url: [plugin: 'omar-chipper', dir: 'js', file: 'spin.min.js']
    resource url: [plugin: 'omar-chipper', dir: 'js', file: 'utils.js']
  }
}
