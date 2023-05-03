package cmsc420_s23;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ClusterAssignment<LPoint extends LabeledPoint2D> {

	private SMkdTree<LPoint> kdTree; // storage for the sites
	private ArrayList<LPoint> centers; // storage for the centers
	private LPoint startCenter;

	public ClusterAssignment(int rebuildOffset, Rectangle2D bbox, LPoint startCenter) {
		this.kdTree = new SMkdTree<LPoint>(rebuildOffset, bbox, startCenter);
		this.centers = new ArrayList<LPoint>();
		this.centers.add(startCenter);
		this.startCenter = startCenter;
	}
	
	public void addSite(LPoint site) throws Exception {
		this.kdTree.insert(site);
	}

	public void deleteSite(LPoint site) throws Exception {
		this.kdTree.delete(site.getPoint2D());
	}

	public void addCenter(LPoint center) throws Exception {
		this.centers.add(center);
		this.kdTree.addCenter(center);
	}

	public int sitesSize() {
		return this.kdTree.size();
	}

	public int centersSize() {
		return this.centers.size();
	}

	public void clear() {
		this.kdTree.clear();
		this.centers = new ArrayList<LPoint>();
		this.centers.add(this.startCenter);
	}

	public ArrayList<String> listKdWithCenters() {
		return this.kdTree.listWithCenters();
	}

	public ArrayList<String> listCenters() {
		ArrayList<String> ans = new ArrayList<String>();
		Collections.sort(centers, new ByNames());
		for (LPoint center : centers) {
			ans.add(center.getLabel() + ": " + center.getPoint2D().toString());
		}
		return ans;
	}

	public ArrayList<String> listAssignments() {
		ArrayList<AssignedPair<LPoint>> pairs = new ArrayList<AssignedPair<LPoint>>();
		this.kdTree.traverseTree(this.kdTree.getRoot(), pairs);
		Collections.sort(pairs);
		ArrayList<String> ans = new ArrayList<String>();
		for (AssignedPair<LPoint> pair : pairs) {
			String str = "[" + pair.getSite().getLabel() + "->" + pair.getCenter().getLabel() + "]"
							+ " distSq = " + pair.getDist();
			ans.add(str);
		}
		return ans;
	}

	private class ByNames implements Comparator<LPoint> {
		public int compare(LPoint pt1, LPoint pt2) {
			return pt1.getLabel().compareTo(pt2.getLabel());
		}
	}
}
