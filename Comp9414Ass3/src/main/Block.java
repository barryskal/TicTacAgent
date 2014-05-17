package main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
 * This class represents one of the 9 tic-tac-toe boards as part of the game. 
 */
public class Block {
	
	private ArrayList<PositionState> positionStates;
	private final int NUM_POSITIONS = 9;
	public static int MIN_POSITION_NUMBER = 1;
	public static int MAX_POSITION_NUMBER = 9;
	
	/*
	 * Creates a new block where each position is set to empty.
	 */
	public Block() {
		positionStates = new ArrayList<PositionState>(NUM_POSITIONS + 1);
		for (int i = 1; i <= NUM_POSITIONS; i++) 
		{
			positionStates.add(PositionState.E);
		}
	}
	
	
	public void setPosition(int position, PositionState positionState)
	{
		/* Need to decrement the position by 1, as the 0th element of the
		 * ArrayList is the number 1 position 
		 */
		position -= 1;
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
		int i = 1;
		for (PositionState currentPosition : positionStates)
		{
			if (currentPosition == PositionState.E)
				listOfEmptyCells.add(i);
			i++;
		}
		
		return listOfEmptyCells;
	}

	

	
}


