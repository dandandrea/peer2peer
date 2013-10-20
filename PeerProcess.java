import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.ArrayList;
import java.lang.NumberFormatException;

public class PeerProcess {
    // Peer ID
    int peerId;

    // Peer common configuration properties
    int numberOfPreferredNeighbors;
    int unchokingInterval;
    int optimisticUnchokingInterval;
    String fileName;
    int fileSize;
    int pieceSize;

    // Run a PeerProcess
    public static void main(String[] args) {
        // Get the peer ID from the command-line arguments
        int peerId = getPeerIdFromArguments(args);

        // Instantiate a PeerProcess
        PeerProcess peerProcess = new PeerProcess(peerId);
    }

    // PeerProcess constructor
    public PeerProcess(int peerId) {
        // Get the peer ID
        this.peerId = peerId;

        // Parse Common.cfg
        // This sets all of the various Common.cfg-related class properties
        parseCommonConfigFile();

        // Validate Common.cfg
        validateCommonConfig();
    }

    // This validates that the file specified in Common.cfg actually exists
    // and is of the specified size
    private void validateCommonConfig() {
        // Validate that the file exists
        File file = new File(fileName);
        if (file.exists() == false) {
            System.out.println("ERROR: Cannot read from file specified in Common.cfg (" + fileName + ")");
            System.out.println("Check \"FileName\" parameter");
            System.exit(1);
        }

        // Validate that the file is of the specified size
        if (file.length() != fileSize) {
            System.out.println("ERROR: File specified in Common.cfg is not of specified size");
            System.out.println("Specified: " + fileSize + ", actual: " + file.length());
            System.exit(1);
        }
    }

    // Parse Common.cfg
    private void parseCommonConfigFile() {
        // Instantiate a Parser
        Parser parser = null;
        try {
            parser = new Parser("Common.cfg");
        }
        catch (FileNotFoundException e) {
            System.out.println("ERROR: Common.cfg file does not exist");
            System.exit(1);
        }

        // Now we have a parsedList and we can get the configuration properties from it
        numberOfPreferredNeighbors = Integer.parseInt(getCommonConfigItem("NumberOfPreferredNeighbors", true, parser));
        unchokingInterval = Integer.parseInt(getCommonConfigItem("UnchokingInterval", true, parser));
        optimisticUnchokingInterval = Integer.parseInt(getCommonConfigItem("OptimisticUnchokingInterval", true, parser));
        fileName = getCommonConfigItem("FileName", false, parser);
        fileSize = Integer.parseInt(getCommonConfigItem("FileSize", true, parser));
        pieceSize = Integer.parseInt(getCommonConfigItem("PieceSize", true, parser));
    }

    private String getCommonConfigItem(String key, boolean isInteger, Parser parser) {
        // Try to get the item from the parser by its key
        List<String> tokenList = parser.getListByKey(key);

        // Was the key not found?
        if (tokenList == null) {
            System.out.println("ERROR: Could not get " + key + " from Common.cfg");
            System.exit(1);
        }

        // Is the item returned by the key a list with 2 values?
        if (tokenList.size() != 2) {
            System.out.println("ERROR: Encountered invalid format when looking up " + key);
            System.exit(1);
        }

        // Do we need to check that the value is a valid integer?
        if (isInteger == true) {
            try {
                Integer.parseInt(tokenList.get(1));
            }
            catch (NumberFormatException e) {
                System.out.println("ERROR: " + key + " is not in a valid integer format");
                System.exit(1);
            }
        }

        // If we made it here then we have a valid value
        // Go ahead and return it
        return tokenList.get(1);
    }

    // Validate command-line arguments
    // Return peer ID if valid
    static private int getPeerIdFromArguments(String[] args) {
        // Must have one command-line argument
        if (args.length != 1) {
            System.out.println("Usage: java PeerProcess <Peer ID>");
            System.exit(1);
        }

        // Validate that command-line argument is an integer
        // Also return the peer ID here if the argument is valid
        int peerId = 0;
        try {
            peerId= Integer.parseInt(args[0]);
        }
        catch (NumberFormatException e) {
            System.out.println("ERROR: Peer ID must be an integer");
            System.exit(1);
        }

        // Return the peer ID
        return peerId;
    }
}
