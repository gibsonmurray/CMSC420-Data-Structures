package cmsc420_s23;

// YOU SHOULD NOT MODIFY THIS FILE

import java.util.Scanner;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Command handler. This reads a single command line, processes the command (by
 * invoking the appropriate method(s) on the data structure), and returns the
 * result as a string.
 */

public class Part0CommandHandler {

	AdjustableStack<String> stack; // the stack
	private HashMap<String, AdjustableStack<String>.Locator> locators; // locators

	/**
	 * Initialize command handler
	 */
	public Part0CommandHandler() {
		stack = new AdjustableStack<String>(); // create the stack
		locators = new HashMap<String, AdjustableStack<String>.Locator>(); // ...and locators
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
			// COMMENT string
			// - comment line for the output
			// -----------------------------------------------------
			if (cmd.compareTo("comment") == 0) {
				String comment = line.next(); // read the comment
				output += "comment: " + comment + "" + System.lineSeparator();
			}
			// -----------------------------------------------------
			// PUSH x
			// - push x onto stack
			// -----------------------------------------------------
			else if (cmd.compareTo("push") == 0) {
				String x = line.next(); // item to push
				output += "push(" + x + "): ";
				AdjustableStack<String>.Locator loc = stack.push(x); // push it
				locators.put(x, loc); // save its locator
				output += "successful" + System.lineSeparator();
			}
			// -----------------------------------------------------
			// POP
			// - pop and print
			// -----------------------------------------------------
			else if (cmd.compareTo("pop") == 0) {
				output += "pop: ";
				String x = stack.pop();
				locators.remove(x); // remove its locator
				if (x == null) {
					output += "[null]" + System.lineSeparator();
				} else {
					output += x + System.lineSeparator(); // report the result
				}
			}
			// -----------------------------------------------------
			// PEEK
			// - peek at the top
			// -----------------------------------------------------
			else if (cmd.compareTo("peek") == 0) {
				output += "peek: ";
				String x = stack.peek(); // get it
				if (x == null) {
					output += "[null]" + System.lineSeparator();
				} else {
					output += x + System.lineSeparator(); // report the result
				}
			}
			// -----------------------------------------------------
			// SIZE
			// - how many elements are in the stack?
			// -----------------------------------------------------
			else if (cmd.compareTo("size") == 0) {
				int size = stack.size();
				output += "size: " + size + System.lineSeparator();
			}
			// -----------------------------------------------------
			// LIST
			// - list the stack top down
			// -----------------------------------------------------
			else if (cmd.compareTo("list") == 0) {
				ArrayList<String> list = stack.list(); // get list of elements
				output += "list:";
				if (list == null) {
					output += " [null]" + System.lineSeparator();
				} else {
					for (int i = 0; i < list.size(); i++) { // and print them
						output += " " + list.get(i);
						if (i != list.size() - 1 && i % 20 == 19) { // add occasional line breaks
							output += System.lineSeparator() + "     ";
						}
					}
					output += System.lineSeparator();
				}
			}
			// -----------------------------------------------------
			// PROMOTE x
			// - promote the entry associated with x
			// -----------------------------------------------------
			else if (cmd.compareTo("promote") == 0) {
				String x = line.next(); // item to promote
				output += "promote(" + x + "): ";
				AdjustableStack<String>.Locator loc = locators.get(x); // get this entry's locator
				if (loc == null) {
					output += "No locator found" + System.lineSeparator();
				} else {
					stack.promote(loc);
				}
				output += "successful" + System.lineSeparator();
			}
			// -----------------------------------------------------
			// DEMOTE x
			// - demote the entry associated with x
			// -----------------------------------------------------
			else if (cmd.compareTo("demote") == 0) {
				String x = line.next(); // item to demote
				output += "demote(" + x + "): ";
				AdjustableStack<String>.Locator loc = locators.get(x); // get this entry's locator
				if (loc == null) {
					output += "No locator found" + System.lineSeparator();
				} else {
					stack.demote(loc);
				}
				output += "successful" + System.lineSeparator();
			}
			// -----------------------------------------------------
			// DEPTH x
			// - report the depth of entry x
			// -----------------------------------------------------
			else if (cmd.compareTo("depth") == 0) {
				String x = line.next(); // item to promote
				output += "depth(" + x + "): ";
				AdjustableStack<String>.Locator loc = locators.get(x); // get this entry's locator
				if (loc == null) {
					output += "No locator found" + System.lineSeparator();
				} else {
					output += stack.getDepth(loc) + System.lineSeparator(); // report the result;
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
		}
		// -----------------------------------------------------
		// Error processing - We
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

}
