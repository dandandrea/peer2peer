import java.io.IOException;

public class PeerThread {
    // Remote peer ID
	// This is the peer ID for which this thread is connected to over the network
    private int remotePeerId;

    // PeerThread constructor
    public PeerThread(int remotePeerId) throws IOException {
        // Set the remote peer ID
        this.remotePeerId = remotePeerId;
    }
}
