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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumSet;
import java.util.GregorianCalendar;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.junit.Test;

import it.geosolutions.geonetwork.GNClient;
import it.geosolutions.geonetwork.op.gn3.GN3MetadataGetInfo.MetadataInfo;
import it.geosolutions.geonetwork.util.GNInsertConfiguration;
import it.geosolutions.geonetwork.util.GNPriv;
import it.geosolutions.geonetwork.util.GNPrivConfiguration;
import org.junit.Assume;

/**
 *
 * @author DamianoG (damiano.giampaoli at geo-solutions.it)
 */
public class GeonetworkUpdateWithInfoTestGN3 extends GeonetworkTest {
    private final static Logger LOGGER = Logger.getLogger(GeonetworkUpdateWithInfoTestGN3.class);
    
    public GeonetworkUpdateWithInfoTestGN3() {
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
        
        MetadataInfo info = null;
        try{
            info = client.getInfo(id);
        }
        catch(UnsupportedOperationException e){
            assertEquals("Operation supported only for GeoNetwork 3", e.getMessage());
            Assume.assumeTrue("Operation supported only for GeoNetwork 3", false);
        }
        assertEquals(id, info.getId());
        assertEquals("a1157d96-c02d-4751-ab1d-63aabf6a7d15",info.getSource());
        assertEquals("datasets",info.getCategory());
        assertEquals("iso19139",info.getSchema());
        String uuid = info.getUuid(); 
        Calendar cal1 = parseDate(info.getCreateDate());
        Calendar cal2 = parseDate(info.getChangeDate());

        Element md = client.get(id);
        
        final String UPDATED_TEXT = "Updated title with info";
        {
            Element chstr = getTitleElement(md);
            assertEquals("TEST GeoBatch Action: GeoNetwork", chstr.getText());
            chstr.setText(UPDATED_TEXT);
        }
        
        File tempFile = File.createTempFile("gnm_update", ".xml");
        FileUtils.forceDeleteOnExit(tempFile);
        XMLOutputter fileOutputter = new XMLOutputter(Format.getCompactFormat());
        FileUtils.writeStringToFile(tempFile, fileOutputter.outputString(md));
        
        //Wait 1 second in order to update the metadata ensuring the change date is changed
        Thread.sleep(1000);
        
        client.updateMetadata(id, tempFile);
        Element md2 = client.get(id);
        
        assertEquals(UPDATED_TEXT, getTitleElement(md2).getText());
        
        info = client.getInfo(uuid);
        
        assertEquals("a1157d96-c02d-4751-ab1d-63aabf6a7d15", info.getSource());
        assertEquals("datasets", info.getCategory());
        assertEquals("iso19139", info.getSchema());
        assertEquals(uuid, info.getUuid());
        Calendar cal1u = parseDate(info.getCreateDate());
        Calendar cal2u = parseDate(info.getChangeDate());
        
        assertTrue(cal1.equals(cal1u));
        assertTrue(cal2.before(cal2u));

    }
    
    private Calendar parseDate(String date) throws ParseException{
        DateFormat df = new SimpleDateFormat("YYYY-MM-DD'T'hh:mm:ss");
        Date dateAsDate = df.parse(date);
        Calendar cal = new GregorianCalendar();
        cal.setTime(dateAsDate);
        return cal;
    }
}


