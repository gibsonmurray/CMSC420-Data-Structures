package cmsc420_s23;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Comparator;

/*
 * AN ADDITIONAL CHALLENGE IS SORT ONLY ONCE ON RESTRUCTURE
 * (LIKELY A TRAVERSAL THING BUT IDK)
 */

public class SMkdTree<LPoint extends LabeledPoint2D> {

	/* ----------------------- PART I: ----------------------- */

	int rebuildOffset;
	Rectangle2D rootCell;
	Node root;

	private abstract class Node {
		abstract LPoint find();
		abstract Node insert();
		abstract Node delete();
	}

	private class InternalNode extends Node {
		int cutDim; // cutting dimension
		double cutVal; // the cutting value
		Node left, right; // children

		private InternalNode(int cutDim, double cutVal) {
			this.cutDim = cutDim;
			this.cutVal = cutVal;
			this.left = null;
			this.right = null;
		}

		@Override
		LPoint find() {
			
		}

		@Override
		SMkdTree<LPoint>.Node insert() {
			
		}

		@Override
		SMkdTree<LPoint>.Node delete() {
			
		}
		
	}

	private class ExternalNode extends Node {
		LPoint point; // the point (null if empty)
		
		private ExternalNode(LPoint point) {
			this.point = point;
		}

		@Override
		LPoint find() {
			
		}

		@Override
		SMkdTree<LPoint>.Node insert() {
			
		}

		@Override
		SMkdTree<LPoint>.Node delete() {
			
		}
	}

	private class ByXThenY implements Comparator<LPoint> {
		public int compare(LPoint pt1, LPoint pt2) {
			/* compare pt1 and pt2 lexicographically by x then y */
			return 0;
		}
	}

	private class ByYThenX implements Comparator<LPoint> {
		public int compare(LPoint pt1, LPoint pt2) {
			/* compare pt1 and pt2 lexicographically by y then x */
			return 0;
		}
	}

	public SMkdTree(int rebuildOffset, Rectangle2D rootCell) {
		
	}

	public int size() {
		return 0;
	}

	public LPoint find(Point2D q) {
		return null;
	}

	private Node bulkCreate(ArrayList<LPoint> pts, Rectangle2D cell) {
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
