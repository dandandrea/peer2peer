import java.io.File;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.lang.NumberFormatException;

public class PeerProcess {
    // Peer ID
    private int peerId;

    // Peer common configuration properties
    private int numberOfPreferredNeighbors;
    private int unchokingInterval;
    private int optimisticUnchokingInterval;
    private String fileName;
    private int fileSize;
    private int pieceSize;

    // PeerInfo List
    private List<PeerInfo> peerInfoList;

    // FileWriter for log file
    FileWriter logFileWriter;

    // Run a PeerProcess
    public static void main(String[] args) throws IOException {
        // Get the peer ID from the command-line arguments
        int peerId = getPeerIdFromArguments(args);

        // Instantiate a PeerProcess
        PeerProcess peerProcess = new PeerProcess(peerId);
    }

    // PeerProcess constructor
    public PeerProcess(int peerId) throws IOException {
        // Get the peer ID
        this.peerId = peerId;

        // Parse Common.cfg
        // This sets all of the various Common.cfg-related class properties
        parseCommonConfigFile();

        // Validate Common.cfg
        validateCommonConfig();

        // Parse PeerInfo.cfg
        parsePeerInfoFile();

        // Instantiate the log FileWriter
        logFileWriter = new FileWriter("log_peer_" + peerId + ".log", true);
    }

    // Write to log (with a timestamp)
    private void writeToLog(String message) throws IOException {
        // Get current date and time
        GregorianCalendar date = new GregorianCalendar();
        int day = date.get(Calendar.DAY_OF_MONTH);
        int month = date.get(Calendar.MONTH);
        int year = date.get(Calendar.YEAR);
        int second = date.get(Calendar.SECOND);
        int minute = date.get(Calendar.MINUTE);
        int hour = date.get(Calendar.HOUR);
        String timestamp = month + "/" + day + "/" + year + " " + hour + ":" + minute + ":" + second;

        // Write to the log and flush (the toilet)
        logFileWriter.write("[" + timestamp + "]: " + message + "\n");
        logFileWriter.flush();
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

    private void parsePeerInfoFile() {
        // Instantiate a Parser
        Parser parser = null;
        try {
            parser = new Parser("PeerInfo.cfg");
        }
        catch (FileNotFoundException e) {
            System.out.println("ERROR: PeerInfo.cfg file does not exist");
            System.exit(1);
        }

        // Instantiate the peer info List
        peerInfoList = new ArrayList<PeerInfo>();

        // Process the parsed List
        for (int i = 0; i < parser.getParsedList().size(); i++) {
            // Verify that there are only 4 items in each List within the parsed List
            if (parser.getParsedList().get(i).size() != 4) {
                System.out.println("ERROR: Found a PeerInfo.cfg line which does not have 4 items");
                System.exit(1);
            }

            // Validate that the 1st, 3rd, and 4th items are integers
            mustBeInteger(parser.getParsedList().get(i).get(0), 1);
            mustBeInteger(parser.getParsedList().get(i).get(2), 3);
            mustBeInteger(parser.getParsedList().get(i).get(3), 4);

            // If we made it here then we have a valid PeerInfo item
            // Instantiate a PeerInfo object and add it to the peer info List
            PeerInfo peerInfo = new PeerInfo();
            peerInfo.setPeerId(Integer.parseInt(parser.getParsedList().get(i).get(0)));
            peerInfo.setHostname(parser.getParsedList().get(i).get(1));
            peerInfo.setPort(Integer.parseInt(parser.getParsedList().get(i).get(2)));
            peerInfo.setHasFile(Integer.parseInt(parser.getParsedList().get(i).get(3)));
            peerInfoList.add(peerInfo);
        }
    }

    // Helper method for dealing with determining if a String can be cast to an int
    private void mustBeInteger(String candidateString, int itemNumber) {
        try {
            Integer.parseInt(candidateString);
        }
        catch (NumberFormatException e) {
            System.out.println("ERROR: Item number " + itemNumber + " must be an integer value");
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

    // Private inner-class for PeerInfo
    private class PeerInfo {
        // The peer info properties
        private int peerId;
        private String hostname;
        private int port;
        private int hasFile;

        // Constructor
        public PeerInfo(int peerId, String hostname, int port, int hasFile) {
            this.peerId = peerId;
            this.hostname = hostname;
            this.port = port;
            this.hasFile = hasFile;
        }

        // Default constructor
        public PeerInfo() {}

        // Getter for peer ID
        public int getPeerId() {
            return peerId;
        }

        // Getter for hostname
        public String getHostname() {
            return hostname;
        }

        // Getter for port
        public int getPort() {
            return port;
        }

        // Getter for hasFile
        public int getHasFile() {
            return hasFile;
        }

        // Setter for peer ID
        public void setPeerId(int peerId) {
            this.peerId = peerId;
        }

        // Setter for hostname
        public void setHostname(String hostname) {
            this.hostname = hostname;
        }

        // Setter for port
        public void setPort(int port) {
            this.port = port;
        }

        // Setter for hasFile
        public void setHasFile(int hasFile) {
            this.hasFile = hasFile;
        }
    }
}
