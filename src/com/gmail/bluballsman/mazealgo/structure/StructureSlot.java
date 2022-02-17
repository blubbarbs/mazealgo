package com.gmail.bluballsman.mazealgo.structure;

import java.awt.Point;

import com.gmail.bluballsman.mazealgo.maze.Maze;
import com.gmail.bluballsman.mazealgo.maze.Tile;

public class StructureSlot {
	private Maze maze;
	private BlueprintCharacter[][] blueprint;
	private int x = 0;
	private int y = 0;
	private int rotations = 0;
	private int width = 0;
	private int height = 0;
	
	public StructureSlot(Maze maze, String strucString) {
		this.maze = maze;
		String[] blueprintString = strucString.split("[.]");
		width = blueprintString[0].length();
		height = blueprintString.length;
		blueprint = new BlueprintCharacter[width][height];
		
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				char c = blueprintString[y].charAt(x);
				blueprint[x][y] = BlueprintCharacter.getByCharacter(c);
			}
		}
	}
	
	public StructureSlot(Maze maze, String strucString, int rotations) {
		this(maze, strucString);
		rotate(rotations);
	}
	
	public StructureSlot(StructureSlot s) {
		maze = s.maze;
		rotations = s.getRotations();
		blueprint = new BlueprintCharacter[s.width][s.height];
		width = s.width;
		height = s.height;
		x = s.x;
		y = s.y;
		
		for(int y = 0; y < s.height; y++) {
			for(int x = 0; x < s.width; x++) {
				blueprint[x][y] = s.getBlueprint()[x][y];
			}
		}
	}
	
	public String getBlueprintString() {
		String s = "\n";
		
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				s += blueprint[x][y].getCharacter();
			}
			s += ".";
		}
		
		return s;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getRotations() {
		return rotations;
	}
	
	public Point getLocation() {
		return new Point(x, y);
	}
	
	public StructureSlot getMirrorSlot() {
		StructureSlot mirrorSlot = new StructureSlot(this);
		Point oppositeEnd = new Point(x + width - 1, y + height - 1);
		Point mirrorPoint = maze.getMirrorPoint(oppositeEnd);
		mirrorSlot.setLocation(mirrorPoint);
		mirrorSlot.rotate(2);
		
		return mirrorSlot;
	}
	
	public BlueprintCharacter[][] getBlueprint() {
		return blueprint;
	}
	
	public boolean doesSlotMatchBlueprint() {
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				Tile t = maze.getTile(x + this.x, y + this.y);
				
				if(!blueprint[x][y].matchesTile(t)) {
					return false;
				}
			}
		}
		return true;
	}
	
	public boolean doesSlotHaveStructure() {
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				Tile t = maze.getTile(x + this.x, y + this.y);
				
				if(t.isStructure()) {
					return true;
				}
			}
		}
		return false;
	}
 	
	public boolean canPlace() {
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				Tile t = maze.getTile(x + this.x, y + this.y);
				
				if(!blueprint[x][y].matchesTile(t) || t.isStructure()) {
					return false;
				}
			}
		}
		return true;
	}
	
	public void setLocation(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public void setLocation(Point p) {
		setLocation(p.x, p.y);
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	public void rotate(int addedRotations) {
		BlueprintCharacter[][] newBlueprint;
		addedRotations = addedRotations < 0 ? 4 + (addedRotations % 4) : addedRotations % 4; 
		
		switch(addedRotations) {
		case 1:
			newBlueprint = new BlueprintCharacter[height][width];
			for(int y = 0; y < height; y++) {
				for(int x = 0; x < width; x++) {
					newBlueprint[y][x] = blueprint[x][height - y - 1];
				}
			}
			break;
		case 2:
			newBlueprint = new BlueprintCharacter[width][height];
			for(int y = 0; y < height; y++) {
				for(int x = 0; x < width; x++) {
					newBlueprint[x][y] = blueprint[width - x - 1][height - y - 1];
				}
			}
			break;
		case 3:
			newBlueprint = new BlueprintCharacter[height][width];
			for(int y = 0; y < height; y++) {
				for(int x = 0; x < width; x++) {
					newBlueprint[y][x] = blueprint[width - x - 1][y];
				}
			}
			break;
		default:
			return;
		}
		
		rotations = (rotations + addedRotations) % 4;
		blueprint = newBlueprint;
		width = blueprint.length;
		height = blueprint[0].length;
	}
	
	public void markStructureTiles() {
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				Tile t = maze.getTile(x + getX(), y + getY());
				
				t.setStructureFlag(true);
			}
		}
	}
	
	public void drawStructureTiles() {
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				BlueprintCharacter bc = blueprint[x][y];
				Point p = new Point(x + this.x, y + this.y);
				Tile t = maze.getTile(p);
				
				if(bc == BlueprintCharacter.WALL) {
					t.setGround(false);
				}
				else if(bc == BlueprintCharacter.GROUND) {
					t.setGround(true);
				}
			}
		}
	}
	
	@Override
	public String toString() {
		return getBlueprintString().replace('.', '\n');
	}
	
	@Override
	public int hashCode() {
		return maze.hashCode() + blueprint.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if(o == this) {
			return true;
		}
		if(o == null || o.getClass() != getClass()) {
			return false;
		}
		
		StructureSlot otherSlot = (StructureSlot) o;
		
		return otherSlot.maze == maze && otherSlot.blueprint.equals(blueprint);
	}
	
	@Override
	public StructureSlot clone() {
		return new StructureSlot(this);
	}
	
}
