/*
 *  GeoNetwork-Manager - Simple Manager Library for GeoNetwork
 *
 *  Copyright (C) 2007,2016 GeoSolutions S.A.S.
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
package it.geosolutions.geonetwork.online;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.Namespace;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;

import it.geosolutions.geonetwork.GN2Client;
import it.geosolutions.geonetwork.GN3Client;
import it.geosolutions.geonetwork.GNClient;
import it.geosolutions.geonetwork.exception.GNLibException;
import it.geosolutions.geonetwork.exception.GNServerException;
import it.geosolutions.geonetwork.util.GNInsertConfiguration;
import it.geosolutions.geonetwork.util.GNSearchRequest;
import it.geosolutions.geonetwork.util.GNSearchResponse;

/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
public abstract class GeonetworkTest extends GeonetworkOnlineTests{
    private final static Logger LOGGER = Logger.getLogger(GeonetworkTest.class);

    @Rule
    public TestName _testName = new TestName();
    
    public GeonetworkTest() {
    }

    @Before 
    public void setUp() throws Exception {
        LOGGER.info("====================> " + _testName.getMethodName());
        removeAllMetadata();
    }

    protected GNClient createClientAndCheckConnection() {
        GNClient client = null;
        switch (gnVersion) {
        case 2:
            client = new GN2Client(gnServiceURL, gnUsername, gnPassword);
            break;
        case 3:
            client = new GN3Client(gnServiceURL, gnUsername, gnPassword);
            break;
        default:
            client = null;
            break;
        }
        boolean logged = (client == null)? false : client.ping();
        assertTrue("Error pinging GN", logged);
        return client;
    }
    
    /**
     * Utility method to remove all metadata in GN.
     */
    protected void removeAllMetadata() throws GNLibException, GNServerException {
        GNClient client = createClientAndCheckConnection();

        GNSearchRequest searchRequest = new GNSearchRequest(); // empty fiter, all metadaat will be returned
        GNSearchResponse searchResponse = client.search(searchRequest);

        LOGGER.info("Found " + searchResponse.getCount() + " existing metadata");
        for (GNSearchResponse.GNMetadata metadata : searchResponse) {
            LOGGER.info("Removing md ID:" + metadata.getId() + " UUID:" + metadata.getUUID());
            Long id = metadata.getId();
            client.deleteMetadata(id);
        }

        // check that the catalog is really empty
        asyncSearchAssertEquals(0, client, searchRequest);
//        searchResponse = client.search(searchRequest);
//        assertEquals(0,searchResponse.getCount());
        LOGGER.info("All metadata removed successfully");
    }

    protected void asyncSearchAssertEquals(int expected, GNClient client, GNSearchRequest searchRequest) throws GNLibException, GNServerException {
        asyncSearchAssertEquals(expected, client, searchRequest, null);
    }

    protected void asyncSearchAssertEquals(int expected, GNClient client, File searchRequest) throws GNLibException, GNServerException {
        asyncSearchAssertEquals(expected, client, null, searchRequest);
    }

    private void asyncSearchAssertEquals(int expected, GNClient client, GNSearchRequest searchRequest, File file) throws GNLibException, GNServerException {

        final int MAXLOOP = 10;

        GNSearchResponse searchResponse = null;

        for (int i = 0; i < MAXLOOP; i++) {

            searchResponse = 
                    searchRequest != null ? client.search(searchRequest) :
                    client.search(file);
            if( searchResponse.getCount() == expected) {
                if(i > 0)
                    LOGGER.info("Search count passed after " + i + " loops");

                return;
            }

            LOGGER.info("search failed ("+searchResponse.getCount()+"!="+expected+"), retrying...");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
            }
        }
        fail("Expected value " + expected + " not confirmed after " + MAXLOOP + " tries. Found " + searchResponse.getCount());
    }
    
    protected GNInsertConfiguration createDefaultInsertConfiguration() {
        GNInsertConfiguration cfg = new GNInsertConfiguration();
        
        cfg.setCategory("datasets");
        cfg.setGroup("1"); // group 1 is usually "all"
        cfg.setStyleSheet("_none_");
        cfg.setValidate(Boolean.FALSE);
        return cfg;
    }
    
    protected File loadFile(String name) {
        try {
            URL url = this.getClass().getClassLoader().getResource(name);
            if(url == null)
                throw new IllegalArgumentException("Cant get file '"+name+"'");
            File file = new File(url.toURI());
            return file;
        } catch (URISyntaxException e) {
            LOGGER.error("Can't load file " + name + ": " + e.getMessage(), e);
            return null;
        }    
    }

    protected Element getTitleElement(Element metadata) {
        //    xmlns:gmd="http://www.isotc211.org/2005/gmd"
        //    xmlns:gco="http://www.isotc211.org/2005/gco"        
        //            
        //    <gmd:identificationInfo>
        //      <gmd:MD_DataIdentification>
        //         <gmd:citation>
        //            <gmd:CI_Citation>
        //               <gmd:title>
        //                  <gco:CharacterString>TEST GeoBatch Action: GeoNetwork</gco:CharacterString>
        final Namespace NS_GMD = Namespace.getNamespace("gmd","http://www.isotc211.org/2005/gmd");
        final Namespace NS_GCO = Namespace.getNamespace("gco","http://www.isotc211.org/2005/gco");

        Element idInfo = metadata.getChild("identificationInfo", NS_GMD);        
        Element dataId = idInfo.getChild("MD_DataIdentification", NS_GMD);
        Element cit    = dataId.getChild("citation", NS_GMD);
        Element cicit  = cit.getChild("CI_Citation", NS_GMD);
        Element title  = cicit.getChild("title", NS_GMD);
        Element chstr  = title.getChild("CharacterString", NS_GCO);
        
        return chstr;
    }
}
