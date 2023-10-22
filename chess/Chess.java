//Spencer Tong and Timmy Chen

package chess;

import java.util.ArrayList;

import chess.ReturnPiece.PieceFile;
import chess.ReturnPlay.Message;

class ReturnPiece {
	static enum PieceType {WP, WR, WN, WB, WQ, WK, 
		            BP, BR, BN, BB, BK, BQ};
	static enum PieceFile {a, b, c, d, e, f, g, h};
	
	PieceType pieceType;
	PieceFile pieceFile;
	int pieceRank;  // 1..8
	public String toString() {
		return ""+pieceFile+pieceRank+":"+pieceType;
	}
	public boolean equals(Object other) {
		if (other == null || !(other instanceof ReturnPiece)) {
			return false;
		}
		ReturnPiece otherPiece = (ReturnPiece)other;
		return pieceType == otherPiece.pieceType &&
				pieceFile == otherPiece.pieceFile &&
				pieceRank == otherPiece.pieceRank;
	}
}

class ReturnPlay {
	enum Message {ILLEGAL_MOVE, DRAW, 
				  RESIGN_BLACK_WINS, RESIGN_WHITE_WINS, 
				  CHECK, CHECKMATE_BLACK_WINS,	CHECKMATE_WHITE_WINS, 
				  STALEMATE};
	
	ArrayList<ReturnPiece> piecesOnBoard;
	Message message;
}

public class Chess {
	static ReturnPlay boardState;
	static int clock = 0; //largest test case is 10-15 moves
	static ReturnPiece whiteKing;
	static ReturnPiece blackKing;
	static boolean kingInCheck = false;
	static ReturnPiece threateningPiece;
	static Player player;

	enum Player { white, black }
	
