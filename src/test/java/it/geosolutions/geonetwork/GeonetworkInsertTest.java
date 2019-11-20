/*
 *  GeoNetwork-Manager - Simple Manager Library for GeoNetwork
 *
 *  Copyright (C) 2007,2011 GeoSolutions S.A.S.
 *  http://www.geo-solutions.it
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package it.geosolutions.geonetwork;

import java.util.EnumSet;
import it.geosolutions.geonetwork.util.GNInsertConfiguration;
import it.geosolutions.geonetwork.util.GNPriv;
import it.geosolutions.geonetwork.util.GNPrivConfiguration;
import org.apache.log4j.Logger;
import java.io.File;
import org.jdom.Element;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ETj (etj at geo-solutions.it)
 */

public class GeonetworkInsertTest extends GeonetworkTest {
    private final static Logger LOGGER = Logger.getLogger(GeonetworkInsertTest.class);

    public GeonetworkInsertTest() {
    }

//   TODO Insert test is disabled need to updated to support GN3

//    @Test
//    public void testInsertPureMetadata() throws Exception {
//        if( ! runIntegrationTest() ) return;
//
//        GNInsertConfiguration cfg = createDefaultInsertConfiguration();
//
//        GNPrivConfiguration pcfg = new GNPrivConfiguration();
//
//        pcfg.addPrivileges(GNPrivConfiguration.GROUP_GUEST,    EnumSet.of(GNPriv.FEATURED));
//        pcfg.addPrivileges(GNPrivConfiguration.GROUP_INTRANET, EnumSet.of(GNPriv.DYNAMIC, GNPriv.FEATURED));
//        pcfg.addPrivileges(GNPrivConfiguration.GROUP_ALL,      EnumSet.of(GNPriv.VIEW, GNPriv.DYNAMIC, GNPriv.FEATURED));
//        pcfg.addPrivileges(2, EnumSet.allOf(GNPriv.class));
//
//        File file = loadFile("metadata.xml");
//        assertNotNull(file);
//
//        GNClient client = createClientAndCheckConnection();
//        long id = client.insertMetadata(cfg, file);
//
//        client.setPrivileges(id, pcfg);
//
//        Element md = client.get(id);
//
//        client.deleteMetadata(id);
//    }
//
//    @Test
//    public void testInsertRequest() throws Exception {
//        if( ! runIntegrationTest() ) return;
//
//        File file = loadFile("request.xml");
//        assertNotNull(file);
//
//        GNClient client = createClientAndCheckConnection();
//
//        // insert
//        long id = client.insertRequest(file);
//        // get
//        Element md = client.get(id);
//        // delete
//        client.deleteMetadata(id);
//    }
//
//    @Test
//    public void testBadDelete() throws Exception {
//        if( ! runIntegrationTest() ) return;
//
//        GNClient client = createClientAndCheckConnection();
//        // delete
//        try {
//            client.deleteMetadata(-10L);
//            fail("Untrapped exception");
//        } catch (Exception e) {
//            LOGGER.info("Exception successfully trapped");
//        }
//    }
//
//    @Test
//    public void testBadGet() throws Exception {
//        if( ! runIntegrationTest() ) return;
//
//        GNClient client = createClientAndCheckConnection();
//        // delete
//        try {
//            client.get(-10L);
//            fail("Untrapped exception");
//        } catch (Exception e) {
//            LOGGER.info("Exception successfully trapped");
//        }
//    }

       
}
