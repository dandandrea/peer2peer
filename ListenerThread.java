import java.io.*;
import java.lang.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.*;

public class ListenerThread extends Thread {
    // Hostname and port to listen on
	private String hostname;
	private int port;

	// Number of milliseconds to sleep between loop iterations
	private int sleepMilliseconds;

	// Peer2Peer instance
	private static Peer2Peer peer2Peer;

	// Constructor
	public ListenerThread(String hostname, int port, int sleepMilliseconds) {
	    this.hostname = hostname;
		this.port = port;
		this.sleepMilliseconds = sleepMilliseconds;
	}

	// Thread.run()
	public void run() {
        // Set Peer2Peer instance
		this.peer2Peer = Peer2Peer.peer2Peer;

		// Wait for Peer2Peer constructor to finish
		while (true) {
		    if (peer2Peer != null) {
			    break;
		    }

            // Sleep and try again
			System.out.println("ListenerThread eaiting for Peer2Peer constructor to finish");
			sleep(sleepMilliseconds);
		}

	    while (true) {
			try{
				// Open an asynchronous server socket channel
				AsynchronousServerSocketChannel asynchronousServerSocketChannel = AsynchronousServerSocketChannel.open();

				// Wait until open
				if (asynchronousServerSocketChannel.isOpen() == true) {
					// Bind to the hostname and port (start listening)
					asynchronousServerSocketChannel.bind(new InetSocketAddress(hostname, port));

					// Accept connections
					Future<AsynchronousSocketChannel> asynchronousSocketChannelFuture = asynchronousServerSocketChannel.accept();

					// Get the inbound connection
					AsynchronousSocketChannel asynchronousSocketChannel = asynchronousSocketChannelFuture.get();

					// Instantiate NonblockingConnection
					NonblockingConnection connection = new NonblockingConnection(asynchronousSocketChannel);

					// Do handshake
					doHandshake(connection);

				    // Close the channel
				    asynchronousServerSocketChannel.close();
				}
			}
			catch (IOException e) {
				System.out.println("Caught IOException in ListenerThread.run(): " + e.getMessage());
			}
			catch (InterruptedException e) {
				System.out.println("Caught InterruptedException in ListenerThread.run(): " + e.getMessage());
			}
			catch (ExecutionException e) {
				System.out.println("Caught ExecutionException in ListenerThread.run(): " + e.getMessage());
			}

			// Sleep before looping again
			sleep(sleepMilliseconds);
		}
	}

    // Perform handshake with new connection
	// This allows us to get the remote peer ID
	// which in turn allows us to determine which
	// slot in the PeerInfoList to place the
	// connection in
	private void doHandshake(NonblockingConnection connection) {
	    System.out.println("TODO: Implement real handshake");

		// Fake the handshake for now
		// Loop 10 times trying to get remote peer ID from connection
		// Drop connection if cannot get remote peer ID
		for (int i = 0; i < 10; i++) {
		    // Get data
		    String remotePeerIdCandidate = connection.getData();

            // Check if the data is a valid remote peer ID
			if (remotePeerIdCandidate != null) {
			    // Trim the data
			    remotePeerIdCandidate = remotePeerIdCandidate.trim();

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
				if (peer2Peer.getPeerId() == remotePeerId) {
				    // Same remote peer ID as my peer ID
					System.out.println("Remote peer ID is same as my peer ID: " + remotePeerId);
					continue;
				}

				// Determine if we have a PeerInfoList slot for this peer ID
				if (peer2Peer.getPeerInfoList().getPeerInfo(remotePeerId) == null) {
				    // Unknown remote peer ID
					System.out.println("Got an unknown remote peer ID: " + remotePeerId);
					continue;
				}

                // Got a valid peer ID
			    System.out.println("Got valid handshake from remote peer ID " + remotePeerId);

                // Store this connection in its approrpiate PeerInfoList slot
			    peer2Peer.getPeerInfoList().getPeerInfo(remotePeerId).setConnection(connection);
				break;
			}

			// Didn't get the remote peer ID yet, sleep and try again
			if (i != 9) {
			    System.out.println("Sleeping and trying to get handshake again");
			    sleep(sleepMilliseconds);
			}
		}
	}

	// Method to clean-up sleeps (don't have to ugly our code with the try/catch)
	private void sleep(int duration) {
		try {
			Thread.sleep(duration);
		}
		catch (InterruptedException e) {}
	}
}
