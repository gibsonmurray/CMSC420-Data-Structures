package cmsc420_s23;

// YOU SHOULD NOT MODIFY THIS FILE

import java.util.Scanner;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Command handler. This reads a single command line, processes the command (by
 * invoking the appropriate method(s) on the data structure), and returns the
 * result as a string.
 */

public class Part1CommandHandler {

	private HashMap<String, WtLeftHeap<Integer, String>> heaps; // map of heaps
	private HashMap<String, WtLeftHeap<Integer, String>.Locator> locators; // map of locators

	/**
	 * Default constructor.
	 */
	public Part1CommandHandler() {
		heaps = new HashMap<String, WtLeftHeap<Integer, String>>(); // initialize heaps
		locators = new HashMap<String, WtLeftHeap<Integer, String>.Locator>(); // and locators
	}

	/**
	 * Process a single command and return the string output. Each command begins
	 * with a command followed by a list of arguments. The arguments are separated
	 * by colons (":").
	 */
	public String processCommand(String inputLine) throws Exception {
		Scanner line = new Scanner(inputLine);
		line.useDelimiter(":"); // use ":" to separate arguments
		String output = new String(); // for storing summary output
		String cmd = (line.hasNext() ? line.next() : ""); // next command
		try {
			// -----------------------------------------------------
			// COMMENT string
			// - comment line for the output
			// -----------------------------------------------------
			if (cmd.compareTo("comment") == 0) {
				String message = line.next(); // read the comment
				output += "comment: " + message + System.lineSeparator();
			}
			// -----------------------------------------------------
			// ALERT string
			// - comment with output also sent to stderr
			// -----------------------------------------------------
			else if (cmd.compareTo("alert") == 0) {
				String message = line.next(); // read the comment
				output += "alert: " + message + System.lineSeparator();
				System.err.println(message);
			}
			// -----------------------------------------------------
			// INSERT ID label key
			// - add key-label pair and save its location
			// -----------------------------------------------------
			else if (cmd.compareTo("insert") == 0) {
				String heapID = line.next(); // read heap ID
				String label = line.next(); // read the label
				int key = line.nextInt(); // read the key
				WtLeftHeap<Integer, String> heap = getHeap(heapID, true); // get/create the heap
				output += "insert(" + key + ", " + label + ") into [" + heapID + "]: ";
				WtLeftHeap<Integer, String>.Locator loc = heap.insert(key, label); // add to heap
				locators.put(label, loc); // add to locator map
				output += "successful" + System.lineSeparator();
			}
			// -----------------------------------------------------
			// MERGE ID1 ID2
			// - merge heaps with ID1 and ID2, result stored in ID1
			// -----------------------------------------------------
			else if (cmd.compareTo("merge") == 0) {
				String heapID1 = line.next(); // read heap IDs
				String heapID2 = line.next();
				WtLeftHeap<Integer, String> heap1 = getHeap(heapID1, false); // get the heaps
				WtLeftHeap<Integer, String> heap2 = getHeap(heapID2, false); 
				output += "merge[" + heapID1 + "] with [" + heapID2 + "]: ";
				
				heap1.mergeWith(heap2); // merge them
				output += "successful" + System.lineSeparator();
			}
			// -----------------------------------------------------
			// EXTRACT ID
			// - extract the maximum element from the heap
			// -----------------------------------------------------
			else if (cmd.compareTo("extract") == 0) {
				String heapID = line.next(); // read heap ID
				WtLeftHeap<Integer, String> heap = getHeap(heapID, false); // get the heap
				output += "extract[" + heapID + "]: ";
				String label = heap.extract(); // extract the maximum
				// Omitted the following line for testing the challenge problem
				// locators.remove(label); // remove this locator
				output += label + System.lineSeparator();
			}
			// -----------------------------------------------------
			// UPDATE-KEY label key
			// - updates key value
			// -----------------------------------------------------
			else if (cmd.compareTo("update-key") == 0) {
				String heapID = line.next(); // read heap ID
				String label = line.next(); // read the label
				int key = line.nextInt(); // read the new key
				WtLeftHeap<Integer, String> heap = getHeap(heapID, false); // get the heap
				WtLeftHeap<Integer, String>.Locator loc = locators.get(label);
				if (loc == null) {
					throw new Exception("Attempt to access nonexistent locator: " + label);
				}
				output += "update-key(" + label + ") to " + key + " in [" + heapID + "]: ";
				heap.updateKey(loc, key); // update the key
				output += "successful" + System.lineSeparator();
			}
			// -----------------------------------------------------
			// CLEAR ID
			// - clear this heap
			// -----------------------------------------------------
			else if (cmd.compareTo("clear") == 0) {
				String heapID = line.next(); // read heap ID
				WtLeftHeap<Integer, String> heap = getHeap(heapID, false); // get the heap
				output += "clear[" + heapID + "]: ";
				heap.clear(); // clear the heap
				output += "successful" + System.lineSeparator();
			}
			// -----------------------------------------------------
			// PEEK-KEY ID
			// - view the maximum key
			// -----------------------------------------------------
			else if (cmd.compareTo("peek-key") == 0) {
				String heapID = line.next(); // read heap ID
				WtLeftHeap<Integer, String> heap = getHeap(heapID, false); // get the heap
				output += "peek-key[" + heapID + "]: ";
				Integer key = heap.peekKey(); // get the maximum
				output += (key == null ? "null" : key) + System.lineSeparator();
			}
			// -----------------------------------------------------
			// PEEK-VALUE ID
			// - view the value of the maximum key
			// -----------------------------------------------------
			else if (cmd.compareTo("peek-value") == 0) {
				String heapID = line.next(); // read heap ID
				WtLeftHeap<Integer, String> heap = getHeap(heapID, false); // get the heap
				output += "peek-value[" + heapID + "]: ";
				String value = heap.peekValue(); // get the maximum
				output += (value == null ? "null" : value) + System.lineSeparator();
			}
			// -----------------------------------------------------
			// SIZE ID
			// - get this heaps size
			// -----------------------------------------------------
			else if (cmd.compareTo("size") == 0) {
				String heapID = line.next(); // read heap ID
				WtLeftHeap<Integer, String> heap = getHeap(heapID, false); // get the heap
				output += "size[" + heapID + "]: ";
				output += heap.size() + System.lineSeparator();
			}
			// -----------------------------------------------------
			// LIST ID
			// - list contents in reverse preorder
			// -----------------------------------------------------
			else if (cmd.compareTo("list") == 0) {
				String heapID = line.next(); // read heap ID
				WtLeftHeap<Integer, String> heap = getHeap(heapID, false); // get the heap
				ArrayList<String> list = heap.list();
				if (list == null) {
					throw new Exception("list returned a null result");
				}
				output += "list[" + heapID + "]:" + System.lineSeparator();
				for (String s : list) {
					output += "  " + s + System.lineSeparator();
				}
				output += heapStructure(list); // summarize heap contents (indented)
			}
			// -----------------------------------------------------
			// Invalid command or empty
			// -----------------------------------------------------
			else {
				if (cmd.compareTo("") == 0)
					System.err.println("Error: Empty command line (Ignored)");
				else
					System.err.println("Error: Invalid command - \"" + cmd + "\" (Ignored)");
			}
			line.close();
		}
		// -----------------------------------------------------
		// Error processing
		// -----------------------------------------------------
		catch (Exception e) { // exception thrown?
			if (e.getMessage() == null) {
				output += "Failure due to unexpected exception (probably runtime error)" + System.lineSeparator();
			} else {
				output += "Failure due to exception: \"" + e.getMessage() + "\"" + System.lineSeparator();
			}
			e.printStackTrace(System.err); // print stack trace
		} catch (Error e) { // error occurred?
			System.err.print("Operation failed due to error: " + e.getMessage());
			e.printStackTrace(System.err);
		} finally { // always executed
			line.close(); // close the input scanner
		}
		return output; // return summary output
	}


