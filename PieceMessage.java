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

	// Constructor Deserialize
	public PieceMessage(int pieceNumber, String piece)
	{
		this.pieceNumber = pieceNumber;
		this.piece = piece;
		this.length = 5 + Integer.toString(pieceNumber).length() + piece.length();
	}

	// Deserialize COnstructor
	public PieceMessage(String message)
	{
		if ( Integer.parseInt(message.substring(5,5)) == type)
		{
			System.out.println(" ERROR: Invalid Message Type ");
		}
		this.length = message.length();
		this.pieceNumber = Integer.parseInt(message.substring(6,9));
		this.piece = message.substring(9,length);
	}

	// Serialize
	public String toString()
	{
		return  length + (type + String.format("%04d",Integer.toString(pieceNumber)) + piece);
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
