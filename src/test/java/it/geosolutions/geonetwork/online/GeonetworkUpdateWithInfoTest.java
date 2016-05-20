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
package it.geosolutions.geonetwork.online;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeTrue;

import java.io.File;
import java.util.EnumSet;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.junit.Test;

import it.geosolutions.geonetwork.GNClient;
import it.geosolutions.geonetwork.exception.GNServerException;
import it.geosolutions.geonetwork.op.GNMetadataGetInfo;
import it.geosolutions.geonetwork.op.GNMetadataGetInfo.MetadataInfo;
import it.geosolutions.geonetwork.op.GNMetadataUpdate;
import it.geosolutions.geonetwork.util.GNInsertConfiguration;
import it.geosolutions.geonetwork.util.GNPriv;
import it.geosolutions.geonetwork.util.GNPrivConfiguration;

/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class GeonetworkUpdateWithInfoTest extends GeonetworkTest {
    private final static Logger LOGGER = Logger.getLogger(GeonetworkUpdateWithInfoTest.class);
    
    public GeonetworkUpdateWithInfoTest() {
    }

    
    @Test
    //@Ignore
    public void testUpdateMetadata() throws Exception {
        
        GNInsertConfiguration cfg = createDefaultInsertConfiguration();

        GNPrivConfiguration pcfg = new GNPrivConfiguration();

        pcfg.addPrivileges(GNPrivConfiguration.GROUP_GUEST,    EnumSet.of(GNPriv.FEATURED));
        pcfg.addPrivileges(GNPrivConfiguration.GROUP_INTRANET, EnumSet.of(GNPriv.DYNAMIC, GNPriv.FEATURED));
        pcfg.addPrivileges(GNPrivConfiguration.GROUP_ALL,      EnumSet.of(GNPriv.VIEW, GNPriv.DYNAMIC, GNPriv.FEATURED));
        pcfg.addPrivileges(2, EnumSet.allOf(GNPriv.class));

        File file = loadFile("metadata.xml");
        assertNotNull(file);

        GNClient client = createClientAndCheckConnection();
        long id = client.insertMetadata(cfg, file);

        client.setPrivileges(id, pcfg);

        //=== using the custom service
        MetadataInfo info = null;

        // first try: the service is installed?
        try {
            info = GNMetadataGetInfo.get(client.getConnection(), gnServiceURL, id, false);
            LOGGER.info("Basic metadataInfo by id is " + info);
            assertNotNull(info);
            assertNull(info.getVersion());
            assertEquals(id, info.getId());
        } catch (GNServerException ex) {
            //It looks like geonetwork return 403 in case of service is not found...
            if(ex.getHttpCode() == 404 || ex.getHttpCode() == 403) {
                LOGGER.error("metadata.info.get is not installed on GeoNetwork 2. Skipping test.");
                assumeTrue(true);
                return;
            } else
                throw ex;
        } catch (Exception ex) {
            LOGGER.error("metadata.info.get doesn't work on GeoNetwork 3. Skipping test...");
            assumeTrue(true);
            return;
           
        }
        
        info = GNMetadataGetInfo.get(client.getConnection(), gnServiceURL, info.getUuid(), false);
        LOGGER.info("Basic metadataInfo by UUID is " + info);
        assertNotNull(info);
        assertNull(info.getVersion());
        assertEquals(id, info.getId());

        info = GNMetadataGetInfo.get(client.getConnection(), gnServiceURL, id, true);
        LOGGER.info("MetadataInfo is " + info);
                
        assertNotNull(info);
        assertEquals(Integer.valueOf(2), info.getVersion()); // the md has just been created
        assertEquals(id, info.getId());


        Element md = client.get(id);
//        XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
//        outputter.output(md, System.out);

        final String UPDATED_TEXT = "Updated title";
        {
            Element chstr = getTitleElement(md);        
            assertEquals("TEST GeoBatch Action: GeoNetwork", chstr.getText());
            chstr.setText(UPDATED_TEXT);
        }
        
        File tempFile = File.createTempFile("gnm_info_update", ".xml");
        FileUtils.forceDeleteOnExit(tempFile);
        XMLOutputter fileOutputter = new XMLOutputter(Format.getCompactFormat());
        FileUtils.writeStringToFile(tempFile, fileOutputter.outputString(md));
        
        GNMetadataUpdate.update(client.getConnection(), gnServiceURL, id, Integer.toString(info.getVersion()), tempFile, null);
        
        {
            Element md2 = client.get(id);
            Element chstr = getTitleElement(md2);        
            assertEquals(UPDATED_TEXT, chstr.getText());            
        }

        info = GNMetadataGetInfo.get(client.getConnection(), gnServiceURL, id, true);
//        String version3 = GNMetadataGetVersion.get(client.getConnection(), gnServiceURL, id);
        LOGGER.info("New MetadataInfo is " + info);
                
        assertNotNull(info.getVersion());
        assertEquals(Integer.valueOf(4), info.getVersion()); // the md has been updated once
        
        
        // try bad version number
        try {
            GNMetadataUpdate.update(client.getConnection(), gnServiceURL, id, "9999", tempFile, null);
            fail("Bad version exception not trapped");
        } catch(GNServerException e) {
            LOGGER.info("Bad version number error trapped properly ("+e.getMessage()+")");
        }
                
//        client.deleteMetadata(id);
    }
}


