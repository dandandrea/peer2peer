import java.util.*;
import java.io.*;

public class HaveMessage implements Message
{
	// The piece number
	private int pieceNumber;

	// Constructor for Have message
	public HaveMessage(int pieceNumber)
	{	
		// Set the piece numer
		this.pieceNumber = pieceNumber;
	}
	
	// Deserialize Constructor
	public HaveMessage(String message)
	{
		if ( message.substring(4,5).equals("4") == false )
		{
			System.out.println(" ERROR in HaveMessage: Invalid Message Type (" + message.substring(4, 5) + ")");
		}

		// Extract the piece number
		pieceNumber = Integer.parseInt(message.substring(5, message.length()));
	}

	// To String
	public String toString()
	{
		// Serialize
		String payload = new Integer(pieceNumber).toString();
		return String.format("%04d", 4 + 1 + payload.length()) + "4" + payload;
	}

	// The get functions
	public int getHavePieceNumber(){
		return pieceNumber;
	}
}
