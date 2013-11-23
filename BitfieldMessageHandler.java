public class BitfieldMessageHandler extends MessageHandler{

	public void handleMessage(BitfieldMessage bitfieldMessage){
		System.out.println("BitfieldMessageHandler: "+ bitfieldMessage.toString());
		System.out.println("Do Choke response");
	}
}