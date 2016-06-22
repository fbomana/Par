
package es.ait.par.gpx;

import java.util.List;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

public class TrkType {

    @Element(name = "name", required = false)
    private String name;
    @Element(name = "cmt", required = false)
    private String cmt;
    @Element(name = "desc", required = false)
    private String desc;
    @Element(name = "src", required = false)
    private String src;
    @ElementList(name = "link", inline = true, required = false)
    private List<LinkType> link;
    @Element(name = "number", required = false)
    private double number;
    @Element(name = "type", required = false)
    private String type;
    @Element(name = "extensions", required = false)
    private ExtensionsType extensions;
    @ElementList(name = "trkseg", inline = true, required = false)
    private List<TrksegType> trkseg;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCmt() {
        return this.cmt;
    }

    public void setCmt(String cmt) {
        this.cmt = cmt;
    }

    public String getDesc() {
        return this.desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getSrc() {
        return this.src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public List<LinkType> getLink() {
        return this.link;
    }

    public void setLink(List<LinkType> link) {
        this.link = link;
    }

    public double getNumber() {
        return this.number;
    }

    public void setNumber(double number) {
        this.number = number;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ExtensionsType getExtensions() {
        return this.extensions;
    }

    public void setExtensions(ExtensionsType extensions) {
        this.extensions = extensions;
    }

    public List<TrksegType> getTrkseg() {
        return this.trkseg;
    }

    public void setTrkseg(List<TrksegType> trkseg) {
        this.trkseg = trkseg;
    }

}
