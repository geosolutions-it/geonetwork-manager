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
import java.util.List;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.log4j.Logger;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * Get the version number for a given Metadata.
 * <br/><br/>
 * GN does not provide the version seq number in an XML format, so this 
 * implementation it's quite a hack: it calls metadata.edit service in debug mode, 
 * and parses the version number from there. <br/>
 * 2 drawbacks here: 
 * - debug mode should be enabled, 
 * - the resulting document is about 1MB
 * 
 * @author ETj (etj at geo-solutions.it)
 */
public class GNMetadataGetVersion {
        
    private final static Logger LOGGER = Logger.getLogger(GNMetadataGetVersion.class);
    
	public static final Namespace NS_GEONET = Namespace.getNamespace("geonet", "http://www.fao.org/geonetwork");    
	public static final Namespace NS_GMD = Namespace.getNamespace("gmd", "http://www.isotc211.org/2005/gmd");

    public static String get(HTTPUtils connection, String gnServiceURL, Long id) throws GNLibException, GNServerException {
        try {
            if(LOGGER.isDebugEnabled())
                LOGGER.debug("Retrieve metadata #"+id);

            String serviceURL = gnServiceURL + "/srv/eng/metadata.edit!?id="+id;
            
            connection.setIgnoreResponseContentOnSuccess(false);
            String response = connection.get(serviceURL);
            if(response != null) {
                if (LOGGER.isDebugEnabled())
                    LOGGER.debug("Response is " + response.length() + " chars long");

                if (connection.getLastHttpStatus() != HttpStatus.SC_OK)
                    throw new GNServerException("Error retrieving metadata in GeoNetwork");

                String version = parseVersion(response);

                if (LOGGER.isDebugEnabled())
                    LOGGER.debug("Metadata " + id + " has version " + version);
                return version;
            }
            else {
                if (LOGGER.isDebugEnabled())
                    LOGGER.debug("Metadata " + id + " is returning empty version ");
                return "";
            }
        } catch (MalformedURLException ex) {
            throw new GNLibException("Bad URL", ex);
        }
    }
    
    private static String parseVersion(String s) throws GNLibException {
        try {
            SAXBuilder builder = new SAXBuilder();
            Element root = builder.build(new StringReader(s)).detachRootElement();
            
            /*
             * <gmd:MD_Metadata 
             *      xmlns:gmd="http://www.isotc211.org/2005/gmd" 
             *      xmlns:gts="http://www.isotc211.org/2005/gts" 
             *      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
             *      xmlns:gml="http://www.opengis.net/gml" 
             *      xmlns:gco="http://www.isotc211.org/2005/gco" 
             *      xmlns:geonet="http://www.fao.org/geonetwork" 
             *      xsi:schemaLocation="http://www.isotc211.org/2005/gmd http://www.isotc211.org/2005/gmd/gmd.xsd">                         
             */
            
            Element metadata = getMetadataChild(root);
            
            if(metadata == null) {
                LOGGER.error("Could not find MD_Metadata child");
                XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
                outputter.output(root, System.out);
                
                throw new GNLibException("Could not find MD_Metadata child");
            }
                        
            Element geonetInfo = metadata.getChild("info", NS_GEONET);
            if(geonetInfo == null) {
                LOGGER.error("Could not find geonet:info child");
                XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
                outputter.output(root, System.out);
                
                throw new GNLibException("Could not find geonet:info child");
            }
            String version = geonetInfo.getChildText("version");
            return version;
                
        } catch (JDOMException ex) {
            LOGGER.error("Error parsing GN response: " + s);
            throw new GNLibException("Error parsing GN response: " + ex.getMessage(), ex);
        } catch (IOException ex) {
            throw new GNLibException("Error while outputting", ex);
        }
    }

	private static Element getMetadataChild(Element root) {
		@SuppressWarnings("unchecked")
		List<Element> children = (List<Element>) root.getChildren();
		
		for (Element child : children) {
			if (child.getName().equals("MD_Metadata")) {
				return child;
			}
		}
		
		return null;
	}
}
