public class MessageFactory{
	// Constructor
	public MessageFactory() {
	}

	public static Message toMessage(String messageString){
		//parse the type from the message
		int type = Integer.parseInt(messageString.subString(5));

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
}
