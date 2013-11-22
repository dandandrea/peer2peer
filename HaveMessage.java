import java.util.*;
import java.io.*;

public class HaveMessage 
{
	//private static byte have 	 = 4;
	
	private int payload;
	
	public HaveMessage(int length, byte type, int payload)
	{	
		this.payload = payload;
	}
	
	public int getPayload()
	{
		return this.payload;
	}

}
