public class BitfieldMessageHandler extends MessageHandler{

	public BitfieldMessageHandler(PeerInfoList peerInfoList){
		super(peerInfoList);
	}

	public void handleMessage(Message message){
		BitfieldMessage bitfieldMessage = (BitfieldMessage)message;

		System.out.println("BitfieldMessageHandler: "+ bitfieldMessage.toString());
		System.out.println("Do bitfieldMessage response");
	}
}