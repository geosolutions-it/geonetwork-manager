/*
 *  GeoNetwork-Manager - Simple Manager Library for GeoNetwork
 *
 *  Copyright (C) 2016 GeoSolutions S.A.S.
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

import java.io.File;

import org.apache.log4j.Logger;
import org.jdom.Element;

import it.geosolutions.geonetwork.exception.GNLibException;
import it.geosolutions.geonetwork.exception.GNServerException;
import it.geosolutions.geonetwork.op.gn210.GNInfo;
import it.geosolutions.geonetwork.op.gn210.GNMetadataDelete;
import it.geosolutions.geonetwork.op.gn210.GNMetadataGet;
import it.geosolutions.geonetwork.op.gn210.GNMetadataInsert;
import it.geosolutions.geonetwork.op.gn210.GNMetadataSearch;
import it.geosolutions.geonetwork.op.gn3.GN3MetadataAdmin;
import it.geosolutions.geonetwork.op.gn3.GN3MetadataGetInfo;
import it.geosolutions.geonetwork.op.gn3.GN3MetadataGetInfo.MetadataInfo;
import it.geosolutions.geonetwork.op.gn3.GN3MetadataUpdate;
import it.geosolutions.geonetwork.util.GNInsertConfiguration;
import it.geosolutions.geonetwork.util.GNPrivConfiguration;
import it.geosolutions.geonetwork.util.GNSearchRequest;
import it.geosolutions.geonetwork.util.GNSearchResponse;

/**
 * 
 * @author DamianoG (damiano.giampaoli at geo-solutions.it)
 */
public class GN3Client extends GNAbstractClient {

    private final static Logger LOGGER = Logger.getLogger(GN3Client.class);

    public GN3Client(String serviceURL) {
        super(serviceURL);
        LOGGER.info("A Geonetwork3 client successfully instantiated!");
    }

    public GN3Client(String serviceURL, String username, String password) {
        super(serviceURL, username, password);
        LOGGER.info("A Geonetwork3 client successfully instantiated!");
    }
    
    @Override
    public boolean ping() {
        return GNInfo.ping(connection, gnServiceURL);
    }

    @Override
    public long insertMetadata(GNInsertConfiguration cfg, File metadataFile) throws GNLibException, GNServerException {
        return GNMetadataInsert.insertMetadata(connection, gnServiceURL, metadataFile, cfg);
    }

    @Override
    public long insertRequest(File requestFile) throws GNLibException, GNServerException {
        return GNMetadataInsert.insertRequest(connection, gnServiceURL, requestFile);
    }

    @Override
    public void setPrivileges(long metadataId, GNPrivConfiguration cfg) throws GNLibException, GNServerException {
        GN3MetadataAdmin.setPriv(connection, gnServiceURL, metadataId, cfg);
        
    }

    @Override
    public GNSearchResponse search(GNSearchRequest searchRequest) throws GNLibException, GNServerException {
        return GNMetadataSearch.search(connection, gnServiceURL, searchRequest);
    }

    @Override
    public GNSearchResponse search(File fileRequest) throws GNLibException, GNServerException {
        return GNMetadataSearch.search(connection, gnServiceURL, fileRequest);
    }

    @Override
    public Element get(Long id) throws GNLibException, GNServerException {
        return GNMetadataGet.get(connection, gnServiceURL, id);
    }

    @Override
    public Element get(String uuid) throws GNLibException, GNServerException {
        return GNMetadataGet.get(connection, gnServiceURL, uuid);
    }

    @Override
    public void deleteMetadata(long id) throws GNLibException, GNServerException {
        GNMetadataDelete.delete(connection, gnServiceURL, id);
        
    }

    @Override
    public void updateMetadata(long id, File metadataFile) throws GNLibException, GNServerException {
        GN3MetadataUpdate.update(connection, gnServiceURL, id, metadataFile);
    }

    @Override
    public void updateMetadata(long id, File metadataFile, String encoding) throws GNLibException, GNServerException {
        GN3MetadataUpdate.update(connection, gnServiceURL, id, metadataFile, encoding);
    }

    @Override
    public MetadataInfo getInfo(Long id) throws GNLibException, GNServerException {
        return GN3MetadataGetInfo.get(connection, gnServiceURL, id);
    }

    @Override
    public MetadataInfo getInfo(String uuid) throws GNLibException, GNServerException {
        return GN3MetadataGetInfo.get(connection, gnServiceURL, uuid);
    }
}
