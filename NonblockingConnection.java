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
	private AsynchronousSocketChannel asynchronousSocketChannel;

	// The handler used by AsynchronousSocketChannel.read()
	private Handler handler;

	// The data received by read()
	private String data;

	// Send queue
	// This is used to queue sends when they fail
	// so that we can retry them later
	private Deque<String> sendQueue;

	// Constructor
	public NonblockingConnection(String hostname, int port) {
		// Set hostname and port
		this.hostname = hostname;
		this.port = port;

		// Instantiate the send queue
		sendQueue = new ArrayDeque<String>();
	}

	// Get data from the server
	public String getData() {
		// Our receiving buffer
		ByteBuffer receivingBuffer = ByteBuffer.allocateDirect(1024);

		// If AsynchronousSocketChannel is not already instantiated or
		// AsynchronousSocketChannel is not already open then try to
		// establish connection (again)
		if (asynchronousSocketChannel == null || asynchronousSocketChannel.isOpen() == false) {
		    // Channel not open, trying to establish connection
			openChannelEstablishConnection();
		}

		// Make the nonblocking read
		// Catch and "eat" ReadPendingException since calling getData() twice
		// while waiting for the first read() to succeed will result in this exception
		// and because this is actually an expected condition
		try {
			// Do the read
			asynchronousSocketChannel.read(receivingBuffer, receivingBuffer, handler);
		}
		catch (ReadPendingException e) {}

		// Display a message if we get disconnected
		if (handler.getIsDisconnected() == true) {
			// Try to establish connection again
		    // Disconnected, trying to establish connection
			openChannelEstablishConnection();
		}

		// Did the read succeed?
		if (handler.getIsDisconnected() == false && handler.getCompleted() == true) {
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

	// Send data to the server
	public void sendData(String data) {
		// If AsynchronousSocketChannel is not already instantiated or
		// AsynchronousSocketChannel is not already open then try to
		// establish connection (again)
		if (asynchronousSocketChannel == null || asynchronousSocketChannel.isOpen() == false) {
		    // Channel not open, trying to establish connection
			openChannelEstablishConnection();
		}

		// Place the data to be sent in the send queue
		sendQueue.add(data);

		// Now try to send the contents of the send queue
		// Stop sending on first failure
		while (sendQueue.peek() != null) {
			// Whether or not there was an error sending
			boolean sendError = false;

			// Get the data at the head of the queue
			String nextData = sendQueue.remove();

			// Convert the String to a ByteBuffer
			ByteBuffer sendingBuffer = ByteBuffer.wrap(nextData.getBytes());

			// Try to send
			try {
				// Send
				asynchronousSocketChannel.write(sendingBuffer).get();
			}
			catch (InterruptedException e) {
				sendError = true;
			}
			catch (ExecutionException e) {
				sendError = true;
			}

			// If there was a send error then place the item which we failed to send
			// back at the head of the queue and stop trying to send for now
			if (sendError == true) {
				// Place this item back at the head of the queue
				sendQueue.addFirst(nextData);

				// Stop trying to send
				break;
			}
		}
	}

	// Put an item at the head of the send queue
	private void putAtHeadOfSendQueue(String data) {
		// Instantiate a new send queue
		Deque<String> newSendQueue = new ArrayDeque<String>();

		// Place the data to be at the head of the new send queue
		// in the new send queue
		newSendQueue.add(data);

		// Now add the contents of the existing send queue to the
		// new send queue
		while (sendQueue.peek() != null) {
			newSendQueue.add(sendQueue.remove());
		}

		// Replace the existing send queue with the new send queue
		sendQueue = newSendQueue;
	}

	// Open channel and establish connection
	// 1. Instantiate the AsynchronousSocketChannel
	// 2. Instantiate Handler
	// 3. Perform connect()
	// Catch and "eat" AlreadyConnectedException since calling connect() twice
	// will result in this exception
	// Also catch ExecutionException which will arise if connection fails
	private void openChannelEstablishConnection() {
		try {
			// Instantiate Handler
			handler = new Handler();

			// Open channel
			asynchronousSocketChannel = AsynchronousSocketChannel.open();

			// Connect
			asynchronousSocketChannel.connect(new InetSocketAddress(hostname, port)).get();
		}
		catch (AlreadyConnectedException e) {
			// This is an okay condition
		}
		catch (ExecutionException e) {
			// Connection error, will try reconnecting on next call
		}
		catch (IOException e) {
			System.out.println("Caught IOException in openChannelEstablishConnection(): " + e.getMessage());
		}
		catch (InterruptedException e) {
			System.out.println("Caught InterruptedException in openChannelEstablishConnection(): " + e.getMessage());
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
