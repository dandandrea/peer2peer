import java.util.*;
import java.io.*;

public class HaveMessage implements Message
{
	private final int type = 4;
	private int length;
	private String payload;
	
	// Constructor for Have message
	public HaveMessage(int havePiece)
	{	
		this.payload = Integer.toString(havePiece);
		this.length = 5 + payload.length();
	}
	
	// Deserialize Constructor
	public HaveMessage(String message)
	{
		if ( Integer.parseInt(message.substring(5,6)) == type)
		{
			System.out.println(" ERROR: Invalid Message Type ");
		}
		this.length = message.length();
		this.payload = message.substring(6,length);
	}

	// To String
	public String toString()
	{
		return length + (type + payload);
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

	public String getPayload()
	{
		return this.payload;
	}

}
