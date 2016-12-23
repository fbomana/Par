
package es.ait.par.gpx;

import java.util.List;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(name = "gpx", strict = false)
public class GpxType {

    @Attribute(name = "version")
    private String version;
    @Attribute(name = "creator")
    private String creator;
    @Element(name = "metadata", required = false)
    private MetadataType metadata;
    @ElementList(name = "wpt", entry="wpt", inline = true, required = false)
    private List<WptType> wpt;
    @ElementList(name = "rte", entry="rte", inline = true, required = false)
    private List<RteType> rte;
    @ElementList(name = "trk", entry="trk", inline = true, required = false)
    private List<TrkType> trk;
    @Element(name = "extensions", required = false)
    private ExtensionsType extensions;

    public String getVersion() {
        return this.version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getCreator() {
        return this.creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public MetadataType getMetadata() {
        return this.metadata;
    }

    public void setMetadata(MetadataType metadata) {
        this.metadata = metadata;
    }

    public List<WptType> getWpt() {
        return this.wpt;
    }

    public void setWpt(List<WptType> wpt) {
        this.wpt = wpt;
    }

    public List<RteType> getRte() {
        return this.rte;
    }

    public void setRte(List<RteType> rte) {
        this.rte = rte;
    }

    public List<TrkType> getTrk() {
        return this.trk;
    }

    public void setTrk(List<TrkType> trk) {
        this.trk = trk;
    }

    public ExtensionsType getExtensions() {
        return this.extensions;
    }

    public void setExtensions(ExtensionsType extensions) {
        this.extensions = extensions;
    }

}
