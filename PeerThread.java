import java.io.*;
import java.lang.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

public class PeerThread extends Thread {
    // Remote peer ID
	// This is the peer ID for which this thread is connected to over the network
    private int remotePeerId;

	// Number of milliseconds to sleep between main thread loop iterations
	private int sleepMilliseconds;

    // Connection for this PeerThread
	private NonblockingConnection connection;

	//thread safe queue
	private final ReentrantLock lock = new ReentrantLock();
	private Queue<Message> outboundMessageQueue;
	private Queue<Message> inboundMessageQueue;

	private MessageHandler messageHandler;

	private int requestedPiece;

	private int pieceCount;

    // PeerThread constructor
    public PeerThread(int remotePeerId, int sleepMilliseconds) throws IOException {
        // Set the remote peer ID
        this.remotePeerId = remotePeerId;

		// Set the sleep milliseconds
		this.sleepMilliseconds = sleepMilliseconds;

		// set the outboundMessageQueue
		outboundMessageQueue = new ConcurrentLinkedQueue<Message>();

		// set the inboundMessageQueue
		inboundMessageQueue = new ConcurrentLinkedQueue<Message>();

		//set messageHandler
		messageHandler =  new MessageHandler(Peer2Peer.peer2Peer.getPeerInfoList() , remotePeerId);

		requestedPiece = -1;
    }

	// PeerThread constructor
	public PeerThread(int remotePeerId, NonblockingConnection connection, int sleepMilliseconds) throws IOException {
        // Set the remote peer ID
        this.remotePeerId = remotePeerId;

		// Set the NonblockingConnection
		this.connection = connection;

		// Set the sleep milliseconds
		this.sleepMilliseconds = sleepMilliseconds;

		// set the outboundMessageQueue
		outboundMessageQueue = new ConcurrentLinkedQueue<Message>();		

		// set the inboundMessageQueue 
		inboundMessageQueue = new ConcurrentLinkedQueue<Message>();

		//set messageHandler
		messageHandler =  new MessageHandler(Peer2Peer.peer2Peer.getPeerInfoList() , remotePeerId);

		requestedPiece = -1;
	}

	// Thread.run()
	public void run() {
	    // Do we already have a connection or do we need to establish a connection?
	    // Did the listener thread construct this item or did Peer2Peer?

        // Calculate the total number of pieces
        int totalNumberOfPieces = (int)Math.ceil((double)Peer2Peer.peer2Peer.getFileSize()/(double)Peer2Peer.peer2Peer.getPieceSize());

	    //PeerThread is outbound
		if (connection == null) {
			// Determine hostname and port to connect to
			String hostname = Peer2Peer.peer2Peer.getPeerInfoList().getPeerInfo(remotePeerId).getHostname();
			int port = Peer2Peer.peer2Peer.getPeerInfoList().getPeerInfo(remotePeerId).getPort();

			// Instantiate NonblockingConnection
			connection = new NonblockingConnection(hostname, port);
			
			// set self add peerThread for the corresponding peerInfo.
		    //TODO: test this
		    Peer2Peer.peer2Peer.getPeerInfoList().getPeerInfo(remotePeerId).setPeerThread(this);


			// Send handshake
			// Stub for now
		    System.out.println("TODO: Implement real handshake send");



		    //send handshake message to the inbound peerthread and wait for handshake back.
		    int remotePeerID = peerDoHandshake(connection);
		    


		    // get pieceList
		    List<Integer> pieceList = Peer2Peer.peer2Peer.getPeerInfoList().getPeerInfo(Peer2Peer.peer2Peer.getPeerId()).getPieceList();
		    
		    // If pieceList isnt empty, send a bitfieldMessage.
		    // if(pieceList.size() >0){
		    if(pieceList.size() == totalNumberOfPieces){
		    	connection.sendData(new BitfieldMessage(pieceList).toString());	
				//connection.sendData(new BitfieldMessage(pieceList).toString());	
			}
			else{
				System.out.println(Peer2Peer.peer2Peer.getPeerId()+" did not send BitfieldMessage");
                sendHaveMessages();
			}
		}
		//peerThread is inbound  ie. listenerThread spawned peerThread send a handShakeMessage
		else {

			//send a handshake to the waiting outbound peerthread.
			//TODO: test this
			connection.sendData(new HandshakeMessage(Peer2Peer.peer2Peer.getPeerId()).toString());

			//add self to peerInfoList
			Peer2Peer.peer2Peer.getPeerInfoList().getPeerInfo(remotePeerId).setPeerThread(this);


			sleep(sleepMilliseconds * 2);
			List<Integer> pieceList = Peer2Peer.peer2Peer.getPeerInfoList().getPeerInfo(Peer2Peer.peer2Peer.getPeerId()).getPieceList();

		    // if(pieceList.size() >0){
		    if(pieceList.size() == totalNumberOfPieces){
				connection.sendData(new BitfieldMessage(pieceList).toString());	
				//connection.sendData(new BitfieldMessage(pieceList).toString());
			}
			else{
				System.out.println(Peer2Peer.peer2Peer.getPeerId()+" did not send BitfieldMessage");
                sendHaveMessages();
			}
		}

		// This is the main loop of the thread
		while (true) {
			//System.out.println("PeerThread: outboundMessageQueue.size(): "+outboundMessageQueue.size());
			// If I have any messages I need to send, get the first message and send it.

			if(outboundMessageQueue.size() > 0){
				lock.lock();
				try{
				// get and remove the first message from the outboundMessageQueue
				Message sendThisMessage = outboundMessageQueue.poll();
				
				//send the message using NBC.
				connection.sendData(sendThisMessage.toString());
				}
				finally{
					lock.unlock();
				}
			}

			// populate inboundMessageQueue with all possible messages from connection.getData() with the help of dechunkin
			try {
				populateInboundMessageQueue();
			}
			catch (Peer2PeerException e) {
				System.out.println("PeerThread: ERROR: Caught Peer2PeerException while calling populateInboundMessageQueue: " + e.getMessage());
			}
			//System.out.println("PeerThread: inboundMessageQueue.size(): "+inboundMessageQueue.size());
			// Check to see if I should handle the next message sent to me by my remotePeer.
			if(inboundMessageQueue.size() > 0){

				// get and remove the first message from the inboundMessageQueue
				Message message = inboundMessageQueue.poll();
				// handle that massage
				messageHandler.handleMessage(message);
			}
			// Sleep
			sleep(sleepMilliseconds);			
		}
	}

