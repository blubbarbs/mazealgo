package com.gmail.bluballsman.mazealgo.structure;

import java.awt.Point;
import java.util.Random;

public class Structure {
	public final int width;
	public final int height;
	public final char[][] blueprint;
	
	public Structure(String strucString, Random r) {
		String[] lines = strucString.split("[.]|\n");
		
		width = lines[0].length();
		height = lines.length;
		blueprint = new char[width][height];
		
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				char c = lines[y].charAt(x);
				
				if(c != '?') {
					blueprint[x][y] = c;
				}
				else {
					blueprint[x][y] = r.nextBoolean() ? '1' : '0';
				}
			}
		}
	}
		
	public Structure(String strucString) {
		this(strucString, new Random());
	}
	
	public Structure(char[][] blueprint) {
		this.blueprint = blueprint;
		width =  blueprint.length;
		height = blueprint[0].length;
	}
	
	public Point getCenter() {
		return new Point((width - 1) / 2, (height - 1) / 2);
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
				for(int x = 0; x < width; x++) {
					newBlueprint[x][y] = blueprint[x][y];
				}
			}
			
			break;
		}
				
		return new Structure(newBlueprint);
	}

	
	@Override
	public Structure clone() {
		return rotate(0);
	}
	
	@Override
	public String toString() {
		String s = "";
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				 s += blueprint[x][y];
			}
			
			s += "\n";
		}		
	
		return s;
	}
	
}
