
package es.ait.par.gpx;

import java.net.URI;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

public class LinkType {

    @Attribute(name = "href")
    private URI href;
    @Element(name = "text", required = false)
    private String text;
    @Element(name = "type", required = false)
    private String type;

    public URI getHref() {
        return this.href;
    }

    public void setHref(URI href) {
        this.href = href;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
