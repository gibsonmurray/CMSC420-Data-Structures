package cmsc420_s23;

import java.util.ArrayList;

public class WtLeftHeap<Key extends Comparable<Key>, Value> {
	private Node root;
	private int size;

	private class Node {
		private Key key;
		private Value value;
		private Node left, right, parent;
		private Locator loc;
		private int weight;

		private Node (Key key, Value value) {
			this.key = key;
			this.value = value;
			this.weight = 0;
			this.left = null;
			this.right = null;
			this.parent = null;
			this.loc = new Locator(this);
		}
	}
	
	public class Locator {
		private Node node;

		public Locator(Node node) {
			this.node = node;
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
		if (this != h2) {
			this.size += h2.size;
			this.root = merge(this.root, h2.root);
			h2.root = null;
			h2.size = 0;
		}
	}

	private Node merge(Node u, Node v) {
		if (u == null) return v;
		if (v == null) return u;
		if (u.key.compareTo(v.key) < 0){
			Node temp = u;
			u = v;
			v = temp;
		}
		if (u.left == null) {
			u.left = v;
			u.left.parent = u;
			u.weight = v.weight + 1;
		} 
		else {
			u.right = merge(u.right, v);
			u.right.parent = u;
			if (u.left.weight < u.right.weight){
				Node temp = u.left;
				u.left = u.right;
				u.right = temp;
			}
			u.weight = u.right.weight + u.left.weight + 1;
		}
		return u;
	}

	public Value extract() throws Exception {
		if (root == null) throw new Exception("Extract from empty heap");
		Value ans = root.value;
		this.root = merge(this.root.left, this.root.right);
		this.size--;
		if (root != null) {
			this.root.parent = null;
		}
		return ans;
	}

	public void updateKey(Locator loc, Key x) {
		if (loc.node.key.compareTo(x) < 0) {
			loc.node.key = x;
			siftUp(loc.node);
		}
		else {
			loc.node.key = x;
			siftDown(loc.node);
		}
		checker(root);
	}

	private void checker(Node node) {
		if (node == null) {
			return;
		}
		if (node.loc.node != node) {
			System.out.println("INVALID LOCATOR:" + node.value.toString());
		}
		checker(node.left);
		checker(node.right);
	}

	private void siftUp(Node node) {
		if (node == root || node.key.compareTo(node.parent.key) < 0) {
			return;
		}
		else {
			swapParent(node);
			siftUp(node.parent);
		}
	}

	private void swapParent(Node node) {
		/* 1. assign node's values to a temp var don't
				worry about parent, children & weight data  */
		Node temp = new Node(node.key, node.value);
		temp.loc = node.loc; //worry about the node pointer later...
		// 2. swap keys
		node.key = node.parent.key;
		node.parent.key = temp.key;
		// 3. swap values
		node.value = node.parent.value;
		node.parent.value = temp.value;
		// 4. swap locator node pointers
		node.loc.node = node.parent;
		node.parent.loc.node = node;
		// 5. swap locators
		node.loc = node.parent.loc;
		node.parent.loc = temp.loc;
	}

	private void siftDown(Node node) {
		if (node.right != null) {
			if (node.key.compareTo(node.right.key) < 0
					&& node.right.key.compareTo(node.left.key) > 0) {
				swapRChild(node);
				siftDown(node.right);
			}
		}
		if (node.left != null) {
			if (node.key.compareTo(node.left.key) < 0) {
				swapLChild(node);
				siftDown(node.left);
			}
		}
	}

	private void swapLChild(Node node) {
		/* 1. assign node's values to a temp var don't
				worry about parent, children & weight data  */
		Node temp = new Node(node.key, node.value);
		temp.loc = node.loc; //worry about the node pointer later...
		// 2. swap keys
		node.key = node.left.key;
		node.left.key = temp.key;
		// 3. swap values
		node.value = node.left.value;
		node.left.value = temp.value;
		// 4. swap locator node pointers
		node.loc.node = node.left;
		node.left.loc.node = node;
		// 5. swap locators
		node.loc = node.left.loc;
		node.left.loc = temp.loc;
	}

	private void swapRChild(Node node) {
		/* 1. assign node's values to a temp var don't
				worry about parent, children & weight data  */
		Node temp = new Node(node.key, node.value);
		temp.loc = node.loc; //worry about the node pointer later...
		// 2. swap keys
		node.key = node.right.key;
		node.right.key = temp.key;
		// 3. swap values
		node.value = node.right.value;
		node.right.value = temp.value;
		// 4. swap locator node pointers
		node.loc.node = node.right;
		node.right.loc.node = node;
		// 5. swap locators
		node.loc = node.right.loc;
		node.right.loc = temp.loc;
	}

	/* rest is fine :) */


	public Key peekKey() {
		if (root == null) return null;
		return root.key;
	}

	public Value peekValue() {
		if (root == null) return null;
		return root.value;
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
			ans.add("(" + curr.key + ", " + curr.value + ") [" + curr.weight + "]");
			ans = listAux(curr.right, ans);
			ans = listAux(curr.left, ans);
			return ans;
		}
	}

}
