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

import it.geosolutions.geonetwork.exception.GNException;
import it.geosolutions.geonetwork.exception.GNLibException;
import it.geosolutions.geonetwork.exception.GNServerException;
import it.geosolutions.geonetwork.util.GNInsertConfiguration;
import it.geosolutions.geonetwork.util.GNSearchRequest;
import it.geosolutions.geonetwork.util.GNSearchResponse;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;

/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class GeonetworkSearchTest extends GeonetworkTest {
    private final static Logger LOGGER = Logger.getLogger(GeonetworkSearchTest.class);

    
    public GeonetworkSearchTest() {
    }

    @Test
    @Ignore
    public void testSearchMetadata() throws GNException, IOException {
        if(!runIntegrationTest())
            return;
        removeAllMetadata();

        GNClient client = createClientAndCheckConnection();
        insertSome();

        {
            GNSearchRequest searchRequest = new GNSearchRequest();
            GNSearchResponse searchResponse = client.search(searchRequest);
//            assertEquals(12, searchResponse.getCount());
            asyncSearchAssertEquals(12, client, searchRequest);
        }

        {
            GNSearchRequest searchRequest = new GNSearchRequest();
            searchRequest.addParam(GNSearchRequest.Param.title, "test0");
            GNSearchResponse searchResponse = client.search(searchRequest);
//            assertEquals(2, searchResponse.getCount());
            asyncSearchAssertEquals(2, client, searchRequest);
        }
        {
            GNSearchRequest searchRequest = new GNSearchRequest();
            searchRequest.addParam(GNSearchRequest.Param.title, "ACK00");
            GNSearchResponse searchResponse = client.search(searchRequest);
//            assertEquals(5, searchResponse.getCount());
            asyncSearchAssertEquals(5, client, searchRequest);
        }

        {
            // create a request file
            Element request = new Element("request").addContent(
                    new Element("any").setText("ACK99"));
            File tempFile = File.createTempFile("gbtest_request", ".xml");
            FileUtils.forceDeleteOnExit(tempFile);
            XMLOutputter outputter = new XMLOutputter(Format.getCompactFormat());
            FileUtils.writeStringToFile(tempFile, outputter.outputString(request));
//            GNSearchResponse searchResponse = client.search(tempFile);
//            assertEquals(7, searchResponse.getCount());
            asyncSearchAssertEquals(7, client, tempFile);
        }
    }

    protected void insertSome() throws IOException, GNException {
        final String TITLETOKEN = "TOKEN_FOR_TITLE";

        GNClient client = createClientAndCheckConnection();

        File origFile = loadFile("metadata_token.xml");
        String orig = FileUtils.readFileToString(origFile);

        File tempFile = File.createTempFile("gbtest", ".xml");
        FileUtils.forceDeleteOnExit(tempFile);

        for (int i = 0; i < 5; i++) {
            String title = "GeoBatch GeoNetworkAction test"+i+ " ACK00";
            String test = orig.replace(TITLETOKEN, title);
            FileUtils.writeStringToFile(tempFile, test);
            long id = insertMetadata(client, tempFile);
            LOGGER.info("Created test metadata id:"+id+" ["+title+"]");
        }

        for (int i = 0; i < 7; i++) {
            String title = "GeoBatch GeoNetworkAction test"+i+ " ACK99";
            String test = orig.replace(TITLETOKEN, title);
            FileUtils.writeStringToFile(tempFile, test);
            long id = insertMetadata(client, tempFile);
            LOGGER.info("Created test metadata id:"+id+" ["+title+"]");
        }
    }

    public long insertMetadata(GNClient client, File file) throws GNServerException, GNLibException {
        GNInsertConfiguration cfg = createDefaultInsertConfiguration();
        long id = client.insertMetadata(cfg, file);
        return id;
    }

    @Test
    @Ignore
    public void testEmptySearch() throws Exception {
        if( ! runIntegrationTest() ) return;

        GNClient client = createClientAndCheckConnection();
        GNSearchRequest searchRequest = new GNSearchRequest();
        searchRequest.addConfig(GNSearchRequest.Config.hitsPerPage, "5");
        searchRequest.addParam(GNSearchRequest.Param.any, "veryunlikelystringtobeplacedinsideametadata");

        GNSearchResponse searchResponse = client.search(searchRequest);
        if(searchResponse.getCount() != 0 ) {
            LOGGER.error(searchResponse.toString());
            for (GNSearchResponse.GNMetadata metadata : searchResponse) {
                Long id = metadata.getId();
                LOGGER.info("id #" + id);
                Element md = client.get(id);
                XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
                LOGGER.error("Metadata -> " + out.outputString(md));
            }
        }
        assertEquals(0, searchResponse.getCount());

    }
    
}
