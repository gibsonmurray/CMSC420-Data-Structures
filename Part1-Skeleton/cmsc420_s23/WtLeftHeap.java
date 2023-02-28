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
		merge(this.root, newNode);
		return new Locator(newNode);
	}

	public void mergeWith(WtLeftHeap<Key, Value> h2) {
		this.root = merge(this.root, h2.root);
		this.root.parent = null;
		h2.root = null;
	}

	private void swap(Node u, Node v) {
		Node temp = new Node(u.k, u.v);
		temp.weight = u.weight;
		temp.left = u.left;
		temp.right = u.right;
		temp.parent = u.parent;
		u = v;
		v = temp;
	}

	private Node merge(Node u, Node v) {
		if (u == null) return v;
		if (v == null) return u;
		if (u.k.compareTo(v.k) < 0) swap(u, v);
		if (u.left == null){
			u.left = v;
			v.parent = u;
		} 
		else {
			Node uRight = merge(u.right, v);
			u.right = uRight;
			uRight.parent = u;
			if (u.left.weight < u.right.weight) swap(u.left, u.right);
			u.weight = u.right.weight + 1;
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
		while (curr.parent != null && curr.k.compareTo(curr.parent.k) > 0) {
			//copy curr's data
			Key tempKey = curr.k;
			Value tempValue = curr.v;
			int tempWeight = curr.weight;
			//swap data w parent
			curr.k = curr.parent.k;
			curr.v = curr.parent.v;
			curr.weight = curr.parent.weight;
			curr.parent.k = tempKey;
			curr.parent.v = tempValue;
			curr.parent.weight = tempWeight;
			//iterate up
			curr = curr.parent;
		}
		while ((curr.left != null && curr.right != null) 
				|| curr.k.compareTo(curr.left.k) < 0 
				|| curr.k.compareTo(curr.right.k) < 0) {
			//swap with left
			if (curr.k.compareTo(curr.left.k) < curr.k.compareTo(curr.right.k) 
				&& curr.k.compareTo(curr.left.k) < 0) {
				//copy curr's data
				Key tempKey = curr.k;
				Value tempValue = curr.v;
				int tempWeight = curr.weight;
				//swap data w left
				curr.k = curr.left.k;
				curr.v = curr.left.v;
				curr.weight = curr.left.weight;
				curr.left.k = tempKey;
				curr.left.v = tempValue;
				curr.left.weight = tempWeight;
				//iterate down + left
				curr = curr.left;
			} 
			else if (curr.k.compareTo(curr.right.k) <= curr.k.compareTo(curr.left.k) 
						&& curr.k.compareTo(curr.right.k) < 0) {
				//copy curr's data
				Key tempKey = curr.k;
				Value tempValue = curr.v;
				int tempWeight = curr.weight;
				//swap data w right
				curr.k = curr.right.k;
				curr.v = curr.right.v;
				curr.weight = curr.right.weight;
				curr.right.k = tempKey;
				curr.right.v = tempValue;
				curr.right.weight = tempWeight;
				//iterate down + right
				curr = curr.right;
			} 
		}
		//EDGE CASES at leaf level
		if (curr.left != null && curr.right == null 
			&& curr.k.compareTo(curr.left.k) < 0) {
			//copy curr's data
			Key tempKey = curr.k;
			Value tempValue = curr.v;
			int tempWeight = curr.weight;
			//swap data w left
			curr.k = curr.left.k;
			curr.v = curr.left.v;
			curr.weight = curr.left.weight;
			curr.left.k = tempKey;
			curr.left.v = tempValue;
			curr.left.weight = tempWeight;
		}
		if (curr.right != null && curr.left == null 
			&& curr.k.compareTo(curr.right.k) < 0) {
			//copy curr's data
			Key tempKey = curr.k;
			Value tempValue = curr.v;
			int tempWeight = curr.weight;
			//swap data w right
			curr.k = curr.right.k;
			curr.v = curr.right.v;
			curr.weight = curr.right.weight;
			curr.right.k = tempKey;
			curr.right.v = tempValue;
			curr.right.weight = tempWeight;
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
		listAux(root, ans);
		return ans;
	}

	private void listAux(Node curr, ArrayList<String> ans){
		if (curr == null) {
			ans.add("[]");
		}
		else {
			ans.add("(" + curr.k + ", " + curr.v + ") [" + curr.weight + "]");
			listAux(curr.right, ans);
			listAux(curr.left, ans);
		}
	}

}
