import java.io.*;
import java.util.concurrent.*;
import java.io.*;
import java.util.*;
import java.lang.*;

// The main class for the peer2peer application
public class Peer2Peer {
    // Peer ID
    private int peerId;

    // Peer common configuration properties
    private int numberOfPreferredNeighbors;
    private int unchokingInterval;
    private int optimisticUnchokingInterval;
    private String fileName;
    private int fileSize;
    private int pieceSize;

    // PeerInfoList
    private PeerInfoList peerInfoList;

	// FileWriter for logging
	private FileWriter logFileWriter;

	// Holds an instance of the Peer2Peer class so that threads can access it
	// This gives threads access to Peer2Peer's logging service, the PeerInfo List, etc
	// This gets set in the main() method
	protected static Peer2Peer peer2Peer;

	// Number of milliseconds to sleep (used throughout the application)
	private static final int SLEEP_MILLISECONDS = 3000;

    // Constructor
    public Peer2Peer(int peerId) throws IOException, InterruptedException, ExecutionException, Peer2PeerException {
        // Get the peer ID
        this.peerId = peerId;

		// Process configuration files
		try {
			// Parse Common.cfg
        	// This sets all of the various Common.cfg-related class properties
        	parseCommonConfigFile();

        	// Validate Common.cfg
        	validateCommonConfig();

			// Parse PeerInfo.cfg
			parsePeerInfoFile();

			// Validate PeerInfo.cfg
			validatePeerInfoFile();
		}
		catch (Peer2PeerException e) {
			System.out.println("Error encountered while processing configuration files: " + e.getMessage());
			System.exit(1);
		}

        // Instantiate the log FileWriter
        logFileWriter = new FileWriter("log_peer_" + peerId + ".log", true);

		// Get the hostname and port to listen on
		String listenHostname = peerInfoList.getPeerInfo(peerId).getHostname();
		int listenPort = peerInfoList.getPeerInfo(peerId).getPort();

		// Start the ListenerThread
		ListenerThread listenerThread = new ListenerThread(listenHostname, listenPort, SLEEP_MILLISECONDS);
		listenerThread.start();
    }

	// Start PeerThreads
	// Need to put this in a separate method because having this in the constructor
	// results in an ugly race condition
	public void startPeerThreads() throws IOException {
		// Start outbound connections
		// Start a thread for each peer that comes before our peer ID in the PeerInfo list
		for (int i = 0; i < peerInfoList.getSize(); i++) {
		    if (peerInfoList.getPeerInfoByIndex(i).getPeerId() < peerId) {
                System.out.println("Connecting to peer ID " + peerInfoList.getPeerInfoByIndex(i).getPeerId());

				// Start thread for this peer
		        PeerThread peerThread = new PeerThread(peerInfoList.getPeerInfoByIndex(i).getPeerId(), SLEEP_MILLISECONDS);
		        peerThread.start();
			}
		}
	}

	// TODO: Remove this loop
	// This loop only exists as a proof-of-concept to show that the connections are working
	public void startListenLoop() {
		while (true) {
		    System.out.println("Looking for incoming data");

            // Check each connected peer for new data
			for (int i = 0; i < peerInfoList.getSize(); i++) {
				// Skip this peer (don't read from ourselves, duh!)
				if (peerInfoList.getPeerInfoByIndex(i).getPeerId() == peerId) {
				    // Don't read from myself (duh!)
				    continue;
			    }

				// Skip this peer if it doesn't have a connection
				if (peerInfoList.getPeerInfoByIndex(i).getConnection() == null) {
				    // No connection
				    continue;
				}

				// See if this connection has sent any data
				String data = null;
				try {
				    data = peerInfoList.getPeerInfoByIndex(i).getConnection().getData();
				}
				catch (Exception e) {
				    System.out.println("Caught an exception trying to get data from peer -- do we need to do something here?");
					continue;
				}

				// Keep getting data until there isn't any more to get
				while (data != null && data.trim().equals("") == false) {
					// Display the data
					System.out.println("Data from peer ID " + peerInfoList.getPeerInfoByIndex(i).getPeerId() + ": " + data);

					// Try to get more data
					data = peerInfoList.getPeerInfoByIndex(i).getConnection().getData();
				}
			}

            // Sleep before trying to read again
		    sleep(SLEEP_MILLISECONDS);
        }
	}

    // This validates that the file specified in Common.cfg actually exists
    // and is of the specified size
    private void validateCommonConfig() throws Peer2PeerException {
        // Validate that the file exists
        File file = new File(fileName);
        if (file.exists() == false) {
            String message = "Cannot read from file specified in Common.cfg (" + fileName + ")";
            message = message + " " + "Check \"FileName\" parameter";
			throw new Peer2PeerException(message);
        }

        // Validate that the file is of the specified size
        if (file.length() != fileSize) {
            String message = "File specified in Common.cfg is not of specified size";
            message = message + " " + "Specified: " + fileSize + ", actual: " + file.length();
			throw new Peer2PeerException(message);
        }
    }