	/**
	 * Plays the next move for whichever player has the turn.
	 * 
	 * @param move String for next move, e.g. "a2 a3"
	 * 
	 * @return A ReturnPlay instance that contains the result of the move.
	 *         See the section "The Chess class" in the assignment description for details of
	 *         the contents of the returned ReturnPlay instance.
	 */
	public static ReturnPlay play(String move) {
		boardState.message = null;
		char playerTurn = Chess.player == Chess.Player.white ? 'W' : 'B';
		move = move.trim();
		
		String[] moveOrder = move.split(" ");
		if (moveOrder.length==1){ //must be resign case
				if (playerTurn == 'W') {
					boardState.message = Message.RESIGN_BLACK_WINS;
				} else {
					boardState.message = Message.RESIGN_WHITE_WINS;
				}
			return boardState;
		} 

		if (moveOrder[0]==moveOrder[1]){ //no piece can stay in place as move
			boardState.message = Message.ILLEGAL_MOVE;
			return boardState;
		}

	  String initialFile = moveOrder[0].substring(0,1);
		int initialRank = Integer.parseInt(moveOrder[0].substring(1));

		String targetFile = moveOrder[1].substring(0,1);
		int targetRank = Integer.parseInt(moveOrder[1].substring(1));
		ReturnPiece pieceOnTarget = getSquare(targetFile+targetRank);

		ReturnPiece currentPiece = getSquare(initialFile+initialRank);

		//initial position has no piece OR is not the same color as the current player--> ILLEGAL 
		if (currentPiece==null || (""+(currentPiece.pieceType)).charAt(0) != playerTurn){ 
			boardState.message = Message.ILLEGAL_MOVE;
			return boardState;
		}

		boolean castlingMove = false; // variable to check if the proposed move is a castle
		//1. check if valid move piece type wise 
			if (currentPiece instanceof Pawn){
				if (!((Pawn)currentPiece).validMove(moveOrder[0], moveOrder[1])){
					boardState.message = Message.ILLEGAL_MOVE;
					return boardState;
				}
			} else if (currentPiece instanceof Bishop){
				if (!((Bishop)currentPiece).validMove(moveOrder[0], moveOrder[1])){
					boardState.message = Message.ILLEGAL_MOVE;
					return boardState;
				}
			} else if (currentPiece instanceof Rook){
				if (!((Rook)currentPiece).validMove(moveOrder[0], moveOrder[1])){
					boardState.message = Message.ILLEGAL_MOVE;
					return boardState;
				}
			} else if (currentPiece instanceof Knight){
				if (!((Knight)currentPiece).validMove(moveOrder[0], moveOrder[1])){
					boardState.message = Message.ILLEGAL_MOVE;
					return boardState;
				}
			} else if (currentPiece instanceof Queen){
				if (!((Queen)currentPiece).validMove(moveOrder[0], moveOrder[1])){
					boardState.message = Message.ILLEGAL_MOVE;
					return boardState;
				}
			} else if (currentPiece instanceof King){
				if (!((King)currentPiece).validMove(moveOrder[0], moveOrder[1])){
					boardState.message = Message.ILLEGAL_MOVE;
					return boardState;
				} else if (Math.abs(targetFile.charAt(0) - initialFile.charAt(0)) == 2) {
					castlingMove = true;
				}
			} 
				//if a castling move, must change both rook and kings location here in Chess.java for SIMULATION board in next step
				if (castlingMove == true) {
					// checking color
					if (playerTurn == 'W') {
						// check if its right side castle or left side castle
						if (targetFile.equals("g")) {
							currentPiece.pieceFile = ReturnPiece.PieceFile.g;
							((Rook)(getSquare("h1"))).firstMoveDone = true;
							getSquare("h1").pieceFile = ReturnPiece.PieceFile.f;
						}
						if (targetFile.equals("c")) {
							currentPiece.pieceFile = ReturnPiece.PieceFile.c;
							((Rook)(getSquare("a1"))).firstMoveDone = true;
							getSquare("a1").pieceFile = ReturnPiece.PieceFile.d;
						}
						((King)whiteKing).firstMoveDone = true;
					}	else {
						if (targetFile.equals("g")) {
							currentPiece.pieceFile = ReturnPiece.PieceFile.g;
							((Rook)(getSquare("h8"))).firstMoveDone = true;
							getSquare("h8").pieceFile = ReturnPiece.PieceFile.f;
						}
						if (targetFile.equals("c")) {
							currentPiece.pieceFile = ReturnPiece.PieceFile.c;
							((Rook)(getSquare("a8"))).firstMoveDone = true;
							getSquare("a8").pieceFile = ReturnPiece.PieceFile.d;
						}
						((King)blackKing).firstMoveDone = true;
					}
					//3. then check if third argument (promotion or draw)
					if (moveOrder.length > 2) {
						if (moveOrder[2].equals("draw?")) {
							boardState.message = Message.DRAW;
						}
					} 
					//4. check if fourth argument (draw)
					if (moveOrder.length > 3) {
						boardState.message = Message.DRAW;
						return boardState;
					}

					clock++;
					Chess.player = Chess.player == Chess.Player.white ? Chess.Player.black : Chess.Player.white;
					return boardState;
				}
				// not a castling move
				//2. check if move places moving player's king in check (after this check the move will go through)

				currentPiece.pieceFile = ReturnPiece.PieceFile.valueOf(targetFile); //moves the piece
				currentPiece.pieceRank = targetRank;

				// en passant, removing eaten pawn
				if (currentPiece instanceof Pawn && 
				Math.abs((initialFile.charAt(0)-targetFile.charAt(0))) == 1 &&
				pieceOnTarget == null) {
					if (playerTurn == 'W') {
						pieceOnTarget = getSquare(""+currentPiece.pieceFile + (currentPiece.pieceRank-1));
					} else {
						pieceOnTarget = getSquare(""+currentPiece.pieceFile + (currentPiece.pieceRank+1));
					}
				}

				// eat the piece
				if (pieceOnTarget != null) {
					boardState.piecesOnBoard.remove(pieceOnTarget);
				}
				if (isCheck(boardState.piecesOnBoard, playerTurn) == true) {
					currentPiece.pieceFile = ReturnPiece.PieceFile.valueOf(initialFile);
					currentPiece.pieceRank = initialRank;
					if (pieceOnTarget != null) {
						boardState.piecesOnBoard.add(pieceOnTarget);
					}
					boardState.message = Message.ILLEGAL_MOVE;
					return boardState;
				}
				
			//if it goes through, check if king/rook/pawn was moved, if so set their fields (firstMove, timeOfTwoJump, etc)
				if ((""+(currentPiece.pieceType)).charAt(1) == 'R') {
					((Rook)currentPiece).firstMoveDone = true;
				} else if ((""+(currentPiece.pieceType)).charAt(1) == 'K') {
					((King)currentPiece).firstMoveDone = true;
				} else if ((""+(currentPiece.pieceType)).charAt(1) == 'P') {
					((Pawn)currentPiece).firstMoveDone = true;
						if (Math.abs(targetRank - initialRank) == 2) {
							((Pawn)currentPiece).timeOfTwoJump = clock;
						} 
				}

				// promotion case
				if ((""+currentPiece.pieceType).charAt(1) == 'P' ) {
					if (targetRank == 1 || targetRank == 8) {
						ReturnPiece promotedPiece;
						if (moveOrder.length > 2 && moveOrder[2] != "draw?") {
							switch(moveOrder[2]) {
							case "K": 
							promotedPiece = Pawn.promote((Pawn)currentPiece, 'K');
							break;
							case "R": 
							promotedPiece = Pawn.promote((Pawn)currentPiece, 'R');
							break;
							case "B": 
							promotedPiece = Pawn.promote((Pawn)currentPiece, 'B');
							break;
							case "P":
							promotedPiece = currentPiece;
							default: 
							promotedPiece = Pawn.promote((Pawn)currentPiece, 'Q');

						}
						} else {
							promotedPiece = Pawn.promote((Pawn)currentPiece, 'Q');
						}
						boardState.piecesOnBoard.add(promotedPiece);
						boardState.piecesOnBoard.remove(currentPiece);
					}
				}

				// check if it puts enemy in check
				char otherPlayer = playerTurn == 'W' ? 'B' : 'W';
				if (isCheck(boardState.piecesOnBoard, otherPlayer)) {
					kingInCheck = true;
					boardState.message = Message.CHECK;
					ReturnPiece kingPieceInCheck = otherPlayer == 'W' ? whiteKing : blackKing;
					if (isCheckmate(boardState.piecesOnBoard, otherPlayer, ""+kingPieceInCheck.pieceFile+kingPieceInCheck.pieceRank, threateningPiece)) {
						boardState.message = playerTurn == 'W' ? Message.CHECKMATE_WHITE_WINS: Message.CHECKMATE_BLACK_WINS;
					} 
				} else {
					kingInCheck = false;
				}
		//3. then check if third argument (promotion or draw)
		if (moveOrder.length > 2) {
			if (moveOrder[2].equals("draw?")) {
				boardState.message = Message.DRAW;
			}
		} 
		//4. check if fourth argument (draw)
		if (moveOrder.length > 3) {
			boardState.message = Message.DRAW;
			return boardState;
		}

		clock++;
		Chess.player = Chess.player == Chess.Player.white ? Chess.Player.black : Chess.Player.white;
		return boardState;
	}
	
	
	/**
	 * This method should reset the game, and start from scratch.
	 */
	public static void start() {
		Chess.boardState = new ReturnPlay();
		//create the piece array field in our Chess.boardState
		Chess.boardState.piecesOnBoard = createPieceArray(); 
		Chess.player = Chess.Player.white;
	}


