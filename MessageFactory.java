/*
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
		if(type !=-1){
			try{
				switch (type){
					case 0: 
						return new ChokeMessage();
					case 1:
						return new UnchokeMessage();
					case 2:
						return new InterestedMessage();
					case 3:
						return new NotInterestedMessage();
					case 4:
						return new HaveMessage();
					case 5:
						return new BitfieldMessage();
					case 6:
						return new RequestMessage();
					case 7:
						return new PieceMessage();
				}
			}
			catch(Exception e){
				System.out.println("ERROR: toMessage(): Failed to construct message");
				e.printStackTrace();
			}
		}	
	}
}
*/