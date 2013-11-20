import java.util.*;
import java.io.*;

public class MessageType
{
	/*private static byte coke 	 = 0;
	private static byte uncoke 	 = 1;
	private static byte interested    = 2;
	private static byte notInterested = 3;
	private static byte have 	 = 4;
	private static byte bitfield	 = 5;
	private static byte request	 = 6;
	private static byte piece 	 = 7;*/

	protected int length;
	protected int type;
	protected String payload;
	protected String message;
	
	public MessageType(int length, int type)
	{	
		this.length = length;	
		this.type = type;
	}

	public MessageType(int type)
	{	
		length = 0;	
		this.type = type;
	}
	protected void buildMessage(){
		length = 5+payload.length();
		//System.out.println("type: "+type);
		//System.out.println("length :" +length);
		message = length+(type+payload);
		//System.out.println("message :"+ message);
	}
	
	//this should be in the subclasses..this is for testing.
	protected void buildPayload(){
		payload = "asldkfhkasdf";
	}	
	
	public void length(int len)
	{
		this.length = len;
	}

	public int getLength()
	{
		return this.length;
	}

	public int getType()
	{
		return this.type;
	}

	public String toString(){
		return message;	
	}
}
