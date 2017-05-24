package hamzei.ehsan.model;

/**
 * Created by Ehsan Hamzei on 5/11/2017.
 */
public class Point {
    private double lon, lat;

    public Point(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    @Override
    public String toString() {
        return "POINT [" + lat + "," + lon + "] ";
    }
}
