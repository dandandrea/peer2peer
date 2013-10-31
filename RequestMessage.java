import java.util.*;
import java.io.*;

public class RequestMessage extends MessageType
{
	//private static byte request	 = 6;

	private int payload;

	public RequestMessage(int length, byte type, int payload)
	{	
		super(length,type);
		this.payload = payload;
	}

	public int getPayload()
	{
		return this.payload;
	}
}
