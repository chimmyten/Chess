package chess;

public class King extends ReturnPiece{
  char color;
  boolean firstMoveDone = false;

  public King(PieceType pieceType, int rank, PieceFile file) {
    this.pieceType = pieceType;
    this.pieceRank = rank;
    this.pieceFile = file;
    this.color = (""+pieceType).charAt(0);
  }

  public boolean validMove(String initialPos, String targetPos) {
    char initialFile = initialPos.charAt(0);
    int initialRank = Integer.parseInt(initialPos.substring(1)); 

    char targetFile = targetPos.charAt(0);
    int targetRank = Integer.parseInt(targetPos.substring(1)); 

    int fileDiff = Math.abs(targetFile - initialFile);
    int rankDiff = Math.abs(targetRank-initialRank);
    ReturnPiece pieceAtTarget = Chess.getSquare(targetPos);

    if (rankDiff>1) return false; //king can never move more than 1 rank
    if (fileDiff>1&&rankDiff!=0) return false; //no possibility of castling(only case when fileDiff>1) if rankDiff!=0

    if (fileDiff>1&&rankDiff==0){ //CASTLING SCENARIO CHECK
      if (Chess.kingInCheck) return false; //cannot castle while in check
      if (firstMoveDone) return false; //king moved already
      if (fileDiff!=2) return false; //not correct fileDiff for castling

      if (targetFile>initialFile){ //iterate to the right checking for all empty spots and rook

        for (int f = initialFile+1; f<='h'; f++){
          char rightFile = (char)f;
          ReturnPiece p = Chess.getSquare(""+rightFile+targetRank);

          if (rightFile=='h'){ //last space so must be rook
            if (p==null) return false;
            if (!(p instanceof Rook)) return false;
            if (((Rook)p).firstMoveDone) return false;
          }

          if (rightFile!='h'){ //space in between
          
          if (p!=null) return false; //if not null false, cannot be pieces in between
          PieceFile originalFile = pieceFile; //store original location and check for check
          pieceFile = PieceFile.valueOf(""+rightFile);
          if (Chess.isCheck(Chess.boardState.piecesOnBoard, color)){
            pieceFile = PieceFile.valueOf(""+originalFile); //if results in check, set back to original before returning false
            return false;
          }
          pieceFile = PieceFile.valueOf(""+originalFile); //set back to original after each location simulation
        }
        

        }

      } else { //iterate to the left, same logic
        for (int f = initialFile-1; f>='a'; f--){
          char leftFile = (char)f;
          ReturnPiece p = Chess.getSquare(""+leftFile+targetRank);

          if (leftFile=='a'){ //last space so must be rook
            if (p==null) return false;
            if (!(p instanceof Rook)) return false;
            if (((Rook)p).firstMoveDone) return false;
          }


          if (leftFile!='a'){ //space in between
          if (p!=null) {
            return false;
           } //if not null false, cannot be pieces in between
          if (leftFile=='b'){ //don't have to check for check for this spot
            continue;
          }
          PieceFile originalFile = pieceFile; //store original location and check for check
          pieceFile = PieceFile.valueOf(""+leftFile);
          if (Chess.isCheck(Chess.boardState.piecesOnBoard, color)){
            pieceFile = PieceFile.valueOf(""+originalFile); //if results in check, set back to original before returning false
            return false;
          }
          pieceFile = PieceFile.valueOf(""+originalFile); //set back to original after each location simulation
        }

        }

      }

      return true;

      
    }


    //at this point we know fileDiff either 0 or 1 and rank diff either 0 or 1
    if (pieceAtTarget!=null){ //check if piece at target location since if null it is valid
      char pieceAtTargetColor = (pieceAtTarget.pieceType+"").charAt(0);
      if (pieceAtTargetColor==color) return false; //same color piece at spot
    }

    return true;
  }

  //note we do not check here if his final position will place him in check or not since that is done in Chess.java


}
