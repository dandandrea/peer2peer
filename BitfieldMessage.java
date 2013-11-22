import java.util.*;
import java.io.*;

public class BitfieldMessage extends MessageType
{
	

	private String message;

	public BitfieldMessage()
	{	
		super(5);	
			
		buildPayload();	
		super.buildMessage();
	}

	//this should be in the subclasses..this is for testing.
	protected void buildPayload(){
		payload = "asldkfhkasdf";
	}
}
