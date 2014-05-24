package main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

/*
 * This class represents one of the 9 tic-tac-toe boards as part of the game. 
 */
public class Block {
	
	private ArrayList<PositionState> positionStates;
	private PositionState currentPlayer;
	private final int NUM_POSITIONS = 9;
	private final static int TOP_LEFT = 1;
	private final static int TOP_RIGHT = 3;
	
	
	/*
	 * Creates a new block where each position is set to empty.
	 */
	public Block(PositionState inCurrentPlayer) {
		currentPlayer = inCurrentPlayer;
		positionStates = new ArrayList<PositionState>(NUM_POSITIONS + 1);
		/* Note that the value at index = 0 is set to E; however it should never be used.
		 * This makes it simpler to reference the positions by array incdex. 
		 */
		positionStates.add(PositionState.E);
		
		for (int i = 1; i <= NUM_POSITIONS; i++) 
		{
			positionStates.add(PositionState.E);
		}
	}
	
	/**
	 * Creates a new Block as a copy of the given block
	 * @param copiedBlock The Block to be copied
	 */
	public Block(Block copiedBlock)
	{
		positionStates = new ArrayList<PositionState>();
		currentPlayer = copiedBlock.getCurrentPlayer();
		for (int cell = 0; cell <= NUM_POSITIONS; cell++)
		{
			positionStates.add(copiedBlock.getPosition(cell));
		}
	}
	
	
	/**
	 * This method returns the representation of this automated player in the Game 
	 * @return A PositionState representing this player.
	 */
	public PositionState getCurrentPlayer()
	{
		return currentPlayer;
	}
	
	/**
	 * Sets the position in this block to the state provided, i.e. not empty.
	 * @param position 		The position that needs to be changed
	 * @param positionState	The state to which it is being changed (i.e. X or Y)
	 */
	public void setPosition(int position, PositionState positionState)
	{
		positionStates.set(position, positionState);
	}
	
	/**
	 * Checks whether the move applied is allowable
	 * @param 	position 	The value of the position, a number from 1 to 9
	 * @return 				True if the cell is empty, false otherwise.
	 */
	public boolean isValidMove(int position)
	{
		return positionStates.get(position) == PositionState.E;
	}
	
	/**
	 * This function generates a list of the empty positions in this block.
	 * @return Returns a list of cell positions (i.e. values 1-9) that are currently empty. 
	 */
	public List<Integer> getListOfEmptyCells()
	{
		ArrayList<Integer> listOfEmptyCells = new ArrayList<Integer>();
		int i = 0;
		for (PositionState currentPosition : positionStates)
		{
			if (currentPosition == PositionState.E)
				listOfEmptyCells.add(i);
			i++;
		}
		
		/* Remove the 0th element, as it is just the base element, which isn't real. */
		listOfEmptyCells.remove(0);
		
		return listOfEmptyCells;
	}

	
	public List<Integer> getListOfBestMovesForThisCell(PositionState whoHasNextMove)
	{
		List<Integer> emptyCells = getListOfEmptyCells();
		ArrayList<Integer> sortedListOfMoves = new ArrayList<Integer>();
		Hashtable<Integer, Integer> heuristicValuesForEmptyOptions = new Hashtable<Integer, Integer>();
		
		for (int emptyCell : emptyCells)
		{
			Block tempBlock = new Block(this);
			tempBlock.setPosition(emptyCell, whoHasNextMove);
			int heuristicValue = tempBlock.heuristicValue();
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
	 * @param currentPlayer	The representation of the current player, this determines whether 
	 * the heuristic value is positive or negative 
	 * @return 	The value obtained by performing the heuristic function. Note that any value 
	 * greater than 100 indicates a terminal position.
	 */
	public int heuristicValue()
	{
		int heuristicValue = 0;
		
		// Analyse top row
		heuristicValue += getSumForGivenLine(TOP_LEFT, 1);
		
		// Bottom Row
		heuristicValue += getSumForGivenLine(TOP_LEFT + 6, 1);
		
		// Left Column
		heuristicValue += getSumForGivenLine(TOP_LEFT, 3);
		
		// Right Column
		heuristicValue += getSumForGivenLine(TOP_RIGHT, 3);
		//System.out.println("Heuristic Value: " + String.valueOf(heuristicValue) + ", Sum right row: " + String.valueOf(getSumForGivenLine(TOP_RIGHT, 3, currentPlayer)));
		
		// Middle Column
		heuristicValue += getSumForGivenLine(TOP_LEFT + 1, 3);
		
		// Middle Row
		heuristicValue += getSumForGivenLine(TOP_LEFT + 3, 1);
		
		// Left Diagonal
		heuristicValue += getSumForGivenLine(TOP_LEFT, 4);
		
		// Right Diagonal
		heuristicValue += getSumForGivenLine(TOP_RIGHT, 2);
		
		return heuristicValue;
	}
	
	/**
	 * This method calculates the heuristic sum for the current block on the given line. 
	 * This is based on the formula given in Question 1 in the week 10 exercises. 
	 * @param startIndex 		The index from which the line starts (i.e. 1 = TOP LEFT)
	 * @param incrementValue	The value being used to increment the index
	 * @param currentPlayer 	The current player (this will determine whether which PositionState 
	 * will generate a positive heuristic value.
	 * @return					Will return the heuristic for the current line. If the block is already 
	 * in a winning position, a value of 100 will be returned. 
	 */
	private int getSumForGivenLine(int startIndex, int incrementValue) 
	{
		int sum = 0; 
		
		for (int position = startIndex; position <= (startIndex + (incrementValue * 2)); position += incrementValue)
		{
			PositionState currentPosition = positionStates.get(position);
			if (currentPosition == PositionState.E)
				continue;
			
			if (currentPosition == currentPlayer)
			{
				if (sum < 0) // There is a mark of the opposite player on this line.
					return 0;
				if (sum == 0) // Line is empty so far
					sum = 1;
				else if (sum == 1) // There is already 1 of the current players marks on the line
					sum = 3;
				else if (sum == 3) // This is a win for the current player
					return 100;
			}
			else // current position contains opposite player
			{
				if (sum > 0) // There is a mark of the opposite player on this line.
					return 0;
				if (sum == 0) // Line is empty so far
					sum = -1;
				else if (sum == -1) // There is already 1 of the opposing players marks on the line
					sum = -3;
				else if (sum == -3) // This is a win for the opposing player
					return -100;
			}
			
		}
		
		return sum;
	}
	
	public void printBlock()
	{
		String [] rowStrings = getBlockAsString();
		for (String rowString : rowStrings)
			System.out.println("|" + rowString);
	}
	
	
	public String[] getBlockAsString()
	{
		String[] blockStrings = new String[3];
		for (int row = 0; row < 3; row++)
		{
			blockStrings[row] = "";
			for (int col = 1; col <= 3; col++)
			{
				int blockIndex = col + (row * 3);
				
				
				
				blockStrings[row] = blockStrings[row].concat(" " + positionStates.get(blockIndex).getValue() + " |");
			}
		}
		return blockStrings;
	}
	
	public PositionState getPosition(int position)
	{
		return positionStates.get(position);
	}
	
	

	
}


