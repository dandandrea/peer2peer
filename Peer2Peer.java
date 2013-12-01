import java.io.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;
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

    //lock for doNotHaveList
    private final ReentrantLock lock = new ReentrantLock();

    private List<Integer> doNotHaveList;

    // PeerInfoList
    private PeerInfoList peerInfoList;

	// FileWriter for logging
	private FileWriter logFileWriter;

	// Holds an instance of the Peer2Peer class so that threads can access it
	// This gives threads access to Peer2Peer's logging service, the PeerInfo List, etc
	// This gets set in the main() method
	protected static Peer2Peer peer2Peer;

	// Number of milliseconds to sleep (used throughout the application)
	private static final int SLEEP_MILLISECONDS = 500;

    // Constructor
    public Peer2Peer(int peerId) throws IOException, InterruptedException, ExecutionException, Peer2PeerException {
        // Get the peer ID
        this.peerId = peerId;

		// Process configuration files
		try {
		    // Create peer directory, if it doesn't already exist
		    createPeerDirectory();

			// Parse Common.cfg
        	// This sets all of the various Common.cfg-related class properties
        	parseCommonConfigFile();

        	// Validate Common.cfg
        	validateCommonConfig();

			// Parse PeerInfo.cfg
			parsePeerInfoFile();

			// Validate PeerInfo.cfg
			validatePeerInfoFile();

            // populate pieceList
            populatePieceList();
		}
		catch (Peer2PeerException e) {
			System.out.println("Error encountered while initializing: " + e.getMessage());
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

	// Create the peer directory, if it doesn't already exist
	private void createPeerDirectory() throws Peer2PeerException {
	    // Get a File for the directory
		File f = new File("peer_" + peerId);

		// Create the directory, if it doesn't already exist
		f.mkdir();

		// Throw an exception if the file is not a directory
		if (f.exists() == true && f.isDirectory() == false) {
		    throw new Peer2PeerException("ERROR: Peer directory exists as file and not as directory; please remove the file and recreate it as a directory");
		}
	}

    // Parse Common.cfg
    private void parseCommonConfigFile() throws Peer2PeerException {
		int length;
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

	// Creates Has Piece List upon initial start-up
	private void populatePieceList()
	{
        doNotHaveList = new ArrayList<Integer>();
		int size = (int)Math.ceil((double)fileSize/(double)pieceSize);
		for (int i=0; i < size; i++){
            if (peerInfoList.getPeerInfo(peerId).getHasFile() == 1){
			    peerInfoList.getPeerInfo(peerId).getPieceList().add(i);
            }
            else{
                doNotHaveList.add(i);
            }
		}
	}

    public int getNumberOfPieces(){
        return (int)Math.ceil((double)fileSize/(double)pieceSize);
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

    // find a piece that I'm interested in, and check that piece out.
    // returns the piece number
    public  int checkoutPiece(int remotePeer){
        // If the list is empty then there is no piece number to return.
        if(doNotHaveList.size() == 0){
            return -1;
        }
        else{
            // temp list for work later
            List<Integer> canidateList = new ArrayList<Integer>();

            // check which pieces i need from remotePeer and add them to the canidateList
            for(int pieceNumber: peerInfoList.getPeerInfo(remotePeer).getPieceList()){

                //if remotePeer has a piece and This peer does not.
                if(doNotHaveList.contains(pieceNumber) == true){
                    
                    //add that piece to the canidateList
                    canidateList.add(pieceNumber);

                }
            }

            //safeguard from race condition.
            if(canidateList.size() == 0){
                return -1;
            }

            // this object can produce a random number.
            Random rand = new Random();

            // start at 0 every random number generated.
            int min = 0;

            // gets the upper bound for the random number range.
            int max = canidateList.size()-1;

            //get the index between 0 and list.size. 
            int randomNumberIndex = rand.nextInt((max - min) + 1) + min;

            //remove that index from the list
            int returnRequestNumber = doNotHaveList.get(randomNumberIndex);

            System.out.println("Peer2Peer: checkoutPiece: ["+randomNumberIndex+"] = "+returnRequestNumber);

            // finalize the checkout by removing it from the doNotHaveList
            doNotHaveList.remove(doNotHaveList.indexOf(returnRequestNumber));

            // return the pieceNumber that is preped for checkout.
            return returnRequestNumber;
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

    public List<Integer> getDoNotHaveList(){
        return doNotHaveList;
    }

    public ReentrantLock getLock(){
        return lock;
    }

    public int getNumberOfPreferredNeighbors(){
        return numberOfPreferredNeighbors;
    }

    public int getUnchokingInterval(){
        return unchokingInterval;
    }

    public int getPieceSize(){
        return pieceSize;
    }

    public String getFileName(){
        return fileName;
    }

    public int getFileSize(){
        return fileSize;
    }

    //unchoke some neighbors
    private void unchokePreferredNeighbors(){

        //get the of interested peers which are canidates to be unchoked
        List<Integer> toBeUnchokedList = peer2Peer.getPeerInfoList().getInterestedList();

        // while i have too make ppl to unchoke...remove 1
        while(toBeUnchokedList.size() > peer2Peer.getNumberOfPreferredNeighbors()){

            // this object can produce a random number.
            Random rand = new Random();

            // start at 0 every random number generated.
            int min = 0;

            // gets the upper bound for the random number range.
            int max = toBeUnchokedList.size()-1;

            //get the index between 0 and max. 
            int randomNumberIndex = rand.nextInt((max - min) + 1) + min;

            //System.out.println("randomNumberIndex:  "+randomNumberIndex);
            
            //remove that index from the list
            toBeUnchokedList.remove(randomNumberIndex);
        }

        //process unchokedList wrt all peers
        for(int i = 0 ; i < getPeerInfoList().getSize() ; i++){

            PeerInfo peerInfo = getPeerInfoList().getPeerInfoByIndex(i);

            //System.out.println("I'm considering unchoking: "+peerInfo.getPeerId());

            //do i need to unchoke?
            if(toBeUnchokedList.contains(peerInfo.getPeerId()) == true){

                //System.out.println(peerInfo.getPeerId() + " got lucky");

                // are they currently choked
                if(peerInfo.getIsChokedByMe() == true){
                    //send them a unchoked message
                    System.out.println("Peer2Peer: unchokePreferredNeighbors: Unchoking: "+ peerInfo.getPeerId());
                    peerInfo.getPeerThread().sendMessage(new UnchokeMessage());

                    //mark them as unchoked
                    peerInfo.setIsChokedByMe(false);
                }
            }
            else{
                //System.out.println(peerInfo.getPeerId() + " didn't get lucky");
                // are they currently unchoked
                if(peerInfo.getIsChokedByMe() == false){
                    System.out.println("Peer2Peer: unchokePreferredNeighbors: Choking: "+ peerInfo.getPeerId());

                    //send them a choked message
                    peerInfo.getPeerThread().sendMessage(new ChokeMessage());

                    //mark them as choked
                    peerInfo.setIsChokedByMe(true);
                }
            }
        }
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

        //TODO: while true needs to be if anyone needs a piece
        while(true){
            System.out.println("About to call unchokePreferredNeighbors()");
            peer2Peer.unchokePreferredNeighbors();

            System.out.println("Peer2Peer: pieceList:"+ peer2Peer.getPeerInfoList().getPeerInfo(peer2Peer.getPeerId()).getPieceList().toString());
            System.out.println("Peer2Peer: doNotHaveList:"+peer2Peer.getDoNotHaveList().toString());

            peer2Peer.sleep(peer2Peer.getUnchokingInterval() * 1000);
        }
	}

    // Method to clean-up sleeps (don't have to ugly our code with the try/catch)
    public static void sleep(int duration) {
        try {
            Thread.sleep(duration);
        }
        catch (InterruptedException e) {}
    }

}
