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
package it.geosolutions.geonetwork.io;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;

import it.geosolutions.geonetwork.GN210Client;
import it.geosolutions.geonetwork.exception.GNLibException;
import it.geosolutions.geonetwork.exception.GNServerException;
import it.geosolutions.geonetwork.util.GNInsertConfiguration;

/**
 * @author alessio.fabiani
 * 
 */
public class GN2MetadataPublisher {

    private final static Logger LOGGER = Logger.getLogger(GN2MetadataPublisher.class);

    /**
     * @param args
     * @throws IOException
     * @throws GNServerException
     * @throws GNLibException
     */
    public static void main(String[] args) throws IOException, GNLibException, GNServerException {

        if (args.length < 4)
            throw new IOException(
                    "Please provide input parameters (gnServiceURL, gnUsername, gnPassword, mdFile).");

        final String gnServiceURL = args[0]; // "http://localhost:8080/geonetwork";
        final String gnUsername = args[1]; // "admin";
        final String gnPassword = args[2]; // "admin";
        final String mdFile = args[3]; // "/your/file/path";

        LOGGER.info("Trying to ingest metadata " + mdFile + " to " + gnServiceURL + "["
                + gnUsername + "]");

        // Create a GeoNetwork client pointing to the GeoNetwork service
        GN210Client client = new GN210Client(gnServiceURL);

        // Perform a login into GN
        boolean logged = client.login(gnUsername, gnPassword);

        if (!logged) {
            throw new RuntimeException("Could not log in");
        }

        // Create a configuration for the metadata.
        // These params are the ones needed by the GN service.
        GNInsertConfiguration cfg = new GNInsertConfiguration();
        cfg.setCategory("datasets");
        cfg.setGroup("1"); // group 1 is usually "all"
        cfg.setStyleSheet("_none_");
        cfg.setValidate(Boolean.TRUE);

        File file = new File(mdFile);

        long id = client.insertMetadata(cfg, file);

        LOGGER.info("Metadata created with id " + id);
    }

}
