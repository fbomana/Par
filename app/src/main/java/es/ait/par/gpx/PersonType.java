
package es.ait.par.gpx;

import org.simpleframework.xml.Element;

public class PersonType {

    @Element(name = "name", required = false)
    private String name;
    @Element(name = "email", required = false)
    private EmailType email;
    @Element(name = "link", required = false)
    private LinkType link;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EmailType getEmail() {
        return this.email;
    }

    public void setEmail(EmailType email) {
        this.email = email;
    }

    public LinkType getLink() {
        return this.link;
    }

    public void setLink(LinkType link) {
        this.link = link;
    }

}