	public static ReturnPiece getSquare(String square){
		String file = square.substring(0,1);
		int rank = Integer.parseInt(square.substring(1));


		for (ReturnPiece piece : boardState.piecesOnBoard){
			if (piece.pieceFile==PieceFile.valueOf(file)&&piece.pieceRank==rank){
				return piece;
			}
		}

		return null;
	}

	public static boolean isCheck(ArrayList<ReturnPiece> piecesOnBoard, char colorOfKingToCheckFor){
		String locationOfKing;

		if (colorOfKingToCheckFor=='W'){
			locationOfKing = Chess.whiteKing.toString().substring(0,2);
		} else { //color is black
			locationOfKing = Chess.blackKing.toString().substring(0,2);
		}

		//check every peice of opposing color
		for(ReturnPiece piece : piecesOnBoard){
			if ((""+piece.pieceType).charAt(0)==colorOfKingToCheckFor) {  //skip if same color
				continue;
			}

			String currentPieceLocation = piece.toString().substring(0,2);

			if (piece instanceof Pawn){
				if (((Pawn)piece).validMove(currentPieceLocation, locationOfKing)){
					Chess.threateningPiece = piece;
					return true;
				}
			} else if (piece instanceof Bishop){
				if (((Bishop)piece).validMove(currentPieceLocation, locationOfKing)){
					Chess.threateningPiece = piece;
					return true;
				}
			} else if (piece instanceof Rook){
				if (((Rook)piece).validMove(currentPieceLocation, locationOfKing)){
					Chess.threateningPiece = piece;
					return true;
				}
			} else if (piece instanceof Knight){
				if (((Knight)piece).validMove(currentPieceLocation, locationOfKing)){
					Chess.threateningPiece = piece;
					return true;
				}
			} else if (piece instanceof Queen){
				if (((Queen)piece).validMove(currentPieceLocation, locationOfKing)){
					Chess.threateningPiece = piece;
					return true;
				}
			} else if (piece instanceof King){
				if (((King)piece).validMove(currentPieceLocation, locationOfKing)){
					Chess.threateningPiece = piece;
					return true;
				}
			} 
		}
			return false;
	}



