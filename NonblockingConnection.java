import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;
import java.util.*;
import java.util.concurrent.*;

// This class implements the abstraction for a connection to a server
public class NonblockingConnection {
	// The hostname and port for the connection
	private String hostname;
	private int port;

	// The connection's AsynchronousSocketChannel
	AsynchronousSocketChannel asynchronousSocketChannel;

	// The handler used by AsynchronousSocketChannel.read()
	Handler handler;

	// The data received by read()
	String data;

	// Constructor
	public NonblockingConnection(String hostname, int port) throws IOException, InterruptedException, ExecutionException {
		// Set hostname and port
		this.hostname = hostname;
		this.port = port;

		// Setup the connection
		setupConnection();
	}

	// Get data from the server
	public String getData() throws IOException {
		// Our receiving buffer
		ByteBuffer receivingBuffer = ByteBuffer.allocateDirect(1024);

		// Make the nonblocking read
		// Catch and "eat" ReadPendingException since calling getData() twice
		// while waiting for the first read() to succeed will result in this exception
		// and because this is actually an expected condition
		try {
			// Do the read
			asynchronousSocketChannel.read(receivingBuffer, receivingBuffer, handler);
		}
		catch (ReadPendingException e) {}

		// Throw an exception if we get disconnected
		if (handler.getIsDisconnected() == true) {
		    System.out.println("Disconnected");
			throw new IOException("Disconnected");
		}

		// Did the read succeed?
		if (handler.getCompleted() == true) {
			// data will hold the data that the handler received upon completion
			// Copy this to _data so that we can return it
			String _data = data;

			// Nullify data since we are about to return a copy of it
			data = null;

			// Reset completed flag
			handler.resetCompleted();

			// Return the data that the handler received
			return _data;
		} else {
			// Handler hasn't returned from last read()
			// Return null as data
			return null;
		}
	}

	// Setup the connection
	private void setupConnection() throws IOException, InterruptedException, ExecutionException {
		// Instantiate the AsynchronousSocketChannel
		asynchronousSocketChannel = AsynchronousSocketChannel.open();

		// If open...
		if (asynchronousSocketChannel.isOpen() == true) {
			// Connect to the specified hostname and port
			Void connect = asynchronousSocketChannel.connect(new InetSocketAddress(hostname, port)).get();

			// If connected...
			if (connect == null) {
				// Instantiate the Handler
				// This will be used for read() calls
				handler = new Handler();
			} else {
				throw new IOException("Unexpected state: connect is not null");
			}
		} else {
			throw new IOException("Unexpected state: Channel is not open");
		}
	}

	// Implementation of CompletionHandler for AsynchronousSocketChannel.read()
	private class Handler implements CompletionHandler<Integer, ByteBuffer> {
		// The state of the handler
		private boolean completed = false;
		private boolean isDisconnected = false;

		// Called when read() succeeds
		public void completed(Integer result, ByteBuffer buffer) {
			// Are we disconnected? Result length will be -1 if disconnected
			if (result == -1) {
				isDisconnected = true;
			}

			// Clear the buffer
			buffer.clear();

			// Get the data
			if (isDisconnected == false) {
				// Set data
				data = Charset.defaultCharset().decode(buffer).toString();
			}
			
			// Set completed to true
			completed = true;

			// "Flip" the buffer
			buffer.flip();
		}

		// Called when a failure occurs
		public void failed(Throwable exception, ByteBuffer buffer) {
			// Set completed to false
			completed = false;

			// Display an error message and thrown an exception
			System.out.println("***** FAIL in Handler.failed() *****");
			throw new UnsupportedOperationException("read() failed!");
		}  

		// Getter for completed
		public boolean getCompleted() {
			return completed;
		}

		// Reset the completed flag
		public void resetCompleted() {
		    completed = false;
		}

		// Getter for isDisconnected
		public boolean getIsDisconnected() {
			return isDisconnected;
		}
	}
}
