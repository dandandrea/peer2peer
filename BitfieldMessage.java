import java.util.*;
import java.io.*;

public class BitfieldMessage implements Message
{
	// Properties of Bitfield	
	private String payload;
	private int length;
	private final int type = 5;
	private String message;

	// Constructor for Bitfield_M
	public BitfieldMessage()
	{
		message = "Hello World";	
		this.length = 5 + message.length();
	}

	// Deserialize Constructor
	public BitfieldMessage(String message)
	{
		if ( Integer.parseInt(message.substring(4,5)) == type)
		{
			System.out.println(" ERROR: Invalid Message Type ");
		}
		this.length = message.length();
		this.message = message.substring(5,length);
	}

	// To String
	public String toString()
	{
		return String.format("%04d", length) + (type + message);
	}

	// The get functions
	public String getPayLoad()
	{
		return this.payload;
	}

	public int getType()
	{
		return this.type;
	}

	public int getLength()
	{
		return this.length;
	}
	
}
