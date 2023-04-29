package cmsc420_s23;

import java.util.ArrayList;

public class ClusterAssignment<LPoint extends LabeledPoint2D> {
	
	// ------------------------------------------------------------------------
	// The following class is not required, but you may find it helpful. It
	// represents the triple (site, center, squared-distance). Feel free to
	// delete or modify.
	// ------------------------------------------------------------------------
	private class AssignedPair implements Comparable<AssignedPair> {
		private LPoint site; // a site
		private LPoint center; // its assigned center
		private double distanceSq; // the squared distance between them
		public int compareTo(AssignedPair o) { /* ... */ return 0; } // for sorting
	}

	public ClusterAssignment(int rebuildOffset, Rectangle2D bbox, LPoint startCenter) { /* ... */ }
	public void addSite(LPoint site) throws Exception { /* ... */ }
	public void deleteSite(LPoint site) throws Exception { /* ... */ }
	public void addCenter(LPoint center) throws Exception { /* ... */ }
	public int sitesSize() { /* ... */ return 0; }
	public int centersSize() { /* ... */ return 0; }
	public void clear() { /* ... */ }
	public ArrayList<String> listKdWithCenters() { /* ... */ return null; }
	public ArrayList<String> listCenters() { /* ... */ return null; }
	public ArrayList<String> listAssignments() { /* ... */ return null; }
}
