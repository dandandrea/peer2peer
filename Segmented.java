import java.io.*;

public class Segmented
{
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
			// fileReader.read(pieceData, pieceNumber*pieceSize, pieceSize);
		}
		catch (Exception e)
		{
			System.out.println("Segmented: ERROR: Unexpected state, file not found while constructing a piece message!!");
			e.printStackTrace();
		}
		
		return pieceDataString = new String(pieceData);
	}

}
