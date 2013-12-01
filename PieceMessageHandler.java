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
				//if peer at i is not me.
				//System.out.println("PieceMessageHandler: comparing id's :" +peerInfoList.getPeerInfoByIndex(i).getPeerId() +" : "+ remotePeerId);
		
				//send a have message
				if(peerInfoList.getPeerInfoByIndex(i).getPeerThread() != null){
					System.out.println("PieceMessageHandler: HaveMessage sent to :" +peerInfoList.getPeerInfoByIndex(i).getPeerId());
					peerInfoList.getPeerInfoByIndex(i).getPeerThread().sendMessage(new HaveMessage(requestPieceNumber));
				}
				else{
					System.out.println("PieceMessageHandler: peerThread was null : "+peerInfoList.getPeerInfoByIndex(i).getPeerId());
				}

				boolean interested =  false;

				//for peer i, check if you are not interested.
               for (int piece :  getPeerInfoList().getPeerInfoByIndex(i).getPieceList()){
                    //if i dont have the piece in question.
                    if(interested == false && getPeerInfoList().getPeerInfo(Peer2Peer.peer2Peer.getPeerId()).getPieceList().contains(piece) == false){
                        //im imterested. thus do nothing
						System.out.println("PieceMessageHandler: I need "+piece+" from " +peerInfoList.getPeerInfoByIndex(i).getPeerId()); 	 
                        interested = true;
                    }
                }

                // send not interested. see top of page 5 of document.
                if(interested == false){
                	if(peerInfoList.getPeerInfoByIndex(i).getPeerThread() != null){
                		System.out.println("PieceMessageHandler: I have all "+peerInfoList.getPeerInfoByIndex(i).getPeerId()+"'s pieces: sending NotInterestedMessage");
                		peerInfoList.getPeerInfoByIndex(i).getPeerThread().sendMessage(new NotInterestedMessage());
               	 	}
                }
			}


			//get that i am choked by them
			boolean amChokedByThem = peerInfoList.getPeerInfo(remotePeerId).getAmChokedBythem();

			//if im unchoked request another piece
			if(amChokedByThem == false){
				//System.out.println("PieceMessageHandler: TODO: I'm not choked, so request another piece");
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
					peerThread.sendMessage(new RequestMessage(checkoutPiece));
				}
			}
		}
		else{

			//TODO: make this more robust
			//if i get a piece i wasnted expecting dont write it to my file, but do the following checks still
			//release checkback in the piece i currently have requested.
			//if im unchoked still
				//request another piece
			System.out.println("PieceMessageHandler: ERROR: I got a message that I wasn't expecting");
		}
	}
}
