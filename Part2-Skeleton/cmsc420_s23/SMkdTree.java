package cmsc420_s23;

import java.util.ArrayList;
import java.util.Collections;
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
	int size;

	private abstract class Node {
		abstract LPoint find(Point2D pt);
		abstract Node insert(LPoint pt, Rectangle2D cell);
		abstract Node delete(Point2D pt);
	}

/* --------------------- INTERNAL NODE ---------------------*/

	private class InternalNode extends Node {
		int cutDim; // cutting dimension
		double cutVal; // the cutting value
		Node left, right; // children
		int size;
		int insertionCount;

		private InternalNode(int cutDim, double cutVal, Node left, Node right) {
			this.cutDim = cutDim;
			this.cutVal = cutVal;
			this.left = left;
			this.right = right;
			this.size = 2;
			this.insertionCount = 0;
		}

		@Override
		LPoint find(Point2D pt) {
			if (cutDim == 0) { // x-split
				if (pt.getX() < cutVal) {
					return this.left.find(pt);
				}
				else {
					return this.right.find(pt);
				}
			}
			else { // y-split
				if (pt.getY() < cutVal) {
					return this.left.find(pt);
				}
				else {
					return this.right.find(pt);
				}
			}
		}

		@Override
		SMkdTree<LPoint>.Node insert(LPoint pt, Rectangle2D cell) {
			this.size++;
			this.insertionCount++;

			if (cutDim == 0) { // x-split
				if (pt.getX() < cutVal) {
					// adjust new cell "view"
					Point2D low = new Point2D(cell.low.getX(), cell.low.getY());
					Point2D high = new Point2D(cutVal, cell.high.getY()); // adjust rightmost cell's X
					Rectangle2D rect = new Rectangle2D(low, high);
					return this.left.insert(pt, rect);
				}
				else {
					Point2D low = new Point2D(cutVal, cell.low.getY()); // adjust leftmost cell's X
					Point2D high = new Point2D(cell.high.getX(), cell.high.getY()); 
					Rectangle2D rect = new Rectangle2D(low, high);
					return this.right.insert(pt, rect);
				}
			}
			else { // y-split
				if (pt.getY() < cutVal) {
					Point2D low = new Point2D(cell.low.getX(), cell.low.getY());
					Point2D high = new Point2D(cell.high.getX(), cutVal); // adjust rightmost cell's Y
					Rectangle2D rect = new Rectangle2D(low, high);
					return this.left.insert(pt, rect);
				}
				else {
					Point2D low = new Point2D(cell.low.getX(), cutVal); // adjust leftmost cell's Y
					Point2D high = new Point2D(cell.high.getX(), cell.high.getY()); 
					Rectangle2D rect = new Rectangle2D(low, high);
					return this.right.insert(pt, rect);
				}
			}
		}

		@Override
		SMkdTree<LPoint>.Node delete(Point2D pt) {
			return null;
		}
		
	}

/* --------------------- EXTERNAL NODE ---------------------*/

	private class ExternalNode extends Node {
		LPoint point; // the point (null if empty)
		
		private ExternalNode(LPoint point) {
			this.point = point;
		}

		@Override
		LPoint find(Point2D pt) {
			if (this.point.getPoint2D().equals(pt)) {
				return this.point;
			}
			else {
				return null;
			}
		}

		@Override
		SMkdTree<LPoint>.Node insert(LPoint pt, Rectangle2D cell) {
			if (this.point == null) {
				this.point = pt;
				return this;
			}
			else {
				ArrayList<LPoint> list = new ArrayList<LPoint>();
				list.add(this.point);
				list.add(pt);
				return bulkCreate(list, cell);
			}
		}

		@Override
		SMkdTree<LPoint>.Node delete(Point2D pt) {
			return null; /* TODO */
		}
	}

/* --------------------- COMPARATOR ---------------------*/

	private class ByXThenY implements Comparator<LPoint> {
		public int compare(LPoint pt1, LPoint pt2) {
			double diffX = pt1.getX() - pt2.getX();
			double diffY;
			if (diffX == 0) {
				diffY = pt1.getY() - pt2.getY();
				if (diffY < 0) {
					return -1;
				}
				else if (diffY > 0) {
					return 1;
				}
				else {
					return 0;
				}
			}
			else if (diffX < 0) {
				return -1;
			}
			else {
				return 1;
			}
		}
	}

	private class ByYThenX implements Comparator<LPoint> {
		public int compare(LPoint pt1, LPoint pt2) {
			double diffX;
			double diffY = pt1.getY() - pt2.getY();
			if (diffY == 0) {
				diffX = pt1.getX() - pt2.getX();
				if (diffX < 0) {
					return -1;
				}
				else if (diffX > 0) {
					return 1;
				}
				else {
					return 0;
				}
			}
			else if (diffY < 0) {
				return -1;
			}
			else {
				return 1;
			}
		}
	}

