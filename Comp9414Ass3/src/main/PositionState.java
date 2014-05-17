package main;


public enum PositionState {

	X("x"),
	O("o"),
	E("e");

	private String mValue;
	
	private PositionState(String inValue) {
		mValue = inValue;
	}
	
	private String getValue()
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
}
