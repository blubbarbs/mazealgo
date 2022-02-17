package com.gmail.bluballsman.mazealgo.structure;

import com.gmail.bluballsman.mazealgo.maze.Tile;

public enum BlueprintCharacter {
	GROUND('1'),
	WALL('0'),
	IGNORE('X');
	
	public static BlueprintCharacter getByCharacter(char c) {
		if(c == '1') {
			return GROUND;
		}
		else if(c == '0') {
			return WALL;
		}
		else if(c == 'X' || c == 'x') {
			return IGNORE;
		}
		else {
			return null;
		}
	}
	
	private char character;
	
	BlueprintCharacter(char character) {
		this.character = character;
	}
	
	public char getCharacter() {
		return character;
	}
	
	public boolean matchesTile(Tile t) {
		if(t == null) {
			return false;
		}
		switch(this) {
		case GROUND:
			return t.isGround();
		case WALL:
			return !t.isGround();
		default:
			return true;
		}
	}
	
	
}