	public static boolean isCheckmate(ArrayList<ReturnPiece> piecesOnBoard, char colorOfKingToCheckFor, String locationOfKing, ReturnPiece threateningPiece){
		
		char kingFile = locationOfKing.charAt(0);
		int kingRank = Integer.parseInt(locationOfKing.substring(1));
		King currentKing;
		char opposingColor;
		String threateningPieceLocation = threateningPiece.toString().substring(0,2);


		if (colorOfKingToCheckFor=='W'){
			currentKing = (King)Chess.whiteKing;
			opposingColor = 'B';
		} else {
			currentKing = (King)Chess.blackKing;
			opposingColor = 'W';

		}

		//check if king can move himself out of check
		
			//check left move
			if (kingFile!='a'){
					if (currentKing.validMove(locationOfKing, ""+(char)(kingFile-1)+kingRank)){
					ReturnPiece pieceOnTarget = getSquare(""+(char)(kingFile-1)+kingRank);
					boolean deletedPiece = false;
						if (pieceOnTarget != null) {
							boardState.piecesOnBoard.remove(pieceOnTarget);
							deletedPiece = true;
						}
						currentKing.pieceFile = PieceFile.valueOf(""+(char)(kingFile-1));
						if (!Chess.isCheck(piecesOnBoard, colorOfKingToCheckFor)){
							 currentKing.pieceFile = PieceFile.valueOf(""+(char)kingFile); //change back to original
							 if (deletedPiece) boardState.piecesOnBoard.add(pieceOnTarget);
							return false;
						}
						if (deletedPiece) boardState.piecesOnBoard.add(pieceOnTarget);

						currentKing.pieceRank = kingRank;
						currentKing.pieceFile = PieceFile.valueOf(""+(char)kingFile);

					}
			}


			//check right move
			if (kingFile!='h'){
					if (currentKing.validMove(locationOfKing, (char)(kingFile+1)+""+kingRank)){
						ReturnPiece pieceOnTarget = getSquare((char)(kingFile+1)+""+kingRank);
						boolean deletedPiece = false;
						if (pieceOnTarget != null) {
							boardState.piecesOnBoard.remove(pieceOnTarget);
							deletedPiece = true;
						}
						currentKing.pieceFile = PieceFile.valueOf(""+(char)(kingFile+1));
						if (!Chess.isCheck(piecesOnBoard, colorOfKingToCheckFor)){
							 currentKing.pieceFile = PieceFile.valueOf(""+(char)kingFile); //change back to original
							if (deletedPiece) boardState.piecesOnBoard.add(pieceOnTarget);
							return false;
						}
						if (deletedPiece) boardState.piecesOnBoard.add(pieceOnTarget);
						currentKing.pieceRank = kingRank;
						currentKing.pieceFile = PieceFile.valueOf(""+(char)kingFile);
					}
			}


			//check up move
			if (kingRank!=8){
					if (currentKing.validMove(locationOfKing, ((char)kingFile+""+(kingRank+1)))){
						ReturnPiece pieceOnTarget = getSquare((char)kingFile+""+(kingRank+1));
						boolean deletedPiece = false;
						if (pieceOnTarget != null) {
							boardState.piecesOnBoard.remove(pieceOnTarget);
							deletedPiece = true;
						}
						currentKing.pieceRank = kingRank+1;
						if (!Chess.isCheck(piecesOnBoard, colorOfKingToCheckFor)){
							currentKing.pieceRank = kingRank; //change back to original
							if (deletedPiece) boardState.piecesOnBoard.add(pieceOnTarget);
							return false;
						}
						if (deletedPiece) boardState.piecesOnBoard.add(pieceOnTarget);

						currentKing.pieceRank = kingRank;
							currentKing.pieceFile = PieceFile.valueOf(""+(char)kingFile);
					}
			}

			//check down move
			if (kingRank!=1){
					if (currentKing.validMove(locationOfKing, ((char)kingFile+""+(kingRank-1)))){
						ReturnPiece pieceOnTarget = getSquare((char)kingFile+""+(kingRank-1));
						boolean deletedPiece = false;
						if (pieceOnTarget != null) {
							boardState.piecesOnBoard.remove(pieceOnTarget);
							deletedPiece = true;
						}
						currentKing.pieceRank = kingRank-1;
						if (!Chess.isCheck(piecesOnBoard, colorOfKingToCheckFor)){
							currentKing.pieceRank = kingRank; //change back to original
							if (deletedPiece) boardState.piecesOnBoard.add(pieceOnTarget);
							return false;
						}
						if (deletedPiece) boardState.piecesOnBoard.add(pieceOnTarget);
						currentKing.pieceRank = kingRank;
						currentKing.pieceFile = PieceFile.valueOf(""+(char)kingFile);
					}
			}

			//check diagonal up and right
			if (kingRank!=8&&kingFile!='h'){
					if (currentKing.validMove(locationOfKing, ((char)(kingFile+1)+""+(kingRank+1)))){
						ReturnPiece pieceOnTarget = getSquare((char)(kingFile+1)+""+(kingRank+1));
						boolean deletedPiece = false;
						if (pieceOnTarget != null) {
							boardState.piecesOnBoard.remove(pieceOnTarget);
							deletedPiece = true;
						}
						currentKing.pieceRank = kingRank+1;
						currentKing.pieceFile = PieceFile.valueOf(""+(char)(kingFile+1));
						if (!Chess.isCheck(piecesOnBoard, colorOfKingToCheckFor)){
							currentKing.pieceRank = kingRank;
							currentKing.pieceFile = PieceFile.valueOf(""+(char)kingFile);
							if (deletedPiece) boardState.piecesOnBoard.add(pieceOnTarget);
							return false;
						}
            
  					if (deletedPiece) boardState.piecesOnBoard.add(pieceOnTarget);
						currentKing.pieceRank = kingRank;
						currentKing.pieceFile = PieceFile.valueOf(""+(char)kingFile);
					}
			}


			//check diagonal up and left
			if (kingRank!=8&&kingFile!='a'){
					if (currentKing.validMove(locationOfKing, ((char)(kingFile-1)+""+(kingRank+1)))){
						ReturnPiece pieceOnTarget = getSquare((char)(kingFile-1)+""+(kingRank+1));
						boolean deletedPiece = false;
						if (pieceOnTarget != null) {
							boardState.piecesOnBoard.remove(pieceOnTarget);
							deletedPiece = true;
						}
						currentKing.pieceRank = kingRank+1;
						currentKing.pieceFile = PieceFile.valueOf(""+(char)(kingFile-1));
						if (!Chess.isCheck(piecesOnBoard, colorOfKingToCheckFor)){
							currentKing.pieceRank = kingRank;
							currentKing.pieceFile = PieceFile.valueOf(""+(char)kingFile);
							if (deletedPiece) boardState.piecesOnBoard.add(pieceOnTarget);
							return false;
						}
						  if (deletedPiece) boardState.piecesOnBoard.add(pieceOnTarget);
              currentKing.pieceRank = kingRank;
							currentKing.pieceFile = PieceFile.valueOf(""+(char)kingFile);
					}
			}


			//check diagonal down and right
			if (kingRank!=1&&kingFile!='h'){
					if (currentKing.validMove(locationOfKing, ((char)(kingFile+1)+""+(kingRank-1)))){
						ReturnPiece pieceOnTarget = getSquare((char)(kingFile+1)+""+(kingRank-1));
						boolean deletedPiece = false;
						if (pieceOnTarget != null) {
							boardState.piecesOnBoard.remove(pieceOnTarget);
							deletedPiece = true;
						}
						currentKing.pieceRank = kingRank-1;
						currentKing.pieceFile = PieceFile.valueOf(""+(char)(kingFile+1));
						if (!Chess.isCheck(piecesOnBoard, colorOfKingToCheckFor)){
              currentKing.pieceRank = kingRank;
							currentKing.pieceFile = PieceFile.valueOf(""+(char)kingFile);
							if (deletedPiece) boardState.piecesOnBoard.add(pieceOnTarget);
							return false;
						}
						
						if (deletedPiece) boardState.piecesOnBoard.add(pieceOnTarget);
						currentKing.pieceRank = kingRank;
						currentKing.pieceFile = PieceFile.valueOf(""+(char)kingFile);
					}
			}


			//check diagonal down and left
			if (kingRank!=1&&kingFile!='h'){
					if (currentKing.validMove(locationOfKing, ((char)(kingFile-1)+""+(kingRank-1)))){
						ReturnPiece pieceOnTarget = getSquare((char)(kingFile-1)+""+(kingRank-1));
						boolean deletedPiece = false;
						if (pieceOnTarget != null) {
							boardState.piecesOnBoard.remove(pieceOnTarget);
							deletedPiece = true;
						}
						currentKing.pieceRank = kingRank-1;
            currentKing.pieceFile = PieceFile.valueOf(""+(char)(kingFile-1));
						if (!Chess.isCheck(piecesOnBoard, colorOfKingToCheckFor)){
							currentKing.pieceRank = kingRank;
							currentKing.pieceFile = PieceFile.valueOf(""+(char)kingFile);
							if (deletedPiece) boardState.piecesOnBoard.add(pieceOnTarget);
							return false;
						}
						if (deletedPiece) boardState.piecesOnBoard.add(pieceOnTarget);
						currentKing.pieceRank = kingRank;
						currentKing.pieceFile = PieceFile.valueOf(""+(char)kingFile);
					}
			}

			//now must check if any piece on same team can eat the threatening piece
			if (reachableByTeam(piecesOnBoard, opposingColor, threateningPieceLocation)){
				// if they can, check if the king is still in check (double check case)
				if (isCheck(piecesOnBoard, colorOfKingToCheckFor)) {
						return false;
				}
			}

			//if no pieces of same team can eat, can any move in front? Only possible if threatening piece is queen, bishop, or rook
			int rankDiff = kingRank - threateningPiece.pieceRank;
			int fileDiff = kingFile - (threateningPiece.pieceFile+"").charAt(0);
			String blockLocation;


			if (threateningPiece instanceof Queen){
				if (rankDiff<0&&fileDiff>0&&rankDiff!=-1){ //queen is top left diagonal of king, not "right next to" though
						int count = 1;
						blockLocation = ""+(char)(kingFile-count)+(kingRank+count);
						while (!blockLocation.equals(threateningPieceLocation)){
							if (reachableByTeam(piecesOnBoard, opposingColor, blockLocation)){
							return false;
							}
							count++;
							blockLocation = ""+(char)(kingFile-count)+(kingRank+count);
						}
				} else if (rankDiff>0&&fileDiff>0&&rankDiff!=1){ //queen is bottom left diagonal of king, not "right next to" though
            int count = 1;
						blockLocation = ""+(char)(kingFile-count)+(kingRank-count);
						while (!blockLocation.equals(threateningPieceLocation)){
							if (reachableByTeam(piecesOnBoard, opposingColor, blockLocation)){
							return false;
							}
							count++;
							blockLocation = ""+(char)(kingFile-count)+(kingRank-count);
						}
				} else if (rankDiff<0&&fileDiff<0&&rankDiff!=1){ //queen is top right
						int count = 1;
						blockLocation = ""+(char)(kingFile+count)+(kingRank+count);
						while (!blockLocation.equals(threateningPieceLocation)){
							if (reachableByTeam(piecesOnBoard, opposingColor, blockLocation)){
							return false;
							}
							count++;
							blockLocation = ""+(char)(kingFile+count)+(kingRank+count);
						}
				} else if (rankDiff>0&&fileDiff<0&&rankDiff!=1){ //queen is bottom right
						int count = 1;
						blockLocation = ""+(char)(kingFile+count)+(kingRank-count);
						while (!blockLocation.equals(threateningPieceLocation)){
							if (reachableByTeam(piecesOnBoard, opposingColor, blockLocation)){
							return false;
							}
							count++;
							blockLocation = ""+(char)(kingFile+count)+(kingRank-count);
						}
				} else if (rankDiff<-1){ //queen is above and not next to
						int count = 1;
						blockLocation = ""+(char)kingFile+(kingRank+count);
						while (!blockLocation.equals(threateningPieceLocation)){
							if (reachableByTeam(piecesOnBoard, opposingColor, blockLocation)){
							return false;
							}
							count++;
							blockLocation = ""+(char)kingFile+(kingRank+count);
						}
					} else if (rankDiff>1){ //queen is below and not next to
						int count = 1;
						blockLocation = ""+(char)kingFile+(kingRank-count);
						while (!blockLocation.equals(threateningPieceLocation)){
							if (reachableByTeam(piecesOnBoard, opposingColor, blockLocation)){
							return false;
							}
							count++;
							blockLocation = ""+(char)kingFile+(kingRank-count);
						}
					} else if (fileDiff>1){ //queen to the left and not next to
						int count = 1;
						blockLocation = ""+(char)(kingFile-1)+kingRank;
						while (!blockLocation.equals(threateningPieceLocation)){
							if (reachableByTeam(piecesOnBoard, opposingColor, blockLocation)){
							return false;
							}
							count++;
							blockLocation = ""+(char)(kingFile-count)+kingRank;
						}
					} else if (fileDiff<-1){ //queen to the right and not next to
						int count = 1;
						blockLocation = ""+(char)(kingFile+1)+kingRank;
						while (!blockLocation.equals(threateningPieceLocation)){
							if (reachableByTeam(piecesOnBoard, opposingColor, blockLocation)){
							return false;
							}
							count++;
							blockLocation = ""+(char)(kingFile+count)+kingRank;
						}
					}

			} else if (threateningPiece instanceof Bishop){
				if (rankDiff<0&&fileDiff>0&&rankDiff!=-1){ 
						int count = 1;
						blockLocation = ""+(char)(kingFile-count)+(kingRank+count);
						while (!blockLocation.equals(threateningPieceLocation)){
							if (reachableByTeam(piecesOnBoard, opposingColor, blockLocation)){
							return false;
							}
							count++;
							blockLocation = ""+(char)(kingFile-count)+(kingRank+count);
						}
				} else if (rankDiff>0&&fileDiff>0&&rankDiff!=1){ 
						int count = 1;
						blockLocation = ""+(char)(kingFile-count)+(kingRank-count);
						while (!blockLocation.equals(threateningPieceLocation)){
							if (reachableByTeam(piecesOnBoard, opposingColor, blockLocation)){
							return false;
							}
							count++;
							blockLocation = ""+(char)(kingFile-count)+(kingRank-count);
						}
				} else if (rankDiff<0&&fileDiff<0&&rankDiff!=1){ 
						int count = 1;
						blockLocation = ""+(char)(kingFile+count)+(kingRank+count);
						while (!blockLocation.equals(threateningPieceLocation)){
							if (reachableByTeam(piecesOnBoard, opposingColor, blockLocation)){
							return false;
							}
							count++;
							blockLocation = ""+(char)(kingFile+count)+(kingRank+count);
						}
				} else if (rankDiff>0&&fileDiff<0&&rankDiff!=1){ 
						int count = 1;
						blockLocation = ""+(char)(kingFile+count)+(kingRank-count);
						while (!blockLocation.equals(threateningPieceLocation)){
							if (reachableByTeam(piecesOnBoard, opposingColor, blockLocation)){
							return false;
							}
							count++;
							blockLocation = ""+(char)(kingFile+count)+(kingRank-count);
						}
				}


			} else if (threateningPiece instanceof Rook){
					if (rankDiff<-1){ 
						int count = 1;
						blockLocation = ""+(char)kingFile+(kingRank+count);
						while (!blockLocation.equals(threateningPieceLocation)){
							if (reachableByTeam(piecesOnBoard, opposingColor, blockLocation)){
							return false;
							}
							count++;
							blockLocation = ""+(char)kingFile+(kingRank+count);
						}
					} else if (rankDiff>1){ 
						int count = 1;
						blockLocation = ""+(char)kingFile+(kingRank-count);
						while (!blockLocation.equals(threateningPieceLocation)){
							if (reachableByTeam(piecesOnBoard, opposingColor, blockLocation)){
							return false;
							}
							count++;
							blockLocation = ""+(char)kingFile+(kingRank-count);
						}
					} else if (fileDiff>1){ 
						int count = 1;
						blockLocation = ""+(char)(kingFile-1)+kingRank;
						while (!blockLocation.equals(threateningPieceLocation)){
							if (reachableByTeam(piecesOnBoard, opposingColor, blockLocation)){
							return false;
							}
							count++;
							blockLocation = ""+(char)(kingFile-count)+kingRank;
						}
					} else if (fileDiff<-1){ 
						int count = 1;
						blockLocation = ""+(char)(kingFile+1)+kingRank;
						while (!blockLocation.equals(threateningPieceLocation)){
							if (reachableByTeam(piecesOnBoard, opposingColor, blockLocation)){
							return false;
							}
							count++;
							blockLocation = ""+(char)(kingFile+count)+kingRank;
						}
					}
			}



		return true;
	}