    private void parsePeerInfoFile() throws Peer2PeerException {
        // Instantiate a Parser
        Parser parser = null;
        try {
            parser = new Parser("PeerInfo.cfg");
        }
        catch (FileNotFoundException e) {
            throw new Peer2PeerException("PeerInfo.cfg file does not exist");
        }

        // Instantiate the PeerInfoList
        peerInfoList = new PeerInfoList();

        // Process the parsed List
        for (int i = 0; i < parser.getParsedList().size(); i++) {
            // Verify that there are only 4 items in each List within the parsed List
            if (parser.getParsedList().get(i).size() != 4) {
                throw new Peer2PeerException("Found a PeerInfo.cfg line which does not have 4 items");
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

    // This validates that the peerId specified in the constructor is found
    // in the peers specified in the PeerInfo.cfg file
    private void validatePeerInfoFile() throws Peer2PeerException {
		// Whether or not we found the peerId
		boolean found = false;

		// Check all items in the PeerInfo list for our peerId
		for (int i = 0; i < peerInfoList.getSize(); i++) {
			// Is this our peerId?
			if (peerInfoList.getPeerInfoByIndex(i).getPeerId() == peerId) {
				// Found it
				found = true;
				break;
			}
		}

		// Throw an exception if we didn't find the peerId
		if (found == false) {
			throw new Peer2PeerException("peerId specified on command-line not found in PeerInfo.cfg");
		}
    }

    // Helper method for dealing with determining if a String can be cast to an int
    private void mustBeInteger(String candidateString, int itemNumber) throws Peer2PeerException {
        try {
            Integer.parseInt(candidateString);
        }
        catch (NumberFormatException e) {
            throw new Peer2PeerException("ERROR: Item number " + itemNumber + " must be an integer value");
        }
    }

    // Parse Common.cfg
    private void parseCommonConfigFile() throws Peer2PeerException {
        // Instantiate a Parser
        Parser parser = null;
        try {
            parser = new Parser("Common.cfg");
        }
        catch (FileNotFoundException e) {
            throw new Peer2PeerException("ERROR: Common.cfg file does not exist");
        }

        // Now we have a parsedList and we can get the configuration properties from it
        numberOfPreferredNeighbors = Integer.parseInt(getCommonConfigItem("NumberOfPreferredNeighbors", true, parser));
        unchokingInterval = Integer.parseInt(getCommonConfigItem("UnchokingInterval", true, parser));
        optimisticUnchokingInterval = Integer.parseInt(getCommonConfigItem("OptimisticUnchokingInterval", true, parser));
        fileName = getCommonConfigItem("FileName", false, parser);
        fileSize = Integer.parseInt(getCommonConfigItem("FileSize", true, parser));
        pieceSize = Integer.parseInt(getCommonConfigItem("PieceSize", true, parser));
    }

    private String getCommonConfigItem(String key, boolean isInteger, Parser parser) throws Peer2PeerException {
        // Try to get the item from the parser by its key
        List<String> tokenList = parser.getListByKey(key);

        // Was the key not found?
        if (tokenList == null) {
            throw new Peer2PeerException("ERROR: Could not get " + key + " from Common.cfg");
        }

        // Is the item returned by the key a list with 2 values?
        if (tokenList.size() != 2) {
            throw new Peer2PeerException("ERROR: Encountered invalid format when looking up " + key);
        }

        // Do we need to check that the value is a valid integer?
        if (isInteger == true) {
            try {
                Integer.parseInt(tokenList.get(1));
            }
            catch (NumberFormatException e) {
                throw new Peer2PeerException("ERROR: " + key + " is not in a valid integer format");
            }
        }

        // If we made it here then we have a valid value
        // Go ahead and return it
        return tokenList.get(1);
    }

    // Validate command-line arguments
    // Return peer ID if valid
    static private int getPeerIdFromArguments(String[] args) throws Peer2PeerException {
        // Must have one command-line argument
        if (args.length != 1) {
            return -1;
        }

        // Validate that command-line argument is an integer
        // Also return the peer ID here if the argument is valid
        int peerId = -1;
        try {
            peerId = Integer.parseInt(args[0]);
        }
        catch (NumberFormatException e) {
            return -1;
        }

        // Return the peer ID
        return peerId;
    }

    // Write to log (with a timestamp)
    protected void writeToLog(String message) {
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
		try {
        	logFileWriter.write("[" + timestamp + "]: " + message + "\n");
        	logFileWriter.flush();
		}
		catch (IOException e) {
			System.out.println("ERROR: Caught IOException in writeToLog(): " + e.getMessage());
		}
    }

	// Protected peerInfoList getter for use by PeerThreads
	protected PeerInfoList getPeerInfoList() {
	    return peerInfoList;
	}

	// Protected peerId getter for use by PeerThreads
	protected int getPeerId() {
	    return peerId;
	}

	// Main entry point for running the peer2peer application from the command-line
    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException, Peer2PeerException {
		// Get the peer ID from the command-line arguments
		int peerId = getPeerIdFromArguments(args);

		// Did we get a valid peer ID?
		if (peerId == -1) {
			System.out.println("Usage: java Peer2Peer <peer ID>");
			System.exit(1);
		}

		// Instantiate a Peer2Peer object
		peer2Peer = new Peer2Peer(peerId);

		// Start PeerThreads
		peer2Peer.startPeerThreads();

		// TODO: Remove this loop
		// This loop only exists as a proof-of-concept to show that the connections are working
		peer2Peer.startListenLoop();

        // Should we ever reach here?
		System.out.println("Made it past the listen loop");
	}

    // Method to clean-up sleeps (don't have to ugly our code with the try/catch)
    private void sleep(int duration) {
        try {
            Thread.sleep(duration);
        }
        catch (InterruptedException e) {}
    }
}
