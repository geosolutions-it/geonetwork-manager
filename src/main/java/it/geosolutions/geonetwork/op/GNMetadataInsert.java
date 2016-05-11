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
import it.geosolutions.geonetwork.util.GNInsertConfiguration;
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
 * <h3>Insert metadata</h3>
 * The input file may be a pure metadata to be inserted into GN, or a full GN
 * insert metadata request. The full request requires some more meta-metadata.
 * <br/>If such further data are not provided in the input file, they may be specified 
 * in the configuration, and the full request to be sent to GN will be automatically built.
 * 
 * @author ETj (etj at geo-solutions.it)
 */
public class GNMetadataInsert {
    private final static Logger LOGGER = Logger.getLogger(GNMetadataInsert.class);

    /**
     * Insert a raw metadata document in GN.<br/>
     * The full GN request is built using GNInsertConfiguration.
     *
     * @return the id of the new metadata entry
     */
    public static long insertMetadata(HTTPUtils connection, String gnServiceURL, File inputFile, GNInsertConfiguration cfg)  throws GNLibException, GNServerException {
        if(LOGGER.isInfoEnabled())
            LOGGER.info("Handling pure metadata file " + inputFile);
        Element insertRequest = buildInsertRequest(inputFile, cfg);

        // insert the metadata
        LOGGER.debug("Creating metadata");
        long metadataId = gnInsertMetadata(connection, gnServiceURL, insertRequest, cfg.getEncoding());
        LOGGER.info("Created metadata " + metadataId);
        return metadataId;
    }

    /**
     * Insert a metadata using a <i>request</i> as required by geonetwork.<br/>
     * Required params should be already provided in <i>inputfile</i>.
     *
     * @return the id of the new metadata entry
     */
    public static long insertRequest(HTTPUtils connection, String gnServiceURL, File inputFile)  throws GNLibException, GNServerException {
        if(LOGGER.isInfoEnabled())
            LOGGER.info("Handling full request file " + inputFile);
        Element insertRequest = parseFile(inputFile);

        // insert the metadata
        LOGGER.debug("Creating metadata");
        long metadataId = gnInsertMetadata(connection, gnServiceURL, insertRequest, null);
        LOGGER.info("Created metadata " + metadataId);
        return metadataId;
    }


    /**
     * Creates a Request document for the geonetwork <tt>metadata.insert</tt> operation.
     * <br/>The metadata is read from the file, the other params are read from
     * the configuration.
     * 
     * <ul>
     * <li><b><tt>data</tt></b>: (mandatory) Contains the metadata record</li>
     * <li><b><tt>group</tt></b> (mandatory): Owner group identifier for metadata</li>
     * <li><b><tt>isTemplate</tt></b>: indicates if the metadata content is a new template or not. Default value: "n"</li>
     * <li><b><tt>title</tt></b>: Metadata title. Only required if isTemplate = "y"</li>
     * <li><b><tt>category</tt></b> (mandatory): Metadata category. Use "_none_" value to don't assign any category</li>
     * <li><b><tt>styleSheet</tt></b> (mandatory): Stylesheet name to transform the metadata before inserting in the catalog. Use "_none_" value to don't apply any stylesheet</li>
     * <li><b><tt>validate</tt></b>: Indicates if the metadata should be validated before inserting in the catalog. Values: on, off (default)    </li>
     * </ul>
     */
    private static Element buildInsertRequest(File inputFile, GNInsertConfiguration cfg)  throws GNLibException, GNServerException {
        if(LOGGER.isDebugEnabled()) 
            LOGGER.debug("Compiling request document");
        
        Element metadataFromFile = parseFile(inputFile);

        XMLOutputter outputter = new XMLOutputter(Format.getRawFormat());
        CDATA cdata = new CDATA(outputter.outputString(metadataFromFile)); // CDATA format is required by GN
        
        Element request = new Element("request");
        request.addContent(new Element("data").addContent(cdata));
        request.addContent(new Element("group").setText(cfg.getGroup()));
        request.addContent(new Element("category").setText(cfg.getCategory()==null?"_none":cfg.getCategory()));
        request.addContent(new Element("styleSheet").setText(cfg.getStyleSheet()==null?"_none":cfg.getStyleSheet()));
        request.addContent(new Element("validate").setText(cfg.getValidate()==null?"off":cfg.getValidate().booleanValue()?"on":"off"));
                    
        return request;
    }
    

    /**
     * Insert a metadata in GN.<br/>
     * 
     * <ul>
     * <li>Url: <tt>http://<i>server</i>:<i>port</i>/geonetwork/srv/en/metadata.insert</tt></li>
     * <li>Mime-type: <tt>application/xml</tt></li>
     * <li>Post request: <pre>{@code 
     * <?xml version="1.0" encoding="UTF-8"?>
     * <request>
     *    <group>2</group>
     *    <category>_none_</category>
     *    <styleSheet>_none_</styleSheet>
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
     * 
     * @return the id of the metadata created in geonetwork
     * 
     * @see <a href="http://geonetwork-opensource.org/latest/developers/xml_services/metadata_xml_services.html#insert-metadata-metadata-insert" >GeoNetwork documentation about inserting metadata</a>
     */
    private static long gnInsertMetadata(HTTPUtils connection, String baseURL, final Element gnRequest, String encoding) throws GNLibException, GNServerException {

        String serviceURL = baseURL + "/srv/eng/xml.metadata.insert";
        String res = gnPut(connection, serviceURL, gnRequest, encoding);
        if(connection.getLastHttpStatus() != HttpStatus.SC_OK)
            throw new GNServerException("Error inserting metadata in GeoNetwork (HTTP code "+connection.getLastHttpStatus()+")");
        
        Element rese = parse(res);
        try {
            return Long.parseLong(rese.getChildText("id"));
        } catch (Exception e) {
            LOGGER.error("Error parsing metadata id from: " + res);
            throw new GNLibException("Error parsing metadata id", e);
        }
    }
    
    private static String gnPut(HTTPUtils connection, String serviceURL, final Element gnRequest, String encoding) throws GNLibException, GNServerException {
        
        final XMLOutputter outputter = new XMLOutputter(Format.getCompactFormat());
        String s = outputter.outputString(gnRequest);
        
        connection.setIgnoreResponseContentOnSuccess(false);
        String res = connection.postXml(serviceURL, s, encoding);
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
