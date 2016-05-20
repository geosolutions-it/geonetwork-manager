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

import it.geosolutions.geonetwork.util.HTTPUtils;

/**
 * 
 * @author DamianoG (damiano.giampaoli at geo-solutions.it)
 */
public abstract class GNAbstractClient implements GNClient{

    // create stateful connection handler (we need the cookies)
    protected HTTPUtils connection;

    protected final String gnServiceURL;
    
    protected GNAbstractClient(String serviceURL){
        this.gnServiceURL = serviceURL;
        connection = new HTTPUtils();
    }
    
    public GNAbstractClient(String serviceURL, String username, String password) {
        this.gnServiceURL = serviceURL;
        connection = new HTTPUtils(username, password);
    }
    
    @Override
    public HTTPUtils getConnection() {
        return connection;
    }
}
