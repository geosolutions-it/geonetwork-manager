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


/*
 * http://geonetwork-opensource.org/latest/developers/xml_services/metadata_xml_services.html#insert-metadata-metadata-insert
 *
    data: (mandatory) Contains the metadata record
    group (mandatory): Owner group identifier for metadata
    isTemplate: indicates if the metadata content is a new template or not. Default value: "n"
    title: Metadata title. Only required if isTemplate = "y"
    category (mandatory): Metadata category. Use "_none_" value to don’t assign any category
    styleSheet (mandatory): Stylesheet name to transform the metadata before inserting in the catalog. Use "_none_" value to don’t apply any stylesheet
    validate: Indicates if the metadata should be validated before inserting in the catalog. Values: on, off (default)
 * 
 * @author ETj (etj at geo-solutions.it)
 */
public class GNInsertConfiguration {
    
    /**
     * group (mandatory): Owner group identifier for metadata
     */    
    private String group;
    /**
     * category (mandatory): Metadata category. Use "_none_" value to don’t assign any category
     */    
    private String category;
    /**
     * styleSheet (mandatory): Stylesheet name to transform the metadata before inserting in the catalog. Use "_none_" value to don’t apply any stylesheet
     */    
    private String styleSheet;
    /**
     * validate: Indicates if the metadata should be validated before inserting in the catalog. Values: on, off (default)    
     */    
    private Boolean validate;
    
    private String encoding = "UTF-8";

    public GNInsertConfiguration() {
    }

    public GNInsertConfiguration(String group, String category, String styleSheet, Boolean validate) {
        this.group = group;
        this.category = category;
        this.styleSheet = styleSheet;
        this.validate = validate;
    }
    
    public GNInsertConfiguration(String group, String category, String styleSheet, Boolean validate, String encoding) {
        this.group = group;
        this.category = category;
        this.styleSheet = styleSheet;
        this.validate = validate;
        this.encoding = encoding;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getStyleSheet() {
        return styleSheet;
    }

    public void setStyleSheet(String styleSheet) {
        this.styleSheet = styleSheet;
    }

    public Boolean getValidate() {
        return validate;
    }

    public void setValidate(Boolean validate) {
        this.validate = validate;
    }

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}    
    
    
}
