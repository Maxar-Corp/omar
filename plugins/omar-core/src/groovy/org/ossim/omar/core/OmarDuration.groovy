package org.ossim.omar.core
class OmarDuration
{
  def days   = 0;
  def months = 0;
  def years  = 0;
  def hours  = 0;
  def minutes= 0;
  def seconds= 0;
  def durationSign   = 1;
  public OmarDuration()
  {
  }
  void clearFields()
  {
    days = 0;
    months = 0;
    years  = 0;
    hours  = 0;
    minutes = 0;
    seconds = 0;
    durationSign = 1;
  }
  void addTo(Calendar calendar)
  {
    calendar.add(Calendar.YEAR, years*durationSign)
    calendar.add(Calendar.MONTH, months*durationSign)
    calendar.add(Calendar.DAY_OF_MONTH, days*durationSign)
    calendar.add(Calendar.HOUR, hours*durationSign)
    calendar.add(Calendar.MINUTE, minutes*durationSign)
    calendar.add(Calendar.SECOND, seconds*durationSign)
  }
}