public class PieceMessageHandler extends MessageHandler{

	public PieceMessageHandler(PeerInfoList peerInfoList){
		super(peerInfoList);
	}

	public void handleMessage(Message message){
		PieceMessage pieceMessage = (PieceMessage)message;

		System.out.println("PieceMessageHandler: "+ pieceMessage.toString());
		System.out.println("Do PieceMessage response");

		//parse the payload and update the file
		//if this peer is NOT choked
			//make prepare a request message.
		//else 
			//do nothing
	}
}