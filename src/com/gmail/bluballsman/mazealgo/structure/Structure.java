package com.gmail.bluballsman.mazealgo.structure;

public class Structure {
	public final int width;
	public final int height;
	public final int rotations;
	public final char[][] blueprint;
	
	
	public Structure(String strucString) {
		String[] lines = strucString.split("[.]");
		
		width = lines[0].length();
		height = lines.length;
		blueprint = new char[width][height];
		rotations = 0;
		
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				 blueprint[x][y] = lines[y].charAt(x);
			}
		}
	}
	
	public Structure(char[][] blueprint, int rotations) {
		this.blueprint = blueprint;
		this.rotations = rotations;
		width =  blueprint[0].length;
		height = blueprint.length;
	}
	
	public Structure rotate(int rotations) {
		char[][] newBlueprint;
		rotations = rotations < 0 ? 4 + (rotations % 4) : rotations % 4; 
		
		switch(rotations) {
		case 1:
			newBlueprint = new char[height][width];
			
			for(int y = 0; y < height; y++) {
				for(int x = 0; x < width; x++) {
					newBlueprint[y][x] = blueprint[x][height - y - 1];
				}
			}
			
			break;
		case 2:
			newBlueprint = new char[width][height];
			
			for(int y = 0; y < height; y++) {
				for(int x = 0; x < width; x++) {
					newBlueprint[x][y] = blueprint[width - x - 1][height - y - 1];
				}
			}
			
			break;
		case 3:
			newBlueprint = new char[height][width];
			
			for(int y = 0; y < height; y++) {
				for(int x = 0; x < width; x++) {
					newBlueprint[y][x] = blueprint[width - x - 1][y];
				}
			}
			
			break;
		default:
			newBlueprint = new char[width][height];
			
			for(int y = 0; y < height; y++) {
				for(int x = 0; x < height; x++) {
					newBlueprint[x][y] = blueprint[x][y];
				}
			}
			
			break;
		}
				
		return new Structure(newBlueprint, (this.rotations + rotations) % 4);
	}

	
	@Override
	public Structure clone() {
		return rotate(0);
	}
	
}
