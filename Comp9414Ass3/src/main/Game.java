package main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Game {

	private ArrayList<Block> board;
	private int nextMoveInBlockNumber;
	private int lastMoveMadeInBlockNumber;
	private final int NUM_BLOCKS = 9;
	public PositionState whoHasNextMove;
	private Player thisPlayer;
	
	
	
	/**
	 * Initialises a new game by creating a board with containining 9 tic-tac-toe blocks that are 
	 * all initially empty.
	 * @param thisPlayerRepresentedBy	The representation of the player that is being controlled by this program
	 */
	public Game(Player inPlayer) {
		board = new ArrayList<Block>();
		thisPlayer = inPlayer;
		// Note that as per the spec, the first move always is the player with X.
		whoHasNextMove = PositionState.X;
		
		
		/* In order to simplify how the blocks are referenced in the ArrayList, the 0th
		 * element is set to null
		 */
		
		board.add(null);
		
		for (int i = 0; i < NUM_BLOCKS; i++)
			board.add(new Block(thisPlayer));
		
	}
	
	/**
	 * Creates a new game that is a copy of the given game
	 * @param copiedGame				The game to be copied
	 * @param nextMoveToBeMadeBy		The representation of the player that is making the next move. 
	 */
	public Game(Game copiedGame, PositionState nextMoveToBeMadeBy)
	{
		board = new ArrayList<Block>();
		thisPlayer = copiedGame.getPlayerRepresentedByThisProgram();
		whoHasNextMove = nextMoveToBeMadeBy;
		
		board.add(null);
		
		for (int block = 1; block <= NUM_BLOCKS; block++)
		{
			board.add(new Block(copiedGame.getBlock(block)));
		}
	}
	
	/**
	 * This sets the given position on the board.
	 * @param who			Who is making this move? "x", or "o"
	 * @param blockNumber	Which block is the move being made in (1-9)
	 * @param position		Which position in the given block is being changed (1-9)
	 */
	public void setMove(PositionState who, int blockNumber, int position)
	{
		
		board.get(blockNumber).setPosition(position, who);
		lastMoveMadeInBlockNumber = blockNumber;
		nextMoveInBlockNumber = position;
		updateWhoHasNextMove();
	}
	
	private void updateWhoHasNextMove()
	{
		if (whoHasNextMove == PositionState.X)
			whoHasNextMove = PositionState.O;
		else
			whoHasNextMove = PositionState.X;
	}
	
	public Block getBlock(int blockNumber) 
	{
		return board.get(blockNumber);
	}

	/**
	 * Determines whether the current state of the board is terminal, 
	 * i.e. either 'X' or 'O' has one
	 * @return	A boolean value representing whether the game has ended.
	 */
	public boolean isTerminalState() 
	{
		return Math.abs(heuristicFunctionValueOfGivenBlock(lastMoveMadeInBlockNumber)) >= 100;  
	}

	/**
	 * Calculates the heuristic function value for the current state of the baord.
	 * @return An int representing the heuristic function value calculated for this board.
	 */
	public int heuristicFunctionValueOfGame()
	{
		int heuristicValue = 0;
		for (Block currentBlock : board)
		{
			/* The block at index, 0 is set to null, so just ignore that */
			if (currentBlock == null)
				continue;
			heuristicValue += currentBlock.calculateHeuristicValue();
		}
		
		return heuristicValue;
	}
	
	/**
	 * Returns the heuristic function value for a given block. 
	 * @param blockId	An int representing the block for which you want 
	 * the heuristic function value. 
	 * @return			An int representing the heuristic function value 
	 * for the given block
	 */
	public int heuristicFunctionValueOfGivenBlock(int blockId) 
	{
		Block nextBlock = getBlock(blockId);
		return nextBlock.calculateHeuristicValue();
	}

	
	public boolean isOpponentPlaying() 
	{
		return thisPlayer.getPlayersMark() != whoHasNextMove;
	}
	
	public int getLastBlockPlayedIn()
	{
		return lastMoveMadeInBlockNumber;
	}

	
	/**
	 * Returns the best score obtained by performing a alpha beta search for a given depth.
	 * The algorithm for this function is based on the COMP9414 week 4 lecture notes, 
	 * slide 26.  
	 * @param depth		An int representing the depth for the search
	 * @param alpha		An int representing the alpha value used as input for the search
	 * @param beta		An int representing the beta value used as input for the search
	 * @return			An int representing the best possible heuristic value found using the 
	 * given search
	 */
	public int alphaBetaSearchResult(int depth, int alpha, int beta) 
	{
		if (isTerminalState())
			return heuristicFunctionValueOfGivenBlock(lastMoveMadeInBlockNumber);
		else if (depth == 0)
			return heuristicFunctionValueOfGame();
	
		List <Integer> listOfEmptyCells = getBlock(nextMoveInBlockNumber).getListOfBestMovesForThisCell(whoHasNextMove);
		
		if (!isOpponentPlaying())
		{
			for (int emptyCell : listOfEmptyCells)
			{
				// Expand this game state
				Game expandedState = new Game(this, whoHasNextMove);
				expandedState.setMove(whoHasNextMove, nextMoveInBlockNumber, emptyCell);
				
				int alphaValue = expandedState.alphaBetaSearchResult(depth - 1, alpha, beta);

				if (alphaValue > alpha)
					alpha = alphaValue;
				
				if (alpha >= beta)
					return alpha;
			}
			return alpha;
		}
		else 
		{

			for (int emptyCell : listOfEmptyCells)
			{
				Game expandedState = new Game(this, whoHasNextMove);
				expandedState.setMove(whoHasNextMove, nextMoveInBlockNumber, emptyCell);
				int betaValue = expandedState.alphaBetaSearchResult(depth - 1, alpha, beta);

				if (betaValue < beta)
					beta = betaValue;
				
				if (beta <= alpha)
					return beta;
			}
			return beta;
		}
	}
	
	
	/**
	 * Prints the state of the current game to stdout. 
	 */
	public void printGame()
	{
		String[] gameArray = getGameAsArray();	
		
		for (int printRow = 0; printRow < 9; printRow++)
		{
			/* Print a separator between the rows of games */
			if ((printRow % 3) == 0)
			{
				char[] separatingRow = new char[gameArray[printRow].length()];
				Arrays.fill(separatingRow, '-');
				System.out.println(separatingRow);
			}
	
			System.out.println("|" + gameArray[printRow]);
				
		}
		
	}

	/**
	 * Returns a representation of the current game as an array of Strings
	 * @return An array of strings showing the current state of the game.
	 */
	private String[] getGameAsArray() {
		String[] gameArray = {"","","","","","","","",""};
		
		int printRow = 0;
		
		for (int gameRow = 0; gameRow < 3; gameRow++)
		{

			int startingBlock = (gameRow * 3) + 1;
			for (int gameCol = startingBlock; gameCol < (startingBlock + 3); gameCol++)
			{
				String[] blockAsString = getBlock(gameCol).getBlockAsString();
				printRow = startingBlock - 1;
				for (int blockRow = 0; blockRow < 3; blockRow++)
				{
					gameArray[printRow] = gameArray[printRow].concat(blockAsString[blockRow] + "|");
					printRow++;
				}
			}
			
		}
		return gameArray;
	}

	
	/**
	 * This method returns the next block to be played in. i.e. the value 1 - 9. 
	 * @return An integer representing the next block to play in. 
	 */
	public int getNextBlockToPlayIn()
	{
		return nextMoveInBlockNumber;
	}
	
	public Player getPlayerRepresentedByThisProgram()
	{
		return thisPlayer;
	}
}
