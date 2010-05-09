package org.ossim.omar
/**
 * Created by IntelliJ IDEA.
 * User: sbortman
 * Date: Dec 18, 2009
 * Time: 7:30:41 PM
 * To change this template use File | Settings | File Templates.
 */
class FileFilterEvent extends EventObject
{
  File file
  String data

  public FileFilterEvent(Object source, File file)
  {
    super(source)
    this.file = file
  }

  public FileFilterEvent(Object source, File file, String data)
  {
    this(source, file)
    this.data = data
  }

  def String toString()
  {
    [file: file, data: data] as String
  }

}
