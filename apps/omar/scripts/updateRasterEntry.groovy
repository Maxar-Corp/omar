import org.ossim.omar.raster.RasterEntry

def count = RasterEntry.count()

def max = 1000
def offset = 0

def start = System.currentTimeMillis()

while ( offset < count )
{
    println offset
    
    def x = {
        maxResults(max)
        firstResult(offset)
    }
    
    def results = RasterEntry.createCriteria().list(x)
    
    RasterEntry.withTransaction {
        for ( def record in results ) 
        {
            def azimuthAngle = record.azimuthAngle
            
            if ( record.azimuthAngle )
            {
                record.azimuthAngle = (record.azimuthAngle + 90) % 360d
                record.save()
            }                
        }            
    }
    
    offset += max   
}

def stop = System.currentTimeMillis()

println "elapsed: ${stop-start}ms"
