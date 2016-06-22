
package es.ait.par.gpx;

import org.simpleframework.xml.Attribute;

public class EmailType {

    @Attribute(name = "id")
    private String id;
    @Attribute(name = "domain")
    private String domain;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDomain() {
        return this.domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

}
