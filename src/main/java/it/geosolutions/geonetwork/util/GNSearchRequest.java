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

import java.util.HashMap;
import java.util.Map;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;


/**
 * http://geonetwork-opensource.org/latest/developers/xml_services/metadata_xml_services.html#search-metadata-xml-search
 *
Search configuration parameters (all values are optional)
<ul>
<li><strong>remote</strong>: Search in local catalog or in a remote catalog. Values: off (default), on</li>
<li><strong>extended</strong>: Values: on, off (default)</li>
<li><strong>timeout</strong>: Timeout for request in seconds (default: 20)</li>
<li><strong>hitsPerPage</strong>: Results per page (default: 10)</li>
<li><strong>similarity</strong>: Lucene accuracy for searches (default 0.8)</li>
<li><strong>sortBy</strong>: Sorting criteria. Values: relevance (default), rating, popularity, changeDate, title</li>
</ul>
 *
 * <p>Search parameters (all values are optional):</p>
<ul class="simple">
<li><strong>eastBL, southBL, northBL, westBL</strong>:
Bounding box to restrict the search</li>
<li><strong>relation</strong>: Bounding box criteria.
Values: equal, overlaps (default), encloses, fullyOutsideOf,
intersection, crosses, touches, within</li>
<li><strong>any</strong>: Text to search in a free text search</li>
<li><strong>title</strong>: Metadata title</li>
<li><strong>abstract</strong>: Metadata abstract</li>
<li><strong>themeKey</strong>: Metadata keywords. To search for several use a value like &#8220;Global&#8221; or &#8220;watersheds&#8221;</li>
<li><strong>template</strong>: Indicates if search for templates or not. Values: n (default), y</li>
<li><strong>dynamic</strong>: Map type. Values: off (default), on</li>
<li><strong>download</strong>: Map type. Values: off (default), on</li>
<li><strong>digital</strong>: Map type. Values: off (default), on</li>
<li><strong>paper</strong>: Map type. Values: off (default), on</li>
<li><strong>group</strong>: Filter metadata by group, if missing search in all groups</li>
<li><strong>attrset</strong>:</li>
<li><strong>dateFrom</strong>: Filter metadata created after specified date</li>
<li><strong>dateTo</strong>: Filter metadata created before specified date</li>
<li><strong>category</strong>: Metadata category. If not specified, search all categories</li>
</ul>
 * 
 * @author ETj (etj at geo-solutions.it)
 */
public class GNSearchRequest {

    /**
     * Config fields
     */
    public enum Config {
        remote,
        extended,
        timeout,
        hitsPerPage,
        similarity,
        sortBy
    }

    public enum SortBy {
        relevance, rating, popularity, changeDate, title;
    }

    public enum BBoxRelation {
        equal,
        overlaps,
        encloses,
        fullyOutsideOf,
        intersection,
        crosses,
        touches,
        within;
    }

    /**
     * Standard params.
     */
    public enum Param {
        any,
        title,
        abs("abstract"),
        themeKey,
        template,
        dynamic,
        download,
        digital,
        paper,
        group,
        attrset,
        dateFrom,
        dateTo,
        category,

        extFrom, // advanced: temporal extent
        extTo    // advanced: temporal extent
        ;

        private final String name;

        private Param() {
            name = name();
        }

        private Param(String name) {
            this.name = name;
        }

        public String toString() {
            return name;
        }
    }

    private Map<String, String> requestFields = new HashMap<String, String>();

    public GNSearchRequest() {
//        addConfig(Config.similarity, "1.0"); // we want exact match by default
    }

    public void addConfig(Config config, String value) {
        // TODO: check value consistency
        requestFields.put(config.name(), value);
    }

    public void addParam(Param param, String value) {
        // TODO: check params consistency
        requestFields.put(param.toString(), value);
    }

    /**
     * Add a non-standard param to the request.<br/>
     * Use this method only if you customized GeoNetwork adding new search fields.
     */
    public void addParam(String param, String value) {
        requestFields.put(param, value);
    }

    
    private static final XMLOutputter XML_OUTPUTTER = new XMLOutputter(Format.getPrettyFormat());
    public String toXml() {
        return XML_OUTPUTTER.outputString(toElement());
    }

    public Element toElement() {
        Element request = new Element("request");
        for (Map.Entry<String, String> entry : requestFields.entrySet()) {
            request.addContent(new Element(entry.getKey()).setText(entry.getValue()));
        }
        return request;
    }
}
