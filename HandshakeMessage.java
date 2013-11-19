import java.util.*;
import java.io.*;

public class HandshakeMessage 
{
	//private static byte have 	 = 4;
	
	private int peerID;
	private static int length = 32;
	private String payload;

	public HandshakeMessage(int peerID){	
		payload = "HELLO "+peerID;
	}
	
	public String getPayload(){
		return this.payload;
	}

	public String toString(){
		return payload;
	}
}
