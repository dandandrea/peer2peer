import java.io.*;
import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
		NonblockingConnection nonblockingConnection = new NonblockingConnection("localhost", 80);

		while (true) {
		    String data = nonblockingConnection.getData();
			if (data != null) {
				System.out.println("Got data:\n" + data);
			}

			try {
				System.out.println("Sleeping after getData() call");
				Thread.sleep(2000);
			}
			catch (Exception e) {}
		}
	}
}
