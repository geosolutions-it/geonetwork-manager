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
import it.geosolutions.geonetwork.util.GNVersion;
import it.geosolutions.geonetwork.util.HTTPUtils;
import java.io.StringReader;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.log4j.Logger;

import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * The xml.metadata.get service can be used to retrieve a metadata record stored in GeoNetwork.
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class GNMetadataGet
{
    private final static Logger LOGGER = Logger.getLogger(GNMetadataGet.class);

    private final GNVersion version;

    public static final GNMetadataGet V26 = new GNMetadataGet(GNVersion.V26);
    public static final GNMetadataGet V28 = new GNMetadataGet(GNVersion.V28);

    public static GNMetadataGet get(GNVersion v) {
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

    private GNMetadataGet(GNVersion v) {
        this.version = v;
    }

    public Element get(HTTPUtils connection, String gnServiceURL, Long id) throws GNLibException, GNServerException
    {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Retrieve metadata #" + id);
        }
        return get(connection, gnServiceURL, id, null);
    }

    public Element get(HTTPUtils connection, String gnServiceURL, String uuid) throws GNLibException, GNServerException
    {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Retrieve metadata " + uuid);
        }
        return get(connection, gnServiceURL, null, uuid);
    }

    protected Element get(HTTPUtils connection, String gnServiceURL, Long id, String uuid) throws GNLibException, GNServerException
    {
        Element request = buildRequest(id, uuid);
        String response = gnGetMetadata(connection, gnServiceURL, request);
        Element eResponse = parse(response);
        return eResponse;
    }

    protected static Element buildRequest(Long id, String uuid)
    {
        Element request = new Element("request");
        if (id != null) {
            request.addContent(new Element("id").setText(String.valueOf(id)));
        } else if (uuid != null) {
            request.addContent(new Element("uuid").setText(uuid));
        }
        return request;
    }

    private String gnGetMetadata(HTTPUtils connection, String baseURL, final Element gnRequest) throws GNServerException
    {
        String serviceURL = baseURL + "/srv/"+getLang()+"/xml.metadata.get";
        String resp = gnPost(connection, serviceURL, gnRequest);
        if (connection.getLastHttpStatus() != HttpStatus.SC_OK) {
            throw new GNServerException("Error retrieving metadata in GeoNetwork");
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
}
