package main;


public enum PositionState {

	X("X"),
	O("O"),
	E("-");

	private String mValue;
	
	private PositionState(String inValue) {
		mValue = inValue;
	}
	
	public String getValue()
	{
		return(mValue);
	}
	
	public static PositionState getPositionFromGivenValue(String inValue)
	{
		for (PositionState pos : PositionState.values())
			if (pos.getValue().equalsIgnoreCase(inValue))
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
