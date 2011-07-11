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

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;


/**
 * 
 * @author ETj (etj at geo-solutions.it)
 */
public class GNPrivConfiguration {
    

    /**
     * Operation privileges as required by GeoNetwork:
     * <UL>
     * <LI><TT>key</TT>: the id of the group these operations are related to.</LI>
     * <LI><TT>value</TT>: the set of operations allowed to this group for the inserted metadata.
     * <br/>Operations are defined as a string of digits, each representing a granted privilege: <UL>
     * <LI>0: view</LI>
     * <LI>1: download</LI>
     * <LI>2: editing</LI>
     * <LI>3: notify</LI>
     * <LI>4: dynamic</LI>
     * <LI>5: featured</LI>
       </UL></LI>
     * </UL>
     * e.g.:<br/>
     *  to assign the privileges "view" and "download" to group 5, this entry shall
     * be added to the Map:
     * <br/><pre>{@code         
     *      operations.put("5,"01"); }
     * </pre>
     */ 
//    private Map<Integer, String> privileges = new HashMap<Integer, String>();
    private List<Privileges> privileges = new ArrayList<Privileges>();
    

    public List<Privileges> getPrivileges() {
        return privileges;
    }

    protected void setPrivileges(List<Privileges> privileges) {
        this.privileges = privileges;
    }
    
    public void addPrivileges(Integer groupCode, String ops) {
        synchronized(this) {
            if(privileges == null)
                privileges = new ArrayList<Privileges>();
        }

        if(!ops.matches("0?1?2?3?4?5?")) {
            throw new IllegalArgumentException("Unrecognized privileges set '"+ops+"'");
        }
        
        privileges.add(new Privileges(groupCode, ops));
    }

    public void addPrivileges(Integer groupCode, EnumSet<GNPriv> privs) {
        synchronized(this) {
            if(privileges == null)
                privileges = new ArrayList<Privileges>();
        }

        StringBuilder sb = new StringBuilder();
        for (GNPriv priv : privs) {
            sb.append(priv.getId());
        }

        addPrivileges(groupCode, sb.toString());
    }
    
    static public class Privileges {
        Integer group;
        String ops;

        public Privileges(Integer group, String ops) {
            this.group = group;
            this.ops = ops;
        }

        public Integer getGroup() {
            return group;
        }

        public void setGroup(Integer group) {
            this.group = group;
        }

        public String getOps() {
            return ops;
        }

        public void setOps(String ops) {
            this.ops = ops;
        }
    }
    
}
