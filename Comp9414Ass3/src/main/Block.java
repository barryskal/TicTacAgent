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

	

	
}


