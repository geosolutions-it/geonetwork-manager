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
package it.geosolutions.geonetwork;

import java.io.File;

import org.apache.log4j.Logger;
import org.jdom.Element;

import it.geosolutions.geonetwork.exception.GNLibException;
import it.geosolutions.geonetwork.exception.GNServerException;
import it.geosolutions.geonetwork.op.gn210.GNInfo;
import it.geosolutions.geonetwork.op.gn210.GNMetadataAdmin;
import it.geosolutions.geonetwork.op.gn210.GNMetadataDelete;
import it.geosolutions.geonetwork.op.gn210.GNMetadataGet;
import it.geosolutions.geonetwork.op.gn210.GNMetadataGetInfo;
import it.geosolutions.geonetwork.op.gn210.GNMetadataGetVersion;
import it.geosolutions.geonetwork.op.gn210.GNMetadataInsert;
import it.geosolutions.geonetwork.op.gn210.GNMetadataSearch;
import it.geosolutions.geonetwork.op.gn210.GNMetadataUpdate;
import it.geosolutions.geonetwork.op.gn3.GN3MetadataGetInfo;
import it.geosolutions.geonetwork.util.GNInsertConfiguration;
import it.geosolutions.geonetwork.util.GNPrivConfiguration;
import it.geosolutions.geonetwork.util.GNSearchRequest;
import it.geosolutions.geonetwork.util.GNSearchResponse;


/**
 * 
 * @author ETj (etj at geo-solutions.it)
 * @author DamianoG (damiano.giampaoli at geo-solutions.it)
 */
public class GN210Client extends GNAbstractClient {
        
    private final static Logger LOGGER = Logger.getLogger(GN210Client.class);

    public GN210Client(String serviceURL) {
        super(serviceURL);
        LOGGER.info("A Geonetwork2 client successfully instantiated!");
    }

    public GN210Client(String serviceURL, String username, String password) {
        super(serviceURL, username, password);
        LOGGER.info("A Geonetwork2 client successfully instantiated!");
    }

    /* (non-Javadoc)
     * @see it.geosolutions.geonetwork.GNClient#ping()
     */
    @Override
    public boolean ping() {
        return GNInfo.ping(connection, gnServiceURL);
    }

    /* (non-Javadoc)
     * @see it.geosolutions.geonetwork.GNClient#insertMetadata(it.geosolutions.geonetwork.util.GNInsertConfiguration, java.io.File)
     */
    @Override
    public long insertMetadata(GNInsertConfiguration cfg, File metadataFile) throws GNLibException, GNServerException {
        return GNMetadataInsert.insertMetadata(connection, gnServiceURL, metadataFile, cfg);
    }

    /* (non-Javadoc)
     * @see it.geosolutions.geonetwork.GNClient#insertRequest(java.io.File)
     */
    @Override
    public long insertRequest(File requestFile) throws GNLibException, GNServerException {
        return GNMetadataInsert.insertRequest(connection, gnServiceURL, requestFile);
    }

    /* (non-Javadoc)
     * @see it.geosolutions.geonetwork.GNClient#setPrivileges(long, it.geosolutions.geonetwork.util.GNPrivConfiguration)
     */
    @Override
    public void setPrivileges(long metadataId, GNPrivConfiguration cfg) throws GNLibException, GNServerException {
        GNMetadataAdmin.setPriv(connection, gnServiceURL, metadataId, cfg);
    }

    /* (non-Javadoc)
     * @see it.geosolutions.geonetwork.GNClient#search(it.geosolutions.geonetwork.util.GNSearchRequest)
     */
    @Override
    public GNSearchResponse search(GNSearchRequest searchRequest) throws GNLibException, GNServerException {
        return GNMetadataSearch.search(connection, gnServiceURL, searchRequest);
    }

    /* (non-Javadoc)
     * @see it.geosolutions.geonetwork.GNClient#search(java.io.File)
     */
    @Override
    public GNSearchResponse search(File fileRequest) throws GNLibException, GNServerException {
        return GNMetadataSearch.search(connection, gnServiceURL, fileRequest);
    }

