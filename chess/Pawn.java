package chess;
//must implement promotion ability
public class Pawn extends ReturnPiece {
  boolean firstMoveDone = false;
  int timeOfTwoJump = -1;
  char color;

  public Pawn(PieceType pieceType, int rank, PieceFile file){
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
    int rankDiff = targetRank-initialRank;
    ReturnPiece pieceAtTarget = Chess.getSquare(targetPos);

    if (Math.abs(rankDiff) > 2) return false;
    if (fileDiff>1) return false; //cannot move more than one file ever, must be 0 or 1
    if (rankDiff<=0&&color=='W') return false; //rankDiff must be positive if white pawn
    if (rankDiff>=0&&color=='B') return false; //rankDiff must be negative if black pawn
    
    //make rankDiff positive for evaluation purpose
    rankDiff = Math.abs(rankDiff);

    if (fileDiff==1){ //diagonal move case
      if (rankDiff==2) return false; //cannot move 2 spaces no matter what if diagonal
      
      //at this point we know moving diagonal by one left or right
      if (pieceAtTarget==null){ //must check for enpassant case
        ReturnPiece adjacentPiece = Chess.getSquare((""+targetFile+initialRank)); //use initial file for adj piece file
        if (adjacentPiece==null) {
          return false; //nothing is adjacent to the pawn
        }
        if (color=='W'&&adjacentPiece.pieceType==PieceType.BP){ //check if adj is opposing pawn  
          if (((Pawn)adjacentPiece).timeOfTwoJump==Chess.clock-1){ //must cast to type pawn so field visible
              firstMoveDone = true;
              return true;
          }
        }
        if (color=='B'&&adjacentPiece.pieceType==PieceType.WP){ //check if adj is opposing pawn
          if (((Pawn)adjacentPiece).timeOfTwoJump==Chess.clock-1){ //can enpassant
              firstMoveDone = true;
              return true;
          }
        }

        return false; //diagonal was empty and adj piece was not valid for enpassant
      } 

      //pawn wants to move diagonal
      //at this point we know not moving more than one tile, checked for enpassant case, and that there IS a piece at target location
      //must check if diagonal peice is right color (opposing color)
      if (color==(pieceAtTarget.pieceType+"").charAt(0)){ //same color and cannot eat
        return false;
      }

      //diagonal piece is opposite color and pawn can eat it
      firstMoveDone = true;
      return true;

    } else { //not moving diagonal, must check if anything in the way
      if (pieceAtTarget!=null) return false; //piece present at target, cannot move forward
      if (rankDiff==2&&firstMoveDone) return false; //trying to move two spaces when past first turn
      
      int oneInFront;
      
      if (color=='B'){
        oneInFront = targetRank+1;
      } else {
        oneInFront = targetRank-1;
      }
      ReturnPiece inFront = Chess.getSquare((""+targetFile)+oneInFront);

      if (rankDiff==2&&inFront!=null) return false;
      return true;
    }
  }


  public static ReturnPiece promote(Pawn startingPawn, char promotedPieceType){ 
    
    
    switch (promotedPieceType){
        case 'Q':
            return new Queen(PieceType.valueOf(startingPawn.color+"Q"), startingPawn.pieceRank, startingPawn.pieceFile);
        
        case 'B':
            return new Bishop(PieceType.valueOf(startingPawn.color+"B"), startingPawn.pieceRank, startingPawn.pieceFile);
        
        case 'N':
           return new Knight(PieceType.valueOf(startingPawn.color+"N"), startingPawn.pieceRank, startingPawn.pieceFile);

        case 'R':
           return new Rook(PieceType.valueOf(startingPawn.color+"R"), startingPawn.pieceRank, startingPawn.pieceFile);
        
           default: 
          return startingPawn;
    }

  }

}
