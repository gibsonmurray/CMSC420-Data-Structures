package cmsc420_s23;

import java.util.ArrayList;

public class FarthestFirst<LPoint extends LabeledPoint2D> {

	public FarthestFirst(int rebuildOffset, Rectangle2D bbox, LPoint startCenter) { /* ... */ }
	public void addSite(LPoint site) throws Exception { /* ... */ }
	public LPoint extractNext() { /* ... */ return null; }
	public int sitesSize() { /* ... */ return 0; }
	public int traversalSize() { /* ... */ return 0; }
	public void clear() { /* ... */ }
	public ArrayList<String> listKdWithCenters() { /* ... */ return null; }
	public ArrayList<LPoint> getTraversal() { /* ... */ return null; }
	public ArrayList<String> listCenters() { /* ... */ return null; }
	public ArrayList<String> listAssignments() { /* ... */ return null; }
}
