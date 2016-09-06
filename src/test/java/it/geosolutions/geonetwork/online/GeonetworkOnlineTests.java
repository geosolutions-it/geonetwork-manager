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

import it.geosolutions.geonetwork.util.GNVersion;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.geotools.test.OnlineTestSupport;

/**
* 
* Utility class to enrich the {@link GeonetworkTest} base test class with the GeoTools OnlineTestCase behaviour.
* see http://docs.geotools.org/latest/developer/conventions/test/online.html for more info   
* 
* @author DamianoG (damiano.giampaoli at geo-solutions.it)
*/
public class GeonetworkOnlineTests extends OnlineTestSupport{

    private final static Logger LOGGER = Logger.getLogger(GeonetworkOnlineTests.class);
    
    protected GNVersion gnv;
    protected String gnServiceURL;
    protected String gnUsername;
    protected String gnPassword;
    
    @Override
    protected String getFixtureId() {
        return "geonetwork-manager.params";
    }
    
    @Override
    protected void connect() {
        int gnVersion;
        String gnVersionAsString = (String)getFixture().get("version");
        try {
            gnVersion = Integer.parseInt(gnVersionAsString);
            gnv = GNVersion.get(gnVersion);
            if(gnv == null){
                throw new IllegalArgumentException("Invalid value for the Geonetwork version. The value provided in the fixture file is: '" + gnVersionAsString + "'");
            }
        }
        catch(Exception e){
            throw new IllegalArgumentException("Invalid value for the Geonetwork version. The value provided in the fixture file is: '" + gnVersionAsString + "'");
        }
        gnServiceURL = (String)getFixture().get("url");
        gnUsername = (String)getFixture().get("username");
        gnPassword = (String)getFixture().get("password");
        
        LOGGER.info("Going to run Online tests against Geonetwork " + gnv);
    }
    
    @Override
    protected Properties createExampleFixture() {
        Properties sampleFixture = new Properties();
        sampleFixture.put("version", "26|28|210|3");
        sampleFixture.put("url", "http://localhost:8080/geonetwork");
        sampleFixture.put("username", "admin");
        sampleFixture.put("password", "admin");
        return sampleFixture;
    }
}
