public class RequestMessageHandler extends MessageHandler{

	public RequestMessageHandler(PeerInfoList peerInfoList , int remotePeerId){
		super(peerInfoList , remotePeerId);
	}

	public void handleMessage(Message message){
		RequestMessage requestMessage = (RequestMessage)message;

		System.out.println("RequestMessageHandler: request from "+ remotePeerId +" : "+ requestMessage.toString());
		
		// if remotePeer is a prefered neighbor or optimistically unchocked
		if(peerInfoList.getPeerInfo(remotePeerId).getIsChokedByMe() == false){
			System.out.println("RequestMessageHandler: building piece for "+remotePeerId +" : "+ requestMessage.getPayload());
			
			//get the peerThead to send the message with.
			PeerThread peerThread = peerInfoList.getPeerInfo(remotePeerId).getPeerThread();

			Peer2Peer peer2Peer = peerThread.getPeer2Peer();

			peerThread.getLock().lock();
			try{
				//send a piece message
				peerThread.sendMessage(new PieceMessage(requestMessage.getPayload(), peer2Peer.getFileSize(), peer2Peer.getPieceSize(), peer2Peer.getFileName()));
			}
			finally{
				peerThread.getLock().unlock();
			}
		}
	}
} 