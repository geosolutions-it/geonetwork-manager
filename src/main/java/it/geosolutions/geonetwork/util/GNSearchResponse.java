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
package it.geosolutions.geonetwork.util;

import java.util.List;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;


/**
 * @author ETj (etj at geo-solutions.it)
 */
public class GNSearchResponse {

    private final static Namespace NS_GEONET = Namespace.getNamespace("geonet", "http://www.fao.org/geonetwork");

    private Element response;

    public GNSearchResponse(Element response) {
        this.response = response;
    }

    public int getCount() {
        String cnt = getSummary().getAttributeValue("count");
        return Integer.parseInt(cnt);
    }

    public int getFrom() {
        String cnt = response.getAttributeValue("from");
        return Integer.parseInt(cnt);
    }

    public int getTo() {
        String cnt = response.getAttributeValue("to");
        return Integer.parseInt(cnt);
    }

    public boolean isCompleteResponse() {
        int cnt = getCount();
        return cnt == 0 || cnt == getTo() - getFrom() + 1;
    }

    public Element getSummary() {
        return response.getChild("summary");
    }

    public GNMetadata getMetadata(int index) {
        Element metadata = ((List<Element>)response.getChildren("metadata")).get(index);
        return new GNMetadata(metadata);
    }

    public static class GNMetadata {
        private Element metadata;

        public GNMetadata(Element metadata) {
            this.metadata = metadata;
        }

        public Element getInfo() {
            return metadata.getChild("info", NS_GEONET);
        }

        public String getId() {
            return getInfo().getChildText("id");
        }

        public String getUUID() {
            return getInfo().getChildText("uuid");
        }

        public String getSchema() {
            return getInfo().getChildText("schema");
        }
    }

    @Override
    public String toString() {
        final XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
        return "GNSearchResponse[" + outputter.outputString(response) + ']';
    }


}
