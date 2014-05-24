package test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import main.Block;
import main.PositionState;

import org.junit.Test;

public class BlockTest {

	
	@Test
	public void testEmptyCellsList() {
		Block testBlock = new Block(PositionState.X);
		
		testBlock.setPosition(4, PositionState.O);
		testBlock.setPosition(8, PositionState.X);
		testBlock.setPosition(1, PositionState.O);
		
		List<Integer> listShouldBe = Arrays.asList(2,3,5,6,7,9);
		assertEquals(listShouldBe, testBlock.getListOfEmptyCells());
	}
	
	
	
	@Test
	public void testHeuristicInLongGameEndingInOpponentWin() 
	{
		PositionState currentPlayer = PositionState.X;
		Block testBlock = new Block(currentPlayer);
		
		PositionState opponent = PositionState.O;
		
		testBlock.setPosition(1, currentPlayer);
		
		assertEquals("Heuristic value with one move in the corner", 3, testBlock.heuristicValue());
		
		testBlock.setPosition(2, opponent);
		
		assertEquals("Heuristic value with two moves", 1, testBlock.heuristicValue());
		
		testBlock.setPosition(4, currentPlayer);
		
		assertEquals("Heuristic value with third move middle-left", 21, testBlock.heuristicValue());
		
		testBlock.setPosition(5, opponent);
		
		assertEquals("Heuristic value after 4th move", -1, testBlock.heuristicValue());
		
		testBlock.setPosition(6, currentPlayer);
		
		assertEquals("Heuristic value after 5th move", 0, testBlock.heuristicValue());
		
		testBlock.setPosition(3, opponent);
		
		assertEquals("Heuristic value after 6th move", -20, testBlock.heuristicValue());
		
		testBlock.setPosition(9, currentPlayer);
		
		assertEquals("Heuristic value after 7th move", -19, testBlock.heuristicValue());
		
		testBlock.setPosition(7, opponent);
		
		testBlock.printBlock();
		assertEquals("Heuristic value after 8th move", -220, testBlock.heuristicValue());
		
	}
	
	@Test
	public void testHeuristicInShortGameWithPlayerWin() 
	{
		PositionState currentPlayer = PositionState.X;
		Block testBlock = new Block(currentPlayer);
		
		PositionState opponent = PositionState.O;
		
		testBlock.setPosition(1, currentPlayer);
		testBlock.setPosition(8, opponent);
		
		assertEquals("Heuristic value after 2nd move", 1, testBlock.heuristicValue());
		
		testBlock.setPosition(5, currentPlayer);
		
		assertEquals("Heuristic value after 3rd move", 6, testBlock.heuristicValue());
		
		testBlock.setPosition(7, opponent);
		
		assertEquals("Heuristic value after 4th move", 2, testBlock.heuristicValue());
		
		testBlock.setPosition(9, currentPlayer);
		//testBlock.printBlock();
		
		assertEquals("winning move", 103, testBlock.heuristicValue());
		
	}
	
	@Test
	public void testHeuristicOpponentWinInAVertical()
	{
		PositionState currentPlayer = PositionState.X;
		Block testBlock = new Block(currentPlayer);
		
		PositionState opponent = PositionState.O;
		
		testBlock.setPosition(3, currentPlayer);
		testBlock.setPosition(1, opponent);
		testBlock.setPosition(8, currentPlayer);
		testBlock.setPosition(7, opponent);
		testBlock.setPosition(6, currentPlayer);
		testBlock.setPosition(4, opponent);
		//testBlock.printBlock();
		
		assertEquals("opponent wins", -97, testBlock.heuristicValue());
	}

	
	@Test
	public void testSortedListOfAvailableMovesl()
	{
		PositionState currentPlayer = PositionState.X;
		Block testBlock = new Block(currentPlayer);
		
		PositionState opponent = PositionState.O;
		
		testBlock.setPosition(3, currentPlayer);
		testBlock.setPosition(1, opponent);
		testBlock.setPosition(8, currentPlayer);
		testBlock.setPosition(7, opponent);
		
		//testBlock.printBlock();
		
		ArrayList<Integer> predictedList = new ArrayList<Integer>();
		predictedList.add(4); // 2
		predictedList.add(5); // 2
		predictedList.add(6); // 1
		predictedList.add(9); // 1
		predictedList.add(2); // 0 
		
	
		
		assertEquals("Check ordering", predictedList, testBlock.getListOfBestMovesForThisCell(currentPlayer));
	}
			
}
