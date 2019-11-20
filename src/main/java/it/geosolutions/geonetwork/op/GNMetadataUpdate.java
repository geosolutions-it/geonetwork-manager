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

import java.io.File;
import java.io.StringReader;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.log4j.Logger;
import org.jdom.CDATA;
import org.jdom.Document;

import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * <h3>Update metadata</h3>
 * 
 * @author ETj (etj at geo-solutions.it)
 */
public class GNMetadataUpdate {
    private final static Logger LOGGER = Logger.getLogger(GNMetadataUpdate.class);

    /**
     *
     */
    public static void update(HTTPUtils connection, String gnServiceURL, Long id, String version, File inputFile)  throws GNLibException, GNServerException {
        if(LOGGER.isInfoEnabled())
            LOGGER.info("Using metadata file " + inputFile);
        Element metadataFromFile = parseFile(inputFile);
        update(connection, gnServiceURL, id, version, metadataFromFile);
    }

    /**
    *
    */
    public static void update(HTTPUtils connection, String gnServiceURL, Long id, String version, Element inputElement)  throws GNLibException, GNServerException {
       Element updateRequest = buildUpdateRequest(inputElement, id, version); 

       // update the metadata
       LOGGER.debug("Updating metadata " + id + " version " + version);
       gnUpdateMetadata(connection, gnServiceURL, updateRequest);
       LOGGER.info("Updated metadata " + id + " version " + version);
   }

    /**
     * Creates a Request document for the geonetwork <tt>metadata.update</tt> operation.
     * <ul>
     * <li> id: (mandatory) Identifier of the metadata to update</li>
     * <li> version: (mandatory) This parameter is used to check if another user has updated the metadata after we retrieved it and before involking the update metadata service. CHECK how to provide value to the user</li>
     * <li>isTemplate: indicates if the metadata content is a new template or not. Default value: "n"</li>
     * <li>showValidationErrors: Indicates if the metadata should be validated before updating in the catalog.</li>
     * <li>title: Metadata title (for templates)</li>
     * <li>data (mandatory) Contains the metadata record</li>
     * </ul>
     */
    private static Element buildUpdateRequest(Element metadataElement, Long id, String version)  throws GNLibException, GNServerException {
        if(LOGGER.isDebugEnabled()) 
            LOGGER.debug("Compiling request document");
        
        XMLOutputter outputter = new XMLOutputter(Format.getRawFormat());
        CDATA cdata = new CDATA(outputter.outputString(metadataElement)); // CDATA format is required by GN
        
        Element request = new Element("request");
        request.addContent(new Element("id").setText(String.valueOf(id)));
        if(!version.isEmpty())
            request.addContent(new Element("version").setText(version));
        request.addContent(new Element("data").addContent(cdata));                    
        return request;
    }
    

    /**
     * Insert a metadata in GN.<br/>
     * 
     * <ul>
     * <li>Url: <tt>http://<i>server</i>:<i>port</i>/geonetwork/srv/en/metadata.update.finish</tt></li>
     * <li>Mime-type: <tt>application/xml</tt></li>
     * <li>Post request: <pre>{@code 
     * 
     * <?xml version="1.0" encoding="UTF-8"?>
     * <request>
     *    <id>2</id>
     *    <version>2</version>
     *    <data><![CDATA[
     *       <gmd:MD_Metadata xmlns:gmd="http://www.isotc211.org/2005/gmd"
     *                    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
     *       ...
     *          </gmd:DQ_DataQuality>
     *         </gmd:dataQualityInfo>
     *       </gmd:MD_Metadata>]]>
     *    </data>
     * </request> }</pre></li>
     * </ul>
     *      * 
     * @see <a href="http://geonetwork-opensource.org/latest/developers/xml_services/metadata_xml_services.html#insert-metadata-metadata-insert" >GeoNetwork documentation about inserting metadata</a>
     */
    private static void gnUpdateMetadata(HTTPUtils connection, String baseURL, final Element gnRequest) throws GNLibException, GNServerException {

        String serviceURL = baseURL + "/srv/eng/metadata.update.finish";
        connection.setIgnoreResponseContentOnSuccess(true);
        String res = gnPost(connection, serviceURL, gnRequest);
        if(connection.getLastHttpStatus() != HttpStatus.SC_OK)
            throw new GNServerException("Error updating metadata in GeoNetwork (HTTP code "+connection.getLastHttpStatus()+")");        
    }
    
    private static String gnPost(HTTPUtils connection, String serviceURL, final Element gnRequest) throws GNLibException, GNServerException {
        
        final XMLOutputter outputter = new XMLOutputter(Format.getCompactFormat());
        String s = outputter.outputString(gnRequest);
        
        connection.setIgnoreResponseContentOnSuccess(false);
        String res = connection.postXml(serviceURL, s);
//        if(LOGGER.isInfoEnabled())
//            LOGGER.info(serviceURL + " returned --> " + res);
        return res;
    }
    
    private static Element parseFile(File file) throws GNLibException {
        try{
			SAXBuilder builder = new SAXBuilder();
			Document doc = builder.build(file);
			return  (Element)doc.getRootElement().detach();
		} catch (Exception ex) {
			LOGGER.warn("Error parsing input file " + file);
            throw new GNLibException("Error parsing input file " + file, ex);
		}
    }    
    
    private static Element parse(String s) throws GNLibException {
        try{
			SAXBuilder builder = new SAXBuilder();
            s = s.trim();
			Document doc = builder.build(new StringReader(s));
			return  (Element)doc.getRootElement().detach();
		} catch (Exception ex) {
			LOGGER.warn("Error parsing input string: >>>" + s +"<<<");
            throw new GNLibException("Error parsing input string", ex);
		}
    }    
}
