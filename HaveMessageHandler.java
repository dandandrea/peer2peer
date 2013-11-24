public class HaveMessageHandler extends MessageHandler{

	public HaveMessageHandler(PeerInfoList peerInfoList){
		super(peerInfoList);
	}

	public void handleMessage(Message message){
		HaveMessage haveMessage = (HaveMessage)message;

		System.out.println("HaveMessageHandler: "+ haveMessage.toString());

		//Do work to the pieceList or bitfield of the peer you are connected to. this might need to be a syncrinized.

		//compare what this peer had to what the remotePeer has

		//if remotePeer has a piece this peer does not have
			//thisPeer.sendMessage(new InterestedMessage());
		//else
			//thisPeer.sendMessage(new NotInterestedMessage());
	}
}