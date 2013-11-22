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

	// Serialize
	public String toString()
	{
		return  length + (type + String.format("%04d",Integer.toString(pieceNumber)) + piece);
	}

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
