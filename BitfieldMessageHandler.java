public class BitfieldMessageHandler extends MessageHandler{

	public BitfieldMessageHandler(PeerInfoList peerInfoList , int remotePeerId){
		super(peerInfoList , remotePeerId);
	}

	public void handleMessage(Message message){
		BitfieldMessage bitfieldMessage = (BitfieldMessage)message;

		System.out.println("BitfieldMessageHandler: "+ bitfieldMessage.getMessage());

		boolean interested = false;

		for (int piece :  bitfield.pieceList){
			if(getPeerInfoList().getPeerInfo(remotePeerId).getPieceList().contains(piece)  == false){
				getPeerInfoList().getPeerInfo(remotePeerId).getPieceList().add(piece);
			}
			if(interested == false && getPeerInfoList().getPeerInfo(Peer2Peer.peer2Peer.getPeerId()).getPieceList().contains(piece) == false){
				interested = true;
			}
		}

		//if remotePeer has pieces this peer does not have
		if(interested){
			peerInfoList.getPeerInfo(remotePeerId).getPeerThread().sendMessage(new InterestedMessage());
		}
		else{
				peerInfoList.getPeerInfo(remotePeerId).getPeerThread().sendMessage(new NotInterestedMessage());
		}
	}
}