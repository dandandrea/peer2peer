public class BitfieldMessageHandler extends MessageHandler{

	public BitfieldMessageHandler(PeerInfoList peerInfoList , int remotePeerId){
		super(peerInfoList , remotePeerId);
	}

	public void handleMessage(Message message){
		BitfieldMessage bitfieldMessage = (BitfieldMessage)message;

		System.out.println("BitfieldMessageHandler: "+ bitfieldMessage.getMessage());

		boolean interested = false;

        Peer2Peer.peer2Peer.getHaveLock().lock();
        try {
            for (int piece :  bitfieldMessage.getPieceList()){
                System.out.println("bfmh: piece from "+ remotePeerId+ ": is : "+ piece);
                if(getPeerInfoList().getPeerInfo(remotePeerId).getPieceList().contains(piece)  == false){
                    getPeerInfoList().getPeerInfo(remotePeerId).getPieceList().add(piece);
                }
                if(interested == false && getPeerInfoList().getPeerInfo(Peer2Peer.peer2Peer.getPeerId()).getPieceList().contains(piece) == false){
                    interested = true;
                }
            }
        }
        finally {
            Peer2Peer.peer2Peer.getHaveLock().unlock();
        }

		peerInfoList.getPeerInfo(remotePeerId).getPeerThread().getLock().lock();
		try{
					//if remotePeer has pieces this peer does not have
			if(interested){
				peerInfoList.getPeerInfo(remotePeerId).getPeerThread().sendMessage(new InterestedMessage());
			}
			else{
				peerInfoList.getPeerInfo(remotePeerId).getPeerThread().sendMessage(new NotInterestedMessage());
			}
		}
		finally{
			getPeerInfoList().getPeerInfo(remotePeerId).getPeerThread().getLock().unlock();
		}
	}
}
