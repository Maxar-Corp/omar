class DataManagerUrlMappings
{
  static mappings = {
    "/dataManager/${opType}" {

      controller = "dataManager"
      action = "action"

      constraints {
        // apply constraints here
      }
    }
  }
}