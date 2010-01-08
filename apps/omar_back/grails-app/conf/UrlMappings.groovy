class UrlMappings
{
  static mappings = {
    "/$controller/$action?/$id?" {
      constraints {
        controller(matches: /.*[^(services)].*/)
      }
    }
    "500"(view: '/error')
  }
}
