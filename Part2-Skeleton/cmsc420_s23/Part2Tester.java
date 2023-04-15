package cmsc420_s23;

// YOU SHOULD NOT MODIFY THIS FILE, EXCEPT TO ALTER THE INPUT/OUTPUT SOURCES

import java.io.FileInputStream;
import java.io.PrintStream;
import java.util.Scanner;

/**
 * Generic testing program. By default, this reads from tests/test01-input.txt and
 * writes the output to tests/test01-output.txt. Change inputFileName and outputFileName
 * below to change this behavior.
 */

public class Part2Tester {

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
	private static String[] inputFileList = { 
			// "test01-input.txt",
			// "test02-input.txt",
			"test03-input.txt",
//			"test04-input.txt",
//			"test05-input.txt",
//			"testEC1-input.txt", // for the Challenge Problem
			};
	private static String[] outputFileList = { 
			// "test01-output.txt",
			// "test02-output.txt",
			"test03-output.txt",
//			"test04-output.txt",
//			"test05-output.txt",
//			"testEC1-output.txt", // for the Challenge Problem
			};
	// --------------------------------------------------------------------------------------------

	/**
	 * Redirects standard in/out to the appropriate files and invokes input
	 * processor.
	 */
	public static void main(String[] args) {

		if (inputFileList.length != outputFileList.length) {
			System.err.println("Part2Tester error: Input and output file lists must be of equal lengths");
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
			Part2CommandHandler commandHandler = new Part2CommandHandler(); // initialize command handler
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
