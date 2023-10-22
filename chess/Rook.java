package chess;

import java.util.ArrayList;

public class Rook extends ReturnPiece{
  char color;
  boolean firstMoveDone = false;

  public Rook(PieceType pieceType, int rank, PieceFile file) {
    this.pieceType = pieceType;
    this.pieceRank = rank;
    this.pieceFile = file;
    this.color = (""+pieceType).charAt(0);
  }

  public boolean validMove(String initialPos, String targetPos) {
    char initialFile = initialPos.charAt(0);
    char initialRank = initialPos.charAt(1);
    
    ArrayList<String> validMoves = new ArrayList<String>();

    // checking above
    char currentFile = initialFile;
    char currentRank = (char)(initialRank+1);
    while (currentRank < '9') {
      String currentPos = new StringBuilder().append(currentFile).append(currentRank).toString();
      ReturnPiece pieceOnSquare = Chess.getSquare(currentPos);
      if (pieceOnSquare != null) {
        // if same color don't add to validMoves and break;
        if ((""+pieceOnSquare.pieceType).charAt(0) == color) {
          break;
          // otherwise add to validMoves and break;
        } else {
          validMoves.add(currentPos);
          break;
        }
      }
      validMoves.add(currentPos);
      currentRank++;
    }
    // checking below
    currentFile = initialFile;
    currentRank = (char)(initialRank-1);
    while (currentRank > '0') {
      String currentPos = new StringBuilder().append(currentFile).append(currentRank).toString();
      ReturnPiece pieceOnSquare = Chess.getSquare(currentPos);
      if (pieceOnSquare != null) {
        // if same color don't add to validMoves and break;
        if ((""+pieceOnSquare.pieceType).charAt(0) == color) {
          break;
          // otherwise add to validMoves and break;
        } else {
          validMoves.add(currentPos);
          break;
        }
      }
      validMoves.add(currentPos);
      currentRank--;
    }
    // checking right
    currentFile = (char)(initialFile+1);
    currentRank = initialRank;
    while (currentFile < 'i') {
      String currentPos = new StringBuilder().append(currentFile).append(currentRank).toString();
      ReturnPiece pieceOnSquare = Chess.getSquare(currentPos);
      if (pieceOnSquare != null) {
        // if same color don't add to validMoves and break;
        if ((""+pieceOnSquare.pieceType).charAt(0) == color) {
          break;
          // otherwise add to validMoves and break;
        } else {
          validMoves.add(currentPos);
          break;
        }
      }
      validMoves.add(currentPos);
      currentFile++;
    }
    // checking left
    currentFile = (char)(initialFile-1);
    currentRank = initialRank;
    while (currentFile >= 'a') {
      String currentPos = new StringBuilder().append(currentFile).append(currentRank).toString();
      ReturnPiece pieceOnSquare = Chess.getSquare(currentPos);
      if (pieceOnSquare != null) {
        // if same color don't add to validMoves and break;
        if ((""+pieceOnSquare.pieceType).charAt(0) == color) {
          break;
          // otherwise add to validMoves and break;
        } else {
          validMoves.add(currentPos);
          break;
        }
      }
      validMoves.add(currentPos);
      currentFile--;
    }

    if (validMoves.indexOf(targetPos) != -1) {
      return true;
    }
    return false;
  }
}
