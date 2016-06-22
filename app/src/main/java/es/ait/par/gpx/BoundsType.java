
package es.ait.par.gpx;

import org.simpleframework.xml.Attribute;

public class BoundsType {

    @Attribute(name = "minlat")
    private double minlat;
    @Attribute(name = "minlon")
    private double minlon;
    @Attribute(name = "maxlat")
    private double maxlat;
    @Attribute(name = "maxlon")
    private double maxlon;

    public double getMinlat() {
        return this.minlat;
    }

    public void setMinlat(double minlat) {
        this.minlat = minlat;
    }

    public double getMinlon() {
        return this.minlon;
    }

    public void setMinlon(double minlon) {
        this.minlon = minlon;
    }

    public double getMaxlat() {
        return this.maxlat;
    }

    public void setMaxlat(double maxlat) {
        this.maxlat = maxlat;
    }

    public double getMaxlon() {
        return this.maxlon;
    }

    public void setMaxlon(double maxlon) {
        this.maxlon = maxlon;
    }

}
