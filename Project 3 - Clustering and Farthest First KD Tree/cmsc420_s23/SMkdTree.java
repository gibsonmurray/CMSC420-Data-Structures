package cmsc420_s23;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Comparator;

public class SMkdTree<LPoint extends LabeledPoint2D> {

	/* ----------------------- PART I: ----------------------- */

	int rebuildOffset;
	Rectangle2D rootCell;
	Node root;
	int size;
	int deleteCount;

	private abstract class Node {
		abstract LPoint find(Point2D pt);

		abstract Node insert(LPoint pt, Rectangle2D cell) throws Exception;

		abstract Node delete(Point2D pt) throws Exception;

		abstract Node restructure(LPoint pt);

		abstract LPoint nearestNeighbor(ArrayList<LPoint> ans, Point2D center, Rectangle2D cell, LPoint best);

	}

	/* --------------------- INTERNAL NODE --------------------- */

	private class InternalNode extends Node {
		int cutDim; // cutting dimension
		double cutVal; // the cutting value
		Node left, right; // children
		int size;
		int insertionCount;
		Rectangle2D subCell;

		private InternalNode(int cutDim, double cutVal, Node left, Node right) {
			this.cutDim = cutDim;
			this.cutVal = cutVal;
			this.left = left;
			this.right = right;
			this.size = 0;
			this.insertionCount = 0;
		}

		@Override
		LPoint find(Point2D pt) {
			if (cutDim == 0) { // x-split
				if (pt.getX() < cutVal) {
					return this.left.find(pt);
				} else {
					return this.right.find(pt);
				}
			} else { // y-split
				if (pt.getY() < cutVal) {
					return this.left.find(pt);
				} else {
					return this.right.find(pt);
				}
			}
		}

		@Override
		SMkdTree<LPoint>.Node insert(LPoint pt, Rectangle2D cell) throws Exception {
			this.subCell = cell;

			if (cutDim == 0) { // x-split
				if (pt.getX() < cutVal) {
					// adjust new cell "view"
					Point2D low = new Point2D(cell.low.getX(), cell.low.getY());
					Point2D high = new Point2D(cutVal, cell.high.getY()); // adjust rightmost cell's X
					Rectangle2D rect = new Rectangle2D(low, high);
					this.left = this.left.insert(pt, rect);
				} else {
					Point2D low = new Point2D(cutVal, cell.low.getY()); // adjust leftmost cell's X
					Point2D high = new Point2D(cell.high.getX(), cell.high.getY());
					Rectangle2D rect = new Rectangle2D(low, high);
					this.right = this.right.insert(pt, rect);
				}
			} else { // y-split
				if (pt.getY() < cutVal) {
					Point2D low = new Point2D(cell.low.getX(), cell.low.getY());
					Point2D high = new Point2D(cell.high.getX(), cutVal); // adjust rightmost cell's Y
					Rectangle2D rect = new Rectangle2D(low, high);
					this.left = this.left.insert(pt, rect);
				} else {
					Point2D low = new Point2D(cell.low.getX(), cutVal); // adjust leftmost cell's Y
					Point2D high = new Point2D(cell.high.getX(), cell.high.getY());
					Rectangle2D rect = new Rectangle2D(low, high);
					this.right = this.right.insert(pt, rect);
				}
			}
			this.size++;
			this.insertionCount++;
			return this;
		}

		@Override
		SMkdTree<LPoint>.Node restructure(LPoint pt) {
			if (this.insertionCount > (this.size + rebuildOffset) / 2) {
				ArrayList<LPoint> list = new ArrayList<LPoint>();
				traverse(this, list);
				return bulkCreate(list, this.subCell);
			} else {
				if (cutDim == 0) { // x-split
					if (pt.getX() < cutVal) {
						this.left = this.left.restructure(pt);
					} else {
						this.right = this.right.restructure(pt);
					}
				} else { // y-split
					if (pt.getY() < cutVal) {
						this.left = this.left.restructure(pt);
					} else {
						this.right = this.right.restructure(pt);
					}
				}
				return this;
			}
		}

