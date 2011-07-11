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
package it.geosolutions.geonetwork.util;


/**
 * Operation privileges as required by GeoNetwork.
 * <br/>Operations are defined as a string of digits, each representing a granted privilege: <UL>
 * <LI>0: view</LI>
 * <LI>1: download</LI>
 * <LI>2: editing</LI>
 * <LI>3: notify</LI>
 * <LI>4: dynamic</LI>
 * <LI>5: featured</LI>
 * </UL>
 *
 * @author ETj (etj at geo-solutions.it)
 */
public enum GNPriv {
    VIEW(0),
    DOWNLOAD(1),
    EDITING(2),
    NOTIFY(3),
    DYNAMIC(4),
    FEATURED(5);

    private int id;

    private GNPriv(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static GNPriv get(int id) {
        for (GNPriv priv : GNPriv.values()) {
            if(priv.getId() == id)
                return priv;
        }
        return null;
    }
}
