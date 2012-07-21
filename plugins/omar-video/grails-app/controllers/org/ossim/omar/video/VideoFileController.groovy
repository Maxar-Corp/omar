package org.ossim.omar.video

import grails.converters.XML
import grails.converters.JSON
import org.ossim.omar.video.VideoFile
import org.ossim.omar.video.VideoDataSet

class VideoFileController
{

  def index( )
  { redirect( action: 'list', params: params ) }

  // the delete, save and update actions only accept POST requests
  def static allowedMethods = [delete: 'POST', save: 'POST', update: 'POST']

  def list( )
  {
    if ( !params.max )
      params.max = 10

    def videoFileList = null

    if ( params.videoDataSetId )
    {
      def videoDataSet = VideoDataSet.get( params.videoDataSetId )

      videoFileList = VideoFile.createCriteria().list( params ) {
        eq( "videoDataSet", videoDataSet )
      }
    }
    else
    {
      videoFileList = VideoFile.createCriteria().list( params ) {}
    }

    withFormat {
      html { [videoFileList: videoFileList] }
      xml { render videoFileList as XML }
      json { render videoFileList as JSON }
    }
  }

  def show( )
  {
    def videoFile = VideoFile.get( params.id )

    if ( !videoFile )
    {
      flash.message = "VideoFile not found with id ${params.id}"
      redirect( action: 'list' )
    }
    else
    {
      withFormat {
        html { [videoFile: videoFile] }
        xml { render videoFile as XML }
        xml { render videoFile as JSON }
      }
    }
  }

  def delete( )
  {
    def videoFile = VideoFile.get( params.id )
    if ( videoFile )
    {
      videoFile.delete()
      flash.message = "VideoFile ${params.id} deleted"
      redirect( action: 'list' )
    }
    else
    {
      flash.message = "VideoFile not found with id ${params.id}"
      redirect( action: 'list' )
    }
  }

  def edit( )
  {
    def videoFile = VideoFile.get( params.id )

    if ( !videoFile )
    {
      flash.message = "VideoFile not found with id ${params.id}"
      redirect( action: 'list' )
    }
    else
    {
      return [videoFile: videoFile]
    }
  }

  def update( )
  {
    def videoFile = VideoFile.get( params.id )
    if ( videoFile )
    {
      videoFile.properties = params
      if ( !videoFile.hasErrors() && videoFile.save() )
      {
        flash.message = "VideoFile ${params.id} updated"
        redirect( action: 'show', id: videoFile.id )
      }
      else
      {
        render( view: 'edit', model: [videoFile: videoFile] )
      }
    }
    else
    {
      flash.message = "VideoFile not found with id ${params.id}"
      redirect( action: 'edit', id: params.id )
    }
  }

  def create( )
  {
    def videoFile = new VideoFile()
    videoFile.properties = params
    return ['videoFile': videoFile]
  }

  def save( )
  {
    def videoFile = new VideoFile( params )
    if ( !videoFile.hasErrors() && videoFile.save() )
    {
      flash.message = "VideoFile ${videoFile.id} created"
      redirect( action: 'show', id: videoFile.id )
    }
    else
    {
      render( view: 'create', model: [videoFile: videoFile] )
    }
  }
}
