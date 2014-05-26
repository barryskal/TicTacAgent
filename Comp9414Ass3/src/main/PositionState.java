package main;


public enum PositionState {

	X('x'),
	O('o'),
	E('-');

	private char mValue;
	
	private PositionState(char inValue) {
		mValue = inValue;
	}
	
	public char getValue()
	{
		return(mValue);
	}
	
	public static PositionState getPositionFromGivenValue(char inValue)
	{
		for (PositionState pos : PositionState.values())
			if (pos.getValue() == inValue)
				return(pos);

		throw new IllegalArgumentException("Invalid position type given " + inValue);
	}
	
	/*public static String getPositionAsString(PositionState )
	{
		String cellValue;
		PositionState cellState = positionStates.get(blockIndex);
		
		if (cellState == PositionState.E)
			cellValue = " ";
		else
			cellValue = cellState.toString();
	}*/
}
