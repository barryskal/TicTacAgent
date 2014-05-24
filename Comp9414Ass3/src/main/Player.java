package main;

import java.util.ArrayList;
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
	private Controller gameController;
	private int myMoveCounter;
	public AlphaBetaSearchTree currentAlphaBetaSearchTree;
	
	public Player(Controller inController) 
	{
		gameController = inController;
	}
	
	private void setPlayersMark (PositionState inPositionState) {
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
	
	
	public void initiateGame(PositionState playerMark) 
	{
		setPlayersMark(playerMark);
		myMoveCounter = 0;
		currentGameState = new Game(thisPlayersMark);
		currentAlphaBetaSearchTree = null;
	}
	
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
	 * @return	The position of the move made
	 */
	public int decideNextMove() 
	{
		Block blockToPlayIn = currentGameState.getBlock(lastPositionPlayed);
		
		int position;
		System.out.println("Move: " + myMoveCounter);
		if (myMoveCounter == 0)
		{
			
			position = getNextMoveRandomly(blockToPlayIn);			
		}
		else if (myMoveCounter < 2)
		{
			//currentAlphaBetaSearchTree = new AlphaBetaSearchTree(currentGameState, 0);
			position = getNextMoveUsingAlphaBeta(1);
		}
		else if (myMoveCounter <= 100)
		{
			//currentAlphaBetaSearchTree = new AlphaBetaSearchTree(currentGameState, 0);
			//currentAlphaBetaSearchTree = currentAlphaBetaSearchTree.selectTreeCorrespondingToOpponentMove(lastPositionPlayed);
			position = getNextMoveUsingAlphaBeta(7);
		}
		else if (myMoveCounter <= 10)
		{
			//currentAlphaBetaSearchTree = currentAlphaBetaSearchTree.selectTreeCorrespondingToOpponentMove(lastPositionPlayed);
			position = getNextMoveUsingAlphaBeta(4);
		}
		else if (myMoveCounter <= 8)
		{
			//currentAlphaBetaSearchTree = currentAlphaBetaSearchTree.selectTreeCorrespondingToOpponentMove(lastPositionPlayed);
			position = getNextMoveUsingAlphaBeta(6);
		}
		else
		{
			//currentAlphaBetaSearchTree = currentAlphaBetaSearchTree.selectTreeCorrespondingToOpponentMove(lastPositionPlayed);
			position = getNextMoveUsingAlphaBeta(9);
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
		
			/*currentAlphaBetaSearchTree.expandTree(depth);
			int alpha= Integer.MIN_VALUE;
			int beta = Integer.MAX_VALUE;
			
			currentAlphaBetaSearchTree.doAlphaBetaSearch(alpha, beta);
			
			// select the best search tree based on the results of the alpha beta search
			
			currentAlphaBetaSearchTree = currentAlphaBetaSearchTree.selectBestTree();
			
			Game gameCorrespondingToBestOption = currentAlphaBetaSearchTree.getRootNode();
			
			int lastPositionPlayedInThatGame = gameCorrespondingToBestOption.getNextBlockToPlayIn();
			
			return lastPositionPlayedInThatGame;*/
			
	
			int alpha= Integer.MIN_VALUE;
			int beta = Integer.MAX_VALUE;
			int bestMoveToMake = 0;
			int result = alpha; 
			
			
			for (Game childNode : currentGameState.getChildrenOfCurrentNode())				
			{
				result = childNode.alphaBetaSearchResult(depth, alpha, beta);
				//childNode.printGame();
				System.out.println("Option: " + childNode.getNextBlockToPlayIn() + ", result: " + result);
				if (result > alpha)
				{
					bestMoveToMake = childNode.getNextBlockToPlayIn();
					alpha = result;
				}					
				
			}
			
			System.out.println("Went with: " + alpha);
			return bestMoveToMake;	
		
	}
	
	
	
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
	
	
	
	

}
