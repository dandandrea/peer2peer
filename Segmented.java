import java.io.*;
import java.util.*;
import java.lang.*;
import java.nio.*;
import java.nio.channels.FileChannel;

public class Segmented
{
	// Gets the piece from file
	public static String getPiece(String fileName, int pieceSize, int pieceNumber, int fileSize) 
	{
		System.out.println("Segmented: Inside getPiece Method " + fileName);
		String pieceDataString;
		char[] pieceData = new char[pieceSize];

		try
		{
			FileReader fileReader = new FileReader(new File(fileName));
			System.out.println("Segmented: pieceNumber: " + pieceNumber);
			System.out.println("Segmented: pieceSize: " + pieceSize);
			System.out.println("Segmented: pieceNumber * pieceSize: " + pieceNumber * pieceSize);
			System.out.println("Segmented: skip amount(pieceSize-(pieceNumber * pieceSize): " + ((pieceNumber * pieceSize)));
			fileReader.skip(pieceNumber * pieceSize);
			fileReader.read(pieceData, 0, pieceSize);
		}
		catch (Exception e)
		{
			System.out.println("Segmented: ERROR: Unexpected state, file not found while constructing a piece message!!");
			e.printStackTrace();
		}
		
		return pieceDataString = new String(pieceData);
	}

	// Write's the piece to file
	public static synchronized void writePiece(String fileName, int pieceSize, int pieceNumber, int fileSize, String piece)
	{
		String name = "peer_" + Peer2Peer.peer2Peer.getPeerId() + System.getProperty("file.separator") + Peer2Peer.peer2Peer.getFileName();

		ByteBuffer buff = ByteBuffer.allocate(pieceSize);
		buff.clear();
		for (int i = 0; i < piece.getBytes().length; i++) {

			// Only add to the buffer if the contents at this index are not equal to zero
			if (piece.getBytes()[i] != 0) {
				buff.put(piece.getBytes()[i]);
			} 
		}
		buff.flip();

		try
		{
			File createfile = new File(name);
			RandomAccessFile file = new RandomAccessFile(name, "rw");

			FileChannel writePiece = file.getChannel();
			writePiece.lock();
			writePiece.position((long)pieceSize*pieceNumber);
			while(buff.hasRemaining())
			{
				writePiece.write(buff);
			}
			writePiece.close();
		}
		catch (Exception e)
		{
			System.out.println("ERROR: Unexpected state, file not found while writing the piece to the file");
			e.printStackTrace();
		}

	}

}