		@Override
		SMkdTree<LPoint>.Node delete(Point2D pt) throws Exception {
			if (cutDim == 0) { // x-split
				if (pt.getX() < cutVal) {
					this.left = this.left.delete(pt);
				} else {
					this.right = this.right.delete(pt);
				}
			} else { // y-split
				if (pt.getY() < cutVal) {
					this.left = this.left.delete(pt);
				} else {
					this.right = this.right.delete(pt);
				}
			}
			this.size--;
			return this;
		}

		@Override
		LPoint nearestNeighbor(ArrayList<LPoint> ans, Point2D center, Rectangle2D cell, LPoint best) {
			int cd = this.cutDim;
			double cv = this.cutVal;
			Rectangle2D leftCell = cell.leftPart(cd, cv);
			Rectangle2D rightCell = cell.rightPart(cd, cv);

			if (center.get(cd) < cv) {
				best = this.left.nearestNeighbor(ans, center, leftCell, best);
				if (best == null || rightCell.distanceSq(center) <= center.distanceSq(best.getPoint2D())) {
					best = this.right.nearestNeighbor(ans, center, rightCell, best);
				}
			} else {
				best = this.right.nearestNeighbor(ans, center, rightCell, best);
				if (best == null || leftCell.distanceSq(center) <= center.distanceSq(best.getPoint2D())) {
					best = this.left.nearestNeighbor(ans, center, leftCell, best);
				}
			}
			return best;
		}
	}

	/* --------------------- EXTERNAL NODE --------------------- */

	private class ExternalNode extends Node {
		LPoint point; // the point (null if empty)

		private ExternalNode(LPoint point) {
			this.point = point;
		}

		@Override
		LPoint find(Point2D pt) {
			if (this.point == null) {
				return null;
			} else if (this.point.getPoint2D().equals(pt)) {
				return this.point;
			} else {
				return null;
			}
		}

		@Override
		SMkdTree<LPoint>.Node insert(LPoint pt, Rectangle2D cell) throws Exception {
			if (this.point == null) {
				this.point = pt;
				return this;
			} else if (this.point.getPoint2D().equals(pt.getPoint2D())) {
				throw new Exception("Insertion of duplicate point");
			} else {
				ArrayList<LPoint> list = new ArrayList<LPoint>();
				list.add(this.point);
				list.add(pt);
				return bulkCreate(list, cell);
			}
		}

		@Override
		SMkdTree<LPoint>.Node restructure(LPoint pt) {
			return this;
		}

		@Override
		SMkdTree<LPoint>.Node delete(Point2D pt) throws Exception {
			if (this.point == null || !this.point.getPoint2D().equals(pt)) {
				throw new Exception("Deletion of nonexistent point");
			}
			this.point = null;
			return this;
		}

		@Override
		LPoint nearestNeighbor(ArrayList<LPoint> ans, Point2D center, Rectangle2D cell, LPoint best) {
			if (this.point == null) {
				return best;
			}
			ByXThenY comp = new ByXThenY();
			if (best == null) {
				best = this.point;
			} else if (center.distanceSq(this.point.getPoint2D()) < center.distanceSq(best.getPoint2D())) {
				best = this.point;
			} else if (center.distanceSq(this.point.getPoint2D()) == center.distanceSq(best.getPoint2D())
					&& comp.compare(this.point, best) < 0) {
				best = this.point;
			}
			ans.add(this.point);
			return best;
		}
	}

	/* --------------------- COMPARATOR --------------------- */

