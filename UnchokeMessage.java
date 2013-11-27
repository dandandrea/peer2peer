import java.util.*;
import java.io.*;

public class UnchokeMessage implements Message
{


	// Constructor for Unchoke_M
	public UnchokeMessage()
	{	
		
	}

	// Deserialize Constructor
	public UnchokeMessage(String message)
	{
		if ( Integer.parseInt(message.substring(4,5)) != 1)
		{
			System.out.println(" ERROR: Invalid Message Type ");
		}
	}
	// To String
	public String toString()
	{
		return "00051";
	}
}
