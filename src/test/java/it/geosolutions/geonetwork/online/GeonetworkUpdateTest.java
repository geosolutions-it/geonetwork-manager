/*
 *  GeoNetwork-Manager - Simple Manager Library for GeoNetwork
 *
 *  Copyright (C) 2007,2016 GeoSolutions S.A.S.
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


import java.io.File;
import java.util.EnumSet;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.junit.Test;

import it.geosolutions.geonetwork.GNClient;
import it.geosolutions.geonetwork.util.GNInsertConfiguration;
import it.geosolutions.geonetwork.util.GNPriv;
import it.geosolutions.geonetwork.util.GNPrivConfiguration;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class GeonetworkUpdateTest extends GeonetworkTest {
    private final static Logger LOGGER = Logger.getLogger(GeonetworkUpdateTest.class);
    
    public GeonetworkUpdateTest() {
    }

    
    @Test
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
        
        Element md = client.get(id);
//        XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
//        outputter.output(md, System.out);

        final String UPDATED_TEXT = "Updated title";
        {
            Element chstr = getTitleElement(md);        
            assertEquals("TEST GeoBatch Action: GeoNetwork", chstr.getText());
            chstr.setText(UPDATED_TEXT);
        }
        
        File tempFile = File.createTempFile("gnm_update", ".xml");
        FileUtils.forceDeleteOnExit(tempFile);
        XMLOutputter fileOutputter = new XMLOutputter(Format.getCompactFormat());
        FileUtils.writeStringToFile(tempFile, fileOutputter.outputString(md));
        
        client.updateMetadata(id, tempFile);
        
        {
            Element md2 = client.get(id);
            Element chstr = getTitleElement(md2);        
            assertEquals(UPDATED_TEXT, chstr.getText());            
        }
    }
    
}


