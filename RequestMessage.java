import java.util.*;
import java.io.*;

public class RequestMessage implements Message
{
	private int payload;
	private int length;
	private final int type = 6;

	// COnstructor for Request_M
	public RequestMessage(int wantedPiece)
	{	
		this.payload = wantedPiece;
		this.length = 5 + Integer.toString(payload).length();
	}
	
	// Deserialize Constructor
	public RequestMessage(String message)
	{
		if ( Integer.parseInt(message.substring(4,5)) != type)
		{
			System.out.println(" ERROR: Invalid Message Type ");
		}
		this.length = message.length();
		this.payload = Integer.parseInt(message.substring(5,length));
	}

	// To String
	public String toString()
	{	
		System.out.println("RequestMessage: About to send requestMessage: " +String.format("%04d", length) + type + payload);
		return String.format("%04d", length) + (type) + (payload);
	}

	// The get functions
	public int getLength()
	{
		return this.length;
	}
	
	public int getType()
	{
		return this.type;
	}

	public int getPayload()
	{
		return this.payload;
	}
}
