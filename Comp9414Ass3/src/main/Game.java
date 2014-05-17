package main;

import java.util.ArrayList;

public class Game {

	private ArrayList<Block> board;
	private int nextMoveInBlockNumber;
	private final int NUM_BLOCKS = 9;
	
	
	
	/**
	 * Initialises a new game by creating a board with containining 9 tic-tac-toe blocks that are 
	 * all initially empty.
	 */
	public Game() {
		
		for (int i = 1; i <= NUM_BLOCKS; i++)
			board.add(new Block());
		
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
		nextMoveInBlockNumber = position;
	}
	
	public Block getBlock(int blockNumber) 
	{
		return board.get(blockNumber);
	}
	
}
