public class ChokeMessageHandler extends MessageHandler{

	public ChokeMessageHandler(PeerInfoList peerInfoList , int remotePeerId){
		super(peerInfoList , remotePeerId);
	}

	public void handleMessage(Message message){
		ChokeMessage chokeMessage = (ChokeMessage)message;

		System.out.println("ChokeMessageHandler: "+ chokeMessage.toString());

		//set that i was choked by them
		peerInfoList.getPeerInfo(remotePeerId).setAmChokedBythem(true);

		//get a handle on the peer thead that is handling this message.
		PeerThread peerThread = peerInfoList.getPeerInfo(remotePeerId).getPeerThread();

		// get the requestedPiece
		int requestReturnNumber = peerThread.getRequestedPiece();

		//if this peerThread has a requestedPiece pending by remotePeer 
		//check that piece back in.
		if(requestReturnNumber != -1){

			//lock doNotHasList
			peerThread.getPeer2Peer().getLock().lock();
			try{
				//check that piece back in
				peerThread.getPeer2Peer().getDoNotHaveList().add(requestReturnNumber);
	
			}
			finally{

			//unlock doNotHasList
			peerThread.getPeer2Peer().getLock().lock();	

			}

			// update peerThead.requestedPiece to reflect that the piece was checked back in.
			peerThread.setRequestedPiece(-1);
		}
	}
}