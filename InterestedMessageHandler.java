public class InterestedMessageHandler extends MessageHandler{

        public InterestedMessageHandler(PeerInfoList peerInfoList,int remotePeerId){
                super(peerInfoList, remotePeerId);
        }

        public void handleMessage(Message message){
                InterestedMessage interestedMessage = (InterestedMessage)message;

                System.out.println("InterestedMessageHandler: "+ interestedMessage.toString());
               
                peerInfoList.getPeerInfo(remotePeerId).setIsInterested(true);
                // Add the peer that sent this to you to a interested list if it is not all ready there.
                // this might need to be sync'ed.

        }
}