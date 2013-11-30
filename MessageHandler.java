public class MessageHandler{

	// handle on peerInfoList to update
	protected PeerInfoList peerInfoList;
	protected int remotePeerId;


	public MessageHandler(PeerInfoList peerInfoList , int remotePeerId){
		this.peerInfoList = peerInfoList;
		this.remotePeerId = remotePeerId;
	}

	public void handleMessage(Message message){

		if(message instanceof ChokeMessage){
			ChokeMessageHandler mh = new ChokeMessageHandler(peerInfoList, remotePeerId);
			mh.handleMessage(message);
		}
	
		else if(message instanceof UnchokeMessage){
			UnchokeMessageHandler mh = new UnchokeMessageHandler(peerInfoList, remotePeerId);
			mh.handleMessage(message); 
		}

		else if(message instanceof InterestedMessage){
			InterestedMessageHandler mh = new InterestedMessageHandler(peerInfoList, remotePeerId);
			mh.handleMessage(message);
		}

		else if(message instanceof NotInterestedMessage){
			NotInterestedMessageHandler mh = new NotInterestedMessageHandler(peerInfoList, remotePeerId);
			mh.handleMessage(message);
		}

		else if(message instanceof HaveMessage){
			HaveMessageHandler mh = new HaveMessageHandler(peerInfoList, remotePeerId);
			mh.handleMessage(message);
		}

		else if(message instanceof BitfieldMessage){
			BitfieldMessageHandler mh = new BitfieldMessageHandler(peerInfoList, remotePeerId);
			mh.handleMessage(message);
		}

		else if(message instanceof RequestMessage){
			RequestMessageHandler mh = new RequestMessageHandler(peerInfoList, remotePeerId);
			mh.handleMessage(message);
		}

		else if(message instanceof PieceMessage){
			PieceMessageHandler mh = new PieceMessageHandler(peerInfoList, remotePeerId);
			mh.handleMessage(message);
		}
	}
	public PeerInfoList getPeerInfoList(){
		return peerInfoList;
	}
}