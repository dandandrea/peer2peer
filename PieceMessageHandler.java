public class PieceMessageHandler extends MessageHandler{

	public PieceMessageHandler(PeerInfoList peerInfoList, int remotePeerId){
		super(peerInfoList , remotePeerId);
	}

	public void handleMessage(Message message){
		PieceMessage pieceMessage = (PieceMessage)message;

		System.out.println("PieceMessageHandler: from "+remotePeerId+" : "+ pieceMessage.toString());
	


		//get a handle on the peer thead that is handling this message.
		PeerThread peerThread = peerInfoList.getPeerInfo(remotePeerId).getPeerThread();

		// get the requestedPiece
		int requestPieceNumber = peerThread.getRequestedPiece();
		System.out.println("PieceMessageHandler: requestPieceNumber: "+requestPieceNumber + " PieceNumber i got: " + pieceMessage.getPieceNumber());

		// Checking instance of piece message
		if(requestPieceNumber == pieceMessage.getPieceNumber()){
			
			if ( pieceMessage instanceof PieceMessage )
			{
				PieceMessage mg = new PieceMessage(pieceMessage.toString(), pieceMessage.getFileSize(), pieceMessage.getPieceSize(), pieceMessage.getFileName() );
			}

			//update my pieceList
			//System.out.println("PieceMessageHandler: adding piece to pieceList");
			peerInfoList.getPeerInfo(peerThread.getPeer2Peer().getPeerId()).getPieceList().add(requestPieceNumber);

			//System.out.println("PieceMessageHandler: send HaveMesage to all other peers");

			//for each peer, send them a have message, and check if im interested in them.
			for(int i =0; i < peerInfoList.getSize(); i++){
			
				if(peerInfoList.getPeerInfoByIndex(i).getPeerThread() != null){
					peerInfoList.getPeerInfoByIndex(i).getPeerThread().getLock().lock();
					try{	
						System.out.println("PieceMessageHandler: HaveMessage sent to :" +peerInfoList.getPeerInfoByIndex(i).getPeerId());
						peerInfoList.getPeerInfoByIndex(i).getPeerThread().sendMessage(new HaveMessage(requestPieceNumber));
					}
					finally{
						peerInfoList.getPeerInfoByIndex(i).getPeerThread().getLock().unlock();
					}
	

						boolean interested = false;

		                //for each piece in my peers piece List , Do i need it? if so im interested.
		                for (int piece :  peerInfoList.getPeerInfoByIndex(i).getPieceList()){
		                    //if i dont have the piece in question.
		                    if(interested == false && getPeerInfoList().getPeerInfo(Peer2Peer.peer2Peer.getPeerId()).getPieceList().contains(piece) == false){
		                            //im interested.
		                            interested = true;
		                            break;
		                    }
		                }
		                //if remotePeer has pieces this peer does not have
		                if(interested == false){
		            		System.out.println("PieceMessageHandler: sending not interested message");
				            peerInfoList.getPeerInfoByIndex(i).getPeerThread().getLock().lock();
							try{	
					        	peerInfoList.getPeerInfoByIndex(i).getPeerThread().sendMessage(new NotInterestedMessage());
							}
							finally{
								peerInfoList.getPeerInfoByIndex(i).getPeerThread().getLock().unlock();
							}
		           
		                }
					
        		}
        		else{
					System.out.println("PieceMessageHandler: peerThread was null : "+peerInfoList.getPeerInfoByIndex(i).getPeerId());
				}
					
			}


			//get that i am choked by them
			boolean amChokedByThem = peerInfoList.getPeerInfo(remotePeerId).getAmChokedBythem();

			System.out.println("PieceMessageHandler: am i choked by"+remotePeerId+" : " +amChokedByThem);

			//if im unchoked request another piece
			if(amChokedByThem == false){
				int checkoutPiece = -1;

				System.out.println("PieceMessageHandler: trying to get lock for "+ remotePeerId);
				//lock doNotHasList
				peerThread.getPeer2Peer().getLock().lock();
				System.out.println("PieceMessageHandler: got the lock for"+remotePeerId);

				try{
	
				//check out a piece
				checkoutPiece = peerThread.getPeer2Peer().checkoutPiece(remotePeerId);
				System.out.println("PieceMessageHandler: The checkoutPiece is : "+checkoutPiece);

				}
				finally{
					//unlock doNotHasList
					System.out.println("PieceMessageHandler: turning the lock back in: "+remotePeerId);
					peerThread.getPeer2Peer().getLock().unlock();	
					System.out.println("PieceMessageHandler: lock turned in: "+remotePeerId);
				}
					//update requestedPiece to reflect the checked out piece.

				System.out.println("PieceMessageHandler: setting requestedPiece to:" +checkoutPiece);
				peerThread.setRequestedPiece(checkoutPiece);

				if(checkoutPiece != -1){
					//send a requestMessage
					peerThread.getLock().lock();
					try{	
						System.out.println("PieceMessageHandler: sending request for " +checkoutPiece+" to "+ remotePeerId);
						peerThread.sendMessage(new RequestMessage(checkoutPiece));
					}
					finally{
						peerThread.getLock().unlock();
					}
				}
			}
		}
		else{
			System.out.println("PieceMessageHandler: ERROR: I got a message that I wasn't expecting");
		}
	}
}
