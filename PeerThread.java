import java.io.*;
import java.lang.*;

public class PeerThread extends Thread {
    // Remote peer ID
	// This is the peer ID for which this thread is connected to over the network
    private int remotePeerId;

	// Number of milliseconds to sleep between main thread loop iterations
	private int sleepMilliseconds;

    // Connection for this PeerThread
	private NonblockingConnection connection;

    // PeerThread constructor
    public PeerThread(int remotePeerId, int sleepMilliseconds) throws IOException {
        // Set the remote peer ID
        this.remotePeerId = remotePeerId;

		// Set the sleep milliseconds
		this.sleepMilliseconds = sleepMilliseconds;
    }

	// PeerThread constructor
	public PeerThread(int remotePeerId, NonblockingConnection connection, int sleepMilliseconds) throws IOException {
        // Set the remote peer ID
        this.remotePeerId = remotePeerId;

		// Set the NonblockingConnection
		this.connection = connection;

		// Set the sleep milliseconds
		this.sleepMilliseconds = sleepMilliseconds;
	}

	// Thread.run()
	public void run() {
	    // Do we already have a connection or do we need to establish a connection?
		if (connection == null) {
			// Determine hostname and port to connect to
			String hostname = Peer2Peer.peer2Peer.getPeerInfoList().getPeerInfo(remotePeerId).getHostname();
			int port = Peer2Peer.peer2Peer.getPeerInfoList().getPeerInfo(remotePeerId).getPort();

			// Instantiate NonblockingConnection
			connection = new NonblockingConnection(hostname, port);

		    // Send handshake
		    // Stub for now
		    System.out.println("TODO: Implement real handshake send");
		    connection.sendData(new Integer(Peer2Peer.peer2Peer.getPeerId()).toString() + "\n");

    		// Need to sleep so that this is the only contents in the receive buffer of the other side
    		sleep(sleepMilliseconds * 2);
		}

		// We'll use this for now to track the order in which transmissions arrive
		int transmissionNumber = 0;

		// This is the main loop of the thread
		while (true) {
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
			connection.sendData("Hello from peer ID " + Peer2Peer.peer2Peer.getPeerId() + " (#" + transmissionNumber + ")\n");

			// Sleep
			sleep(sleepMilliseconds);
		}
	}

	// Method to clean-up sleeps (don't have to ugly our code with the try/catch)
	private void sleep(int duration) {
		try {
			Thread.sleep(sleepMilliseconds);
		}
		catch (InterruptedException e) {}
	}
}
