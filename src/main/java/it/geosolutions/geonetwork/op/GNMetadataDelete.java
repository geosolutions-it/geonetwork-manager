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
import it.geosolutions.geonetwork.util.GNPrivConfiguration;
import it.geosolutions.geonetwork.util.HTTPUtils;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.log4j.Logger;

import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * The metadata.delete service allows to remove a metadata record from the catalog.
 * The metadata content is backup in MEF format by default in data\removed folder.
 *
 * @see http://geonetwork-opensource.org/manuals/2.6.3/developer/xml_services/metadata_xml_services.html#delete-metadata-metadata-delete
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class GNMetadataDelete {
        
    private final static Logger LOGGER = Logger.getLogger(GNMetadataDelete.class);

    public static void delete(HTTPUtils connection, String gnServiceURL, long metadataId) throws GNLibException, GNServerException {
        if(LOGGER.isDebugEnabled())
            LOGGER.debug("Deleting md#" + metadataId);
        Element idRequest = buildIdRequest(metadataId);
        gnDeleteMetadata(connection, gnServiceURL, idRequest);
        if(LOGGER.isInfoEnabled())
            LOGGER.info("Deleted md#" + metadataId);
    }
    
    /**
     * 
     * @see {@link http://geonetwork-opensource.org/latest/developers/xml_services/metadata_xml_services.html#update-operations-allowed-for-a-metadata-metadata-admin }
     */
    private static Element buildIdRequest(long metadataId) throws GNLibException {
        if(LOGGER.isDebugEnabled()) 
            LOGGER.debug("Compiling id request document");
                
        Element request = new Element("request");
        request.addContent(new Element("id").setText(Long.toString(metadataId)));
                    
        return request;
    }
    
    private static void gnDeleteMetadata(HTTPUtils connection, String baseURL, final Element gnRequest) throws GNServerException {

        String serviceURL = baseURL + "/srv/eng/xml.metadata.delete";
        gnPost(connection, serviceURL, gnRequest);
        if(connection.getLastHttpStatus() != HttpStatus.SC_OK)
            throw new GNServerException("Error deleting metadata in GeoNetwork");
    }
    
    private static String gnPost(HTTPUtils connection, String serviceURL, final Element gnRequest) {
        
        final XMLOutputter outputter = new XMLOutputter(Format.getCompactFormat());
        String s = outputter.outputString(gnRequest);
        
        connection.setIgnoreResponseContentOnSuccess(false);
        String res = connection.postXml(serviceURL, s);
//        if(LOGGER.isInfoEnabled())
//            LOGGER.info(serviceURL + " returned --> " + res);
        return res;
    }

}
