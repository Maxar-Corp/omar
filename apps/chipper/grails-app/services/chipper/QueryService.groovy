package chipper

import geoscript.workspace.WFS

class QueryService
{
  static final EQUAL = [name: "=", label: "is", cardinality: "ONE"]
  static final NOT_EQUAL = [name: "<>", label: "is not", cardinality: "ONE"]
  static final LIKE = [name: "LIKE", label: "like", cardinality: "ONE"]
  static final LESS_THAN = [name: "<", label: "less than", cardinality: "ONE"]
  static final GREATER_THAN = [name: ">", label: "greater than", cardinality: "ONE"]
  static final INTERSECTS = [name: "&&", label: "intersects", cardinality: "ONE"]


  def getTableMetadata()
  {
    def serviceAddress = "http://omar.ngaiost.org/omar/wfs"
    def wfs = new WFS("${serviceAddress}?service=WFS&version=1.0.0&request=GetCapabilities")

    def featureTypes = wfs.layers*.name

    def tables = featureTypes.collect { featureType -> [
        name: featureType,
        columns: wfs[featureType].schema.fields.collect { field -> [
            name: field.name,
            label: field.name.split('_')*.capitalize().join(' '),
            type: field.typ
        ] },
        fks: []
    ] }

    //println tables

    def typesNames = tables.columns.groupBy { it.type }.keySet().flatten().unique().sort()

    def types = typesNames.collect {
      def type
      switch ( it )
      {
      case 'MultiPolygonPropertyType':
      case 'PolygonPropertyType':
        type = [name: it, editor: 'TEXT', operators: [INTERSECTS]]
        break
      case 'boolean':
        type = [name: it, editor: 'SELECT', operators: [EQUAL, NOT_EQUAL]]
        break
      case 'string':
        type = [name: it, editor: 'TEXT', operators: [EQUAL, NOT_EQUAL, LIKE]]
        break
      case 'dateTime':
        type = [name: it, editor: 'DATE', operators: [EQUAL, NOT_EQUAL]]
        break
      case 'decimal':
      case 'double':
      case 'int':
      case 'long':
        type = [name: it, editor: 'TEXT', operators: [EQUAL, NOT_EQUAL, LESS_THAN, GREATER_THAN]]
        break
      default:
        type = [name: it, editor: 'TEXT', operators: [EQUAL, NOT_EQUAL]]
      }
      return type
    }

    //types.each { println it }


    wfs?.close()

    [meta: [tables: tables, types: types]]
  }
}