    /* (non-Javadoc)
     * @see it.geosolutions.geonetwork.GNClient#get(java.lang.Long)
     */
    @Override
    public Element get(Long id) throws GNLibException, GNServerException {
        return GNMetadataGet.get(connection, gnServiceURL, id);
    }

    /* (non-Javadoc)
     * @see it.geosolutions.geonetwork.GNClient#get(java.lang.String)
     */
    @Override
    public Element get(String uuid) throws GNLibException, GNServerException {
        return GNMetadataGet.get(connection, gnServiceURL, uuid);
    }

    /* (non-Javadoc)
     * @see it.geosolutions.geonetwork.GNClient#deleteMetadata(long)
     */
    @Override
    public void deleteMetadata(long id) throws GNLibException, GNServerException {
        GNMetadataDelete.delete(connection, gnServiceURL, id);
    }
    
    /* (non-Javadoc)
     * @see it.geosolutions.geonetwork.GNClient#updateMetadata(long, java.io.File)
     */
    @Override
    public void updateMetadata(long id, File metadataFile) throws GNLibException, GNServerException {
        String version = GNMetadataGetVersion.get(connection, gnServiceURL, id);
        GNMetadataUpdate.update(connection, gnServiceURL, id, version, metadataFile);
    }

    @Override
    public void updateMetadata(long id, File metadataFile, String encoding) throws GNLibException, GNServerException {
        String version = GNMetadataGetVersion.get(connection, gnServiceURL, id);
        GNMetadataUpdate.update(connection, gnServiceURL, id, version, metadataFile, encoding);
    }

    @Override
    public GN3MetadataGetInfo.MetadataInfo getInfo(Long id)
            throws GNLibException, GNServerException {
        throw new UnsupportedOperationException("Operation supported only for GeoNetwork 3");
    }

    @Override
    public GN3MetadataGetInfo.MetadataInfo getInfo(String uuid)
            throws GNLibException, GNServerException {
        throw new UnsupportedOperationException("Operation supported only for GeoNetwork 3");
    }
    
    /** *****************************
     *   *** These operations are kept for backward compatibility with the previous version (GN2 only)
     ** *****************************/
    
    /**
     * Update the metadata accepting as parameter the metadata version without use internally GNMetadataGetVersion
     */
    public void updateMetadata(long id, int version, File metadataFile) throws GNLibException, GNServerException {
        GNMetadataUpdate.update(connection, gnServiceURL, id, Integer.toString(version), metadataFile);
    }
    
    /**
     * 
     * Update the metadata accepting as parameter the metadata version without use internally GNMetadataGetVersion
     */
    public void updateMetadata(long id, int version, File metadataFile, String encoding) throws GNLibException, GNServerException {
        GNMetadataUpdate.update(connection, gnServiceURL, id, Integer.toString(version), metadataFile, encoding);
    }
    
    /**
     * @deprecated
     * Use the getInfo methods from standard API
     * 
     * Uses the custom service xml.metadata.info.get.
     * See http://trac.osgeo.org/geonetwork/ticket/1062
     */
    @Deprecated
    public GNMetadataGetInfo.MetadataInfo getInfo(Long id, boolean forUpdate) throws GNLibException, GNServerException {
        return GNMetadataGetInfo.get(connection, gnServiceURL, id, forUpdate);
    }
    
    /**
     * @deprecated
     * Use the getInfo methods from standard API
     * 
     * Uses the custom service xml.metadata.info.get.
     * See http://trac.osgeo.org/geonetwork/ticket/1062
     */
    @Deprecated
    public GNMetadataGetInfo.MetadataInfo getInfo(String uuid, boolean forUpdate) throws GNLibException, GNServerException {
        return GNMetadataGetInfo.get(connection, gnServiceURL, uuid, forUpdate);
    }
    
    /**
     * @deprecated
     * No need for login anymore, just instantiate the proper client version providing the credentials to be authenticated 
     */
    @Deprecated
    public boolean login(String username, String password) {
        LOGGER.error("Login operation is no longer supported. Please use authenticated constructor");
        return false;
    }

    
}
