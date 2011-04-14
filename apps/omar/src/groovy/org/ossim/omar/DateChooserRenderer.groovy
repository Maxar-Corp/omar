package org.ossim.omar


import groovy.xml.MarkupBuilder
import java.text.SimpleDateFormat
import java.text.DateFormat
import de.andreasschmitt.richui.taglib.Resource
import de.andreasschmitt.richui.taglib.renderer.RenderException
import de.andreasschmitt.richui.taglib.renderer.RenderUtils

/**
 * Created by IntelliJ IDEA.
 * User: sbortman
 * Date: 4/12/11
 * Time: 8:57 PM
 * To change this template use File | Settings | File Templates.
 */
class DateChooserRenderer extends de.andreasschmitt.richui.taglib.renderer.DateChooserRenderer
{
  protected void renderTagContent(Map attrs, Closure body, MarkupBuilder builder) throws RenderException
  {
    String id = attrs.containerId ?: "c" + RenderUtils.getUniqueId()
    String inputId = "i" + RenderUtils.getUniqueId()

    if ( !attrs.id )
    {
      attrs.id = attrs.name
    }

    if ( attrs?.inputId )
    {
      inputId = attrs.inputId
    }

    if ( !attrs?.'class' )
    {
      attrs.'class' = ""
    }

    if ( !attrs?.style )
    {
      attrs.style = ""
    }

    if ( !attrs.timezone )
    {
      attrs.timezone = TimeZone.getDefault()
    }

    String formattedValue = ""
    String day = ""
    String month = ""
    String year = ""
    String hour = "00"
    String minute = "00"

    if ( attrs?.value )
    {
      try
      {
        DateFormat fmt = new SimpleDateFormat(attrs.format)
        fmt.setTimeZone(attrs.timezone)
        formattedValue = fmt.format(attrs.value)

        Calendar cal = new GregorianCalendar(attrs.timezone)
        cal.setTime(attrs.value)
        day = Integer.toString(cal.get(Calendar.DAY_OF_MONTH))
        month = Integer.toString(cal.get(Calendar.MONTH) + 1)
        year = Integer.toString(cal.get(Calendar.YEAR))

        hour = Integer.toString(cal.get(Calendar.HOUR_OF_DAY))
        if ( hour == "0" )
        {
          hour = "00"
        }

        minute = Integer.toString(cal.get(Calendar.MINUTE))
        if ( minute == "0" )
        {
          minute = "00"
        }
      }
      catch (Exception e)
      {
        log.error("Error formatting date", e)
      }
    }

    //Default HTML attributes
    Map htmlAttributes = ["class": "${attrs?.'class'}", style: "${attrs?.style}", type: "text", name: "${inputId}", id: "${inputId}", value: "${formattedValue}"]

    //Add additional attributes
    attrs.each { key, value ->
      if ( key.startsWith("html:") )
      {
        htmlAttributes[key.replace("html:", "")] = value
      }
    }

    if ( attrs.render == null || attrs.render != "script" )
    {
      builder.input(htmlAttributes)
      builder.div("id": id, "class": "datechooser yui-skin-sam", "")
      builder.input(type: "hidden", name: "${attrs?.name}", id: "${attrs?.id}", value: "date.struct")

      if ( attrs?.time )
      {
        builder.input("class": "${attrs?.hourClass}", style: "${attrs?.hourStyle}", type: "text", name: "${attrs?.name}_hour", id: "${attrs?.id}_hour", value: hour)
        builder.yield(":", false)
        builder.input("class": "${attrs?.minuteClass}", style: "${attrs?.minuteStyle}", type: "text", name: "${attrs?.name}_minute", id: "${attrs?.id}_minute", value: minute)

      }

      builder.input(type: "hidden", name: "${attrs?.name}_day", id: "${attrs?.id}_day", value: day)
      builder.input(type: "hidden", name: "${attrs?.name}_month", id: "${attrs?.id}_month", value: month)
      builder.input(type: "hidden", name: "${attrs?.name}_year", id: "${attrs?.id}_year", value: year)
    }

    if ( attrs.render == null || attrs.render != "input" )
    {
      builder.script(type: "text/javascript") {
        builder.yield("	var dateChooser = new DateChooser();\n", false)
        builder.yield("	dateChooser.setDisplayContainer(\"$id\");\n", false)
        builder.yield("	dateChooser.setInputId(\"${inputId}\");\n", false)
        builder.yield("	dateChooser.setStructId(\"${attrs?.id}\");\n", false)
        builder.yield("	dateChooser.setFormat(\"${attrs?.format}\");\n", false)
        if ( attrs?.locale )
        {
          builder.yield("	dateChooser.setLocale(\"${attrs?.locale}\");\n", false)
        }

        // Add callbackHandler for focus, blur and change
        if ( attrs?.onFocus )
        {
          builder.yield("  dateChooser.setFocusCallback(\"${attrs?.onFocus}\");\n", false)
        }
        if ( attrs?.onBlur )
        {
          builder.yield("  dateChooser.setBlurCallback(\"${attrs?.onBlur}\");\n", false)
        }
        if ( attrs?.onChange )
        {
          builder.yield("  dateChooser.setChangeCallback(\"${attrs?.onChange}\");\n", false)
        }

        if ( attrs?.navigator )
        {
          builder.yield("	dateChooser.setNavigator(${attrs?.navigator});\n", false)
        }

        if ( attrs?.firstDayOfWeek )
        {
          Map days = [su: 0, mo: 1, tu: 2, we: 3, th: 4, fr: 5, sa: 6]

          if ( days.containsKey(attrs.firstDayOfWeek.toLowerCase()) )
          {
            String dayOfWeek = days[attrs.firstDayOfWeek.toLowerCase()]
            builder.yield("	dateChooser.setFirstDayOfWeek(\"${dayOfWeek}\");\n", false)
          }
        }
        builder.yield("	dateChooser.init();\n", false)
      }
    }
  }

}
