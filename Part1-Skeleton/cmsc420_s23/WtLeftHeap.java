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
	}

	public Locator insert(Key x, Value v) {
		Node newNode = new Node(x, v);
		newNode.weight = 1;
		this.root = merge(this.root, newNode);
		this.size++;
		return new Locator(newNode);
	}

	public void mergeWith(WtLeftHeap<Key, Value> h2) {
		this.root = merge(this.root, h2.root);
		this.root.parent = null;
		h2.root = null;
	}

	//refine for later
	private void swapSiblings(Node u, Node v) {
		Node temp = new Node(u.k, u.v);
		temp.weight = u.weight;
		temp.left = u.left;
		temp.right = u.right;
		temp.parent = u.parent;
		u = v;
		v = temp;
	}
	
	//swaps fields for updating key method
	private void swapFields(Node u, Node v) {
		//copy u's data
		Key tempKey = u.k;
		Value tempValue = u.v;
		int tempWeight = u.weight;
		//swap data w v
		u.k = v.k;
		u.v = v.v;
		u.weight = v.weight;
		v.k = tempKey;
		v.v = tempValue;
		v.weight = tempWeight;
	}

	private Node merge(Node u, Node v) {
		if (u == null) return v;
		if (v == null) return u;
		if (u.k.compareTo(v.k) < 0) swapSiblings(u, v);
		if (u.left == null){
			u.left = v;
			u.weight = v.weight + 1;
			v.parent = u;
		} 
		else {
			Node uRight = merge(u.right, v);
			u.right = uRight;
			uRight.parent = u;
			if (u.left.weight < u.right.weight) swapSiblings(u.left, u.right);
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
			swapFields(curr, curr.parent);
			//iterate up
			curr = curr.parent;
		}
		//check children
		while ((curr.left != null && curr.right != null) 
				|| curr.k.compareTo(curr.left.k) < 0 
				|| curr.k.compareTo(curr.right.k) < 0) {
			if (curr.k.compareTo(curr.left.k) < curr.k.compareTo(curr.right.k) 
					&& curr.k.compareTo(curr.left.k) < 0) {
				swapFields(curr, curr.left);
				curr = curr.left;
			} 
			else if (curr.k.compareTo(curr.right.k) <= curr.k.compareTo(curr.left.k) 
						&& curr.k.compareTo(curr.right.k) < 0) {
				swapFields(curr, curr.right);
				curr = curr.right;
			} 
		}
		//EDGE CASES at leaf level
		if (curr.left != null && curr.right == null 
			&& curr.k.compareTo(curr.left.k) < 0) {
			swapFields(curr, curr.left);
		}
		if (curr.right != null && curr.left == null 
			&& curr.k.compareTo(curr.right.k) < 0) {
			swapFields(curr, curr.right);
		}
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
