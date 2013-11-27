public class NotInterestedMessageHandler extends MessageHandler{

        public NotInterestedMessageHandler(PeerInfoList peerInfoList,int remotePeerId){
                super(peerInfoList, remotePeerId);
        }

        public void handleMessage(Message message){
                NotInterestedMessage notInterestedMessage = (NotInterestedMessage)message;

                System.out.println("NotInterestedMessageHandler: "+ notInterestedMessage.toString());
                
				peerInfoList.getPeerInfo(remotePeerId).setIsInterested(true);
                // remove remotePeer from Interested list if needed. otherwise do nothing.

        }
}