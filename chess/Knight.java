package chess;

public class Knight extends ReturnPiece{
  char color;

  public Knight(PieceType pieceType, int rank, PieceFile file) {
    this.pieceType = pieceType;
    this.pieceRank = rank;
    this.pieceFile = file;
    this.color = (""+pieceType).charAt(0);
  }

  public boolean validMove(String initialPos, String targetPos) {
    char initialFile = initialPos.charAt(0);
    char initialRank = initialPos.charAt(1);

    char targetFile = targetPos.charAt(0);
    char targetRank = targetPos.charAt(1);

    int fileDiff = Math.abs(targetFile-initialFile);
    int rankDiff = Math.abs(targetRank - initialRank);
    // fileDiff and rankDiff must be either 1 or 2, and they can't be the same
    if (fileDiff == 1) {
      if (rankDiff == 2) {
        if (Chess.getSquare(targetPos) == null) {
          return true;
        } else if ((""+Chess.getSquare(targetPos).pieceType).charAt(0) != color) {
          return true;
        }
      }
    }
    if (fileDiff == 2) {
      if (rankDiff == 1) {
        if (Chess.getSquare(targetPos) == null) {
          return true;
        } else if ((""+Chess.getSquare(targetPos).pieceType).charAt(0) != color) {
          return true;
        }
      }
    }

    return false;
  }
}
