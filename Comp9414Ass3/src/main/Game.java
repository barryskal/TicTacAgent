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
	private PositionState playerMark;
	
	
	
	/**
	 * Initialises a new game by creating a board with containining 9 tic-tac-toe blocks that are 
	 * all initially empty.
	 * @param thisPlayerRepresentedBy	The representation of the player that is being controlled by this program
	 */
	public Game(PositionState thisPlayerRepresentedBy) {
		board = new ArrayList<Block>();
		playerMark = thisPlayerRepresentedBy;
		// Note that as per the spec, the first move always is the player with X.
		whoHasNextMove = PositionState.X;
		
		
		/* In order to simplify how the blocks are referenced in the ArrayList, the 0th
		 * element is set to null
		 */
		
		board.add(null);
		
		for (int i = 0; i < NUM_BLOCKS; i++)
			board.add(new Block(playerMark));
		
	}
	
	/**
	 * Creates a new game that is a copy of the given game
	 * @param copiedGame				The game to be copied
	 * @param nextMoveToBeMadeBy		The representation of the player that is making the next move. 
	 * @param thisPlayerRepresentedBy	The representation of the player that is being controlled by this program
	 */
	public Game(Game copiedGame, PositionState nextMoveToBeMadeBy, PositionState thisPlayerRepresentedBy)
	{
		board = new ArrayList<Block>();
		playerMark = thisPlayerRepresentedBy;
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
		setWhoHasNextMove();
	}
	
	private void setWhoHasNextMove()
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

	
	public boolean isTerminalState() 
	{
		return Math.abs(heuristicFunctionValueOfCurrentBlock(lastMoveMadeInBlockNumber)) >= 100;  
	}

	
	public int heuristicFunctionValueOfGame()
	{
		int heuristicValue = 0;
		for (Block currentBlock : board)
		{
			/* The block at index, 0 is set to null, so just ignore that */
			if (currentBlock == null)
				continue;
			heuristicValue += currentBlock.heuristicValue();
		}
		
		return heuristicValue;
	}
	
	public int heuristicFunctionValueOfCurrentBlock(int blockId) 
	{
		Block nextBlock = getBlock(blockId);
		return nextBlock.heuristicValue();
	}

	
	public boolean isOpponentPlaying() 
	{
		return playerMark != whoHasNextMove;
	}
	
	public int getLastBlockPlayedIn()
	{
		return lastMoveMadeInBlockNumber;
	}

	
	public List<Game> getChildrenOfCurrentNode() 
	{
		List<Integer> listOfEmptyCells = getBlock(nextMoveInBlockNumber).getListOfBestMovesForThisCell(whoHasNextMove);
		ArrayList<Game> childrenOfThisGame = new ArrayList<Game>(listOfEmptyCells.size());
		
		for (int currentCell : listOfEmptyCells)
		{
			Game copiedGame = new Game(this, whoHasNextMove, playerMark);
			copiedGame.setMove(whoHasNextMove, nextMoveInBlockNumber, currentCell);
			childrenOfThisGame.add(copiedGame);
		}
		
		return childrenOfThisGame;
	}
	
	
	public int alphaBetaSearchResult(int depth, int alpha, int beta) 
	{
		if (isTerminalState())
			return heuristicFunctionValueOfCurrentBlock(lastMoveMadeInBlockNumber);
		else if (depth == 0)
			return heuristicFunctionValueOfGame();
	
		
		List<Game> childrenOfCurrentNode = getChildrenOfCurrentNode();
		
		if (!isOpponentPlaying())
		{
			for (Game childNode : childrenOfCurrentNode)
			{
				int alphaValue = childNode.alphaBetaSearchResult(depth - 1, alpha, beta);
				//System.out.println("My Alpha Beta Score: " + alphaValue);
				//childNode.printGame();
				if (alphaValue > alpha)
					alpha = alphaValue;
				
				if (alpha >= beta)
					return alpha;
			}
			return alpha;
		}
		else 
		{
			//boolean debug = false; 
			//if (nextMoveInBlockNumber == 5)
				//debug = true;
			for (Game childNode : childrenOfCurrentNode)
			{
				int betaValue = childNode.alphaBetaSearchResult(depth - 1, alpha, beta);
				//if (debug && childNode.getLastBlockPlayedIn() == 5)
					//System.out.println("Option: " + childNode.getNextBlockToPlayIn() + ", Opponent Alpha Beta Score: " + betaValue);
				//childNode.printGame();
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
	
	
}
