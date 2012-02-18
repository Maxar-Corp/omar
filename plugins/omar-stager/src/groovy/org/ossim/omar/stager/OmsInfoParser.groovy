package org.ossim.omar.stager

import groovy.util.slurpersupport.GPathResult
import org.ossim.omar.core.Repository

/**
 * Created by IntelliJ IDEA.
 * User: sbortman
 * Date: May 16, 2009
 * Time: 7:36:25 PM
 * To change this template use File | Settings | File Templates.
 */

public interface OmsInfoParser
{
  public def processDataSets(GPathResult oms, Repository repository);
}

