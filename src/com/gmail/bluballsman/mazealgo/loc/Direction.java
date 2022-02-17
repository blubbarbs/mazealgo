package com.gmail.bluballsman.mazealgo.loc;

public enum Direction {
	NORTH(0, 1),
	EAST(1, 0),
	SOUTH(0, -1),
	WEST(-1, 0);
	
	public final int X_OFFSET;
	public final int Y_OFFSET;
	
	Direction(int xOffset, int yOffset) {
		X_OFFSET = xOffset;
		Y_OFFSET = yOffset;
	}
		
}
