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

	private int length;
	private byte type;
	
	public MessageType(int length, byte type)
	{	
		this.length = length;	
		this.type = type;
	}

	public void length(int len)
	{
		this.length = len;
	}

	public int getLength()
	{
		return this.length;
	}

	public byte getType()
	{
		return this.type;
	}
}
