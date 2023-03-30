package cmsc420_s23;

// YOU SHOULD NOT MODIFY THIS FILE

import java.util.Scanner;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Command handler. This reads a single command line, processes the command (by
 * invoking the appropriate method(s) on the data structure), and returns the
 * result as a string.
 */

public class Part2CommandHandler {

	private boolean initialized; // have we initialized the structure yet?
	private SMkdTree<Airport> kdTree; // the kd-tree
	private HashMap<String, Airport> airports; // airport codes seen so far

	/**
	 * Initialize command handler
	 */
	public Part2CommandHandler() {
		initialized = false;
		kdTree = null;
		airports = new HashMap<String, Airport>();
	}

	/**
	 * Process a single command and return the string output. Each command begins
	 * with a command followed by a list of arguments. The arguments are separated
	 * by colons (":").
	 */

	public String processCommand(String inputLine) throws Exception {
		String output = new String(); // for storing summary output
		Scanner line = new Scanner(inputLine);
		try {
			line.useDelimiter(":"); // use ":" to separate arguments
			String cmd = (line.hasNext() ? line.next() : ""); // next command
			// -----------------------------------------------------
			// INITIALIZE
			// - this command must come first in the input
			// - sets the bounding box and rebuild offset
			// -----------------------------------------------------
			if (cmd.compareTo("initialize") == 0) {
				int rebuildOffset = line.nextInt(); // rebuild offset
				double xMin = line.nextDouble(); // bounding box
				double xMax = line.nextDouble();
				double yMin = line.nextDouble();
				double yMax = line.nextDouble();
				if (xMin > xMax || yMin > yMax) {
					throw new Exception("Error - invalid bounding box dimensions");
				}
				if (initialized) {
					throw new Exception("Error - Structure already initialized");
				} else {
					Rectangle2D bbox = new Rectangle2D(new Point2D(xMin, yMin), new Point2D(xMax, yMax));
					kdTree = new SMkdTree<Airport>(rebuildOffset, bbox); // create a new tree
					output += "initialize: rebuild-offset = " + rebuildOffset + " bounding-box = " + bbox + System.lineSeparator();
					initialized = true;
				}
			}
			// -----------------------------------------------------
			// COMMENT string
			// - comment line for the output
			// -----------------------------------------------------
			else if (cmd.compareTo("comment") == 0) {
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
				System.err.println("... alert: " + message);
			}
			// -----------------------------------------------------
			// INSERT code city x y
			// - insert a point
			// -----------------------------------------------------
			else if (cmd.compareTo("insert") == 0) {
				confirmInitialized(); // confirm that we are initialized
				String code = line.next(); // get parameters
				String city = line.next();
				double x = line.nextDouble();
				double y = line.nextDouble();
				Airport ap = new Airport(code, city, x, y); // create airport object
				output += "insert(" + code + "): ";
				Airport ap2 = airports.get(code);
				if (ap2 != null) { // code already exists?
					throw new Exception("Insertion of duplicate airport code");
				}
				kdTree.insert(ap); // insert into kd-tree
				airports.put(code, ap); // add to dictionary
				output += "successful {" + ap.getString("attributes") + "}" + System.lineSeparator();
			}
			// -----------------------------------------------------
			// DELETE
			// - delete a point given its code
			// -----------------------------------------------------
			else if (cmd.compareTo("delete") == 0) {
				confirmInitialized(); // confirm that we are initialized
				String code = line.next(); // get parameters
				output += "delete(" + code + "): ";
				Airport ap = airports.get(code); // look up the airport
				if (ap == null) { // no such airport?
					throw new Exception("Deletion of nonexistent airport code");
				}
				kdTree.delete(ap.getPoint2D()); // delete from kd-tree
				airports.remove(code); // delete from dictionary
				output += "successful" + System.lineSeparator();
			}
			// -----------------------------------------------------
			// DELETE-POINT
			// - delete a point given its coordinates
			// -----------------------------------------------------
			else if (cmd.compareTo("delete-point") == 0) {
				confirmInitialized(); // confirm that we are initialized
				double x = line.nextDouble();
				double y = line.nextDouble();
				Point2D q = new Point2D(x, y);
				output += "delete-point(" + q + "): ";
				kdTree.delete(q); // delete from kd-tree
				output += "successful" + System.lineSeparator();
			}

			// -----------------------------------------------------
			// CLEAR
			// -----------------------------------------------------
			else if (cmd.compareTo("clear") == 0) {
				confirmInitialized(); // confirm that we are initialized
				kdTree.clear(); // clear the kd-tree
				airports.clear(); // clear the airports map
				output += "clear: successful" + System.lineSeparator();
			}
			// -----------------------------------------------------
			// SIZE
			// -----------------------------------------------------
			else if (cmd.compareTo("size") == 0) {
				confirmInitialized(); // confirm that we are initialized
				int size = kdTree.size(); // get the tree's current size
				output += "size: " + size + System.lineSeparator();
			}
			// -----------------------------------------------------
			// DELETE-COUNT
			// -----------------------------------------------------
			else if (cmd.compareTo("delete-count") == 0) {
				confirmInitialized(); // confirm that we are initialized
				int deleteCt = kdTree.deleteCount(); // get the tree's delete count
				output += "delete-count: " + deleteCt + System.lineSeparator();
			}
			// -----------------------------------------------------
			// FIND code
			// -----------------------------------------------------
			else if (cmd.compareTo("find") == 0) {
				confirmInitialized(); // confirm that we are initialized
				double x = line.nextDouble();
				double y = line.nextDouble();
				Point2D pt = new Point2D(x, y);
				Airport result = kdTree.find(pt);
				output += summarizeSearch(cmd, pt, result); // summarize result
			}
			// -----------------------------------------------------
			// NEAREST-NEIGHBOR
			// Find the nearest neighbor to a query point
			// -----------------------------------------------------
			else if (cmd.compareTo("nearest-neighbor") == 0) {
				confirmInitialized(); // confirm that we are initialized
				double x = line.nextDouble();
				double y = line.nextDouble();
				Point2D q = new Point2D(x, y);
				Airport result = kdTree.nearestNeighbor(q);
				output += summarizeSearch(cmd, q, result); // summarize result
			}
			// -----------------------------------------------------
			// NEAREST-NEIGHBOR-VISIT
			// Nearest neighbor listing visited nodes
			// -----------------------------------------------------
			else if (cmd.compareTo("nearest-neighbor-visit") == 0) {
				confirmInitialized(); // confirm that we are initialized
				double x = line.nextDouble();
				double y = line.nextDouble();
				Point2D q = new Point2D(x, y);
				ArrayList<Airport> visited = kdTree.nearestNeighborVisit(q);
				if (visited == null)
					throw new Exception("Error - nearest-neighbor-visit returned a null result");
				Iterator<Airport> iter = visited.iterator(); // iterator for the list
				output += "visited:" + System.lineSeparator();
				while (iter.hasNext()) { // output the preorder list (flat)
					Airport next = iter.next();
					output += "  " + next.toString() + " squared dist = " + q.distanceSq(next.getPoint2D()) + System.lineSeparator();
				}
			}
			// -----------------------------------------------------
			// LIST - get a preorder list of entries and print
			// the tree with indentation
			// -----------------------------------------------------
			else if (cmd.compareTo("list") == 0) {
				confirmInitialized(); // confirm that we are initialized
				ArrayList<String> list = kdTree.list();
				if (list == null)
					throw new Exception("Error - list returned a null result");
				Iterator<String> iter = list.iterator(); // iterator for the list
				output += "list:" + System.lineSeparator();
				while (iter.hasNext()) { // output the preorder list (flat)
					output += "  " + iter.next() + System.lineSeparator();
				}
				output += treeStructure(list); // summarize tree contents (indented)
			}
			// -----------------------------------------------------
			// ITERATE - use the iterator (Challenge problem)
			// -----------------------------------------------------
			else if (cmd.compareTo("iterate") == 0) {
				confirmInitialized(); // confirm that we are initialized
				Iterator<Airport> iter = kdTree.iterator();
				if (iter == null)
					throw new Exception("Error - iterate returned a null result");
				output += "iterator:" + System.lineSeparator();
				while (iter.hasNext()) { // output the contents of the iterator
					output += "  " + iter.next() + System.lineSeparator();
				}
				try {
					iter.next();
					output += "Failure - We are expecting iterator.next() to generate an exception" + System.lineSeparator();
				} catch (NoSuchElementException e) { // exception thrown?
					output += "successful" + System.lineSeparator();
				}
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
		} catch (Exception e) { // exception thrown?
			if (e.getMessage() == null) {
				output += "Failure due to unexpected exception (probably runtime error)" + System.lineSeparator();
			} else {
				output += "Failure due to exception: \"" + e.getMessage() + "\"" + System.lineSeparator();
			}
		} catch (Error e) { // error occurred?
			System.err.print("Operation failed due to runtime error: " + e.getMessage());
			e.printStackTrace(System.err);
		} finally { // always executed
			line.close(); // close the input scanner
		}
		return output; // return summary output
	}

	/**
	 * Confirm that the data structure has been initialized, or throw an exception.
	 */
	void confirmInitialized() throws Exception {
		if (!initialized) {
			throw new Exception("Error: First command must be 'initialize'.");
		}
	}

	/**
	 * Summarize the results of a search command.
	 */
	static String summarizeSearch(String cmd, Point2D pt, Airport result) {
		String output = new String(cmd + "(" + pt + "): ");
		if (result != null) {
			output += "found [" + result + "]" + System.lineSeparator();
		} else {
			output += "not found" + System.lineSeparator();
		}
		return output;
	}

	/**
	 * Print the tree contents with indentation.
	 */
	static String treeStructure(ArrayList<String> entries) {
		String output = "Tree structure:" + System.lineSeparator();
		Iterator<String> iter = entries.iterator(); // iterator for the list
		if (iter.hasNext()) { // tree is nonempty
			output += treeStructureHelper(iter, "  "); // print everything
		}
		return output;
	}

	/**
	 * Recursive helper for treeStructure. The argument iterator specifies the next
	 * node from the preorder list to be printed, and the argument indent indicates
	 * the indentation to be performed (of the form "| | | ...").
	 */
	static String treeStructureHelper(Iterator<String> iter, String indent) {
		final String levelIndent = "| "; // the indentation for each level of the tree
		String output = "";
		if (iter.hasNext()) {
			String entry = iter.next(); // get the next entry
			Boolean isExtern = (entry.length() > 0 && entry.charAt(0) == '['); // external?
			if (isExtern) { // print external node entry
				output += indent + entry + System.lineSeparator();
			} else {
				output += treeStructureHelper(iter, indent + levelIndent); // print right subtree
				output += indent + entry + System.lineSeparator(); // print this node
				output += treeStructureHelper(iter, indent + levelIndent); // print left subtree
			}
		} else {
			System.err.println("Unexpected trailing elements in entries list"); // shouldn't get here!
		}
		return output;
	}
}
