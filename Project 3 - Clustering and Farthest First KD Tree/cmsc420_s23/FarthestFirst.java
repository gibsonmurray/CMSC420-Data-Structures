package cmsc420_s23;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import cmsc420_s23.WtLeftHeap.Locator;

public class FarthestFirst<LPoint extends LabeledPoint2D> {

	private LPoint startCenter; // the initial center
	private WtLeftHeap<Double, AssignedPair> heap; // heap for assigned pairs
	private HashMap<String, Locator> map; // a map used for saving heap locators
	private SMkdTree<LPoint> kdTree; // kd-tree for sites
	private ArrayList<LPoint> traversal; // the traversal
	private HashMap<String, LPoint> points; // stores all the labeled points to rebuild heap

	public FarthestFirst(int rebuildOffset, Rectangle2D bbox, LPoint startCenter) {
		this.startCenter = startCenter;
		this.kdTree = new SMkdTree<LPoint>(rebuildOffset, bbox, startCenter);
		this.heap = new WtLeftHeap<Double, AssignedPair>();
		this.map = new HashMap<String, Locator>();
		this.traversal = new ArrayList<LPoint>();
		this.traversal.add(startCenter);
		this.points = new HashMap<String, LPoint>();
	}

	public void addSite(LPoint site) throws Exception {
		this.kdTree.insert(site);
		String name = insertMapHeap(site);
		this.points.put(name, site);
	}

	public LPoint extractNext() throws Exception {
		if (this.kdTree.size() == 0) {
			return null;
		}
		AssignedPair<LPoint> next = this.heap.extract();
		LPoint siteToCenter = next.getSite();
		this.kdTree.delete(siteToCenter.getPoint2D());
		this.kdTree.addCenter(siteToCenter);
		this.traversal.add(siteToCenter);
		this.points.remove(siteToCenter.getLabel());

		// redoes the map and heap to update values with new center
		this.heap.clear();
		this.map.clear();
		for (LPoint pt : points.values()) {
			insertMapHeap(pt);
		}

		return siteToCenter;
	}

	private String insertMapHeap(LPoint site) {
		AssignedPair<LPoint> pair = this.kdTree.getPair(site);
		Locator loc = this.heap.insert(pair.getDist(), pair);
		String name = site.getLabel();
		this.map.put(name, loc);
		return name;
	}

	public int sitesSize() {
		return this.kdTree.size();
	}

	public int traversalSize() {
		return this.traversal.size();
	}

	public void clear() {
		this.kdTree.clear();
		this.heap.clear();
		this.traversal.clear();
		this.map.clear();
	}

	public ArrayList<String> listKdWithCenters() {
		return this.kdTree.listWithCenters();
	}

	public ArrayList<LPoint> getTraversal() {
		return this.traversal;
	}

	public ArrayList<String> listCenters() {
		ArrayList<String> ans = new ArrayList<String>();
		for (LPoint center : this.traversal) {
			ans.add(center.getLabel() + ": " + center.getPoint2D().toString());
		}
		Collections.sort(ans);
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
}