	public static boolean reachableByTeam(ArrayList<ReturnPiece> piecesOnBoard, char colorToIgnore, String targetLocation){
			
		PieceFile targetFile = PieceFile.valueOf(targetLocation.substring(0, 1));
		int targetRank = Integer.parseInt(targetLocation.substring(1, 2));

		ReturnPiece pieceOnTarget = getSquare(targetLocation);
		char colorToCheckFor = colorToIgnore == 'W' ? 'B' : 'W';
		
		for (ReturnPiece piece : Chess.boardState.piecesOnBoard){
				if ((""+piece.pieceType).charAt(0)==colorToIgnore){
				continue;
				}			
				
				String currentPieceLocation = piece.toString().substring(0,2);
				PieceFile currentPieceFile = PieceFile.valueOf(currentPieceLocation.substring(0, 1));
				int currentPieceRank = Integer.parseInt(currentPieceLocation.substring(1, 2));

				if (piece instanceof Pawn){
				if (((Pawn)piece).validMove(currentPieceLocation, targetLocation)){
					
					// double check case, simulate blocking/eating and check if king still in check
					if (pieceOnTarget != null) {
						piecesOnBoard.remove(pieceOnTarget);
					}
					piece.pieceFile = targetFile;
					piece.pieceRank = targetRank;
					if (isCheck(piecesOnBoard, colorToCheckFor)) {
						piece.pieceFile = currentPieceFile;
						piece.pieceRank = currentPieceRank;
						if (pieceOnTarget != null) {
							piecesOnBoard.add(pieceOnTarget);
						}
						return false;
					}
						piece.pieceFile = currentPieceFile;
						piece.pieceRank = currentPieceRank;
						if (pieceOnTarget != null) {
							piecesOnBoard.add(pieceOnTarget);
						}
					return true;
				}
			} else if (piece instanceof Bishop){
				if (((Bishop)piece).validMove(currentPieceLocation, targetLocation)){
					if (pieceOnTarget != null) {
						piecesOnBoard.remove(pieceOnTarget);
					}
					piece.pieceFile = targetFile;
					piece.pieceRank = targetRank;
					if (isCheck(piecesOnBoard, colorToCheckFor)) {
						piece.pieceFile = currentPieceFile;
						piece.pieceRank = currentPieceRank;
						if (pieceOnTarget != null) {
							piecesOnBoard.add(pieceOnTarget);
						}
						return false;
					}
						piece.pieceFile = currentPieceFile;
						piece.pieceRank = currentPieceRank;
						if (pieceOnTarget != null) {
							piecesOnBoard.add(pieceOnTarget);
						}
					return true;
				}
			}  else if (piece instanceof Rook){
				if (((Rook)piece).validMove(currentPieceLocation, targetLocation)){
					if (pieceOnTarget != null) {
						piecesOnBoard.remove(pieceOnTarget);
					}
					piece.pieceFile = targetFile;
					piece.pieceRank = targetRank;
					if (isCheck(piecesOnBoard, colorToCheckFor)) {
						piece.pieceFile = currentPieceFile;
						piece.pieceRank = currentPieceRank;
						if (pieceOnTarget != null) {
							piecesOnBoard.add(pieceOnTarget);
						}
						return false;
					}
						piece.pieceFile = currentPieceFile;
						piece.pieceRank = currentPieceRank;
						if (pieceOnTarget != null) {
							piecesOnBoard.add(pieceOnTarget);
						}
				return true; 
		 	}
		 } else if (piece instanceof Knight){
		 	if (((Knight)piece).validMove(currentPieceLocation, targetLocation)){
				if (pieceOnTarget != null) {
						piecesOnBoard.remove(pieceOnTarget);
					}
					piece.pieceFile = targetFile;
					piece.pieceRank = targetRank;
					if (isCheck(piecesOnBoard, colorToCheckFor)) {
						piece.pieceFile = currentPieceFile;
						piece.pieceRank = currentPieceRank;
						if (pieceOnTarget != null) {
							piecesOnBoard.add(pieceOnTarget);
						}
						return false;
					}
						piece.pieceFile = currentPieceFile;
						piece.pieceRank = currentPieceRank;
						if (pieceOnTarget != null) {
							piecesOnBoard.add(pieceOnTarget);
						}
				return true;
		 	}
		 } else if (piece instanceof Queen){
		 	if (((Queen)piece).validMove(currentPieceLocation, targetLocation)){
				if (pieceOnTarget != null) {
						piecesOnBoard.remove(pieceOnTarget);
					}
					piece.pieceFile = targetFile;
					piece.pieceRank = targetRank;
					if (isCheck(piecesOnBoard, colorToCheckFor)) {
						piece.pieceFile = currentPieceFile;
						piece.pieceRank = currentPieceRank;
						if (pieceOnTarget != null) {
							piecesOnBoard.add(pieceOnTarget);
						}
						return false;
					}
						piece.pieceFile = currentPieceFile;
						piece.pieceRank = currentPieceRank;
						if (pieceOnTarget != null) {
							piecesOnBoard.add(pieceOnTarget);
						}
				return true;
		 	}
		    }	
			}

			return false;

	}


