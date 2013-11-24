public class RequestMessageHandler extends MessageHandler{

	public RequestMessageHandler(PeerInfoList peerInfoList){
		super(peerInfoList);
	}

	public void handleMessage(Message message){
		RequestMessage requestMessage = (RequestMessage)message;

		System.out.println("RequestMessageHandler: "+ requestMessage.toString());
		System.out.println("Do RequestMessage response");


		// if remotePeer is a prefered neighbor or optimistically unchocked
			// get piece and prepare pieceMessage for sending
		//else
			//no nothing.
	}
}