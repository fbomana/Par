
package es.ait.par.gpx;

import java.net.URI;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

public class CopyrightType {

    @Attribute(name = "author")
    private String author;
    @Element(name = "year", required = false)
    private String year;
    @Element(name = "license", required = false)
    private URI license;

    public String getAuthor() {
        return this.author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getYear() {
        return this.year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public URI getLicense() {
        return this.license;
    }

    public void setLicense(URI license) {
        this.license = license;
    }

}
