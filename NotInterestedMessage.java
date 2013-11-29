import java.util.*;
import java.io.*;

public class NotInterestedMessage implements Message
{
		public NotInterestedMessage(){
	}

	public NotInterestedMessage(String message)
	{
		if ( Integer.parseInt(message.substring(4,5)) != 3 ){
			System.out.println(" ERROR: Invalid Message Type ");
		}
	}
	// To String
	public String toString()
	{
		return "00053";
	}
}
