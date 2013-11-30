public class HaveMessageHandler extends MessageHandler{

        public HaveMessageHandler(PeerInfoList peerInfoList, int remotePeerId){
                super(peerInfoList, remotePeerId);
        }

        public void handleMessage(Message message){
                HaveMessage haveMessage = (HaveMessage)message;

                System.out.println("HaveMessageHandler: "+ haveMessage.toString());

                int pieceToAdd = haveMessage.getHavePieceNumber();

                System.out.println("HaveMessageHandler: pieceToAdd from "+ remotePeerId+ " is : "+ pieceToAdd);
                //Do i know about the peer having this pieceToAdd?
                if(getPeerInfoList().getPeerInfo(remotePeerId).getPieceList().contains(pieceToAdd)  == false){
                        //add it to the pieceList
                        getPeerInfoList().getPeerInfo(remotePeerId).getPieceList().add(pieceToAdd);
                }

                System.out.println("HaveMessageHandler:I"+Peer2Peer.peer2Peer.getPeerId()+" HAVE: "+getPeerInfoList().getPeerInfo(Peer2Peer.peer2Peer.getPeerId()).getPieceList());
                System.out.println("HaveMessageHandler:YOU"+remotePeerId+ "HAVE: "+getPeerInfoList().getPeerInfo(remotePeerId).getPieceList());

                
                boolean interested = false;

                //for each piece in my peers piece List , Do i need it? if so im interested.
                for (int piece :  getPeerInfoList().getPeerInfo(remotePeerId).getPieceList()){

                        //if i dont have the piece in question.
                        if(interested == false && getPeerInfoList().getPeerInfo(Peer2Peer.peer2Peer.getPeerId()).getPieceList().contains(piece) == false){
                                System.out.println("HaveMessageHandler: sending interestedMessage");
                                //im interested.
                                interested = true;
                                break;
                        }
                }

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
}