/*
 *  GeoNetwork-Manager - Simple Manager Library for GeoNetwork
 *
 *  Copyright (C) 2007-2016 GeoSolutions S.A.S.
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

import it.geosolutions.geonetwork.op.gn2x.GNMetadataGet;
import it.geosolutions.geonetwork.op.gn2x.GNMetadataSearch;
import it.geosolutions.geonetwork.op.gn2x.GNMetadataAdmin;
import it.geosolutions.geonetwork.op.gn2x.GNMetadataUpdate;
import it.geosolutions.geonetwork.op.gn2x.GNInfo;
import it.geosolutions.geonetwork.op.gn2x.GNMetadataDelete;
import it.geosolutions.geonetwork.op.gn2x.GNMetadataInsert;
import it.geosolutions.geonetwork.op.gn2x.GNMetadataGetVersion;
import it.geosolutions.geonetwork.op.gn2x.GNLogin;
import java.io.File;

import org.apache.log4j.Logger;
import org.jdom.Element;

import it.geosolutions.geonetwork.exception.GNLibException;
import it.geosolutions.geonetwork.exception.GNServerException;
import it.geosolutions.geonetwork.op.gn2x.custom.GNMetadataGetInfo;
import it.geosolutions.geonetwork.op.gn3.GN3MetadataGetInfo;
import it.geosolutions.geonetwork.util.GNInsertConfiguration;
import it.geosolutions.geonetwork.util.GNPrivConfiguration;
import it.geosolutions.geonetwork.util.GNSearchRequest;
import it.geosolutions.geonetwork.util.GNSearchResponse;
import it.geosolutions.geonetwork.util.GNVersion;
import it.geosolutions.geonetwork.util.HTTPUtils;


/**
 *
 * @author ETj (etj at geo-solutions.it)
 * @author DamianoG (damiano.giampaoli at geo-solutions.it)
 */
abstract class GN2xClient extends GNAbstractClient
{
    private final static Logger LOGGER = Logger.getLogger(GN2xClient.class);

    private boolean loggedin = false;
    private final String username;
    private final String password;

    protected final GNVersion version;

    public GN2xClient(GNVersion v, String serviceURL) {
        this(v, serviceURL, null, null);
    }

    public GN2xClient(GNVersion v, String serviceURL, String username, String password) {
        super(serviceURL);
        this.username = username;
        this.password = password;
        this.version = v;

        LOGGER.info("A Geonetwork "+v+" client successfully instantiated!");
    }

    @Override
    public HTTPUtils getConnection() throws GNLibException {
        if( ! loggedin && username != null && password != null) {
            LOGGER.info("Logging into GeoNetwork with user '" + username + "'");
            if(GNLogin.get(version).login(connection, gnServiceURL, username, password)) {
                loggedin = true;
            } else {
                throw new GNLibException("Authentication error");
            }
        }

        return connection;
    }
    /* (non-Javadoc)
     * @see it.geosolutions.geonetwork.GNClient#ping()
     */
    @Override
    public boolean ping() {
        try {
            return GNInfo.get(version).ping(getConnection(), gnServiceURL);
        } catch (GNLibException ex) {
            LOGGER.warn("Error in ping: " + ex.getMessage());
            return false;
        }
    }

    /* (non-Javadoc)
     * @see it.geosolutions.geonetwork.GNClient#insertMetadata(it.geosolutions.geonetwork.util.GNInsertConfiguration, java.io.File)
     */
    @Override
    public long insertMetadata(GNInsertConfiguration cfg, File metadataFile) throws GNLibException, GNServerException {
        return GNMetadataInsert.get(version).insertMetadata(getConnection(), gnServiceURL, metadataFile, cfg);
    }

    /* (non-Javadoc)
     * @see it.geosolutions.geonetwork.GNClient#insertRequest(java.io.File)
     */
    @Override
    public long insertRequest(File requestFile) throws GNLibException, GNServerException {
        return GNMetadataInsert.get(version).insertRequest(getConnection(), gnServiceURL, requestFile);
    }

