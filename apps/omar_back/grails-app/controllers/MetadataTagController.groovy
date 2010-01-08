class MetadataTagController
{

  def index = { redirect(action: list, params: params) }

  // the delete, save and update actions only accept POST requests
  def static allowedMethods = [delete: 'POST', save: 'POST', update: 'POST']

  def list = {
    if ( !params.max )
      params.max = 10

    def metadataTagList = null

    if ( params.rasterEntryId )
    {
      def rasterEntry = RasterEntry.get(params.rasterEntryId)

      metadataTagList = MetadataTag.createCriteria().list(params) {
        eq("rasterEntry", rasterEntry)
      }
    }
    else
    {
      metadataTagList = MetadataTag.createCriteria().list(params) {}
    }

    [metadataTagList: metadataTagList]
  }

  def show = {
    def metadataTag = MetadataTag.get(params.id)

    if ( !metadataTag )
    {
      flash.message = "MetadataTag not found with id ${params.id}"
      redirect(action: list)
    }
    else
    { return [metadataTag: metadataTag] }
  }

  def delete = {
    def metadataTag = MetadataTag.get(params.id)
    if ( metadataTag )
    {
      metadataTag.delete()
      flash.message = "MetadataTag ${params.id} deleted"
      redirect(action: list)
    }
    else
    {
      flash.message = "MetadataTag not found with id ${params.id}"
      redirect(action: list)
    }
  }

  def edit = {
    def metadataTag = MetadataTag.get(params.id)

    if ( !metadataTag )
    {
      flash.message = "MetadataTag not found with id ${params.id}"
      redirect(action: list)
    }
    else
    {
      return [metadataTag: metadataTag]
    }
  }

  def update = {
    def metadataTag = MetadataTag.get(params.id)
    if ( metadataTag )
    {
      metadataTag.properties = params
      if ( !metadataTag.hasErrors() && metadataTag.save() )
      {
        flash.message = "MetadataTag ${params.id} updated"
        redirect(action: show, id: metadataTag.id)
      }
      else
      {
        render(view: 'edit', model: [metadataTag: metadataTag])
      }
    }
    else
    {
      flash.message = "MetadataTag not found with id ${params.id}"
      redirect(action: edit, id: params.id)
    }
  }

  def create = {
    def metadataTag = new MetadataTag()
    metadataTag.properties = params
    return ['metadataTag': metadataTag]
  }

  def save = {
    def metadataTag = new MetadataTag(params)
    if ( !metadataTag.hasErrors() && metadataTag.save() )
    {
      flash.message = "MetadataTag ${metadataTag.id} created"
      redirect(action: show, id: metadataTag.id)
    }
    else
    {
      render(view: 'create', model: [metadataTag: metadataTag])
    }
  }
}
