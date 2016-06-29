
package es.ait.par.gpx;

import android.location.Location;

import java.util.ArrayList;
import java.util.List;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

public class TrksegType {

    @ElementList(name = "trkpt", entry="trkpt", inline = true, required = false)
    private List<WptType> trkpt;
    @Element(name = "extensions", required = false)
    private ExtensionsType extensions;

    public List<WptType> getTrkpt() {
        return this.trkpt;
    }

    public void setTrkpt(List<WptType> trkpt) {
        this.trkpt = trkpt;
    }

    public ExtensionsType getExtensions() {
        return this.extensions;
    }

    public void setExtensions(ExtensionsType extensions) {
        this.extensions = extensions;
    }

    public void addPoint( Location location )
    {
        if ( trkpt == null )
        {
            trkpt = new ArrayList<WptType>();
        }
        trkpt.add( new WptType( location ));
    }


}
