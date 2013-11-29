import java.util.*;
import java.io.*;

public class BitfieldMessage implements Message
{
	// Properties of Bitfield	
	private List<Integer> pieceList;
	private final int type = 5;
	private String message;

	// Constructor for Bitfield_M
	public BitfieldMessage(List<Integer> pieceList)
	{
		this.pieceList = pieceList;
		this.message = convertList(pieceList);
		System.out.println(pieceList.toString());
	}

	// Deserialize Constructor
	public BitfieldMessage(String message) 
	{
		if ( Integer.parseInt(message.substring(4,5)) != type)
		{
			System.out.println(" ERROR: Invalid Message Type ");
		}
		this.message = deserializeConvert(message.substring(5,message.length()));
		
		restructurePieceList();
	}

	//Creates the pieceList List
	private void restructurePieceList()
	{
		this.pieceList = new ArrayList<Integer>();

		for ( int i=0; i< this.message.length(); i++)
		{
			if( this.message.charAt(i) == '1')
			{
				pieceList.add(i);
			}
		}
	}

	// Converts from ascii string to bit strings
	private String deserializeConvert(String message)
	{
		// gets bits of individual character of string
		String bitString = "";
		for (int i = 0; i < message.length(); i++) 
		{
			bitString+= getBits( (byte) ((int)message.charAt(i) & 0xff >> 0));
		}
		return bitString;
	}

	// Converts from ascii string to bit strings
	private String getBits( byte inByte )
	{
		// Go through each bit with a mask
		StringBuilder builder = new StringBuilder();

		for ( int j = 0; j < 8; j++ )
		{
			// Shift each bit by 1 starting at zero shift
			byte tmp = (byte) ( inByte >> j );

			// Check byte with mask 00000001 for LSB
			int expect1 = tmp & 0x01;

			builder.append(expect1);
		}

		return ( builder.reverse().toString() );
	}

	// Converts pieceList into bit string
	private String convertList(List<Integer> pieceList)
	{
		int mod = pieceList.size() % 8;
		String[] newList = new String[ pieceList.size() + ((mod == 0) ? 0:(8-mod)) ];
		String asciiString = "";
		
		// Initialize
		for (int i=0; i< newList.length; i++)
		{
			newList[i] = "0";
		}

		// Adds 1 if the piece lists has index
		for (int i= 0; i < pieceList.size(); i++)
		{
			int j = (int)pieceList.get(i);
			newList[j] = "1";
		}

		// Cats Strings
		for(int i=0; i < newList.length; i++)
		{
			asciiString += newList[i];
		} 
		
		// Changes numbers of string object into ascii of string object
		try
		{		
			asciiString =  toText(asciiString);
		}
		catch( Exception e )
		{
			System.out.println(" ERROR: Unsupported Encoding Exception ");
		}
		
		return asciiString;
	}

	//Takes a binary String and converts it to 8bit acsii.
	private String toText(String info) throws UnsupportedEncodingException
	{
		System.out.println("ASDK:FHASDFASDFASDFASDf: "+ info);
		String returnString = "";
		for (int i = 0; i < info.length()/8; i++) {
			int charCode = Integer.parseInt(info.substring(0+(i*8), (i+1)*8), 2);
			returnString += new Character((char)charCode).toString();
		}
		System.out.println("returnString BitfieldMessage: "+ returnString);

		return returnString;
	}

	// To String
	public String toString()
	{
		System.out.println("I have this message:  " + message + " with length: "+message.length());
		return String.format("%04d", message.length() + 5) + (type + message);
	}

	// The get functions

	public List<Integer> getPieceList()
	{
		return pieceList;
	}

	public String getMessage()
	{
		return this.message;
	}

	public int getType()
	{
		return this.type;
	}	
}
