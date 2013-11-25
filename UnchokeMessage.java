import java.util.*;
import java.io.*;

public class UnchokeMessage implements Message
{
private final int type = 1;
	private int length;
	private String payload;

	// Constructor for Unchoke_M
	public UnchokeMessage()
	{	
		this.payload = " SCUMBAG I am Unchoking YOU For some reason I can't explain!! ";
		this.length = 5 + payload.length();
	}

	// Deserialize Constructor
	public UnchokeMessage(String message)
	{
		if ( Integer.parseInt(message.substring(4,5)) != type)
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
