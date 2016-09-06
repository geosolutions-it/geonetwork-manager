/*
 *  GeoNetwork-Manager - Simple Manager Library for GeoNetwork
 *
 *  Copyright (C) 2007-2016 GeoSolutions S.A.S.
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

package it.geosolutions.geonetwork.op.gn2x;

import it.geosolutions.geonetwork.exception.GNLibException;
import it.geosolutions.geonetwork.exception.GNServerException;
import it.geosolutions.geonetwork.util.GNSearchRequest;
import it.geosolutions.geonetwork.util.GNSearchResponse;
import it.geosolutions.geonetwork.util.GNVersion;
import it.geosolutions.geonetwork.util.HTTPUtils;
import java.io.File;
import java.io.StringReader;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.log4j.Logger;
import org.jdom.Document;

import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * The xml.search service can be used to retrieve the metadata stored in GeoNetwork.
 * 
 * @author ETj (etj at geo-solutions.it)
 */
public class GNMetadataSearch
{
    private final static Logger LOGGER = Logger.getLogger(GNMetadataSearch.class);

    private final GNVersion version;

    public static final GNMetadataSearch V26 = new GNMetadataSearch(GNVersion.V26);
    public static final GNMetadataSearch V28 = new GNMetadataSearch(GNVersion.V28);

    public static GNMetadataSearch get(GNVersion v) {
        switch (v) {
            case V26:
                return V26;
            case V28:
                return V28;
            default:
                throw new IllegalStateException("Bad version requested " + v);
        }
    }

    private String getLang() {
        return version == GNVersion.V26 ? "en" : "eng";
    }

    private GNMetadataSearch(GNVersion v) {
        this.version = v;
    }

    public GNSearchResponse search(HTTPUtils connection, String gnServiceURL, GNSearchRequest searchRequest) throws GNLibException, GNServerException {
        if(LOGGER.isDebugEnabled())
            LOGGER.debug("Search metadata");
        Element request = searchRequest.toElement();
        return search(connection, gnServiceURL, request);
    }

    public GNSearchResponse search(HTTPUtils connection, String gnServiceURL, File fileRequest) throws GNLibException, GNServerException {
        if(LOGGER.isDebugEnabled())
            LOGGER.debug("Search metadata " + fileRequest);
        Element request = parseFile(fileRequest);
        return search(connection, gnServiceURL, request);
    }

    private GNSearchResponse search(HTTPUtils connection, String gnServiceURL, Element request) throws GNLibException, GNServerException {
        String response = gnSearchMetadata(connection, gnServiceURL, request);
        Element eResponse = parse(response);
        GNSearchResponse searchResponse = new GNSearchResponse(eResponse);
        if(LOGGER.isInfoEnabled())
            LOGGER.info("Got response with " + searchResponse.getCount() + " metadata");
        return searchResponse;
    }

    private String gnSearchMetadata(HTTPUtils connection, String baseURL, final Element gnRequest) throws GNServerException
    {
        String serviceURL = baseURL + "/srv/"+getLang()+"/xml.search";
        String resp = gnPost(connection, serviceURL, gnRequest);
        if (connection.getLastHttpStatus() != HttpStatus.SC_OK) {
            throw new GNServerException("Error searching metadata in GeoNetwork");
        }
        return resp;
    }

    private static String gnPost(HTTPUtils connection, String serviceURL, final Element gnRequest)
    {

        final XMLOutputter outputter = new XMLOutputter(Format.getCompactFormat());
        String xmlReq = outputter.outputString(gnRequest);

        connection.setIgnoreResponseContentOnSuccess(false);
        return connection.postXml(serviceURL, xmlReq);
    }

    private static Element parse(String s) throws GNLibException
    {
        try {
            SAXBuilder builder = new SAXBuilder();
            return builder.build(new StringReader(s)).detachRootElement();
        } catch (Exception ex) {
            LOGGER.error("Error parsing GN response: " + s);
            throw new GNLibException("Error parsing GN response: " + ex.getMessage(), ex);
        }
    }

    private static Element parseFile(File file) throws GNLibException
    {
        try {
            SAXBuilder builder = new SAXBuilder();
            Document doc = builder.build(file);
            return (Element) doc.getRootElement().detach();
        } catch (Exception ex) {
            LOGGER.warn("Error parsing input file " + file);
            throw new GNLibException("Error parsing input file " + file, ex);
        }
    }
}
