/*
 *  GeoNetwork-Manager - Simple Manager Library for GeoNetwork
 *
 *  Copyright (C) 2007,2012 GeoSolutions S.A.S.
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
import org.jdom.input.SAXBuilder;

/**
 * Get some info (id, uuid, version number) for a given Metadata.
 * <br/><br/>
 * This operation uses a custom GN service that retrieves id, uuid of a metadata,
 * and asks for a new version number.
 *
 * See http://trac.osgeo.org/geonetwork/ticket/1062
 *
 * If your GN instance does not implement the xmlk.metadata.info.getForUpdate service,
 * please use the {@link GNMetadataGetVersion} operation.
 * 
 * @author ETj (etj at geo-solutions.it)
 */

@Deprecated
public class GNMetadataGetInfo {

    private final static Logger LOGGER = Logger.getLogger(GNMetadataGetInfo.class);


    public static class MetadataInfo {
        long id;
        String uuid;
        Integer version;

        public long getId() {
            return id;
        }

        public String getUuid() {
            return uuid;
        }

        /**
         * @return the updated version number, or null if update was not requested.
         */
        public Integer getVersion() {
            return version;
        }

        @Override
        public String toString() {
            return "MetadataInfo[" 
                    + "id=" + id
                    + ", uuid=" + uuid
                    + (version != null? ", version=" + version : "")
                    + ']';
        }
    }
            

    public static MetadataInfo get(HTTPUtils connection, String gnServiceURL, Long id, boolean forUpdate) throws GNLibException, GNServerException {
        return getAux(connection, gnServiceURL, "id="+id, forUpdate);
    }
    public static MetadataInfo get(HTTPUtils connection, String gnServiceURL, String uuid, boolean forUpdate) throws GNLibException, GNServerException {
        return getAux(connection, gnServiceURL, "uuid="+uuid, forUpdate);
    }

    protected static MetadataInfo getAux(HTTPUtils connection, String gnServiceURL, String queryId, boolean forUpdate) throws GNLibException, GNServerException {
        try {
            if(LOGGER.isDebugEnabled())
                LOGGER.debug("Retrieve metadata info for " + queryId);

            String serviceName = forUpdate? "getForUpdate" : "get";
            String serviceURL = gnServiceURL + "/srv/eng/xml.metadata.info."+serviceName+"?"+queryId;
            
            connection.setIgnoreResponseContentOnSuccess(false);
            String response = connection.get(serviceURL);

            if(LOGGER.isDebugEnabled()) {
                if(response != null)
                    LOGGER.debug("Response is " + response.length() + " chars long");
                else
                    LOGGER.debug("Response is null");
            }
            
            if(connection.getLastHttpStatus() != HttpStatus.SC_OK)
                throw new GNServerException("Error retrieving data in GeoNetwork", connection.getLastHttpStatus());

            MetadataInfo ret = parseMetadataInfo(response);
                        
            if(LOGGER.isDebugEnabled())
                LOGGER.debug("Metadata " + queryId + " has info " + ret);
                        
            return ret;
        } catch (MalformedURLException ex) {
            throw new GNLibException("Bad URL", ex);
        }
    }
    
    private static MetadataInfo parseMetadataInfo(String response) throws GNLibException {
        try {
            SAXBuilder builder = new SAXBuilder();
            Element root = builder.build(new StringReader(response)).detachRootElement();
                        
            String id = root.getChildText("id");
            String uuid = root.getChildText("uuid");
            String sver = root.getChildText("version");

            MetadataInfo info = new MetadataInfo();
            info.id = Long.parseLong(id);
            info.uuid = uuid;

            if(sver!=null && ! sver.isEmpty())
                info.version = Integer.parseInt(sver);

            return info;
                
        } catch (JDOMException ex) {
            LOGGER.error("Error parsing GN response: " + response);
            throw new GNLibException("Error parsing GN response: " + ex.getMessage(), ex);
        } catch (IOException ex) {
            throw new GNLibException("Error while outputting", ex);
        } catch(NumberFormatException ex) {
            LOGGER.error("Error parsing number in GN response: " + response);
            throw new GNLibException("Error parsing number in GN response: " + ex.getMessage(), ex);
        }
    }
}
