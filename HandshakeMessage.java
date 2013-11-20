import java.util.*;
import java.io.*;

public class HandshakeMessage {
	private String message;

	public HandshakeMessage(int peerID){	
		message = "HELLO00000000000000000000000000"+peerID;
	}
	
	public String getPayload(){
		return this.message;
	}

	public String toString(){
		return message;
	}
}
