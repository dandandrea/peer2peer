import java.io.*;
import java.lang.*;
import java.util.*;
import java.util.concurrent.*;

public class PeerThread extends Thread {
    // Remote peer ID
	// This is the peer ID for which this thread is connected to over the network
    private int remotePeerId;

	// Number of milliseconds to sleep between main thread loop iterations
	private int sleepMilliseconds;

    // Connection for this PeerThread
	private NonblockingConnection connection;

	//thread safe queue
	private Queue<Message> outboundMessageQueue;
	private Queue<Message> inboundMessageQueue;

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
	}

	// Thread.run()
	public void run() {
	    // Do we already have a connection or do we need to establish a connection?
	    // Did the listener thread construct this item or did Peer2Peer?

	    //PeerThread is outbound
		if (connection == null) {
			// Determine hostname and port to connect to
			String hostname = Peer2Peer.peer2Peer.getPeerInfoList().getPeerInfo(remotePeerId).getHostname();
			int port = Peer2Peer.peer2Peer.getPeerInfoList().getPeerInfo(remotePeerId).getPort();

			// Instantiate NonblockingConnection
			connection = new NonblockingConnection(hostname, port);

			// Send handshake
			// Stub for now
		    System.out.println("TODO: Implement real handshake send");

		    //send handshake message to the inbound peerthread and wait for handshake back.
		    int remotePeerID = peerDoHandshake(connection);
		    
		    //add self to peerInfoList
		    //TODO: test this
		    Peer2Peer.peer2Peer.getPeerInfoList().getPeerInfo(remotePeerId).setPeerThread(this);

		    //TODO: only send if you actually need to...
		    List<Integer> pieceList = Peer2Peer.peer2Peer.getPeerInfoList().getPeerInfo(Peer2Peer.peer2Peer.getPeerId()).getPieceList();
		    if(pieceList.size() >0){
				connection.sendData(new BitfieldMessage().toString());	
			}
			else{
				System.out.println(Peer2Peer.peer2Peer.getPeerId()+" did not send BitfieldMessage");
			}
		}
		//peerThread is inbound  ie. listenerThread spawned peerThread send a handShakeMessage
		else {
			//add self to peerInfoList
			Peer2Peer.peer2Peer.getPeerInfoList().getPeerInfo(remotePeerId).setPeerThread(this);

			//send a handshake to the waiting outbound peerthread.
			//TODO: test this
			connection.sendData(new HandshakeMessage(Peer2Peer.peer2Peer.getPeerId()).toString());

			sleep(sleepMilliseconds * 2);
			List<Integer> pieceList = Peer2Peer.peer2Peer.getPeerInfoList().getPeerInfo(Peer2Peer.peer2Peer.getPeerId()).getPieceList();
		    if(pieceList.size() >0){
				connection.sendData(new BitfieldMessage().toString());	
			}
			else{
				System.out.println(Peer2Peer.peer2Peer.getPeerId()+" did not send BitfieldMessage");
			}
		}

		// We'll use this for now to track the order in which transmissions arrive
		int transmissionNumber = 0;

		// This is the main loop of the thread
		while (true) {

			// If I have any messages I need to send, get the first message and send it.
			if(outboundMessageQueue.size() > 0){

				// get and remove the first message from the outboundMessageQueue
				Message sendThisMessage = outboundMessageQueue.poll();
				
				//send the message using NBC.
				connection.sendData(sendThisMessage.toString());
			}

			// populate inbountMessageQueue with all possible messages from connection.getData() with the help of dechunkin
			populateMessageQueue();


			// Check to see if I should handle a message sent to me by my remotePeer
			if(inboundMessageQueue.size() > 0){
				//TODO: write the handle message switch case method.
				//messageHandler.handle(message);
			}

            System.out.println("PeerThread looking for data");

			// Get data from remote peer
			String data = connection.getData();

			// Display data, if any
			if (data != null) {
				System.out.println("Peer ID " + Peer2Peer.peer2Peer.getPeerId() + " got data from peer ID " + remotePeerId + ":\n" + data);
			}
			
			// Increment the transmission number
			transmissionNumber++;

			// Send data to the remote peer
			connection.sendData("Hello from "+ Peer2Peer.peer2Peer.getPeerId() + " (#" + transmissionNumber + ")\n");
			

			// Sleep
			sleep(sleepMilliseconds);
			
		}
	}

	// Method to add a message to the outboundMessageQueue
	public void sendMessage(Message message){
		outboundMessageQueue.add(message);
	}

	// Populate the message queue from inbound data.
	private void populateMessageQueue(){

		// Get data from the NBC buffer
		String fullMessageString = connection.getData();

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

	// Method to clean-up sleeps (don't have to ugly our code with the try/catch)
	private void sleep(int duration) {
		try {
			Thread.sleep(sleepMilliseconds);
		}
		catch (InterruptedException e) {}
	}

	private int peerDoHandshake(NonblockingConnection connection) {
	    //send a handshake message
	    connection.sendData(new HandshakeMessage(Peer2Peer.peer2Peer.getPeerId()).toString());

		//wait for a handshake back.
		while(true) {
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

			// Didn't get the remote peer ID yet, sleep and try again
			//if (i != 9) {
			    System.out.println("Sleeping and trying to get handshake again");
			    sleep(sleepMilliseconds);
			//}
		}
	}

	private void handleMessage(){

	}
}
