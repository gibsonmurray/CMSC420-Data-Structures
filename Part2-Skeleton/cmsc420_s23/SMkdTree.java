package cmsc420_s23;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

/*
 * AN ADDITIONAL CHALLENGE IS SORT ONLY ONCE ON RESTRUCTURE
 * (LIKELY A TRAVERSAL THING BUT IDK)
 */

public class SMkdTree<LPoint extends LabeledPoint2D> {

	/* ----------------------- PART I: ----------------------- */

	public SMkdTree(int rebuildOffset, Rectangle2D rootCell) {

	}

	public int size() {
		return 0; 
	}

	public LPoint find(Point2D q) {
		return null; 
	}

	public void insert(LPoint pt) throws Exception {
		
	}

	public void clear() {
		
	}
	
	public ArrayList<String> list() {
		return null;
	}

	/* ----------------------- PART II: ----------------------- */

	public int deleteCount() {
		return 0;
	}

	public void delete(Point2D pt) throws Exception {
		
	}

	public LPoint nearestNeighbor(Point2D center) {
		return null;
	}

	public ArrayList<LPoint> nearestNeighborVisit(Point2D center) {
		return null;
	}

	/* ----------------------- CHALLENGE: ----------------------- */

	private class LPointIterator implements Iterator<LPoint> {
		public LPointIterator() {
			
		}

		public boolean hasNext() {
			return false;
		}

		public LPoint next() throws NoSuchElementException {
			return null; 
		}

	}

	public LPointIterator iterator() {
		return new LPointIterator();
	}
}

