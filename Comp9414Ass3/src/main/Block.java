package main;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/*
 * This class represents one of the 9 tic-tac-toe boards as part of the game. 
 */
public class Block {
	
	private String positionStates;
	private Player thisPlayer;
	private final static int TOP_LEFT = 1;
	private final static int TOP_RIGHT = 3;
	
	
	/*
	 * Creates a new block where each position is set to empty.
	 */
	public Block(Player inPlayer) {
		thisPlayer = inPlayer;
		
		/* Note that the value at index = 0 is set to E; however it should never be used.
		 * This makes it simpler to reference the positions by array index. 
		 */
		positionStates = "----------";

	}
	
	/**
	 * Creates a new Block as a copy of the given block
	 * @param copiedBlock The Block to be copied
	 */
	public Block(Block copiedBlock)
	{
		thisPlayer = copiedBlock.getCurrentPlayer();
		positionStates = copiedBlock.getPositionStates();
		
	}
	
	
	/**
	 * This method returns the representation of this automated player in the Game 
	 * @return A PositionState representing this player.
	 */
	public Player getCurrentPlayer()
	{
		return thisPlayer;
	}
	
	/**
	 * Sets the position in this block to the state provided, i.e. not empty.
	 * @param position 		The position that needs to be changed
	 * @param positionState	The state to which it is being changed (i.e. X or Y)
	 */
	public void setPosition(int position, PositionState positionState)
	{
		positionStates = positionStates.substring(0, position) + positionState.getValue() + positionStates.substring(position + 1);

	}
	
	/**
	 * Checks whether the move applied is allowable
	 * @param 	position 	The value of the position, a number from 1 to 9
	 * @return 				True if the cell is empty, false otherwise.
	 */
	public boolean isValidMove(int position)
	{
		return positionStates.charAt(position) == '-';
	}
	
	/**
	 * This function generates a list of the empty positions in this block.
	 * @return Returns a list of cell positions (i.e. values 1-9) that are currently empty. 
	 */
	public List<Integer> getListOfEmptyCells()
	{
		ArrayList<Integer> listOfEmptyCells = new ArrayList<Integer>();
		int i = 0;
		for (char currentPosition : positionStates.toCharArray())
		{
			if (currentPosition == '-')
				listOfEmptyCells.add(i);
			i++;
		}
		
		/* Remove the 0th element, as it is just the base element, which isn't real. */
		listOfEmptyCells.remove(0);
		
		return listOfEmptyCells;
	}

	/**
	 * Performs a similar function to getListOfEmptyCells. Howver, instead of just returning 
	 * a list of empty cells. This method sorts those cells in order of which move would 
	 * generate the best heuristic value for the current block. That is, the move that
	 * would generate the best heuristic value (for the player who has the next move) will 
	 * be at the head of the list. 
	 * @param whoHasNextMove	A PositionState representing who has the next move in this block
	 * @return	Returns a list of cell positions (i.e. values 1-9) that are currently empty.
	 */
	public List<Integer> getListOfBestMovesForThisCell(PositionState whoHasNextMove)
	{
		List<Integer> emptyCells = getListOfEmptyCells();
		ArrayList<Integer> sortedListOfMoves = new ArrayList<Integer>();
		Hashtable<Integer, Integer> heuristicValuesForEmptyOptions = new Hashtable<Integer, Integer>();
		
		for (int emptyCell : emptyCells)
		{
			Block tempBlock = new Block(this);
			tempBlock.setPosition(emptyCell, whoHasNextMove);
			int heuristicValue = tempBlock.calculateHeuristicValue();
			heuristicValuesForEmptyOptions.put(emptyCell, heuristicValue);
			if (sortedListOfMoves.isEmpty())
			{
				sortedListOfMoves.add(emptyCell);
			}
			else
			{
				
				int insertIndex = 0;
				while (insertIndex < sortedListOfMoves.size())
				{
					int heuristicValueAIndex = heuristicValuesForEmptyOptions.get(sortedListOfMoves.get(insertIndex));
					if (heuristicValue > heuristicValueAIndex)
					{
						sortedListOfMoves.add(insertIndex, emptyCell);
						break;
					}
					insertIndex++;
				}
				
				if (insertIndex == sortedListOfMoves.size())
					sortedListOfMoves.add(emptyCell);
				
				
			}
			
		}
		
		return sortedListOfMoves;
	}
	
	
	
	
	/**
	 * Performs the heuristic function on this block. 
	 * @return 	The value obtained by performing the heuristic function. Note that any value 
	 * greater than 100 indicates a terminal position.
	 */
	public int calculateHeuristicValue()
	{
		// Look up if we've calculated a heuristic for this block already
		int heuristicValue = thisPlayer.getHeuristcValueIfExists(this);
		
		if (heuristicValue != -1000)
			return heuristicValue;
		
		heuristicValue = 0;
		
		// Analyse top row
		heuristicValue += getSumForGivenLine(TOP_LEFT, 1);
		
		// Bottom Row
		heuristicValue += getSumForGivenLine(TOP_LEFT + 6, 1);
		
		// Left Column
		heuristicValue += getSumForGivenLine(TOP_LEFT, 3);
		
		// Right Column
		heuristicValue += getSumForGivenLine(TOP_RIGHT, 3);
		
		// Middle Column
		heuristicValue += getSumForGivenLine(TOP_LEFT + 1, 3);
		
		// Middle Row
		heuristicValue += getSumForGivenLine(TOP_LEFT + 3, 1);
		
		// Left Diagonal
		heuristicValue += getSumForGivenLine(TOP_LEFT, 4);
		
		// Right Diagonal
		heuristicValue += getSumForGivenLine(TOP_RIGHT, 2);
		
		/* Update the hash table of heuristic values so that we don't have to 
		 * perform this calculation again.
		 */
		thisPlayer.updateHeuristicValue(this, heuristicValue);
		
		return heuristicValue;
	}

	
	
