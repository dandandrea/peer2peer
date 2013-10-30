import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.io.*;
import java.util.*;


public class ListenerThread 
{
	final static String Server_IP = "127.0.0.1";
	final static int Server_Port = 8000;

	public static void main(String[] args)
	{

		// The handler used by AsynchronousSocketChannel.read()
		Handler handler = new Handler();		

		try(AsynchronousServerSocketChannel asynchronousServerSocketChannel = AsynchronousServerSocketChannel.open())
		{
		
			if (asynchronousServerSocketChannel.isOpen())
			{
				//bind to local address
		       		 asynchronousServerSocketChannel.bind(new InetSocketAddress(Server_IP, Server_Port));
	 			
				System.out.println("Waiting for Connections!");
				while(true)
				{
					Future<AsynchronousSocketChannel> asynchronousSocketChannelFuture =
				        asynchronousServerSocketChannel.accept();		

					try (AsynchronousSocketChannel asynchronousSocketChannel = asynchronousSocketChannelFuture.get())
					{
						System.out.println(" Established Conection with " + asynchronousSocketChannel.getRemoteAddress());
						
						try
						{
							while(true)
							{
								//Incoming message
								ByteBuffer incomingBuffer = ByteBuffer.allocateDirect(1024);
								asynchronousSocketChannel.read(incomingBuffer, incomingBuffer, handler);
								
								// Spawned Thread
								Thread.sleep(10000);

								// replying message to connected client
								ByteBuffer outgoing_Reply = ByteBuffer.wrap("Hello Sir \n".getBytes());
					 			asynchronousSocketChannel.write(outgoing_Reply).get();						
							}
						}
						catch( Exception e )
						{ }

						/*// replying message to connected client
						ByteBuffer outgoing_Reply = ByteBuffer.wrap("Hello Sir \n".getBytes());
						asynchronousSocketChannel.write(outgoing_Reply).get();*/
					
					}
					catch (ReadPendingException e) {}

					/*// Throw an exception if we get disconnected
					if (handler.getIsDisconnected() == true) 
					{
					 System.out.println("Disconnected");
						throw new IOException("Disconnected");
				 	}*/
				}
			}
		} 
		catch (IOException | InterruptedException | ExecutionException error)
		{
			System.err.println(error);
		}
	}

	private static class Handler implements CompletionHandler<Integer, ByteBuffer>
	{
		public void completed(Integer result, ByteBuffer buffer)
		{	
			buffer.flip();
			String message = Charset.defaultCharset().decode(buffer).toString();
			buffer.clear();

			// Message received from client
			System.out.println(" Message received from client " + message);
		}
		
		public void failed(Throwable exception, ByteBuffer buffer) 
		{
	                // Display an error message and thrown an exception
	                System.out.println("***** FAIL in Handler.failed() *****");
	                throw new UnsupportedOperationException("read failed!");
	        }
	}		

		/*// Implementation of CompletionHandler for AsynchronousSocketChannel.read()
        	private static class Handler implements CompletionHandler<Integer, ByteBuffer> 
		{
		        // The state of the handler
		        private boolean completed = false;
		        private boolean isDisconnected = false;

		        // Called when read() succeeds
		        public void completed(Integer result, ByteBuffer buffer) 
			{
		                // Are we disconnected? Result length will be -1 if disconnected
		                if (result == -1)
				{
		                        isDisconnected = true;
		                }

		                // Clear the buffer
		                buffer.clear();

		                // Get the data
		                if (isDisconnected == false) 
				{
		                        // Set data
		                        String data = Charset.defaultCharset().decode(buffer).toString();
		                }
			        
			        // Set completed to true
			        completed = true;

			        // "Flip" the buffer
			        buffer.flip();
		        }

		        // Called when a failure occurs
		        public void failed(Throwable exception, ByteBuffer buffer) 
			{
		                // Set completed to false
		                completed = false;

		                // Display an error message and thrown an exception
		                System.out.println("***** FAIL in Handler.failed() *****");
		                throw new UnsupportedOperationException("read() failed!");
		        }

		        // Getter for completed
		        public boolean getCompleted() 
			{
		                return completed;
		        }

		        // Reset the completed flag
		        public void resetCompleted() 
			{
		         	completed = false;
		        }

		        // Getter for isDisconnected
		        public boolean getIsDisconnected() 
			{
		                return isDisconnected;
		        }
		}*/
	}