	// Method to add a message to the outboundMessageQueue
	public void sendMessage(Message message){
		outboundMessageQueue.add(message);
	}

	public Peer2Peer getPeer2Peer(){
		return Peer2Peer.peer2Peer;
	}

	public int getRequestedPiece(){
		return requestedPiece;
	}

	public void setRequestedPiece(int requestedPiece){
		this.requestedPiece = requestedPiece;
	}

	public ReentrantLock getLock(){
		return lock;
	}

    private void sendHaveMessages() {
        // For every piece we have...
        for (int pieceIndex = 0; pieceIndex < Peer2Peer.peer2Peer.getPeerInfoList().getPeerInfo(Peer2Peer.peer2Peer.getPeerId()).getPieceList().size(); pieceIndex++) {
            Peer2Peer.peer2Peer.getLock().lock();
            try {
                System.out.println("Sending have message in lieu of bitfield message, saying that we have piece " + pieceIndex + " (to peer " + remotePeerId + ")");
                Peer2Peer.peer2Peer.getPeerInfoList().getPeerInfo(remotePeerId).getPeerThread().sendMessage(new HaveMessage(Peer2Peer.peer2Peer.getPeerInfoList().getPeerInfo(Peer2Peer.peer2Peer.getPeerId()).getPieceList().get(pieceIndex)));
            }
            finally {
                Peer2Peer.peer2Peer.getLock().unlock();
            }
        }
    }

	// Populate the message queue from inbound data.
	private void populateInboundMessageQueue() throws Peer2PeerException {

		// Get data from the NBC buffer
		String fullMessageString = connection.getData();

		

		if(fullMessageString != null){
			System.out.println(Peer2Peer.peer2Peer.getPeerInfoList().getPeerInfo(remotePeerId).getPeerId()+": fullMessageString: "+fullMessageString);
			// Break apart fullMessageString into individual messageStrings
			List<String> dechunkedMessageList = dechunkin(fullMessageString);
		
			// for each messageString build a message object and add it to the inboundMessageQueue
			for (String messageString  : dechunkedMessageList) {

				// Build a message object by using the message factory
				Message message = MessageFactory.toMessage(messageString);

				//add that message object to the inboundMessageQeue
	         	inboundMessageQueue.add(message);
        }
      }
	}

	// Method to clean-up sleeps (don't have to ugly our code with the try/catch)
	private void sleep(int duration) {
		try {
			Thread.sleep(sleepMilliseconds);
		}
		catch (InterruptedException e) {}
	}

