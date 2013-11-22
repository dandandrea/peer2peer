import java.util.*;
import java.io.*;

public class BitfieldMessage implements Message
{
	// Properties of Bitfield	
	private String payload;
	private int length;
	private final int type = 5;
	private String message;

	// Constructor
	public BitfieldMessage()
	{
		message = "Hello World";	
		this.length = 5 + message.length();
	}

	// To String
	public String toString()
	{
		return length + (type + message);
	}

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
