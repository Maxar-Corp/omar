package org.ossim.omar

import grails.test.*
import geoscript.geom.Geometry
class GeoscriptTests extends GrailsUnitTestCase {
    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testSomething() {
     def geom =  Geometry.fromWKT('LINESTRING(3 4,10 50,20 25)')

      println geom;
    }
}
