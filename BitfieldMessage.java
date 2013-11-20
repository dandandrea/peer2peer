import java.util.*;
import java.io.*;

public class BitfieldMessage extends MessageType
{
	

	private String message;

	public BitfieldMessage()
	{	
		super(5);	
			
		buildPayload();	
		buildMessage();
	}

}
