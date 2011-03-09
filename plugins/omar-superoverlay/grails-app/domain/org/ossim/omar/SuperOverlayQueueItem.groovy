package org.ossim.omar

import java.util.Date;

class SuperOverlayQueueItem {

  String indexId
  Date   dateCreated
  Date   startTime = null
  Date   endTime   = null
  String action = "create"
  double priority = 0
  String status = "ready"
  String message = null
  String baseDir
  String entryId = "0"
  String kwl
  
  static constraints = {
    indexId(nullable:false)
    dateCreated(nullable:true)
    startTime(nullable:true)
    endTime(nullable:true)
    action(nullable:true)
    priority(nullable:false)
    status(nullable:false)
    message(nullable:true)
    baseDir(nullable:true)
    entryId(nullable:true)
    kwl(nullable:true)
  }
  static mapping = {
    columns {
      message    type: 'text'
      kwl        type: 'text'
	  baseDir    type: 'text'
	  outputFile type: 'text'
    }
  }
  static def itemExists(def params) {
	  def result = false
	  SuperOverlayQueueItem.withTransaction(){
		  result = SuperOverlayQueueItem.findByIndexId(params.indexId) != null
	  }
	
    return result
  }
  static addItem(def params, def uniqueFlag=true)
  {
    def item = new SuperOverlayQueueItem(params);
    if(uniqueFlag)
    {
	    SuperOverlayQueueItem.withTransaction
	    {
			if(!SuperOverlayQueueItem.findByIndexId(params.indexId))
			{
				item.save();
			}
	    }
    }
    else
    {
      SuperOverlayQueueItem.withTransaction
      {
         item.save();
      }
    }
  }
}
