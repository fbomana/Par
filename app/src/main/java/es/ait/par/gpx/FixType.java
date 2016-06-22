
package es.ait.par.gpx;


public enum FixType {

    FixType_none("none"),
    FixType_2d("2d"),
    FixType_3d("3d"),
    FixType_dgps("dgps"),
    FixType_pps("pps");
    private String value;

    private FixType(String value) {
        this.value = value;
    }

    public String toString() {
         return this.value;
    }

}
