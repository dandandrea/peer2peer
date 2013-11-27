import java.io.*;

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

}
