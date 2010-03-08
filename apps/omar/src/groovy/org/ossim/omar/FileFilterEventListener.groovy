package org.ossim.omar
/**
 * Created by IntelliJ IDEA.
 * User: sbortman
 * Date: Dec 18, 2009
 * Time: 7:29:20 PM
 * To change this template use File | Settings | File Templates.
 */
interface FileFilterEventListener extends EventListener
{
  public void filterFile(FileFilterEvent fileEventObject)

  public void acceptFile(FileFilterEvent fileEventObject)

  public void rejectFile(FileFilterEvent fileEventObject)

  public void processSuccess(FileFilterEvent fileEventObject)

  public void processFailure(FileFilterEvent fileEventObject)
  
  public void processData(FileFilterEvent fileEventObject)

}
