public class BitfieldMessageHandler extends MessageHandler{

	public BitfieldMessageHandler(PeerInfoList peerInfoList , int remotePeerId){
		super(peerInfoList , remotePeerId);
	}

	public void handleMessage(Message message){
		BitfieldMessage bitfieldMessage = (BitfieldMessage)message;

		System.out.println("BitfieldMessageHandler: "+ bitfieldMessage.toString());

		System.out.println("adding 1 to remotePeer's pieceList: "+getPeerInfoList().getPeerInfo(remotePeerId).getPieceList().add(1));
		System.out.println(getPeerInfoList().getPeerInfo(remotePeerId).getPieceList().get(0));


		//Do work to the bitfield of the peer you are connected to. this might need to be a syncrinized.

		//if remotePeer has pieces this peer does not have
			peerInfoList.getPeerInfo(remotePeerId).getPeerThread().sendMessage(new InterestedMessage());
			//thisPeer.sendMessage(new InterestedMessage());
		//else
			//thisPeer.sendMessage(new NotInterestedMessage());
	}
}