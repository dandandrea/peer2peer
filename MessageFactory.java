
import java.lang.*;

public class MessageFactory{
	// Constructor
	public MessageFactory() {
	}

	public static Message toMessage(String messageString){
		int type=-1;
		//parse the type from the message
		try{
			type = Integer.parseInt(messageString.subString(5));
		}
		catch(Exception e){
			System.out.println("ERROR: toMessage(): Failed to extract message type.");
			e.printStackTrace();
		}
		//if i was able to parse the messageString try to build a message object
		if(type !=-1){
			try{
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
				}
			}
			catch(Exception e){
				System.out.println("ERROR: toMessage(): Failed to construct message");
				e.printStackTrace();
			}
		}	
	}
}
