import java.util.*;

// Class for holding PeerInfo
// One instance of a PeerInfo class is created for each line in PeerInfo.cfg
public class PeerInfo {
    // The peer info properties
    private int peerId;
    private String hostname;
    private int port;
    private int hasFile;
    private PeerThread peerThread;
    private List<Integer> pieceList;
	private boolean isInterested;
    private boolean amChokedByThem;
    private boolean isChokedByMe;

    // Constructor
    public PeerInfo(int peerId, String hostname, int port, int hasFile) {
        this.peerId = peerId;
        this.hostname = hostname;
        this.port = port;
        this.hasFile = hasFile;
        pieceList = new ArrayList<Integer>(); 
        isInterested = false;
        amChokedByThem = true;
        isChokedByMe = true;       
    }

    // Default constructor
    public PeerInfo() {
        pieceList = new ArrayList<Integer>();
        isInterested = false;
        amChokedByThem = true;
        isChokedByMe = true;     
    }

    public boolean getAmChokedBythem(){
        return amChokedByThem;
    }

    public void setAmChokedBythem(boolean amChokedByThem){
        this.amChokedByThem = amChokedByThem;
    }

    public boolean getIsChokedByMe(){
        return isChokedByMe;
    }

    public void setIsChokedByMe(boolean isChokedByMe){
        this.isChokedByMe = isChokedByMe;
    }

	// Getter for isInterested
	public boolean getIsInterested() {
	    return isInterested;
	}

	// Setter for isInterested
	public void setIsInterested(boolean isInterested) {
	    this.isInterested = isInterested;
	}

    //get peer thread
    public PeerThread getPeerThread(){
        return peerThread;
    }

    public void setPeerThread(PeerThread peerThread){
        this.peerThread = peerThread;
    }

    public List<Integer> getPieceList(){
        return pieceList;
    }

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
}
