import org.ossim.omar.raster.RasterFile

def c = RasterFile.createCriteria()

def results = c.scroll() {
  projections {
    property( 'name' )
  }
}

results?.first()

def outputFile = "raster_file_list.txt" as File

outputFile.withPrintWriter { out ->
  while ( results.next() )
  {
    def filename = results.get( 0 )

    out << "${filename}\n"
  }

}
results?.close()
