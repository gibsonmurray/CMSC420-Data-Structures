package cmsc420_s23;

import java.util.ArrayList;

public class WtLeftHeap<Key extends Comparable<Key>, Value> {
	private Node root;
	private int size;

	private class Node {
		private Key k;
		private Value v;
		private Node left, right, parent;
		private int weight;

		private Node (Key k, Value v) {
			this.k = k;
			this.v = v;
			this.weight = 0;
			this.left = null;
			this.right = null;
			this.parent = null;
		}
	}
	
	public class Locator {
		private Node node;

		public Locator(Node n) {
			this.node = n;
		}
	}

	public WtLeftHeap() {
		this.root = null;
		this.size = 0;
	}
	
	public int size() {
		return size;
	}

	public void clear() {
		this.root = null;
		this.size = 0;
	}

	public Locator insert(Key x, Value v) {
		Node newNode = new Node(x, v);
		newNode.weight = 1;
		this.root = merge(this.root, newNode);
		this.size++;
		return new Locator(newNode);
	}

	public void mergeWith(WtLeftHeap<Key, Value> h2) {
		this.size += h2.size;
		this.root = merge(this.root, h2.root);
		this.root.parent = null;
		h2.root = null;
		h2.size = 0;
	}

	private Node merge(Node u, Node v) {
		if (u == null) return v;
		if (v == null) return u;
		if (u.k.compareTo(v.k) < 0){
			Node temp = new Node(u.k, u.v);
			temp.weight = u.weight;
			temp.left = u.left;
			temp.right = u.right;
			temp.parent = u.parent;
			u = v;
			v = temp;
		}
		if (u.left == null) {
			u.left = v;
			u.weight = v.weight + 1;
			v.parent = u;
		} 
		else {
			Node uRight = merge(u.right, v);
			u.right = uRight;
			uRight.parent = u;
			if (u.left.weight < u.right.weight){
				Node temp = new Node(u.left.k, u.left.v);
				temp.weight = u.left.weight;
				temp.left = u.left.left;
				temp.right = u.left.right;
				temp.parent = u;
				u.left = u.right;
				u.right = temp;
			}
			u.weight = u.right.weight + u.left.weight + 1;
		}
		return u;
	}

	public Value extract() throws Exception {
		if (root == null) throw new Exception("Extract from empty heap");
		Value ans = root.v;
		this.root = merge(this.root.left, this.root.right);
		this.root.parent = null;
		return ans;
	}

	public void updateKey(Locator loc, Key x) {
		Node curr = loc.node;
		curr.k = x;
		// check parents
		while (curr.parent != null && curr.k.compareTo(curr.parent.k) > 0) {
			curr = swapParent(curr);
			//iterate up
			curr = curr.parent;
		}
		//check children
		while ((curr.left != null && curr.right != null) 
				|| curr.k.compareTo(curr.left.k) < 0 
				|| curr.k.compareTo(curr.right.k) < 0) {
			if (curr.k.compareTo(curr.left.k) < curr.k.compareTo(curr.right.k) 
					&& curr.k.compareTo(curr.left.k) < 0) {
				curr = swapLChild(curr);
				curr = curr.left;
			} 
			else if (curr.k.compareTo(curr.right.k) <= curr.k.compareTo(curr.left.k) 
						&& curr.k.compareTo(curr.right.k) < 0) {
				curr = swapRChild(curr);
				curr = curr.right;
			} 
		}
		//EDGE CASES at leaf level
		if (curr.left != null && curr.right == null 
			&& curr.k.compareTo(curr.left.k) < 0) {
			curr = swapLChild(curr);
		}
		if (curr.right != null && curr.left == null 
			&& curr.k.compareTo(curr.right.k) < 0) {
				curr = swapRChild(curr);

		}
	}

	private Node swapLChild(Node parent) {
		Key tempKey = parent.k;
		Value tempValue = parent.v;
		parent.k = parent.left.k;
		parent.v = parent.left.v;
		parent.left.k = tempKey;
		parent.left.v = tempValue;
		return parent;
	}

	private Node swapRChild(Node parent) {
		Key tempKey = parent.k;
		Value tempValue = parent.v;
		parent.k = parent.right.k;
		parent.v = parent.right.v;
		parent.right.k = tempKey;
		parent.right.v = tempValue;
		return parent;
	}

	private Node swapParent(Node child) {
		Key tempKey = child.k;
		Value tempValue = child.v;
		child.k = child.parent.k;
		child.v = child.parent.v;
		child.parent.k = tempKey;
		child.parent.v = tempValue;
		return child;
	}

	public Key peekKey() {
		if (root == null) return null;
		return root.k;
	}

	public Value peekValue() {
		if (root == null) return null;
		return root.v;
	}

	public ArrayList<String> list() {
		ArrayList<String> ans = new ArrayList<String>();
		return listAux(root, ans);
	}

	private ArrayList<String> listAux(Node curr, ArrayList<String> ans){
		if (curr == null) {
			ans.add("[]");
			return ans;
		}
		else {
			ans.add("(" + curr.k + ", " + curr.v + ") [" + curr.weight + "]");
			ans = listAux(curr.right, ans);
			ans = listAux(curr.left, ans);
			return ans;
		}
	}

}
