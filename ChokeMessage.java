import java.util.*;
import java.io.*;

public class ChokeMessage implements Message
{
	private final int type = 0;
	private int length;

	// Constructor for Choke_M
	public ChokeMessage()
	{	
		this.length = 5 + payload.length();
	}

	// Deserialize Constructor
	public ChokeMessage(String message)
	{
		if ( Integer.parseInt(message.substring(4,5)) == type)
		{
			System.out.println(" ERROR: Invalid Message Type ");
		}
		this.length = message.length();
		this.payload = message.substring(5,length);
	}

	// To String
	public String toString()
	{
		return String.format("%04d", length) + (type + payload);
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
}
