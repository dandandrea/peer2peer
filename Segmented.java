import java.io.*;
import java.util.*;
import java.lang.*;
import java.nio.*;
import java.nio.channels.FileChannel;

public class Segmented
{
	public static String getPiece(String fileName, int pieceSize, int pieceNumber, int fileSize) 
	{
		System.out.println(" Inside getPiece Method " + fileName);
		String pieceDataString;
		char[] pieceData = new char[pieceSize];

		try
		{
			FileReader fileReader = new FileReader(new File(fileName));
			System.out.println("pieceNumber: " + pieceNumber);
			System.out.println("pieceSize: " + pieceSize);
			System.out.println("pieceNumber * pieceSize: " + pieceNumber * pieceSize);
			fileReader.skip((pieceNumber * pieceSize)-pieceSize);
			fileReader.read(pieceData, 0, pieceSize);
			// fileReader.read(pieceData, pieceNumber*pieceSize, pieceSize);
		}
		catch (Exception e)
		{
			System.out.println("ERROR: Unexpected state, file not found while constructing a piece message!!");
			e.printStackTrace();
		}
		
		return pieceDataString = new String(pieceData);
	}

	public static void writePiece(String fileName, int pieceSize, int pieceNumber, int fileSize, String piece)
	{

		ByteBuffer buff = ByteBuffer.allocate(pieceSize);
		buff.clear();
		buff.put(piece.getBytes());
		buff.flip();
 
		try
		{
			RandomAccessFile file = new RandomAccessFile(fileName, "w");
			FileChannel writePiece = file.getChannel();
			writePiece.position((long)((pieceSize*pieceNumber)-pieceSize));
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
