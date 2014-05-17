package main;

import java.io.IOException;
import java.util.List;
import java.util.Random;


/**
 * 
 * @author Barry SSkalrud
 * The player class represents this player. It contains methods for parsing commands from the server,
 * deciding which move to make next and others
 *
 */
public class Player {
	
	private PositionState thisPlayersMark;
	private PositionState opponentMark;
	private int lastPositionPlayed;
	private Game currentGameState;
	private Controller gameController;
	
	public Player(Controller inController) 
	{
		gameController = inController;
	}
	
	public void setPlayersMark (PositionState inPositionState) {
		thisPlayersMark = inPositionState;
		
		// Set the opponents mark as the opposite of this player.
		if (thisPlayersMark == PositionState.X)
			opponentMark = PositionState.O;
		else
			opponentMark = PositionState.X;
	}
	
	public PositionState getPlayersMark()
	{
		return thisPlayersMark;
	}
	
	public void initiateGameWithOpponentsStartingMove(int blockNumber, int positionNumber) 
	{
		currentGameState = new Game();
		currentGameState.setMove(opponentMark, blockNumber, positionNumber);
	}
	
	public void initiateGame() 
	{
		currentGameState = new Game();
	}
	
	public void updateBoardWithPlayerMove(int blockNumber, int positionNumber) 
	{
		currentGameState.setMove(thisPlayersMark, blockNumber, positionNumber);
		lastPositionPlayed = positionNumber;
	}
	
	/**
	 * This method places the opponent's piece in the given block number and position number.
	 * It is normally called using the alternative signature; however it should be called directly
	 * at the start of the game when the opponent moves first. 
	 * @param blockNumber		The block where the move is being played.
	 * @param positionNumber	The position in the block where the move is being played. 
	 */
	public void makeOpponentMove(int blockNumber, int positionNumber)
	{
		currentGameState.setMove(opponentMark, blockNumber, positionNumber);
	}
	
	/**
	 * Normally a move is received from the server with just the position number. This 
	 * move should be made in the block designated by this player's last move. 
	 * @param positionNumber	The position in the designated block where this move is being made
	 */
	public void makeOpponentMove(int positionNumber) 
	{
		// The block the opponent is playing in is equal to the last position that this player played in
		int blockNumber = lastPositionPlayed;
		
		makeOpponentMove(blockNumber, positionNumber);
		lastPositionPlayed = positionNumber;
	}
	
	/**
	 * This method, decides on the next move and updates the current board as appropriate.
	 * @return	The position of the move made
	 */
	public int decideNextMove() 
	{
		Block blockToPlayIn = currentGameState.getBlock(lastPositionPlayed);
		
		int position = getNextMoveRandomly(blockToPlayIn);
		
		updateBoardWithPlayerMove(lastPositionPlayed, position);
		
		return position;
			
	}
	
	
	public int getNextMoveRandomly(Block blockToPlayIn) 
	{
		List<Integer> listOfEmptyCells = blockToPlayIn.getListOfEmptyCells();
		
		/* Get a random index between 0 and the last index of the list
		 * of empty cells
		 */
		Random random = new Random();
		int randomIndex = random.nextInt(listOfEmptyCells.size());
		
		return listOfEmptyCells.get(randomIndex);
	}
	
	
	
	

}
