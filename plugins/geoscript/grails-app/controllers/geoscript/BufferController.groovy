package geoscript

//import geoscript.geom.Geometry

class BufferController
{

  def index( )
  {
    if ( params.geom )
    {
      def g = Geometry.fromWKT( params.geom )
      def d = params.distance as Double
      def wkt = g.buffer( d ).wkt
      render( wkt )
    }
  }

}

