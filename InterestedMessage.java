import java.util.*;
import java.io.*;

public class InterestedMessage implements Message
{
	public InterestedMessage(){
	}

	public InterestedMessage(String message)
	{
		if ( Integer.parseInt(message.substring(4,5)) != 2)
			System.out.println(" ERROR: Invalid Message Type ");
		}
	}
	// To String
	public String toString()
	{
		return "00052";
	}
}
