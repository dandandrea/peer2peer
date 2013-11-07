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

	// Constructor
	public ListenerThread(String hostname, int port, int sleepMilliseconds) {
	    this.hostname = hostname;
		this.port = port;
		this.sleepMilliseconds = sleepMilliseconds;
	}

	// Thread.run()
	public void run() {
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
					System.out.println("About to block");
					AsynchronousSocketChannel asynchronousSocketChannel = asynchronousSocketChannelFuture.get();
					System.out.println("Unblocked");

					// TODO: Do handshake
					System.out.println("TODO: Handshake");

					// TODO: Hand off asynchronous socket channel to PeerThread
					System.out.println("TODO: Hand off async socket channel to PeerThread");

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

	// Method to clean-up sleeps (don't have to ugly our code with the try/catch)
	private void sleep(int duration) {
		try {
			Thread.sleep(duration);
		}
		catch (InterruptedException e) {}
	}
}