	public static ArrayList<ReturnPiece> createPieceArray() {
		ArrayList<ReturnPiece> pieceArr = new ArrayList<>();

		// Creating pawns
		char file = 'a';
		while (file < 'i') {
			pieceArr.add(new Pawn(ReturnPiece.PieceType.WP, 2, ReturnPiece.PieceFile.valueOf(""+file)));
			pieceArr.add(new Pawn(ReturnPiece.PieceType.BP, 7, ReturnPiece.PieceFile.valueOf(""+file)));
			file++;
		}
		
	// Rooks
	pieceArr.add(new Rook(ReturnPiece.PieceType.WR, 1, ReturnPiece.PieceFile.a));
	pieceArr.add(new Rook(ReturnPiece.PieceType.WR, 1, ReturnPiece.PieceFile.h));
	pieceArr.add(new Rook(ReturnPiece.PieceType.BR, 8, ReturnPiece.PieceFile.a));
	pieceArr.add(new Rook(ReturnPiece.PieceType.BR, 8, ReturnPiece.PieceFile.h));	

	// Knights
	pieceArr.add(new Knight(ReturnPiece.PieceType.WN, 1, ReturnPiece.PieceFile.b));
	pieceArr.add(new Knight(ReturnPiece.PieceType.WN, 1, ReturnPiece.PieceFile.g));
	pieceArr.add(new Knight(ReturnPiece.PieceType.BN, 8, ReturnPiece.PieceFile.b));
	pieceArr.add(new Knight(ReturnPiece.PieceType.BN, 8, ReturnPiece.PieceFile.g));

	// Bishops
	pieceArr.add(new Bishop(ReturnPiece.PieceType.WB, 1, ReturnPiece.PieceFile.c));
	pieceArr.add(new Bishop(ReturnPiece.PieceType.WB, 1, ReturnPiece.PieceFile.f));
	pieceArr.add(new Bishop(ReturnPiece.PieceType.BB, 8, ReturnPiece.PieceFile.c));
	pieceArr.add(new Bishop(ReturnPiece.PieceType.BB, 8, ReturnPiece.PieceFile.f));

	// Queens
	pieceArr.add(new Queen(ReturnPiece.PieceType.WQ, 1, ReturnPiece.PieceFile.d));
	pieceArr.add(new Queen(ReturnPiece.PieceType.BQ, 8, ReturnPiece.PieceFile.d));

	// Kings
	ReturnPiece whiteKing = new King(ReturnPiece.PieceType.WK, 1, ReturnPiece.PieceFile.e);
	ReturnPiece blackKing = new King(ReturnPiece.PieceType.BK, 8, ReturnPiece.PieceFile.e);
	pieceArr.add(whiteKing);
	pieceArr.add(blackKing);

	Chess.whiteKing = whiteKing;
	Chess.blackKing = blackKing;
		
	return pieceArr;
	}
} 
