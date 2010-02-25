class Utility
{
  static void removeEmptyParams(def params)
  {
    def nullMap = params?.findAll { entry ->(entry.value == "" || entry.value == "null")}
    nullMap?.each {params?.remove(it.key)}
   
  }
  static Date buildDate(String )
}