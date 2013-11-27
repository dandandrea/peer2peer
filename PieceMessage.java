import java.util.*;
import java.io.*;

public class PieceMessage implements Message
{
	//private static byte piece = 7;

	// Properties of Piece Message
    private int pieceNumber;
	private String piece;
	private int length;
	private final int type = 7;
	private String fileName;
	private int pieceSize;
	private int fileSize;

	// Constructor Serialize

	// ********************TODO Grab piece from file in message*****************//
	public PieceMessage(int pieceNumber, int fileSize, int pieceSize, String fileName)
	{
		this.pieceNumber = pieceNumber;
		this.fileSize = fileSize;
		this.pieceSize = pieceSize;
		this.fileName = fileName;
		this.piece = grabPiece();

		this.length = 5 + Integer.toString(pieceNumber).length() + piece.length();
	}

	// Deserialize COnstructor
	public PieceMessage(String message)
	{
		if ( Integer.parseInt(message.substring(4,5)) != type)
		{
			System.out.println(" ERROR: Invalid Message Type ");
		}
		this.length = message.length();
		this.pieceNumber = Integer.parseInt(message.substring(5,9));
		this.piece = message.substring(9,length);
	}

	// Serialize
	public String toString()
	{
		return  String.format("%04d", length) + (type + String.format("%04d",Integer.toString(pieceNumber)) + piece);
	}


	// Grab the piece of File
	private String grabPiece()
	{
		System.out.println(" GrabPiece() method " + this.fileName);
		return Segmented.getPiece(this.fileName, this.pieceSize, this.pieceNumber, this.fileSize); 
	}

	// The get functions
	public int getPieceNumber()
	{
		return this.pieceNumber;
	}

	public int getLength()
	{
		return this.length;
	}
	
	public String getPiece()
	{
		return this.piece;
	}

	public int getType()
	{
		return this.type;
	}
}