	/**
	 * Get the heap associated with a given identifier. Optionally, the heap
	 * is created if it does not exist.
	 */
	private WtLeftHeap<Integer, String> getHeap(String heapID, boolean create) throws Exception {
		WtLeftHeap<Integer, String> heap = heaps.get(heapID); // get the heap
		if (heap == null) {
			if (create) {
				heap = new WtLeftHeap<Integer, String>(); // create the heap
				heaps.put(heapID, heap); // add it to the map
			} else {
				throw new Exception("Attempt to access nonexistent heap: " + heapID);
			}
		}
		return heap;
	}

	/**
	 * Print the heap contents with indentation.
	 */
	private static String heapStructure(ArrayList<String> entries) {
		String output = "Formatted structure:" + System.lineSeparator();
		Iterator<String> iter = entries.iterator(); // iterator for the list
		if (iter.hasNext()) { // tree is nonempty
			output += heapStructureHelper(iter, "  "); // print everything
		}
		return output;
	}

	/**
	 * Recursive helper for treeStructure. The argument iterator specifies the next
	 * node from the preorder list to be printed, and the argument indent indicates
	 * the indentation to be performed (of the form "| | | ...").
	 */
	private static String heapStructureHelper(Iterator<String> iter, String indent) {
		final String levelIndent = "| "; // the indentation for each level of the tree
		String output = "";
		if (iter.hasNext()) {
			String entry = iter.next(); // get the next entry
			Boolean isExtern = (entry.length() > 0 && entry.charAt(0) == '['); // external?
			if (!isExtern) { // not external
				output += heapStructureHelper(iter, indent + levelIndent); // print left subtree
				output += indent + entry + System.lineSeparator(); // print this node
				output += heapStructureHelper(iter, indent + levelIndent); // print right subtree
			}
		} else {
			System.err.println("Unexpected trailing elements in entries list"); // shouldn't get here!
		}
		return output;
	}
}
