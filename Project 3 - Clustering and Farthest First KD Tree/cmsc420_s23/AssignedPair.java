package cmsc420_s23;

public class AssignedPair<LPoint extends LabeledPoint2D> implements Comparable<AssignedPair<LPoint>> {
    private LPoint site; // a site
    private LPoint center; // its assigned center
    private double distanceSq; // the squared distance between them

    public void setSite(LPoint site) {
        this.site = site;
    }

    public void setCenter(LPoint center) {
        this.center = center;
    }

    public void setDist(double distanceSq) {
        this.distanceSq = distanceSq;
    }

    public LPoint getSite() {
        return this.site;
    }

    public LPoint getCenter() {
        return this.center;
    }

    public double getDist() {
        return this.distanceSq;
    }

    public int compareTo(AssignedPair<LPoint> o) { // for sorting
        if (this.distanceSq < o.distanceSq) {
            return -1;
        } else if (this.distanceSq > o.distanceSq) {
            return 1;
        } else {
            return this.site.getPoint2D().compareTo(o.site.getPoint2D());
        }
    }
}