    /* (non-Javadoc)
     * @see it.geosolutions.geonetwork.GNClient#setPrivileges(long, it.geosolutions.geonetwork.util.GNPrivConfiguration)
     */
    @Override
    public void setPrivileges(long metadataId, GNPrivConfiguration cfg) throws GNLibException, GNServerException {
        GNMetadataAdmin.get(version).setPriv(getConnection(), gnServiceURL, metadataId, cfg);
    }

    /* (non-Javadoc)
     * @see it.geosolutions.geonetwork.GNClient#search(it.geosolutions.geonetwork.util.GNSearchRequest)
     */
    @Override
    public GNSearchResponse search(GNSearchRequest searchRequest) throws GNLibException, GNServerException {
        return GNMetadataSearch.get(version).search(getConnection(), gnServiceURL, searchRequest);
    }

    /* (non-Javadoc)
     * @see it.geosolutions.geonetwork.GNClient#search(java.io.File)
     */
    @Override
    public GNSearchResponse search(File fileRequest) throws GNLibException, GNServerException {
        return GNMetadataSearch.get(version).search(getConnection(), gnServiceURL, fileRequest);
    }

    /* (non-Javadoc)
     * @see it.geosolutions.geonetwork.GNClient#get(java.lang.Long)
     */
    @Override
    public Element get(Long id) throws GNLibException, GNServerException {
        return GNMetadataGet.get(version).get(getConnection(), gnServiceURL, id);
    }

    /* (non-Javadoc)
     * @see it.geosolutions.geonetwork.GNClient#get(java.lang.String)
     */
    @Override
    public Element get(String uuid) throws GNLibException, GNServerException {
        return GNMetadataGet.get(version).get(getConnection(), gnServiceURL, uuid);
    }

    /* (non-Javadoc)
     * @see it.geosolutions.geonetwork.GNClient#deleteMetadata(long)
     */
    @Override
    public void deleteMetadata(long id) throws GNLibException, GNServerException {
        GNMetadataDelete.get(version).delete(getConnection(), gnServiceURL, id);
    }

    /* (non-Javadoc)
     * @see it.geosolutions.geonetwork.GNClient#updateMetadata(long, java.io.File)
     */
    @Override
    public void updateMetadata(long id, File metadataFile) throws GNLibException, GNServerException {
        String mdversion = GNMetadataGetVersion.get(version).get(connection, gnServiceURL, id);
        GNMetadataUpdate.get(version).update(getConnection(), gnServiceURL, id, mdversion, metadataFile);
    }

    @Override
    public void updateMetadata(long id, File metadataFile, String encoding) throws GNLibException, GNServerException {
        String mdversion = GNMetadataGetVersion.get(version).get(connection, gnServiceURL, id);
        GNMetadataUpdate.get(version).update(getConnection(), gnServiceURL, id, mdversion, metadataFile, encoding);
    }

    @Override
    public GN3MetadataGetInfo.MetadataInfo getInfo(Long id)
            throws GNLibException, GNServerException {
        throw new UnsupportedOperationException("Operation supported only by GeoNetwork 3");
    }

    @Override
    public GN3MetadataGetInfo.MetadataInfo getInfo(String uuid)
            throws GNLibException, GNServerException {
        throw new UnsupportedOperationException("Operation supported only by GeoNetwork 3");
    }

    /** *****************************
     *   *** These operations are kept for backward compatibility with the previous version (GN2 only)
     ** *****************************/

    /**
     * Update the metadata accepting as parameter the metadata version without use internally GNMetadataGetVersion
     */
    public void updateMetadata(long id, int mdversion, File metadataFile) throws GNLibException, GNServerException {
        GNMetadataUpdate.get(version).update(getConnection(), gnServiceURL, id, Integer.toString(mdversion), metadataFile);
    }

    /**
     *
     * Update the metadata accepting as parameter the metadata version without use internally GNMetadataGetVersion
     */
    public void updateMetadata(long id, int mdversion, File metadataFile, String encoding) throws GNLibException, GNServerException {
        GNMetadataUpdate.get(version).update(getConnection(), gnServiceURL, id, Integer.toString(mdversion), metadataFile, encoding);
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
        return GNMetadataGetInfo.get(getConnection(), gnServiceURL, id, forUpdate);
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
        return GNMetadataGetInfo.get(getConnection(), gnServiceURL, uuid, forUpdate);
    }

}
