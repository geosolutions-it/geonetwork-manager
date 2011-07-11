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

import it.geosolutions.geonetwork.util.GNInsertConfiguration;
import it.geosolutions.geonetwork.util.GNPrivConfiguration;
import org.apache.log4j.Logger;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import junit.framework.TestCase;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class GeonetworkInsertTest extends TestCase {
    private final static Logger LOGGER = Logger.getLogger(GeonetworkInsertTest.class);

    boolean runIntegrationTest = false;

    private static final String gnServiceURL = "http://localhost:8080/geonetwork";
    private static final String gnUsername = "admin";
    private static final String gnPassword = "admin";
    
    public GeonetworkInsertTest() {
    }

//    @Before 
    public void setUp() throws Exception {
        super.setUp();
        LOGGER.info("====================> " + getName());
    }
    
    @Test
    public void testInsertPureMetadata() throws Exception {
        if( ! runIntegrationTest ) return;
        
        GNInsertConfiguration cfg = createConfiguration();

        GNPrivConfiguration pcfg = new GNPrivConfiguration();
        pcfg.addPrivileges(0, "012345");
        pcfg.addPrivileges(1, "012345");
        pcfg.addPrivileges(2, "012345");
        pcfg.addPrivileges(3, "012345");
        pcfg.addPrivileges(4, "012345");
        
        File file = loadFile("metadata.xml");
        assertNotNull(file);

        GNClient client = new GNClient(gnServiceURL);
        boolean logged = client.login(gnUsername, gnPassword);
        assertTrue("Could not log in", logged);

        long id = client.insertMetadata(cfg, file);

        client.setPrivileges(id, pcfg);
    }

    @Test
    public void testInsertRequest() throws Exception {
        if( ! runIntegrationTest ) return;
        
        GNInsertConfiguration cfg = createConfiguration();
        
        File file = loadFile("request.xml");
        assertNotNull(file);
        
        GNClient client = new GNClient(gnServiceURL);
        boolean logged = client.login(gnUsername, gnPassword);
        assertTrue("Could not log in", logged);

        long id = client.insertRequest(file);
    }

    protected GNInsertConfiguration createConfiguration() {
        GNInsertConfiguration cfg = new GNInsertConfiguration();
        
        cfg.setCategory("datasets");
        cfg.setGroup("1"); // group 1 is usually "all"
        cfg.setStyleSheet("_none_");
        cfg.setValidate(Boolean.FALSE);
        return cfg;
    }
    
    private File loadFile(String name) {        
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
    
}
