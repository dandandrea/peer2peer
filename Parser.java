import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.FileNotFoundException;

public class Parser {
    // The parsed List
    private List<List<String>> parsedList;

    // Constructor
    public Parser(String fileName) throws FileNotFoundException {
        // Instantiate parsedList
        parsedList = new ArrayList<List<String>>();

        // Parse the file
        parseFile(fileName);
    }

    // Get a List by its "key"
    // We consider the first item in each List to be its key
    public List<String> getListByKey(String key) {
        // Search parsedList for a List with this "key"
        for (int i = 0; i < parsedList.size(); i++) {
            if (parsedList.get(i).get(0).equals(key)) {
                return parsedList.get(i);
            }
        }

        // If we made it here then the "key" was not found
        return null;
    }

    // Getter for parsedList
    public List<List<String>> getParsedList() {
        return parsedList;
    }

    // Parse the file
    private void parseFile(String fileName) throws FileNotFoundException {
        // Get a File object for the file to be parsed
        File fileToParse = new File(fileName);

        // Get a Scanner for the file's lines
        Scanner lineScanner = new Scanner(fileToParse);

        // Start scanning the lines
        while (lineScanner.hasNextLine() == true) {
            // Instantiate a List for this line's tokens
            List<String> tokenList = new ArrayList<String>();

            // Get the next line
            String nextLine = lineScanner.nextLine();

            // Get a Scanner for the line's tokens
            Scanner tokenScanner = new Scanner(nextLine);

            // Start scanning the tokens
            while (tokenScanner.hasNext() == true) {
                // Get the next token
                String nextToken = tokenScanner.next();

                // Add the token to the token List
                tokenList.add(nextToken);
            }

            // Add this line's token List to the master parsed List
            parsedList.add(tokenList);
        }
    }
}
