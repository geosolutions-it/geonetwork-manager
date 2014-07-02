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
package it.geosolutions.geonetwork.op;

import it.geosolutions.geonetwork.exception.GNLibException;
import it.geosolutions.geonetwork.exception.GNServerException;
import it.geosolutions.geonetwork.util.HTTPUtils;
import java.io.IOException;
import java.io.StringReader;

import java.net.MalformedURLException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.log4j.Logger;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * Get the internal id for a given Metadata record
 */
public class GNMetadataGetId {
        
    private final static Logger LOGGER = Logger.getLogger(GNMetadataGetId.class);
    
	public static final Namespace NS_GEONET = Namespace.getNamespace("geonet", "http://www.fao.org/geonetwork");    

	public static long get(HTTPUtils connection, String gnServiceURL, String uuid) throws GNLibException, GNServerException {
        try {
            if(LOGGER.isDebugEnabled())
                LOGGER.debug("Retrieve metadata: "+uuid);

            String serviceURL = gnServiceURL + "/srv/eng/q?_uuid="+uuid;
            
            connection.setIgnoreResponseContentOnSuccess(false);
            String response = connection.get(serviceURL);
            if(LOGGER.isDebugEnabled())
                LOGGER.debug("Response is " + response.length() + " chars long");
            
            if(connection.getLastHttpStatus() != HttpStatus.SC_OK)
                throw new GNServerException("Error retrieving metadata in GeoNetwork");

            long id = parseId(response);
                        
            if(LOGGER.isDebugEnabled())
                LOGGER.debug("Metadata " + uuid + " has id " + String.valueOf(id)); 
                        
            return id;
        } catch (MalformedURLException ex) {
            throw new GNLibException("Bad URL", ex);
        }
    }
    
    private static long parseId(String s) throws GNLibException {
        try {
            SAXBuilder builder = new SAXBuilder();
            Element root = builder.build(new StringReader(s)).detachRootElement();
            
            /*
             * <response> 
             *      <summary>
             *      ...
             *      </summary>
             *      <metadata>
             *          <id>55</id>
             *          ...
             *      </metadata>
             *  </response> 
             */
            
            Element response = root.getChild("metadata");
            if(response == null) {
                LOGGER.error("Could not find metadata child");
                XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
                outputter.output(root, System.out);
                
                throw new GNLibException("Could not find metadata child");
            }
                        
            Element geonetInfo = response.getChild("info", NS_GEONET);
            if(geonetInfo == null) {
                LOGGER.error("Could not find geonet:info child");
                XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
                outputter.output(root, System.out);
                
                throw new GNLibException("Could not find geonet:info child");
            }
            String id = geonetInfo.getChildText("id");
            return Long.parseLong(id);
                
        } catch (JDOMException ex) {
            LOGGER.error("Error parsing GN response: " + s);
            throw new GNLibException("Error parsing GN response: " + ex.getMessage(), ex);
        } catch (IOException ex) {
            throw new GNLibException("Error while outputting", ex);
        }
    }
}
