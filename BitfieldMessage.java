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
	public BitfieldMessage(List pieceList)
	{
		this.message = convertList(pieceList);
		this.length = 5 + message.length();
	}

	// Deserialize Constructor
	public BitfieldMessage(String message)
	{
		if ( Integer.parseInt(message.substring(4,5)) == type)
		{
			System.out.println(" ERROR: Invalid Message Type ");
		}
		
		byte[] inBytes = message.getBytes();
		this.message = getBits(inBytes);
		
		this.length = message.length();
		this.message = message.substring(5,length);
	}

	// Converts from acsii string to bit strings
	private String getBits( byte[] inByte )
	{
		// Go through each bit with a mask
		StringBuilder builder = new StringBuilder();
		for (int i=0; i<inByte.length; i++)
		{
			for ( int j = 0; j < 8; j++ )
			{
				// Shift each bit by 1 starting at zero shift
				byte tmp = (byte) ( inByte[i] >> j );

				// Check byte with mask 00000001 for LSB
				int expect1 = tmp & 0x01;

				builder.append(expect1);
			}
		}
		return ( builder.reverse().toString() );
	}

	// Converts pieceList into bit string
	private String convertList(List pieceList)
	{
		String[] newList = new String[pieceList.size() + (pieceList.size() % 8)];
		String acsiiString = "";
		
		// Initialize
		for (int i=0; i< newList.length; i++)
		{
			newList[i] = "0";
		}

		// Adds 1 if the piece lists has index
		for (int i=0; i<pieceList.size(); i++)
		{
			for (int j=0; j < pieceList.size(); j++) 
				
				if (i+1 == pieceList.get(j))
				{
					newList[i] = "1";
				}
		}

		// Cats Strings
		for(int i=0; i <= newList.length; i++)
		{
			acsiiString += newList[i];
		} 

		try
		{		
			acsiiString =  toText(acsiiString);
		}
		catch( Exception e )
		{
			System.out.println(" ERROR: Unsupported Encoding Exception ");
		}
		
		return acsiiString;
	}

	//Takes a binary String and converts it to 8bit acsii.
	private String toText(String info) throws UnsupportedEncodingException
	{
		String returnString = "";
		for (int i = 0; i < info.length()/8; i++) {
			int charCode = Integer.parseInt(info.substring(1+(i*8), (i+1)*8), 2);
			returnString += new Character((char)charCode).toString();
		}
		return returnString;
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
