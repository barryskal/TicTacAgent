package test;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import main.Block;
import main.PositionState;

import org.junit.Test;

public class BlockTest {

	
	@Test
	public void testEmptyCellsList() {
		Block testBlock = new Block();
		
		testBlock.setPosition(4, PositionState.O);
		testBlock.setPosition(8, PositionState.X);
		testBlock.setPosition(1, PositionState.O);
		
		List<Integer> listShouldBe = Arrays.asList(2,3,5,6,7,9);
		assertEquals(listShouldBe, testBlock.getListOfEmptyCells());
	}
			
}
