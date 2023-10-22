package chess;
import java.util.ArrayList;

public class Bishop extends ReturnPiece {
  char color;

  public Bishop(PieceType pieceType, int rank, PieceFile file ) {
    this.pieceType = pieceType;
    this.pieceRank = rank;
    this.pieceFile = file;
    this.color = (""+pieceType).charAt(0);
  }

  // returns true or false based on whether the given move is valid
  public boolean validMove(String initialPos, String targetPos) {
    char initialFile = initialPos.charAt(0);
    char initialRank = initialPos.charAt(1);

    ArrayList<String> validMoves = new ArrayList<String>();

    // loop for each direction?
    char currentFile = (char)(initialFile + 1);
    char currentRank = (char)(initialRank + 1);
    while (currentFile < 'i' && currentRank < '9') {
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
      currentRank++;
    }

    currentFile = (char)(initialFile - 1);
    currentRank = (char)(initialRank + 1);
    while (currentFile > 'a'-1 && currentRank < '9') {
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
      currentRank++;
    }

    currentFile = (char)(initialFile + 1);
    currentRank = (char)(initialRank - 1);
    while (currentFile < 'i' && currentRank > '0') {
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
      currentRank--;
    }

    currentFile = (char)(initialFile - 1);
    currentRank = (char)(initialRank - 1);
    while (currentFile > 'a'-1 && currentRank > '0') {
      String currentPos = new StringBuilder().append(currentFile).append(currentRank).toString();
      //if the piece on that square 
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
      currentRank--;
    }

    if (validMoves.indexOf(targetPos) != -1) {
      return true;
    }
    return false;
  }
}