	/**
	 * This method calculates the heuristic sum for the current block on the given line. 
	 * This is based on the formula given in Question 1 in the week 10 exercises. 
	 * @param startIndex 		The index from which the line starts (i.e. 1 = TOP LEFT)
	 * @param incrementValue	The value being used to increment the index
	 * @return					Will return the heuristic for the current line. If the block is already 
	 * in a winning position, a value of 200/-200 will be returned. 
	 */
	private int getSumForGivenLine(int startIndex, int incrementValue) 
	{
		int sum = 0; 
		
		for (int position = startIndex; position <= (startIndex + (incrementValue * 2)); position += incrementValue)
		{
			char currentPosition = getPosition(position);
			if (currentPosition == '-')
				continue;
			
			if (currentPosition == thisPlayer.getPlayersMark().getValue())
			{
				if (sum < 0) // There is a mark of the opposite player on this line.
					return 0;
				if (sum == 0) // Line is empty so far
					sum = 1;
				else if (sum == 1) // There is already 1 of the current players marks on the line
					sum = 20;
				else if (sum == 20) // This is a win for the current player
					return 200;
			}
			else // current position contains opposite player
			{
				if (sum > 0) // There is a mark of the opposite player on this line.
					return 0;
				if (sum == 0) // Line is empty so far
					sum = -1;
				else if (sum == -1) // There is already 1 of the opposing players marks on the line
					sum = -20;
				else if (sum == -20) // This is a win for the opposing player
					return -200;
			}
			
		}
		
		return sum;
	}
	
	/**
	 * Prints a representation of the current block to stdout
	 */
	public void printBlock()
	{
		String [] rowStrings = getBlockAsString();
		for (String rowString : rowStrings)
			System.out.println("|" + rowString);
	}
	
	/**
	 * Returns a representation of the current block as an array of strings
	 * @return	An array of strings representing the state of the current block.
	 */
	public String[] getBlockAsString()
	{
		String[] blockStrings = new String[3];
		for (int row = 0; row < 3; row++)
		{
			blockStrings[row] = "";
			for (int col = 1; col <= 3; col++)
			{
				int blockIndex = col + (row * 3);
				
				blockStrings[row] = blockStrings[row].concat(" " + getPosition(blockIndex) + " |");
			}
		}
		return blockStrings;
	}
	
	/**
	 * Returns a char representing the state of the given position
	 * @param position	An int representing the position for which we are 
	 * interested in
	 * @return			A char representing the current state of the position. 
	 */
	public char getPosition(int position)
	{
		return positionStates.charAt(position);
	}
	
	/**
	 * Returns the string representing the state of each cell in the block.
	 * @return A string representing the state of each cell in the block.
	 */
	public String getPositionStates()
	{
		return positionStates;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((positionStates == null) ? 0 : positionStates.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Block other = (Block) obj;
		if (positionStates == null) {
			if (other.positionStates != null)
				return false;
		} else if (!positionStates.equals(other.positionStates))
			return false;
		return true;
	}
	
}


