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

import it.geosolutions.geonetwork.op.GNMetadataAdmin;
import it.geosolutions.geonetwork.op.GNLogin;
import it.geosolutions.geonetwork.op.GNMetadataInsert;
import it.geosolutions.geonetwork.exception.GNLibException;
import it.geosolutions.geonetwork.exception.GNServerException;
import it.geosolutions.geonetwork.util.GNInsertConfiguration;
import it.geosolutions.geonetwork.util.GNPrivConfiguration;
import it.geosolutions.geonetwork.util.HTTPUtils;

import java.io.File;
import org.apache.log4j.Logger;


/**
 * Facade for the various GN operations
 * 
 * @author ETj (etj at geo-solutions.it)
 */
public class GNClient {
        
    private final static Logger LOGGER = Logger.getLogger(GNClient.class);

    // create stateful (we need the cookies) connection handler
    private HTTPUtils connection = new HTTPUtils();

    private final String gnServiceURL;

    public GNClient(String serviceURL) {
        this.gnServiceURL = serviceURL;
    }
    
    /**
     * Facade for {@link GNLogin#login(it.geosolutions.geonetwork.util.HTTPUtils, java.lang.String, java.lang.String, java.lang.String) }
     */
    public boolean login(String username, String password) {
        return GNLogin.login(connection, gnServiceURL, username, password);
    }

    /**
     * Facade for {@link GNMetadataInsert#insertMetadata(HTTPUtils, String, File, GNInsertConfiguration)}
     */
    public long insertMetadata(GNInsertConfiguration cfg, File metadataFile) throws GNLibException, GNServerException {
        return GNMetadataInsert.insertMetadata(connection, gnServiceURL, metadataFile, cfg);
    }

    /**
     * Facade for {@link GNMetadataInsert#insertRequest((HTTPUtils connection, String gnServiceURL, File inputFile))}
     */
    public long insertRequest(File requestFile) throws GNLibException, GNServerException {
        return GNMetadataInsert.insertRequest(connection, gnServiceURL, requestFile);
    }

    /**
     * Facade for {@link GNMetadataAdmin#setPriv(it.geosolutions.geonetwork.util.HTTPUtils, java.lang.String, long, it.geosolutions.geonetwork.util.GNPrivConfiguration) }
     */
    public void setPrivileges(long metadataId, GNPrivConfiguration cfg) throws GNLibException, GNServerException {
        GNMetadataAdmin.setPriv(connection, gnServiceURL, metadataId, cfg);
    }

}
