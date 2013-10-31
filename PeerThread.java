import java.io.*;
import java.lang.*;

public class PeerThread extends Thread {
    // Remote peer ID
	// This is the peer ID for which this thread is connected to over the network
    private int remotePeerId;

	// Number of milliseconds to sleep between main thread loop iterations
	private static final int SLEEP_MILLISECONDS = 1000;

    // PeerThread constructor
    public PeerThread(int remotePeerId) throws IOException {
        // Set the remote peer ID
        this.remotePeerId = remotePeerId;
    }

	// Thread.run()
	public void run() {
		// Instantiate NonblockingConnection
		NonblockingConnection nonblockingConnection = null;
		try {
			nonblockingConnection = new NonblockingConnection("localhost", 80);
		}
		catch (Exception e) {
			System.out.println("Caught exception during instantiation of NonBlockingConnection: " + e.getMessage());
		}

		// This is the main loop of the thread
		while (true) {
			// Get data from remote peer
			String data = null;
			try {
				data = nonblockingConnection.getData();
			}
			catch (Exception e) {
				System.out.println("Caught exception during nonblockingConnection.getData(): " + e.getMessage());
			}

			// Display data, if any
			if (data != null) {
				System.out.println("Got data:\n" + data);
			}

			// Sleep
			try {
				System.out.println("Thread for remote peer ID " + remotePeerId + " sleeping after getData() call");
				Thread.sleep(SLEEP_MILLISECONDS);
			}
			catch (InterruptedException e) {}
		}
	}
}
