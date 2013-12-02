public class UnchokeMessageHandler extends MessageHandler{

	public UnchokeMessageHandler(PeerInfoList peerInfoList , int remotePeerId){
		super(peerInfoList , remotePeerId);
	}

	public void handleMessage(Message message){
		UnchokeMessage unchokeMessage = (UnchokeMessage)message;

		System.out.println("UnchokeMessageHandler: unchoked by "+remotePeerId+" : "+ unchokeMessage.toString());

		// Write to log
		Peer2Peer.peer2Peer.writeToLog("Peer [peer_ID " + Peer2Peer.peer2Peer.getPeerId() + "] is unchoked by [peer_ID " + remotePeerId + "].");

		// set choked boolean to false;
		// find piece and prepare a pieceMessage for a piece this peer does not have
		// send that piece 

		//set that i was choked by them to false
		peerInfoList.getPeerInfo(remotePeerId).setAmChokedBythem(false);

		//get a handle on the peer thead that is handling this message.
		PeerThread peerThread = peerInfoList.getPeerInfo(remotePeerId).getPeerThread();

		// get the requestedPiece
		int request = peerThread.getRequestedPiece();
		System.out.println("UnchokeMessageHandler: checking current requestPiece "+request);


		//if this peerThread has a requestedPiece pending by remotePeer 
		//check that piece back in.
		if(request == -1){

			int checkoutPiece = -1;

			System.out.println("UnchokeMessageHandler: trying to get lock: "+remotePeerId);
			//lock doNotHasList
			peerThread.getPeer2Peer().getLock().lock();
			System.out.println("UnchokeMessageHandler: got the lock: "+remotePeerId);

			try{

				//check out a piece
				checkoutPiece = peerThread.getPeer2Peer().checkoutPiece(remotePeerId);
				System.out.println("UnchokeMessageHandler: The checkoutPiece is : "+checkoutPiece);

			}
			finally{
				//unlock doNotHasList
				System.out.println("UnchokeMessageHandler: turning the lock back in: "+remotePeerId);
				peerThread.getPeer2Peer().getLock().unlock();	
				System.out.println("UnchokeMessageHandler: lock turned in: "+remotePeerId);
			}
			//update requestedPiece to reflect the checked out piece.

			System.out.println("UnchokeMessageHandler: setting requestedPiece to:" +checkoutPiece);
			peerThread.setRequestedPiece(checkoutPiece);

			if(checkoutPiece != -1){
				//send a requestMessage
				peerThread.sendMessage(new RequestMessage(checkoutPiece));
			}
		}
	}
}
