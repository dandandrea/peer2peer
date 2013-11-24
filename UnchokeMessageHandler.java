public class UnchokeMessageHandler extends MessageHandler{

	public UnchokeMessageHandler(PeerInfoList peerInfoList , int remotePeerId){
		super(peerInfoList , remotePeerId);
	}

	public void handleMessage(Message message){
		UnchokeMessage unchokeMessage = (UnchokeMessage)message;

		System.out.println("UnchokeMessageHandler: "+ unchokeMessage.toString());
		System.out.println("Do UnchokeMessage response");

		// set choked boolean to false;
		// find piece and prepare a pieceMessage for a piece this peer does not have
		// send that piece 

	}
}