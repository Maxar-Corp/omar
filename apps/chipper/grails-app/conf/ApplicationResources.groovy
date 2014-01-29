modules = {
  application {
    resource url: 'js/application.js'
  }
  standard {
//    resource url: "js/jquery-easyui/themes/icon.css", disposition: 'head'
    resource url: [dir: "js/jquery-easyui/themes/default", file: "easyui.css"], disposition: 'head'
    resource url: [plugin: 'jquery', dir: "js/jquery", file: "jquery-1.10.2.min.js"], disposition: 'head'
    resource url: [dir: "js/jquery-easyui", file: "jquery.easyui.min.js"], disposition: 'head'
//    resource url: "js/jquery-easyui/locale/easyui-lang-${locale}.js", disposition: 'head'

  }
}