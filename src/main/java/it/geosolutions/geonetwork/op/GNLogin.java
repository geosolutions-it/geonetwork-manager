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
package it.geosolutions.geonetwork.op;

import it.geosolutions.geonetwork.util.HTTPUtils;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.log4j.Logger;

import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * Perform a GN login.<br/>
 * GN auth is carried out via a JSESSIONID cookie returned by a successful login
 * call.<br/>
 *
 * <ul>
 * <li>Url: <tt>http://<i>server</i>:<i>port</i>/geonetwork/srv/en/xml.user.login</tt></li>
 * <li>Mime-type: <tt>application/xml</tt></li>
 * <li>Post request: <pre>{@code
 *   <?xml version="1.0" encoding="UTF-8"?>
 *   <request>
 *       <username>admin</username>
 *       <password>admin</password>
 *   </request>
 * }</pre></li>
 * </ul>
 * 
 * @author ETj (etj at geo-solutions.it)
 */
public class GNLogin {
    
    private final static Logger LOGGER = Logger.getLogger(GNLogin.class);

     /*
     * @return true if login was successful
     * 
     * @see <a href="http://geonetwork-opensource.org/manuals/trunk/developer/xml_services/login_xml_services.html#login-services" >GeoNetwork documentation about login</a>
     */
    public static boolean login(HTTPUtils connection, String serviceURL, String username, String password) {
        Element request = new Element("request");
        request.addContent(new Element("username").setText(username));
        request.addContent(new Element("password").setText(password));
        
        XMLOutputter outputter = new XMLOutputter(Format.getCompactFormat());
        String xml = outputter.outputString(request);
        
        String loginURL = serviceURL+"/srv/en/xml.user.login";
        String out = connection.postXml(loginURL, xml);
        
        return connection.getLastHttpStatus() == HttpStatus.SC_OK;
    }
}
