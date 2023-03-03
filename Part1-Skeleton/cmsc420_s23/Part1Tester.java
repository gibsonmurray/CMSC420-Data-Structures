package cmsc420_s23;

// YOU SHOULD NOT MODIFY THIS FILE, EXCEPT TO ALTER THE INPUT/OUTPUT SOURCES

import java.io.FileInputStream;
import java.io.PrintStream;
import java.util.Scanner;

/**
 * Generic testing program. By default, this reads and writes to files in the
 * test directory. Change inputFileList and outputFileList to determine which
 * files to use. By setting USE_STD_IO to true, the program reads from standard
 * input and writes to standard output.
 */

public class Part1Tester {

	// --------------------------------------------------------------------------------------------
	// Uncomment these to read from standard input and output
	// --------------------------------------------------------------------------------------------
//	private static final boolean USE_STD_IO = true;
//	private static final String inFolder = "";
//	private static final String outFolder = "";
//	private static String[] inputFileList = {};
//	private static String[] outputFileList = {};
	// --------------------------------------------------------------------------------------------
	// Uncomment these to read from files
	// --------------------------------------------------------------------------------------------
	private static final boolean USE_STD_IO = false;
	private static final String inFolder = "tests"; // folder with input files (from project root)
	private static final String outFolder = "tests"; // folder with output files (from project root)
	private static String[] inputFileList = { // input files
			// "test01a-input.txt",
			// "test01b-input.txt", 
			// "test02a-input.txt",
			// "test02b-input.txt",
//			"test03a-input.txt",
			"test03b-input.txt",
//			"test04a-input.txt",
//			"test04b-input.txt",
//			"test05a-input.txt",
//			"test05b-input.txt",
//			"testEC1-input.txt", // for extra-credit only
			};
	private static String[] outputFileList = { // associated output files
			// "test01a-output.txt",
			// "test01b-output.txt",
			// "test02a-output.txt",
			// "test02b-output.txt",
//			"test03a-output.txt",
			"test03b-output.txt",
//			"test04a-output.txt",
//			"test04b-output.txt",
//			"test05a-output.txt",
//			"test05b-output.txt",
//			"testEC1-output.txt", // for extra-credit only
			};
	// --------------------------------------------------------------------------------------------

	/**
	 * Redirects standard in/out to the appropriate files and invokes input
	 * processor.
	 */
	public static void main(String[] args) {

		if (inputFileList.length != outputFileList.length) {
			System.err.println("Part1Tester error: input and output file lists must be of equal lengths");
		} else {
			if (!USE_STD_IO) { // Read/write to files
				int m = 0;
				try {
					for (m = 0; m < inputFileList.length; m++) {
						FileInputStream inStream = new FileInputStream(inFolder + "/" + inputFileList[m]);
						PrintStream outStream = new PrintStream(outFolder + "/" + outputFileList[m]);
						System.setIn(inStream);
						System.setOut(outStream);
						processInput(inputFileList[m], outputFileList[m]);
						inStream.close();
						outStream.close();
					}
				} catch (Exception e) {
					System.err.println(
							"Error encountered in processing file: " + inputFileList[m] + " or " + outputFileList[m]);
					e.printStackTrace();
				}
			} else { // Use standard input/output
				processInput(null, null);
			}
		}
	}

	/**
	 * Invokes the command handler on the specified file.
	 */
	static void processInput(String inputFileName, String outputFileName) {
		if (inputFileName != null) {
			System.err.println("Starting execution from " + inputFileName + " ...");			
		}
		try {
			Scanner scanner = new Scanner(System.in); // input scanner
			Part1CommandHandler commandHandler = new Part1CommandHandler(); // initialize command handler
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine(); // input next line
				String output = commandHandler.processCommand(line); // process this command
				System.out.print(output); // output summary
				System.out.flush(); // flush the output (helpful when debugging)
			}
			scanner.close();
			if (inputFileName != null) {
				System.err.println("... Completed. Your output can be found in " + outputFileName);
				System.err.println("    (You may need to refresh the folder list to see it)");
			}

		} catch (Exception e) {
			System.err.println("Unexpected error encountered: " + e.getMessage());
			e.printStackTrace(System.err);
		}
	}
}
