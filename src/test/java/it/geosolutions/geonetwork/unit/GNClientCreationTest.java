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
package it.geosolutions.geonetwork.unit;

import org.junit.Test;

import it.geosolutions.geonetwork.GN2Client;
import it.geosolutions.geonetwork.GN3Client;
import it.geosolutions.geonetwork.GNClient;
import junit.framework.Assert;

/**
*
* @author DamianoG (damiano.giampaoli at geo-solutions.it)
*/
public class GNClientCreationTest extends Assert{

    @Test
    public void constructorTest(){
        GNClient gnClient = new GN2Client("http://testurl1.com");
        assertNotNull(gnClient.getConnection());
        gnClient = new GN3Client("http://testurl2.com");
        assertNotNull(gnClient.getConnection());
        gnClient = new GN2Client("http://testurl3.com","usr","pswd");
        assertNotNull(gnClient.getConnection());
        gnClient = new GN3Client("http://testurl4.com","usr","pswd");
        assertNotNull(gnClient.getConnection());
    }
}
