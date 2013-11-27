
import java.lang.*;

public class MessageFactory{
	// Constructor
	public MessageFactory() {
	}

	public static Message toMessage(String messageString) throws Peer2PeerException {
		int type=-1;
		//parse the type from the message
		try{
			System.out.println("MessageFactory: The substring is: " + messageString.substring(4,5));
			type = Integer.parseInt(messageString.substring(4,5));
		}
		catch(Exception e){
			throw new Peer2PeerException("ERROR: toMessage(): Failed to extract message type (got " + type + ")");
		}
		//if i was able to parse the messageString try to build a message object
		if(type !=-1){
			switch (type){
				case 0: 
					return new ChokeMessage(messageString);
				case 1:
					return new UnchokeMessage(messageString);
				case 2:
					return new InterestedMessage(messageString);
				case 3:
					return new NotInterestedMessage(messageString);
				case 4:
					return new HaveMessage(messageString);
				case 5:
					return new BitfieldMessage(messageString);
				case 6:
					return new RequestMessage(messageString);
				case 7:
					return new PieceMessage(messageString);
				default:
					throw new Peer2PeerException("ERROR: MessageFactory: toMessage(): unknown message type: "+ type);
			}
		}
		else {
			throw new Peer2PeerException("ERROR: Unable to get message type");
		}
	}
}
