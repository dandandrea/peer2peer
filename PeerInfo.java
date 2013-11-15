// Class for holding PeerInfo
// One instance of a PeerInfo class is created for each line in PeerInfo.cfg
public class PeerInfo {
    // The peer info properties
    private int peerId;
    private String hostname;
    private int port;
    private int hasFile;
	private NonblockingConnection connection;

    // Constructor
    public PeerInfo(int peerId, String hostname, int port, int hasFile) {
        this.peerId = peerId;
        this.hostname = hostname;
        this.port = port;
        this.hasFile = hasFile;
    }

    // Default constructor
    public PeerInfo() {}

    // Getter for peer ID
    public int getPeerId() {
        return peerId;
    }

    // Getter for hostname
    public String getHostname() {
        return hostname;
    }

    // Getter for port
    public int getPort() {
        return port;
    }

    // Getter for hasFile
    public int getHasFile() {
        return hasFile;
    }

    // Setter for peer ID
    public void setPeerId(int peerId) {
        this.peerId = peerId;
    }

    // Setter for hostname
    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    // Setter for port
    public void setPort(int port) {
        this.port = port;
    }

    // Setter for hasFile
    public void setHasFile(int hasFile) {
        this.hasFile = hasFile;
    }

	// Getter for connection
	public NonblockingConnection getConnection() {
	    return connection;
	}

	// Setter for connection
	public void setConnection(NonblockingConnection connection) {
	    this.connection = connection;
	}
}
