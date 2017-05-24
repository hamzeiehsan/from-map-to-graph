package hamzei.ehsan.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Ehsan Hamzei on 5/11/2017.
 */
public class BoundingBox implements Serializable {
    private Point NW, SE;

    public BoundingBox(List<Point> points) {
        calculateBoundingBox(points);
    }

    public Point getSE() {
        return SE;
    }

    public void setSE(Point SE) {
        this.SE = SE;
    }

    public Point getNW() {
        return NW;
    }

    public void setNW(Point NW) {
        this.NW = NW;
    }

    private void calculateBoundingBox(List<Point> points) {
        Double minLat = null, maxLat = null, minLon = null, maxLon = null;
        for (Point p : points) {
            if (minLat == null || minLat > p.getLat())
                minLat = p.getLat();
            if (maxLat == null || maxLat < p.getLat())
                maxLat = p.getLat();
            if (minLon == null || minLon > p.getLon())
                minLon = p.getLon();
            if (maxLon == null || maxLon < p.getLon())
                maxLon = p.getLon();
        }
        this.setSE(new Point(minLat, minLon));
        this.setNW(new Point(maxLat, maxLon));
    }

    @Override
    public String toString() {
        return "BOUND {" + this.getSE() + "," + this.getNW() + "}";
    }
}