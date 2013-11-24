import java.util.*;

// Class for holding PeerInfo objects and methods to make related operations more convenient
// Will eventually provide thread synchronization
public class PeerInfoList {
    // The List of PeerInfo objects
    private List<PeerInfo> peerInfoList;

    // Constructor
    public PeerInfoList() {
	    // Instantiate the PeerInfo List
		peerInfoList = new ArrayList<PeerInfo>();
    }

	// Add a PeerInfo object
	public void add(PeerInfo peerInfo) {
	    // Add the object to the List
		peerInfoList.add(peerInfo);
	}

	// Get a PeerInfo ojbect by its peer ID
	public PeerInfo getPeerInfo(int peerId) {
	    // Search the List
		for (int i = 0; i < peerInfoList.size(); i++) {
		    // Is this our match?
			if (peerInfoList.get(i).getPeerId() == peerId) {
			    // Return the match
				return peerInfoList.get(i);
			}
		}

		// If we make it here then there was no match
		return null;
	}

	// Get a PeerInfo object by its index
	public PeerInfo getPeerInfoByIndex(int index) {
	    // Return the object
		return peerInfoList.get(index);
	}

	// Get the size of the PeerInfo List
	public int getSize() {
	    // Return the size
		return peerInfoList.size();
	}

	// Method to get a list of interested peers
	public List<Integer> getInterestedList() {
	    // The list of interested peers
		List<Integer> interestedList = new ArrayList<Integer>();

		// Add the interested peers
		for (int i = 0; i < peerInfoList.size(); i++) {
		    // Is this peer interested? If so then add it to the list
			if (peerInfoList.get(i).getIsInterested() == true) {
			    // Add it
				interestedList.add(peerInfoList.get(i).getPeerId());
			}
		}

		// Return the interested list
		return interestedList;
	}

	public void dump() {
	    for (int i = 0; i < peerInfoList.size(); i++) {
		    System.out.println(i + ": " + peerInfoList.get(i).getHostname() + " " + peerInfoList.get(i).getPort());
		}
	}
}
