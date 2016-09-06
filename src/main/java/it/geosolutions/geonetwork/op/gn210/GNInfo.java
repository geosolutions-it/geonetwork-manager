/*
 *  GeoNetwork-Manager - Simple Manager Library for GeoNetwork
 *
 *  Copyright (C) 2007,2014 GeoSolutions S.A.S.
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
package it.geosolutions.geonetwork.op.gn210;

import it.geosolutions.geonetwork.util.HTTPUtils;
import java.net.MalformedURLException;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.log4j.Logger;


/**
 * 
 * @author ETj (etj at geo-solutions.it)
 */
public class GNInfo {
        
    private final static Logger LOGGER = Logger.getLogger(GNInfo.class);

    // needs authentication
    public static boolean ping(HTTPUtils connection, String serviceURL) {
        if(LOGGER.isDebugEnabled())
            LOGGER.debug("PING");

        connection.setIgnoreResponseContentOnSuccess(true);
        String url = serviceURL + "/srv/eng/util.ping";

        try {
            connection.get(url);
        } catch (MalformedURLException ex) {
            LOGGER.error(ex.getMessage());
            return false;
        }

        if(connection.getLastHttpStatus() != HttpStatus.SC_OK) {
            if(LOGGER.isInfoEnabled())
                LOGGER.info("PING failed");
            return false;
        }

        return true;
    }
}
