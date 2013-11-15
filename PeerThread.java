import java.io.*;
import java.lang.*;

public class PeerThread extends Thread {
    // Remote peer ID
	// This is the peer ID for which this thread is connected to over the network
    private int remotePeerId;

	// Number of milliseconds to sleep between main thread loop iterations
	private int sleepMilliseconds;

    // PeerThread constructor
    public PeerThread(int remotePeerId, int sleepMilliseconds) throws IOException {
        // Set the remote peer ID
        this.remotePeerId = remotePeerId;

		// Set the sleep milliseconds
		this.sleepMilliseconds = sleepMilliseconds;
    }

	// Thread.run()
	public void run() {
	    // Determine hostname and port to connect to
		String hostname = Peer2Peer.peer2Peer.getPeerInfoList().getPeerInfo(remotePeerId).getHostname();
		int port = Peer2Peer.peer2Peer.getPeerInfoList().getPeerInfo(remotePeerId).getPort();

		// Instantiate NonblockingConnection
		NonblockingConnection nonblockingConnection = new NonblockingConnection(hostname, port);

		// We'll use this for now to track the order in which transmissions arrive
		int transmissionNumber = 0;

		// This is the main loop of the thread
		while (true) {
			// Get data from remote peer
			String data = nonblockingConnection.getData();

			// Display data, if any
			if (data != null) {
				System.out.println("Peer ID " + Peer2Peer.peer2Peer.getPeerId() + " got data from peer ID " + remotePeerId + ":\n" + data);
			}

			// Increment the transmission number
			transmissionNumber++;

			// Send data to the remote peer
			nonblockingConnection.sendData("Hello from peer ID " + Peer2Peer.peer2Peer.getPeerId() + " (#" + transmissionNumber + ")\n");

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
