package cmsc420_s23; // Do not alter this line or your program will fail the autograder

import java.util.ArrayList;
import java.util.HashMap;

public class AdjustableStack<Element> {
	private ArrayList<Element> elements;
	private HashMap<Element, Locator> data;

	public class Locator {
		private int index;
	}

	public AdjustableStack() {
		this.elements = new ArrayList<Element>();
		this.data = new HashMap<Element, Locator>();
	}

	public Locator push(Element element) {
		Locator newLoc = new Locator();
		newLoc.index = elements.size();
		elements.add(element);
		data.put(element, newLoc);
		return newLoc;
	}

	public Element pop() throws Exception {
		if (this.elements.size() == 0) {
			throw new Exception("Pop of empty stack");
		}
		Element ans = elements.get(elements.size() - 1);
		elements.remove(elements.size() - 1);
		data.remove(ans);
		return ans;
	}

	public Element peek() throws Exception {
		if (elements.size() == 0) {
			throw new Exception("Peek of empty stack");
		}
		return elements.get(size() - 1);
	}

	public int size() {
		return this.elements.size();
	}

	public ArrayList<Element> list() {
		ArrayList<Element> ans = new ArrayList<Element>();
		for (int i = elements.size() - 1; i >= 0; i--) {
			ans.add(elements.get(i));
		}
		return ans;
	}

	public void promote(Locator loc) {
		if (loc.index < elements.size() - 1) {
			int indexHigh = loc.index + 1;
			int indexLow = loc.index;
			// swap the elements
			Element target = elements.get(indexLow);
			Element swapped = elements.get(indexHigh);
			elements.set(indexLow, swapped);
			elements.set(indexHigh, target);
			// update the dictionary
			data.get(target).index++;
			data.get(swapped).index--;
		}
	}

	public void demote(Locator loc) {
		if (loc.index > 0) {
			int indexHigh = loc.index;
			int indexLow = loc.index - 1;
			// swap the elements
			Element target = elements.get(indexHigh);
			Element swapped = elements.get(indexLow);
			elements.set(indexHigh, swapped);
			elements.set(indexLow, target);
			// update the dictionary
			data.get(target).index--;
			data.get(swapped).index++;
		}
	}

	public int getDepth(Locator loc) {
		return this.elements.size() - loc.index - 1;
	}
}
