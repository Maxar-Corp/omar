modules = {
  application {
    resource url: 'js/application.js'
  }

  easyui {
    dependsOn( 'jquery' )
    resource url: [dir: "js/jquery-easyui/themes/default", file: "easyui.css"]
    resource url: [dir: "js/jquery-easyui/themes", file: "icon.css"]
    resource url: [dir: "js/jquery-easyui/demo", file: "demo.css"]
    resource url: [dir: "js/jquery-easyui", file: "jquery.easyui.min.js"]
  }

  underscore {
    resource url: [dir: 'js/backbone', file: "underscore-min.js"]
  }

  backbone {
    dependsOn( 'underscore,jquery' )
    resource url: [dir: "js/backbone", file: "backbone-min.js"]
  }

  conditionBuilder {
    dependsOn 'jquery,backbone'
    resource url: [dir: "js/jsexpbuilder2/src/cb", file: "condition-builder.css"]
    resource url: [dir: "js/jsexpbuilder2/src/cb/res", file: "json2.js"]
    resource url: [dir: "js/jsexpbuilder2/src/cb", file: "condition-builder.js"]
  }

  standard {
    dependsOn 'easyui,conditionBuilder'
  }

//  foobar {
//    resource url: [plugin: 'jquery', dir: 'js/jquery', file: 'jquery-1.11.0.min.js']
//    resource url: [dir: 'js/backbone', file: 'underscore-min.js']
//    resource url: [dir: 'js/backbone', file: 'backbone-min.js']
//
//    resource url: [dir: "js/jsexpbuilder2/src/cb", file: "condition-builder.css"]
//    resource url: [dir: "js/jsexpbuilder2/src/cb/res", file: "json2.js"]
//    resource url: [dir: "js/jsexpbuilder2/src/cb", file: "condition-builder.js"]
//
//  }
}