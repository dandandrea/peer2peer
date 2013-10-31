import java.util.*;
import java.io.*;

public class PieceMessage extends MessageType
{
	//private static byte piece 	 = 7;

	int payload;

	public PieceMessage(int length, byte type, int payload)
	{	
		super(length,type);
		this.payload = payload;
	}

	public int getPayload()
	{
		return this.payload;
	}
}
