package cmsc420_s23;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;

public class SMkdTree<LPoint extends LabeledPoint2D> {

	/* ----------------------- PART I: ----------------------- */

	private abstract class Node {
		LinkedList<LPoint> contenders;
		Rectangle2D subCell;

		abstract Node find(Point2D pt);

		abstract Node insert(LPoint pt, Rectangle2D cell, LinkedList<LPoint> contenders) throws Exception;

		abstract Node delete(Point2D pt) throws Exception;

		abstract Node restructure(LPoint pt);

		abstract LPoint nearestNeighbor(ArrayList<LPoint> ans, Point2D center, Rectangle2D cell, LPoint best);

		abstract void addCenter(LPoint center);

	}

	/* --------------------- INTERNAL NODE --------------------- */

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
			this.size = 0;
			this.insertionCount = 0;
			this.contenders = new LinkedList<LPoint>();
		}

		@Override
		Node find(Point2D pt) {
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
		SMkdTree<LPoint>.Node insert(LPoint pt, Rectangle2D cell, LinkedList<LPoint> contenders) throws Exception {
			if (this.subCell == null) {
				this.subCell = cell;
			}

			if (cutDim == 0) { // x-split
				if (pt.getX() < cutVal) {
					Rectangle2D rect = cell.leftPart(cutDim, cutVal);
					this.left = this.left.insert(pt, rect, this.contenders);
				} else {
					Rectangle2D rect = cell.rightPart(cutDim, cutVal);
					this.right = this.right.insert(pt, rect, this.contenders);
				}
			} else { // y-split
				if (pt.getY() < cutVal) {
					Rectangle2D rect = cell.leftPart(cutDim, cutVal);
					this.left = this.left.insert(pt, rect, this.contenders);
				} else {
					Rectangle2D rect = cell.rightPart(cutDim, cutVal);
					this.right = this.right.insert(pt, rect, this.contenders);
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
				LinkedList<LPoint> contenders = new LinkedList<LPoint>(this.contenders);
				Node newNode = bulkCreate(list, this.subCell, contenders);
				return newNode;
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

		@Override
		void addCenter(LPoint center) {
			this.contenders.add(center);
			filterContenders(this);
			if (this.contenders.contains(center)) {
				this.left.addCenter(center);
				this.right.addCenter(center);
			}
		}
	}

	/* --------------------- EXTERNAL NODE --------------------- */

	private class ExternalNode extends Node {
		LPoint point; // the point (null if empty)

		private ExternalNode(LPoint point, Rectangle2D subCell) {
			this.point = point;
			this.subCell = subCell;
			this.contenders = new LinkedList<LPoint>();
		}

		@Override
		Node find(Point2D pt) {
			if (this.point == null) {
				return null;
			} else if (this.point.getPoint2D().equals(pt)) {
				return this;
			} else {
				return null;
			}
		}

		@Override
		SMkdTree<LPoint>.Node insert(LPoint pt, Rectangle2D cell, LinkedList<LPoint> contenders) throws Exception {
			if (this.point == null) {
				this.point = pt;
				this.contenders = new LinkedList<LPoint>(contenders);
				return this;
			} else if (this.point.getPoint2D().equals(pt.getPoint2D())) {
				throw new Exception("Insertion of duplicate point");
			} else {
				ArrayList<LPoint> list = new ArrayList<LPoint>();
				list.add(this.point);
				list.add(pt);
				Node newNode = bulkCreate(list, this.subCell, this.contenders);
				return newNode;
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

		@Override
		void addCenter(LPoint center) {
			this.contenders.add(center);
			filterContenders(this);
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

	private int rebuildOffset;
	private Rectangle2D rootCell;
	private Node root;
	private int size;
	private int deleteCount;
	private LPoint startCenter;

	public SMkdTree(int rebuildOffset, Rectangle2D rootCell, LPoint startCenter) {
		this.rebuildOffset = rebuildOffset;
		this.rootCell = rootCell;
		this.root = new ExternalNode(null, this.rootCell);
		this.root.contenders.add(startCenter);
		this.startCenter = startCenter;
		this.size = 0;
		this.deleteCount = 0;
	}

	public int size() {
		return this.size;
	}

	public Node find(Point2D q) {
		return this.root.find(q);
	}

	private Node bulkCreate(ArrayList<LPoint> pts, Rectangle2D cell, LinkedList<LPoint> contenders) {
		int len = pts.size();
		if (len == 0) {
			ExternalNode newNode = new ExternalNode(null, cell);
			newNode.contenders = new LinkedList<LPoint>(contenders);
			return newNode;

		} else if (len == 1) {
			ExternalNode newNode = new ExternalNode(pts.get(0), cell);
			newNode.contenders = new LinkedList<LPoint>(contenders);
			filterContenders(newNode);
			return newNode;
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
			return bulkCreate(pts, cell, len, splitIndex, cutDim, cutVal, contenders);
		}
	}

	private Node bulkCreate(ArrayList<LPoint> pts, Rectangle2D cell, int len, int splitIndex, int cutDim,
			double cutVal, LinkedList<LPoint> contenders) {

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

		Rectangle2D leftRect = cell.leftPart(cutDim, cutVal);
		Rectangle2D rightRect = cell.rightPart(cutDim, cutVal);

		Node leftNode = bulkCreate(leftList, leftRect, contenders);
		Node rightNode = bulkCreate(rightList, rightRect, contenders);

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

		node.contenders = new LinkedList<LPoint>(contenders);
		node.subCell = cell;
		filterContenders(node);
		return node;
	}

	public void insert(LPoint pt) throws Exception {
		if (pt.getX() < this.rootCell.low.getX() || pt.getX() > this.rootCell.high.getX()
				|| pt.getY() < this.rootCell.low.getY() || pt.getY() > this.rootCell.high.getY()) {
			throw new Exception("Attempt to insert a point outside bounding box");
		}

		this.root = root.insert(pt, rootCell, this.root.contenders);
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

	public void traverseTree(Node curr, ArrayList<AssignedPair<LPoint>> list) {
		if (curr.getClass() == InternalNode.class) {
			InternalNode node = (InternalNode) curr;
			traverseTree(node.right, list);
			traverseTree(node.left, list);
		} else {
			ExternalNode node = (ExternalNode) curr;
			if (node.point != null) {
				AssignedPair<LPoint> newPair = getPair(node.point);
				list.add(newPair);
			}
		}
	}

	public Node getRoot() {
		return this.root;
	}

	public void clear() {
		this.root = new ExternalNode(null, this.rootCell);
		this.root.contenders = new LinkedList<LPoint>();
		this.root.contenders.add(this.startCenter);
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
			this.root = bulkCreate(list, rootCell, this.root.contenders);
			this.deleteCount = 0;
		}
	}

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
	public void addCenter(LPoint center) {
		this.root.addCenter(center);
	}

	public ArrayList<String> listWithCenters() {
		return listWithCentersAux(new ArrayList<String>(), root);
	}

	private void filterContenders(Node node) {
		double rmin = Double.MAX_VALUE;
		for (LPoint c : node.contenders) {
			double ri = node.subCell.maxDistanceSq(c.getPoint2D());
			rmin = Math.min(rmin, ri);
		}

		LinkedList<LPoint> tempContenders = new LinkedList<LPoint>(node.contenders);
		for (LPoint c : tempContenders) {
			double ri = node.subCell.distanceSq(c.getPoint2D());
			if (ri > rmin) {
				node.contenders.remove(c);
			}
		}
	}

	private ArrayList<String> listWithCentersAux(ArrayList<String> ans, Node curr) {
		if (curr.getClass() == InternalNode.class) {
			InternalNode node = (InternalNode) curr;
			String str = "";
			if (node.cutDim == 0) {
				str += "(x=" + node.cutVal + ") ";
			} else {
				str += "(y=" + node.cutVal + ") ";
			}
			str += node.size + ":" + node.insertionCount + " => {";
			ArrayList<String> names = new ArrayList<String>();
			for (LPoint c : node.contenders) {
				names.add(c.getLabel());
			}
			Collections.sort(names);
			for (int i = 0; i < names.size(); i++) {
				if (i > 9) {
					str = str.substring(0, str.length() - 1);
					break;
				}
				str += names.get(i);
				if (i != names.size() - 1) {
					str += " ";
				}
			}
			if (names.size() > 10) {
				str += "...";
			}
			str += "}";
			ans.add(str);
			ans = listWithCentersAux(ans, node.right);
			ans = listWithCentersAux(ans, node.left);
		} else {
			ExternalNode node = (ExternalNode) curr;
			String str = "";
			if (node.point == null) {
				str += "[null] => {";
			} else {
				str += "[" + node.point.toString() + "] => {";
			}
			ArrayList<String> names = new ArrayList<String>();
			for (LPoint c : node.contenders) {
				names.add(c.getLabel());
			}
			Collections.sort(names);
			for (int i = 0; i < names.size(); i++) {
				if (i > 9) {
					str = str.substring(0, str.length() - 1);
					break;
				}
				str += names.get(i);
				if (i != names.size() - 1) {
					str += " ";
				}
			}
			if (names.size() > 10) {
				str += "...";
			}
			str += "}";
			ans.add(str);
		}
		return ans;
	}

	public AssignedPair<LPoint> getPair(LPoint pt) {
		ExternalNode node = (ExternalNode) find(pt.getPoint2D());
		AssignedPair<LPoint> newPair = new AssignedPair<LPoint>();
		newPair.setSite(node.point);
		HashMap<Double, LPoint> dict = new HashMap<Double, LPoint>();
		for (LPoint contender : node.contenders) {
			double distance = node.point.getPoint2D().distanceSq(contender.getPoint2D());
			if (dict.containsKey(distance)) {
				LPoint currCenter = dict.get(distance);
				ByXThenY comparator = new ByXThenY();
				int i = comparator.compare(contender, currCenter);
				if (i < 0) {
					dict.put(distance, contender);
				}
			} else {
				dict.put(distance, contender);
			}
		}
		double minDist = Collections.min(dict.keySet());
		newPair.setDist(minDist);
		newPair.setCenter(dict.get(minDist));
		return newPair;
	}
}
