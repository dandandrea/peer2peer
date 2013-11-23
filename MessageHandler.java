Public class MessageHandler{

	// handle on peerInfoList to update
	private PeerInfoList peerInfoList;


	public MessageHandler(PeerInfoList peerInfoList){
		this.peerInfoList = peerInfoList
	}

	public void handleMessage(ChokeMessage chokeMessage){
		ChokeMessageHandler mh = new ChokeMessageHandler(peerInfoList);
		m.handleMessage(chokeMessage);
	}

	public void handleMessage(UnchokeMessage unchokeMessage){
		UnchokeMessageHandler mh = new UnchokeMessageHandler(peerInfoList);
		mh.handleMessage(unchokeMessage);
	}

	public void handleMessage(InterstedMessage interestedMessage){
		InterstedMessageHandler mh = new InterstedMessageHandler(peerInfoList);
		mh.handleMessage(interstedMessage);
	}

	public void handleMessage(NotInterstedMessage notInterestedMessage){
		NotInterstedMessageHandler mh = new NotInterstedMessageHandler(peerInfoList);
		mh.handleMessage(notInterstedMessage);
	}

	public void handleMessage(BitfieldMessage bitfieldMessage){
		BitfieldMessageHandler mh = new bitfieldMessageHandler(peerInfoList);
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



}