import java.util.*;
import java.io.*;

public class BitfieldMessage extends MessageType
{
	//private static byte bitfield	 = 5;

	private int payload;

	public BitfieldMessage(int length,byte type, int payload)
	{	
		super(length,type);
		this.payload = payload;
	}

	public int getPayload()
	{
		return this.payload;
	}
}
