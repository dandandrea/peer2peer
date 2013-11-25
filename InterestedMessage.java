import java.util.*;
import java.io.*;

public class InterestedMessage implements Message
{
	private final int type = 2;
	private int length;

	// Constructor for Interested_M
	public InterestedMessage()
	{	
		this.length = 5;
	}

	// Deserialize Constructor
	public InterestedMessage(String message)
	{
		if ( Integer.parseInt(message.substring(4,5)) == type)
		{
			System.out.println(" ERROR: Invalid Message Type ");
		}
		this.length = message.length();
	}

	// To String
	public String toString()
	{
		return String.format("%04d", length) + (type);
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
