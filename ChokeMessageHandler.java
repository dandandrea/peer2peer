public class ChokeMessageHandler extends MessageHandler{

	public ChokeMessageHandler(PeerInfoList peerInfoList){
		super(peerInfoList);
	}

	public void handleMessage(Message message){
		ChokeMessage chokeMessage = (ChokeMessage)message;

		System.out.println("ChokeMessageHandler: "+ chokeMessage.toString());
		System.out.println("Do bitfieldMessage response");


		//set choked boolean to true.
		// allow the piece currently requested to be requested by a different peerThead.

	}
}