	private int peerDoHandshake(NonblockingConnection connection) {
	    //send a handshake message
		//wait for a handshake back.
		while(true) {
	    	//send handshake again to solve race condition.
			connection.sendData(new HandshakeMessage(Peer2Peer.peer2Peer.getPeerId()).toString());

		    // Get data
		    String remotePeerIdCandidate = connection.getData();

	        // Check if the data is a valid remote peer ID
			if (remotePeerIdCandidate !=null) {
			    // Trim the data
				//checking if handshake header is equal to "HELLO"

				try{
					if(!remotePeerIdCandidate.trim().substring(0,5).equals("HELLO")){
						System.out.println("Handchake message header is incorret");
						continue;
					}
				}
				catch(Exception e){
					System.out.println("error parsing handshake message");
					e.printStackTrace();
					continue;
				}
			    //get the remotePeerIcCanidate from the end of the handshake message.
			    //System.out.println(remotePeerIdCandidate);
			    try{
			    	remotePeerIdCandidate = remotePeerIdCandidate.trim().substring(31,35);
				}
				catch(Exception e){
					System.out.println("error parsing handshake message");
					e.printStackTrace();
					continue;
				}	
	            // Display remote peer ID candidate
	            System.out.println("Got remote peer ID candidate: " + remotePeerIdCandidate);

				// Try to parse string as integer
				int remotePeerId;
				try {
				    remotePeerId = Integer.parseInt(remotePeerIdCandidate);
		        }
				catch (NumberFormatException e) {
				    // Invalid remote peer ID
					System.out.println("Invalid remote peer ID: " + e.getMessage());
					continue;
			    }

				// Don't allow this peer's peer ID to be used as the remote peer ID
				if (Peer2Peer.peer2Peer.getPeerId() == remotePeerId) {
				    // Same remote peer ID as my peer ID
					System.out.println("Remote peer ID is same as my peer ID: " + remotePeerId);
					continue;
				}

				// Determine if we have a PeerInfoList slot for this peer ID
				if (Peer2Peer.peer2Peer.getPeerInfoList().getPeerInfo(remotePeerId) == null) {
				    // Unknown remote peer ID
					System.out.println("Got an unknown remote peer ID: " + remotePeerId);
					continue;
				}

	            // Got a valid peer ID
			    System.out.println("Got valid handshake from remote peer ID " + remotePeerId);

				// Return the remote peer ID
				return remotePeerId;
			}
		    System.out.println("Sleeping and trying to get handshake again");
		    sleep(sleepMilliseconds);
		}
	}

	// Method to do dechunkin'
	// Takes a string which comes from connection.getData() and breaks it into 1 or more strings,
	// each of which represent a single message
	// This is needed because a single call to connection.getData() could result in returning
	// one long string with several messages within it
	private List<String> dechunkin(String inputString) {
	    // Instantiate the message string list to be returnd
		List<String> messageStringList = new ArrayList<String>();

		// Loop and do dechunkin'
		while (true) {
	        // Gotta say "dechunkin'" because "dechunkin'"!
		    //System.out.println("Trying to dechunkin' another message");

		    // Are there no more messages?
			if (inputString == null || inputString.length() == 0) {
			    //System.out.println("No more messages to dechunkin'");
			    break;
			}

            // Do we have a message whose length is greater than 0 but less than 6?
			// That would be an invalid message because the "length" field
			// takes 4 bytes and the "type" field takes 1 byte
			if (inputString.length() < 5) {
			    System.out.println("ERROR: Got message with invalid format: " + inputString);
				break;
			}

			// Is this a handshake message? If so then just throw it out
			if (inputString.substring(0, 5).equals("HELLO") == true) {
			    //System.out.println("Discarding handshake message");
				inputString = inputString.substring(35, inputString.length());
				continue;
			}

			// If we made it here then we have a message whose length is at least 6
			// We'll take this to mean there are 4 bytes for the "length" field,
			// 1 byte for the "type" field, and that the remaining "length" bytes
			// are the payload

			// Get the length of this message
			int messageLength;
			try {
				messageLength = Integer.parseInt(inputString.substring(0, 4));
			}
			catch (NumberFormatException e) {
				System.out.println("ERROR: Message has invalid format, got bad length header [string is: " + inputString + "] [length is: " + inputString.length() + "]: " + e.getMessage());
				break;
			}
			//System.out.println("Got message with length of " + messageLength);

			// Does the indicated length of the message exceed
			// the actual length of the remaining message?
			if (messageLength > inputString.length()) {
			    System.out.println("ERROR: Indicated length (" + messageLength + ") exceeds actual length of remaining string (" + inputString.length() + ")");
				break;
			}

			// We can extract the message now that we have the length
			String extractedMessage = inputString.substring(0, messageLength);
			//System.out.println("Extracted a message: " + extractedMessage);

			// Add the extracted message to the message string list
			messageStringList.add(extractedMessage);

			// Remove the extracted message from the input string
			inputString = inputString.substring(messageLength, inputString.length());
		}

		// Return the message string list
		return messageStringList;
	}

	public int getPieceCount()
	{
		return this.pieceCount;
	}
}
