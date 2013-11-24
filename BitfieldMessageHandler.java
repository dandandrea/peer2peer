public class BitfieldMessageHandler extends MessageHandler{

	public BitfieldMessageHandler(PeerInfoList peerInfoList){
		super(peerInfoList);
	}

	public void handleMessage(Message message){
		BitfieldMessage bitfieldMessage = (BitfieldMessage)message;

		System.out.println("BitfieldMessageHandler: "+ bitfieldMessage.toString());
		//Do work to the bitfield of the peer you are connected to. this might need to be a syncrinized.

		//if remotePeer has pieces this peer does not have
			//thisPeer.sendMessage(new InterestedMessage());
		//else
			//thisPeer.sendMessage(new NotInterestedMessage());
	}
}