package chipper

class QueryController
{
  def queryService

  def index()
  {
    [tableMetadata: queryService.tableMetadata]
  }
}
