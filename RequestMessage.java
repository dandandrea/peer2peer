import java.util.*;
import java.io.*;

public class RequestMessage 
{
	//private static byte request	 = 6;

	private int payload;

	public RequestMessage(int length, byte type, int payload)
	{	
		this.payload = payload;
	}

	public int getPayload()
	{
		return this.payload;
	}
}
