
package es.ait.par.gpx;

import android.location.Location;

import java.util.List;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

public class WptType {

    @Attribute(name = "lat")
    private double lat;
    @Attribute(name = "lon")
    private double lon;
    @Element(name = "ele", required = false)
    private double ele;
    @Element(name = "time", required = false)
    private String time;
    @Element(name = "magvar", required = false)
    private double magvar;
    @Element(name = "geoidheight", required = false)
    private double geoidheight;
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
    @Element(name = "sym", required = false)
    private String sym;
    @Element(name = "type", required = false)
    private String type;
    @Element(name = "fix", required = false)
    private FixType fix;
    @Element(name = "sat", required = false)
    private double sat;
    @Element(name = "hdop", required = false)
    private double hdop;
    @Element(name = "vdop", required = false)
    private double vdop;
    @Element(name = "pdop", required = false)
    private double pdop;
    @Element(name = "ageofdgpsdata", required = false)
    private double ageofdgpsdata;
    @Element(name = "dgpsid", required = false)
    private double dgpsid;
    @Element(name = "extensions", required = false)
    private ExtensionsType extensions;

    public double getLat() {
        return this.lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return this.lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getEle() {
        return this.ele;
    }

    public void setEle(double ele) {
        this.ele = ele;
    }

    public String getTime() {
        return this.time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public double getMagvar() {
        return this.magvar;
    }

    public void setMagvar(double magvar) {
        this.magvar = magvar;
    }

    public double getGeoidheight() {
        return this.geoidheight;
    }

    public void setGeoidheight(double geoidheight) {
        this.geoidheight = geoidheight;
    }

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

    public String getSym() {
        return this.sym;
    }

    public void setSym(String sym) {
        this.sym = sym;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public FixType getFix() {
        return this.fix;
    }

    public void setFix(FixType fix) {
        this.fix = fix;
    }

    public double getSat() {
        return this.sat;
    }

    public void setSat(double sat) {
        this.sat = sat;
    }

    public double getHdop() {
        return this.hdop;
    }

    public void setHdop(double hdop) {
        this.hdop = hdop;
    }

    public double getVdop() {
        return this.vdop;
    }

    public void setVdop(double vdop) {
        this.vdop = vdop;
    }

    public double getPdop() {
        return this.pdop;
    }

    public void setPdop(double pdop) {
        this.pdop = pdop;
    }

    public double getAgeofdgpsdata() {
        return this.ageofdgpsdata;
    }

    public void setAgeofdgpsdata(double ageofdgpsdata) {
        this.ageofdgpsdata = ageofdgpsdata;
    }

    public double getDgpsid() {
        return this.dgpsid;
    }

    public void setDgpsid(double dgpsid) {
        this.dgpsid = dgpsid;
    }

    public ExtensionsType getExtensions() {
        return this.extensions;
    }

    public void setExtensions(ExtensionsType extensions) {
        this.extensions = extensions;
    }

    public WptType()
    {
    }

    public WptType( Location location )
    {
        this.lat = location.getLatitude();
        this.lon = location.getLongitude();
        this.ele = location.getAltitude();
        this.geoidheight = location.getAltitude();
        this.time = new java.util.Date( location.getTime()).toString();
    }

}
