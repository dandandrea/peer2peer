public class NotInterestedMessageHandler extends MessageHandler{

        public NotInterestedMessageHandler(PeerInfoList peerInfoList,int remotePeerId){
                super(peerInfoList, remotePeerId);
        }

        public void handleMessage(Message message){
            NotInterestedMessage notInterestedMessage = (NotInterestedMessage)message;

            System.out.println("NotInterestedMessageHandler: from "+remotePeerId+" : "+ notInterestedMessage.toString());

            // Write to log
		    Peer2Peer.peer2Peer.writeToLog("Peer [peer_ID " + Peer2Peer.peer2Peer.getPeerId() + "] received a 'not interested' message from [peer_ID " + remotePeerId + "].");

			peerInfoList.getPeerInfo(remotePeerId).setIsInterested(false);
        }
}
