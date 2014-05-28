package main;

import java.util.Hashtable;
import java.util.List;
import java.util.Random;


/**
 * 
 * @author Barry Skalrud
 * The player class represents this player. It contains methods for parsing commands from the server,
 * deciding which move to make next and others
 *
 */
public class Player {
	
	private PositionState thisPlayersMark;
	private PositionState opponentMark;
	private int lastPositionPlayed;
	private Game currentGameState;
	private int myMoveCounter;
	private Hashtable<String, Integer> heuristicResults;
	
	/**
	 * Creates a new player object. 
	 */
	public Player()
	{
		heuristicResults = new Hashtable<String, Integer>();
	}
	
	/**
	 * Sets the representation of the player controlled by this program
	 * @param inPositionState A position state representing this player
	 */
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
	
	/**
	 * Begins a representation of a new game (i.e. no moves on the board) using the given 
	 * representation of the player controlled by this program. 
	 * @param playerMark	A PositionState representation of the current player, i.e. 'X' or 'O'
	 */
	public void initiateGame(PositionState playerMark) 
	{
		setPlayersMark(playerMark);
		myMoveCounter = 0;
		currentGameState = new Game(this);
	}
	
	
	/**
	 * Updates the current game with a move from this player. 
	 * @param blockNumber 		An int representing the block number in which the move is made.
	 * @param positionNumber	An int representing the position of the move within the given block
	 */
	public void updateBoardWithPlayerMove(int blockNumber, int positionNumber) 
	{
		currentGameState.setMove(thisPlayersMark, blockNumber, positionNumber);
		myMoveCounter++;
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
		if (Agent.debugMode)
			System.out.println("Opponent move: " + blockNumber + ", " + positionNumber);
		
		currentGameState.setMove(opponentMark, blockNumber, positionNumber);
		lastPositionPlayed = positionNumber;
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
	 * @return	An int representing position of the move to be made in the current block.
	 */
	public int decideNextMove() 
	{
		Block blockToPlayIn = currentGameState.getBlock(lastPositionPlayed);
		
		int position;
		
		if (Agent.debugMode) 
			System.out.println("Move: " + myMoveCounter);
		
		/*
		 * Note that in order to speed up game play, the first move is made randomly and the subesequent
		 * two moves have a shallow search depth. From there the depth is steadily increased so that the 
		 * heuristic hash table can be populated, which will make the deeper searches later on faster.   
		 */
		if (myMoveCounter == 0)
		{
			position = getNextMoveRandomly(blockToPlayIn);			
		}
		else if (myMoveCounter < 2)
		{
			position = getNextMoveUsingAlphaBeta(1);
		}
		else if (myMoveCounter <= 5)
		{
			position = getNextMoveUsingAlphaBeta(7);
		}
		else if (myMoveCounter <= 11)
		{
			position = getNextMoveUsingAlphaBeta(9);
		}
		else if (myMoveCounter <= 15)
		{
			position = getNextMoveUsingAlphaBeta(11);
		}
		else
		{
			position = getNextMoveUsingAlphaBeta(13);
		}
		
		updateBoardWithPlayerMove(lastPositionPlayed, position);
		
		return position;
			
	}
	
	/**
	 * Determines the position of the next move in the current block using alpha-beta pruning and 
	 * the given depth
	 * @param depth		The value of the depth for the alpha-beta search
	 * @return			An integer representing the position of the next move to make. 
	 */
	public int getNextMoveUsingAlphaBeta(int depth)
	{
			int alpha= Integer.MIN_VALUE;
			int beta = Integer.MAX_VALUE;
			int bestMoveToMake = 0;
			int result = alpha; 
			
			List<Integer> listOfEmptyCells = currentGameState.getBlock(currentGameState.getNextBlockToPlayIn()).getListOfBestMovesForThisCell(thisPlayersMark);
			for (int emptyCell : listOfEmptyCells)
			{
				Game expandedState = new Game(currentGameState, thisPlayersMark);
				expandedState.setMove(thisPlayersMark, currentGameState.getNextBlockToPlayIn(), emptyCell);
				
				result = expandedState.alphaBetaSearchResult(depth, alpha, beta);
				
				if (Agent.debugMode)
					System.out.println("Option: " + expandedState.getNextBlockToPlayIn() + ", result: " + result);
				
				if (result > alpha)
				{
					bestMoveToMake = expandedState.getNextBlockToPlayIn();
					alpha = result;
				}					
				
			}
			
			if (Agent.debugMode)
				System.out.println("Went with: " + alpha);
			
			return bestMoveToMake;	
		
	}
	
	
	/**
	 * Returns a position to play in that is chosen from the empty cells in the given
	 * block
	 * @param blockToPlayIn	An int representing the block that is to be played in 
	 * @return				An int representing the position for the next move. 
	 */
	private int getNextMoveRandomly(Block blockToPlayIn) 
	{
		List<Integer> listOfEmptyCells = blockToPlayIn.getListOfEmptyCells();
		
		/* Get a random index between 0 and the last index of the list
		 * of empty cells
		 */
		Random random = new Random();
		int randomIndex = random.nextInt(listOfEmptyCells.size());
		
		return listOfEmptyCells.get(randomIndex);
	}
	
	public Game getCurrentState()
	{
		return currentGameState;
	}
	
	
	/**
	 * If a heuristic has previously been calculated for the given block, it will be 
	 * retrieved, saving the extra calculation. 
	 * @param block		A Block object for which the heuristic is being calculated.
	 * @return			Returns	-1000 if the given block has not been encountered before 
	 * 							
	 */
	public int getHeuristcValueIfExists(Block block)
	{
		Integer heuristicValue = heuristicResults.get(block.getPositionStates());
		if (heuristicValue == null)
			return -1000;
		
		return heuristicValue;
	}
	
	/**
	 * Updates the data structure containing calculated heuristic values for different Block 
	 * configurations. 
	 * @param block				A Block object representing the block configuration
	 * @param heuristicValue	An int representing the calculated heuristic value for the
	 * given Block object. 
	 */
	public void updateHeuristicValue(Block block, int heuristicValue)
	{
		heuristicResults.put(block.getPositionStates(), heuristicValue);
	}
	

}
