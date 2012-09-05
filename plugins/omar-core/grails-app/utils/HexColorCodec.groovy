/**
 * Created with IntelliJ IDEA.
 * User: sbortman
 * Date: 9/5/12
 * Time: 9:52 AM
 * To change this template use File | Settings | File Templates.
 */
class HexColorCodec
{
  static encode = { color ->
    def r = Integer.toHexString( color?.red ).padLeft( 2, '0' )
    def g = Integer.toHexString( color?.green ).padLeft( 2, '0' )
    def b = Integer.toHexString( color?.blue ).padLeft( 2, '0' )
    "#${ r }${ g }${ b }"
  }
}
