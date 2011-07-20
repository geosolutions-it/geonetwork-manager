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
package it.geosolutions.geonetwork;

import it.geosolutions.geonetwork.util.GNSearchRequest;
import it.geosolutions.geonetwork.util.GNSearchResponse;
import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.junit.Test;

/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class GeonetworkSearchTest extends GeonetworkTest {
    private final static Logger LOGGER = Logger.getLogger(GeonetworkSearchTest.class);

    
    public GeonetworkSearchTest() {
    }

    @Test
    public void testSearch() throws Exception {
        if( ! runIntegrationTest ) return;

        GNClient client = createClientAndLogin();
        GNSearchRequest searchRequest = new GNSearchRequest();
        searchRequest.addConfig(GNSearchRequest.Config.hitsPerPage, "5");
        searchRequest.addParam(GNSearchRequest.Param.any, "veryunlikelystringtobeplacedinsideametadata");

        GNSearchResponse searchResponse = client.search(searchRequest);
        if(searchResponse.getCount() != 0 ) {
            LOGGER.error(searchResponse.toString());
            Long id = Long.valueOf(searchResponse.getMetadata(0).getId());
            Element md = client.get(id);
            XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
            LOGGER.error("Metadata -> " + out.outputString(md));
        }
        assertEquals(0, searchResponse.getCount());

    }
    
}
