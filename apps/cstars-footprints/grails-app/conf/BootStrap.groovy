import geoscript.workspace.Directory
import cstars.footprints.ImageCollection
import cstars.footprints.ImageFootprint
import cstars.footprints.Sensor

class BootStrap
{
  def init = { servletContext ->
    def baseName = "./ShapeFiles/Planned"

    def dir = new Directory(baseName as File)

    dir.layers.each { layerName ->
      def layer = dir[layerName]
      def x = layer.name.split("_") 

      def timeStamp = Date.parse("yyyyMMdd'T'HHmmss", x[0])
      def sensorName = x[1]

      def sensor = Sensor.findByName(sensorName)

      if ( !sensor )
      {
        Sensor.withTransaction {
          sensor = new Sensor(name: sensorName).save()
        }
      }

      def imageCollection = ImageCollection.findByTimeStampAndSensor(timeStamp, sensor)

      if ( !imageCollection )
      {
        ImageCollection.withTransaction {
          imageCollection = new ImageCollection(timeStamp: timeStamp, sensor: sensor)

          layer.features.each { feature ->
            def imageFootprint = new ImageFootprint(groundGeom: feature.attributes['the_geom'].g)

            imageCollection.addToFootprints(imageFootprint)
          }

          if ( !imageCollection.save() )
          {
            imageCollection.errors.each { println it }
          }
        }
      }
    }
  }

  def destroy = {
  }
}
