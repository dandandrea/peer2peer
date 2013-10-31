import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
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
				//set options
				//asynchronousServerSocketChannel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);

				//bind to local address
		       		asynchronousServerSocketChannel.bind(new InetSocketAddress(Server_IP, Server_Port));
				
				System.out.println("Waiting for Connections!");
				while(true)
				{
					System.out.println("At top of while(true) loop");

					Future<AsynchronousSocketChannel> asynchronousSocketChannelFuture =
				        asynchronousServerSocketChannel.accept();		

					System.out.println("Made it here");

					try (AsynchronousSocketChannel asynchronousSocketChannel = asynchronousSocketChannelFuture.get())
					{
						//set options
						asynchronousSocketChannel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);

						//Incoming message
						ByteBuffer incomingBuffer = ByteBuffer.allocateDirect(1024);
						asynchronousSocketChannel.read(incomingBuffer, incomingBuffer, handler);
						
						System.out.println(" Established Conection with " + asynchronousSocketChannel.getRemoteAddress());
						
						try
						{	
							while(true)
							{
								System.out.println(" The connection is sleeping ");
								// Spawned Thread
								Thread.sleep(5000);
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
			if (buffer != null)
			{
				buffer.flip();
				String message = Charset.defaultCharset().decode(buffer).toString();
				buffer.clear();
				// Message received from client
				System.out.println(" Message received from client " + message);
			}
		}
		
		public void failed(Throwable exception, ByteBuffer buffer) 
		{
	                // Display an error message and thrown an exception
	                System.out.println("***** FAIL in Handler.failed() *****");
	                throw new UnsupportedOperationException("read failed!");
	        }
	}		
}
