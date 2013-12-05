public class HaveMessageHandler extends MessageHandler{

        public HaveMessageHandler(PeerInfoList peerInfoList, int remotePeerId){
                super(peerInfoList, remotePeerId);
        }

        public void handleMessage(Message message){
                HaveMessage haveMessage = (HaveMessage)message;

                System.out.println("HaveMessageHandler: "+ haveMessage.toString());

                int pieceToAdd = haveMessage.getHavePieceNumber();

				// Write to log
				Peer2Peer.peer2Peer.writeToLog("Peer [peer_ID " + Peer2Peer.peer2Peer.getPeerId() + "] received a 'have' message from [peer_ID " + remotePeerId + "] for the piece [piece " + pieceToAdd + "].");

                System.out.println("HaveMessageHandler: pieceToAdd from "+ remotePeerId+ " is : "+ pieceToAdd);
                //Do i know about the peer having this pieceToAdd?
                Peer2Peer.peer2Peer.getHaveLock().lock();
                try {
                    if(peerInfoList.getPeerInfo(remotePeerId).getPieceList().contains(pieceToAdd)  == false){
                            //add it to the pieceList
                            peerInfoList.getPeerInfo(remotePeerId).getPieceList().add(pieceToAdd);
                    }
                }
                finally {
                    Peer2Peer.peer2Peer.getHaveLock().unlock();
                }

                System.out.println("HaveMessageHandler:I"+Peer2Peer.peer2Peer.getPeerId()+" HAVE: "+peerInfoList.getPeerInfo(Peer2Peer.peer2Peer.getPeerId()).getPieceList());
                System.out.println("HaveMessageHandler:YOU"+remotePeerId+ "HAVE: "+peerInfoList.getPeerInfo(remotePeerId).getPieceList());

                
                boolean interested = false;

                //for each piece in my peers piece List , Do i need it? if so im interested.
                for (int piece :  peerInfoList.getPeerInfo(remotePeerId).getPieceList()){

                        //if i dont have the piece in question.
                        if(interested == false && peerInfoList.getPeerInfo(Peer2Peer.peer2Peer.getPeerId()).getPieceList().contains(piece) == false){
                                System.out.println("HaveMessageHandler: sending interestedMessage");
                                //im interested.
                                interested = true;
                                break;
                        }
                }
                peerInfoList.getPeerInfo(remotePeerId).getPeerThread().getLock().lock();
                try{
                        //if remotePeer has pieces this peer does not have
                        if(interested){
                                System.out.println("HaveMessageHandler: sending interestedMessage");
                                peerInfoList.getPeerInfo(remotePeerId).getPeerThread().sendMessage(new InterestedMessage());
                        }
                        else{
                                System.out.println("HaveMessageHandler: sending not interested message");
                                peerInfoList.getPeerInfo(remotePeerId).getPeerThread().sendMessage(new NotInterestedMessage());
                        }

                }
                finally{
                        //unlock doNotHasList
                        peerInfoList.getPeerInfo(remotePeerId).getPeerThread().getLock().unlock();     

                }
 
        }
}
