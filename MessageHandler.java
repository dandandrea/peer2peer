public class MessageHandler{

	// handle on peerInfoList to update
	protected PeerInfoList peerInfoList;
	protected int remotePeerId;


	public MessageHandler(PeerInfoList peerInfoList , int remotePeerId){
		this.peerInfoList = peerInfoList;
		this.remotePeerId = remotePeerId;
	}

	public void handleMessage(Message message){
		if(message instanceof BitfieldMessage){
			MessageHandler mh = new BitfieldMessageHandler(peerInfoList , remotePeerId);
			mh.handleMessage(message);
		}
		else{
			System.out.println("ERROR: handleMessage(): WHY AM I HERE");
		}
	}


	public void handleMessage(ChokeMessage chokeMessage){
		ChokeMessageHandler mh = new ChokeMessageHandler(peerInfoList);
		mh.handleMessage(chokeMessage);
	}

	public void handleMessage(UnchokeMessage unchokeMessage){
		UnchokeMessageHandler mh = new UnchokeMessageHandler(peerInfoList);
		mh.handleMessage(unchokeMessage); 
	}

	public void handleMessage(InterestedMessage interestedMessage){
		InterestedMessageHandler mh = new InterestedMessageHandler(peerInfoList);
		mh.handleMessage(interestedMessage);
	}

	public void handleMessage(NotInterestedMessage notInterestedMessage){
		NotInterestedMessageHandler mh = new NotInterestedMessageHandler(peerInfoList);
		mh.handleMessage(notInterestedMessage);
	}

	public void handleMessage(HaveMessage haveMessage){
		HaveMessageHandler mh = new HaveMessageHandler(peerInfoList);
		mh.handleMessage(haveMessage);
	}

	public void handleMessage(BitfieldMessage bitfieldMessage){
		BitfieldMessageHandler mh = new BitfieldMessageHandler(peerInfoList);
		mh.handleMessage(bitfieldMessage);
	}

	public void handleMessage(RequestMessage requestMessage){
		RequestMessageHandler mh = new RequestMessageHandler(peerInfoList);
		mh.handleMessage(requestMessage);
	}

	public void handleMessage(PieceMessage pieceMessage){
		PieceMessageHandler mh = new PieceMessageHandler(peerInfoList);
		mh.handleMessage(pieceMessage);
	}

	public PeerInfoList getPeerInfoList(){
		return peerInfoList;
	}
}