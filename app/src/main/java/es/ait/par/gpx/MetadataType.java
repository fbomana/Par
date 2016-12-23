
package es.ait.par.gpx;

import java.util.List;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

public class MetadataType {

    @Element(name = "name", required = false)
    private String name;
    @Element(name = "desc", required = false)
    private String desc;
    @Element(name = "author", required = false)
    private PersonType author;
    @Element(name = "copyright", required = false)
    private CopyrightType copyright;
    @ElementList(name = "link", inline = true, required = false)
    private List<LinkType> link;
    @Element(name = "time", required = false)
    private String time;
    @Element(name = "keywords", required = false)
    private String keywords;
    @Element(name = "bounds", required = false)
    private BoundsType bounds;
    @Element(name = "extensions", required = false)
    private ExtensionsType extensions;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return this.desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public PersonType getAuthor() {
        return this.author;
    }

    public void setAuthor(PersonType author) {
        this.author = author;
    }

    public CopyrightType getCopyright() {
        return this.copyright;
    }

    public void setCopyright(CopyrightType copyright) {
        this.copyright = copyright;
    }

    public List<LinkType> getLink() {
        return this.link;
    }

    public void setLink(List<LinkType> link) {
        this.link = link;
    }

    public String getTime() {
        return this.time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getKeywords() {
        return this.keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public BoundsType getBounds() {
        return this.bounds;
    }

    public void setBounds(BoundsType bounds) {
        this.bounds = bounds;
    }

    public ExtensionsType getExtensions() {
        return this.extensions;
    }

    public void setExtensions(ExtensionsType extensions) {
        this.extensions = extensions;
    }

}
