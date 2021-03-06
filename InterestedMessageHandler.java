public class InterestedMessageHandler extends MessageHandler{

        public InterestedMessageHandler(PeerInfoList peerInfoList,int remotePeerId){
                super(peerInfoList, remotePeerId);
        }

        public void handleMessage(Message message){
                InterestedMessage interestedMessage = (InterestedMessage)message;

                System.out.println("InterestedMessageHandler: "+ interestedMessage.toString());

		// Write to log
		Peer2Peer.peer2Peer.writeToLog("Peer [peer_ID " + Peer2Peer.peer2Peer.getPeerId() + "] received an 'interested' message from [peer_ID " + remotePeerId + "].");

                peerInfoList.getPeerInfo(remotePeerId).setIsInterested(true);
                // Add the peer that sent this to you to a interested list if it is not all ready there.
                // this might need to be sync'ed.

        }
}
