class UrlMappings
{

  static mappings = {
    "/$controller/$action?/$id?" {
      constraints {
        // apply constraints here
      }
    }

//    "/"( view: "/index" )
    "/"( controller: 'twoColorMulti' )
    "500"( view: '/error' )
  }
}
