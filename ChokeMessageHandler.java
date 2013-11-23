public class ChokeMessageHandler extends MessageHandler{

	public void handleMessage(ChokeMessage chokeMessage){
		System.out.println("ChokeMessageHandler: "+ chokeMessage.toString());
		System.out.println("Do Choke response");
	}
}