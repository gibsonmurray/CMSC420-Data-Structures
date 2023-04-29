package cmsc420_s23;

import java.util.ArrayList;

public class SMkdTree<LPoint extends LabeledPoint2D> {
	SMkdTree(int rebuildOffset, Rectangle2D rootCell, LPoint startCenter) { /* ... */ }
	public void clear() { /* ... */ }
	public int size() { /* ... */  return 0; }
	public int deleteCount() { /* ... */  return 0; }
	public LPoint find(Point2D q) { /* ... */ return null; }
	public void insert(LPoint pt) throws Exception { /* ... */ }
	public void delete(Point2D pt) throws Exception { /* ... */ }
	public ArrayList<String> list() { /* ... */ return null; }
	public LPoint nearestNeighbor(Point2D center) { /* ... */ return null; }
	public ArrayList<LPoint> nearestNeighborVisit(Point2D center) { /* ... */ return null; }
	
	// ----------------------------------------------------------------
	// Possible new additions for Programming Assignment 3
	// You may modify/add/remove these as you see fit, since they will 
	// just be used internally by your code.
	// ----------------------------------------------------------------
	// void addCenter(LPoint center) { /* ... */ return null; }
	// ArrayList<String> listWithCenters() { /* ... */ return null; }
}