/* --------------------- SMKD TREE METHODS ---------------------*/

	public SMkdTree(int rebuildOffset, Rectangle2D rootCell) {
		this.rebuildOffset = rebuildOffset;
		this.rootCell = rootCell;
		this.root = null;
		this.size = 0;
	}

	public int size() {
		return this.size;
	}

	public LPoint find(Point2D q) {
		return this.root.find(q);
	}

	private Node bulkCreate(ArrayList<LPoint> pts, Rectangle2D cell) {
		int len = pts.size();
		if (len == 0) {
			return new ExternalNode(null);
		}
		else if (len == 1) {
			return new ExternalNode(pts.get(0));
		}
		else {
			/*
			 * \get midpoint based on current cell & cut based on greatest side length
			 * select two points
			 * if both points are on one side of the midpoint, slide to closer points X/Y
			 * otherwise leave cut value the same
			 * create new cells& arrays and recursively call
			 */
			double xLength = cell.high.getX() - cell.low.getX();
			double yLength = cell.high.getY() - cell.low.getY();
			int cutDim = xLength < yLength ? 1 : 0;
			double cutVal = xLength < yLength ? yLength / 2 : xLength / 2;
			int splitIndex = len;
			if (cutDim == 0) { // x split
				Collections.sort(pts, new ByXThenY());
				for (int i = 0; i < len; i++) {
					if (pts.get(i).getX() > cutVal) {
						splitIndex = i;
						break;
					}
				}
				ArrayList<LPoint> leftList = new ArrayList<LPoint>(pts.subList(0, splitIndex));
				ArrayList<LPoint> rightList = new ArrayList<LPoint>(pts.subList(splitIndex, len));

				//Potential sliding midpoint solution
				if (splitIndex == 0) { // slides it to leftmost
					cutVal = pts.get(0).getX();
				}
				if (splitIndex == len) { //slides it to rightmost
					cutVal = pts.get(len - 1).getX();
				}

				// create new left cell
				Point2D leftLow = new Point2D(cell.low.getX(), cell.low.getY()); 
				Point2D leftHigh = new Point2D(cutVal, cell.high.getY()); // adjust leftmost cell's X
				Rectangle2D leftRect = new Rectangle2D(leftLow, leftHigh);

				//create new right cell
				Point2D rightLow = new Point2D(cutVal, cell.low.getY()); // adjust rightmost cell's X
				Point2D rightHigh = new Point2D(cell.high.getX(), cell.high.getY()); 
				Rectangle2D rightRect = new Rectangle2D(rightLow, rightHigh);

				Node leftNode = bulkCreate(leftList, leftRect);
				Node rightNode = bulkCreate(rightList, rightRect);
				return new InternalNode(cutDim, cutVal, leftNode, rightNode);
			}
			else { // y split
				Collections.sort(pts, new ByYThenX());
				for (int i = 0; i < len; i++) {
					if (pts.get(i).getY() > cutVal) {
						splitIndex = i;
						break;
					}
				}
				ArrayList<LPoint> bottomList = new ArrayList<LPoint>(pts.subList(0, splitIndex));
				ArrayList<LPoint> topList = new ArrayList<LPoint>(pts.subList(splitIndex, len));

				//Potential sliding midpoint solution
				if (splitIndex == 0) { // slides it to bottommost
					cutVal = pts.get(0).getY();
				}
				if (splitIndex == len) { //slides it to topmost
					cutVal = pts.get(len - 1).getY();
				}

				// create new bottom cell
				Point2D bottomLow = new Point2D(cell.low.getX(), cell.low.getY()); 
				Point2D bottomHigh = new Point2D(cell.high.getY(), cutVal); // adjust leftmost cell's X
				Rectangle2D bottomRect = new Rectangle2D(bottomLow, bottomHigh);

				//create new top cell
				Point2D topLow = new Point2D(cutVal, cell.low.getY()); // adjust rightmost cell's X
				Point2D topHigh = new Point2D(cell.high.getX(), cell.high.getY()); 
				Rectangle2D topRect = new Rectangle2D(topLow, topHigh);

				Node bottomNode = bulkCreate(bottomList, bottomRect);
				Node topNode = bulkCreate(topList, topRect);
				return new InternalNode(cutDim, cutVal, bottomNode, topNode);
			}
		}
	}

	public void insert(LPoint pt) throws Exception {
		if (pt.getX() < this.rootCell.low.getX() || pt.getX() > this.rootCell.high.getX()
			|| pt.getY() < this.rootCell.low.getY() || pt.getY() > this.rootCell.high.getY()) {
			throw new Exception("Attempt to insert a point outside bounding box");
		}
		if (find(pt.getPoint2D()) != null) {
			throw new Exception("Insertion of duplicate point");
		}
		this.root = root.insert(pt, rootCell);
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
