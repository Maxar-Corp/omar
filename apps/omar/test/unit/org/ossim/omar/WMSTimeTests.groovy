package org.ossim.omar

import grails.test.*

import org.ossim.omar.core.ISO8601DateParser

class WMSTimeTests extends GrailsUnitTestCase {
    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testSomething() {
      def intervals = ISO8601DateParser.parseWMSIntervals("P1Y/19990101");

      assertEquals("Should have 1 intervals P1Y/19990101", 1, intervals.size())
      intervals = ISO8601DateParser.parseWMSIntervals("P1Y/19990101,20090101/P1Y");
      assertEquals("Should have 2 intervals P1Y/19990101,20090101/P1Y", 2, intervals.size())
      intervals = ISO8601DateParser.parseWMSIntervals("1999");
      assertEquals("Should have 1 interval 1999", 1, intervals.size())
      intervals = ISO8601DateParser.parseWMSIntervals("1999/P1Y,1977/P333D");
      assertEquals("Should have 2 interval 1999/P1Y,1977/P333D", 2, intervals.size())
      intervals = ISO8601DateParser.parseWMSIntervals("1999/1999");
      assertEquals("Intervals should be equal", intervals[0].getStart(), intervals[0].getEnd())
      intervals = ISO8601DateParser.parseWMSIntervals("1999/P1Y,P1Y/2000");
      assertEquals("Intervals 1999/P1Y,P1Y/2000 should be equal", intervals[0].getStart(), intervals[1].getStart())
      assertEquals("Intervals 1999/P1Y,P1Y/2000 should be equal", intervals[0].getEnd(), intervals[1].getEnd())
    }
}
