package cmsc420_s23;

import java.util.ArrayList;

public class WtLeftHeap<Key extends Comparable<Key>, Value> {
	
	public class Locator { /* ... */ }

	public WtLeftHeap() { /* ... */ }
	public int size() { /* ... */ return 0;}
	public void clear() { /* ... */ }
	public Locator insert(Key x, Value v) { /* ... */ return null; }
	public void mergeWith(WtLeftHeap<Key, Value> h2) { /* ... */ }
	public Value extract() throws Exception { /* ... */ return null; }
	public void updateKey(Locator loc, Key x) throws Exception { /* ... */ }
	public Key peekKey() {  /* ... */ return null; }
	public Value peekValue() { /* ... */ return null; }
	public ArrayList<String> list() { /* ... */ return null; }

}
