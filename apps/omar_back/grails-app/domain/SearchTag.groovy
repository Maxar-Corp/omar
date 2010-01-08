class SearchTag {
  String name
  String description

  static constraints = {
    name(unique:true)
    description(unique:true)
  }

  static mapping = {
    cache true
    columns {
      name index: "search_tag_name_idx"
    }
  }
}
