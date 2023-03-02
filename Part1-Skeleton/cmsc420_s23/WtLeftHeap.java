package cmsc420_s23;

import java.util.ArrayList;

public class WtLeftHeap<Key extends Comparable<Key>, Value> {
	private Node root;
	private int size;

	private class Node {
		private Key k;
		private Value v;
		private Node left, right;
		private Locator loc;
		private int weight;

		private Node (Key k, Value v) {
			this.k = k;
			this.v = v;
			this.weight = 0;
			this.left = null;
			this.right = null;
			this.loc = new Locator(this);
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
		return newNode.loc;
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
		this.size--;
		return ans;
	}

	public void updateKey(Locator loc, Key x) {
		if (loc.node.k.compareTo(x) < 0) {
			this.root = siftUp(this.root, loc.node.k, x, loc);
		}
		else if (loc.node.left.k.compareTo(x) > 0 || loc.node.right.k.compareTo(x) > 0) {
			this.root = siftDown(loc.node, loc);
		}
	}

	private Node siftUp(Node node, Key pre, Key post, Locator loc) {
		if (node == null){
			return node;
		} 
		else if (node.left != null && node.left.k == pre && node.k.compareTo(post) < 0) {
			node.left.k = post;
			swapLChild(node);
		}
		else if (node.right != null && node.right.k == pre && node.k.compareTo(post) < 0) {
			node.right.k = post;
			swapRChild(node);
		}
		else {
			node.left = siftUp(node.left, pre, post, loc);
			if (node.left != null && node.left.k == post && node.k.compareTo(post) < 0) {
				swapLChild(node);
				loc.node = node;
			}
			node.right = siftUp(node.right, pre, post, loc);
			if (node.left != null && node.left.k == post && node.k.compareTo(post) < 0) {
				swapRChild(node);
				loc.node = node;
			}
		}
		return node;
	}

	private Node siftDown(Node node, Locator loc) {
		if (node == null) {
			return node;
		}
		else if (node.left != null && node.right != null) {
			int compL = node.k.compareTo(node.left.k);
			int compR = node.k.compareTo(node.right.k);
			if (compL < 0 && compR < 0) {
				if (compL > compR) {
					swapLChild(node);
					loc.node = node.left;
					node.left = siftDown(node.left, loc);
				}
				else if (compL < compR) {
					swapRChild(node);
					loc.node = node.right;
					node.right = siftDown(node.right, loc);
				}
			}
		}
		else if (node.left != null && node.right == null) {
			int compL = node.k.compareTo(node.left.k);
			if (compL < 0) {
				swapLChild(node);
				loc.node = node.left;
				node.left = siftDown(node.left, loc);
			}
		}
		else if (node.left == null && node.right != null) {
			int compR = node.k.compareTo(node.right.k);
			if (compR < 0) {
				swapLChild(node);
				loc.node = node.right;
				node.right = siftDown(node.right, loc);
			}
		}
		return node;
	}

	private Node swapLChild(Node parent) {
		Key tempKey = parent.k;
		Value tempValue = parent.v;
		Locator tempLoc = parent.loc;
		parent.k = parent.left.k;
		parent.v = parent.left.v;
		parent.loc = parent.left.loc;
		parent.loc.node = parent;
		parent.left.k = tempKey;
		parent.left.v = tempValue;
		parent.left.loc = tempLoc;
		parent.left.loc.node = parent.left;
		return parent;
	}

	private Node swapRChild(Node parent) {
		Key tempKey = parent.k;
		Value tempValue = parent.v;
		Locator tempLoc = parent.loc;
		parent.k = parent.right.k;
		parent.v = parent.right.v;
		parent.loc = parent.right.loc;
		parent.loc.node = parent;
		parent.right.k = tempKey;
		parent.right.v = tempValue;
		parent.right.loc = tempLoc;
		parent.right.loc.node = parent.right;
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