	private class ByXThenY implements Comparator<LPoint> {
		public int compare(LPoint pt1, LPoint pt2) {
			double diffX = pt1.getX() - pt2.getX();
			double diffY;
			if (diffX == 0) {
				diffY = pt1.getY() - pt2.getY();
				if (diffY < 0) {
					return -1;
				} else if (diffY > 0) {
					return 1;
				} else {
					return 0;
				}
			} else if (diffX < 0) {
				return -1;
			} else {
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
				} else if (diffX > 0) {
					return 1;
				} else {
					return 0;
				}
			} else if (diffY < 0) {
				return -1;
			} else {
				return 1;
			}
		}
	}

	/* --------------------- SMKD TREE METHODS --------------------- */

	public SMkdTree(int rebuildOffset, Rectangle2D rootCell) {
		this.rebuildOffset = rebuildOffset;
		this.rootCell = rootCell;
		this.root = new ExternalNode(null);
		this.size = 0;
		this.deleteCount = 0;
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
		} else if (len == 1) {
			return new ExternalNode(pts.get(0));
		} else {
			double xLength = cell.high.getX() - cell.low.getX();
			double yLength = cell.high.getY() - cell.low.getY();
			boolean isXLess = xLength < yLength;
			int cutDim = isXLess ? 1 : 0;
			if (cutDim == 0) {
				Collections.sort(pts, new ByXThenY());
			} else {
				Collections.sort(pts, new ByYThenX());
			}

			// if two points are on the same line (and they are the only two) swap the cut
			// dimension
			if (cutDim == 0 && pts.get(0).getX() == pts.get(len - 1).getX()) {
				cutDim = 1;
			} else if (cutDim == 1 && pts.get(0).getY() == pts.get(len - 1).getY()) {
				cutDim = 0;
			}

			// set cut value based on dimension
			double cutVal = cutDim == 1 ? cell.high.getY() - (yLength / 2) : cell.high.getX() - (xLength / 2);
			int splitIndex = len;
			return bulkCreate(pts, cell, len, splitIndex, cutDim, cutVal);
		}
	}

	private Node bulkCreate(ArrayList<LPoint> pts, Rectangle2D cell, int len, int splitIndex, int cutDim,
			double cutVal) {

		// checks to see if we need to slide split
		for (int i = 0; i < len; i++) {
			if (pts.get(i).get(cutDim) >= cutVal) {
				splitIndex = i;
				break;
			}
		}

		if (splitIndex == 0) { // slides it to leftmost
			cutVal = pts.get(0).get(cutDim);
		}
		if (splitIndex == len) { // slides it to rightmost
			cutVal = pts.get(len - 1).get(cutDim);
		}

		ArrayList<LPoint> leftList = new ArrayList<LPoint>();
		ArrayList<LPoint> rightList = new ArrayList<LPoint>();

		// partitions left and right subtrees
		for (int i = 0; i < len; i++) {
			if (pts.get(i).get(cutDim) < cutVal) {
				leftList.add(pts.get(i));
			} else {
				rightList.add(pts.get(i));
			}
		}

		Rectangle2D leftRect = null;
		Rectangle2D rightRect = null;

		if (cutDim == 0) {
			// create new left cell
			Point2D leftLow = new Point2D(cell.low.getX(), cell.low.getY());
			Point2D leftHigh = new Point2D(cutVal, cell.high.getY()); // adjust leftmost cell's X
			leftRect = new Rectangle2D(leftLow, leftHigh);

			// create new right cell
			Point2D rightLow = new Point2D(cutVal, cell.low.getY()); // adjust rightmost cell's X
			Point2D rightHigh = new Point2D(cell.high.getX(), cell.high.getY());
			rightRect = new Rectangle2D(rightLow, rightHigh);
		} else {
			// create new left cell
			Point2D leftLow = new Point2D(cell.low.getX(), cell.low.getY());
			Point2D leftHigh = new Point2D(cell.high.getX(), cutVal); // adjust leftmost cell's Y
			leftRect = new Rectangle2D(leftLow, leftHigh);

			// create new right cell
			Point2D rightLow = new Point2D(cell.low.getX(), cutVal); // adjust rightmost cell's Y
			Point2D rightHigh = new Point2D(cell.high.getX(), cell.high.getY());
			rightRect = new Rectangle2D(rightLow, rightHigh);
		}

		Node leftNode = bulkCreate(leftList, leftRect);
		Node rightNode = bulkCreate(rightList, rightRect);

		// fix size
		InternalNode node = new InternalNode(cutDim, cutVal, leftNode, rightNode);
		if (leftNode.getClass() == InternalNode.class) {
			InternalNode temp = (InternalNode) leftNode;
			node.size += temp.size;
		} else if (leftNode.getClass() == ExternalNode.class) {
			ExternalNode temp = (ExternalNode) leftNode;
			if (temp.point != null) {
				node.size += 1;
			}
		}
		if (rightNode.getClass() == InternalNode.class) {
			InternalNode temp = (InternalNode) rightNode;
			node.size += temp.size;
		} else if (rightNode.getClass() == ExternalNode.class) {
			ExternalNode temp = (ExternalNode) rightNode;
			if (temp.point != null) {
				node.size += 1;
			}
		}
		return node;
	}

	public void insert(LPoint pt) throws Exception {
		if (pt.getX() < this.rootCell.low.getX() || pt.getX() > this.rootCell.high.getX()
				|| pt.getY() < this.rootCell.low.getY() || pt.getY() > this.rootCell.high.getY()) {
			throw new Exception("Attempt to insert a point outside bounding box");
		}

		this.root = root.insert(pt, rootCell);
		this.size++;

		this.root = root.restructure(pt);
	}

	private void traverse(Node curr, ArrayList<LPoint> list) {
		if (curr.getClass() == InternalNode.class) {
			InternalNode node = (InternalNode) curr;
			traverse(node.right, list);
			traverse(node.left, list);
		} else {
			ExternalNode node = (ExternalNode) curr;
			if (node.point != null) {
				list.add(node.point);
			}
		}
	}

	public void clear() {
		this.root = new ExternalNode(null);
		this.size = 0;
	}

	public ArrayList<String> list() {
		return listAux(new ArrayList<String>(), this.root);
	}

	private ArrayList<String> listAux(ArrayList<String> ans, Node curr) {
		if (curr.getClass() == InternalNode.class) {
			InternalNode node = (InternalNode) curr;
			if (node.cutDim == 0) {
				ans.add("(x=" + node.cutVal + ") " + node.size + ":" + node.insertionCount);
			} else {
				ans.add("(y=" + node.cutVal + ") " + node.size + ":" + node.insertionCount);
			}
			ans = listAux(ans, node.right);
			ans = listAux(ans, node.left);
		} else {
			ExternalNode node = (ExternalNode) curr;
			if (node.point != null) {
				ans.add("[" + node.point.toString() + "]");
			} else {
				ans.add("[null]");
			}
		}
		return ans;
	}

	/* ----------------------- PART II: ----------------------- */

	public int deleteCount() {
		return this.deleteCount;
	}

	public void delete(Point2D pt) throws Exception {
		this.root = this.root.delete(pt);
		this.deleteCount++;
		this.size--;
		if (this.deleteCount > this.size) {
			ArrayList<LPoint> list = new ArrayList<LPoint>();
			traverse(root, list);
			this.root = bulkCreate(list, rootCell);
			this.deleteCount = 0;
		}
	}

	/*
	 * Some Error in this method
	 */
	public LPoint nearestNeighbor(Point2D center) {
		return root.nearestNeighbor(new ArrayList<LPoint>(), center, rootCell, null);
	}

	public ArrayList<LPoint> nearestNeighborVisit(Point2D center) {
		ArrayList<LPoint> ans = new ArrayList<LPoint>();
		root.nearestNeighbor(ans, center, rootCell, null);
		Collections.sort(ans, new ByXThenY());
		return ans;
	}

	// ----------------------------------------------------------------
	// Possible new additions for Programming Assignment 3
	// You may modify/add/remove these as you see fit, since they will
	// just be used internally by your code.
	// ----------------------------------------------------------------
	// void addCenter(LPoint center) { /* ... */ return null; }
	// ArrayList<String> listWithCenters() { /* ... */ return null; }
}
