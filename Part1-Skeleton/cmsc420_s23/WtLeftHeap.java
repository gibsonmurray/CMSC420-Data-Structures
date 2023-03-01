package cmsc420_s23;

import java.util.ArrayList;

public class WtLeftHeap<Key extends Comparable<Key>, Value> {
	private Node root;
	private int size;

	private class Node {
		private Key k;
		private Value v;
		private Node left, right;
		private int weight;

		private Node (Key k, Value v) {
			this.k = k;
			this.v = v;
			this.weight = 0;
			this.left = null;
			this.right = null;
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
			u = v;
			v = temp;
		}
		if (u.left == null) {
			u.left = v;
			u.weight = v.weight + 1;
		} 
		else {
			u.right = merge(u.right, v);
			if (u.left.weight < u.right.weight){
				Node temp = new Node(u.left.k, u.left.v);
				temp.weight = u.left.weight;
				temp.left = u.left.left;
				temp.right = u.left.right;
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
		return ans;
	}

	public void updateKey(Locator loc, Key x) {
		Node curr = loc.node;
		if (curr.k.compareTo(x) < 0) {
			curr.k = x;
			this.root = siftUp(curr, curr.k);
		}
		else if (curr.left.k.compareTo(x) > 0 || curr.right.k.compareTo(x) > 0) {
			curr.k = x;
			this.root = siftDown(curr);
		}
	}

	private Node siftUp(Node node, Key k) {
		if (node != null) {
			node.left = siftUp(node.left, k);
			node.right = siftUp(node.right, k);
		}
		if (node.left != null && node.left.k.compareTo(k) == 0
				&& node.k.compareTo(node.left.k) < 0) {
			node = swapLChild(node);
			return node;
		}
		if (node.right != null && node.left.k.compareTo(k) == 0
				&& node.k.compareTo(node.right.k) < 0) {
			node = swapRChild(node);
			return node;
		}
		return node;
	}

	private Node siftDown(Node node) {
		if (node.left == null && node.right == null) return root;
		else if (node.left != null && node.right == null
			&& node.left.k.compareTo(node.k) > 0) {
			node = swapLChild(node);
			return siftDown(node.left);
		}
		else if (node.left == null && node.right != null 
			&& node.right.k.compareTo(node.k) > 0) {
			node = swapRChild(node);
			return siftDown(node.right);
		}
		else {
			int lComp = node.left.k.compareTo(node.k);
			int rComp = node.right.k.compareTo(node.k);
			if (lComp > 0 && rComp > 0) {
				if (lComp > rComp) {
					node = swapLChild(node);
					return siftDown(node.left);
				}
				else {
					node = swapRChild(node);
					return siftDown(node.right);
				}
			}
		}
		return root;
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
