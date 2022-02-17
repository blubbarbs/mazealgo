package com.gmail.bluballsman.mazealgo.maze;

import java.awt.Point;
import java.util.ArrayList;

import com.gmail.bluballsman.mazealgo.structure.StructureSlot;

public class SymmetricMaze extends Maze {

	public SymmetricMaze(int width, int height) {
		super(width, height);
	}
	
	public SymmetricMaze(int width, int height, long randomSeed) {
		super(width, height, randomSeed);
	}
	
	@Override
	public void setGround(int x, int y, boolean isGround) {
		Point mirror = getMirrorPoint(x, y);
		super.setGround(x, y, isGround);
		super.setGround(mirror.x, mirror.y, isGround);
	}
	
	@Override
	public void setStructureFlag(int x, int y, boolean structureFlag) {
		Point mirror = getMirrorPoint(x, y);
		super.setStructureFlag(x, y, structureFlag);
		super.setStructureFlag(mirror.x, mirror.y, structureFlag);
	}
	
	@Override
	public ArrayList<StructureSlot> findMatches(String strucPattern) {
		ArrayList<StructureSlot> matches = new ArrayList<StructureSlot>();
		Point centerPoint = getCenterPoint();
		StructureSlot s = new StructureSlot(this, strucPattern);
		
		for(int rotations = 0; rotations < 4; rotations++) {
			s.rotate(1);
			
			// Because the maze is symmetrical, we only need to look at half of it in order to check if the structure fits.
			// Since the maze is symmetrical until the center point, we need to do 2 loops: one that goes until right before
			// the center line and one that does the center line but only until halfway
			for(int y = 0; y < centerPoint.y; y++) {
				for(int x = 0; x < width - s.getWidth(); x++) {
					s.setLocation(x, y);
					
					if(s.canPlace()) {
						matches.add(s.clone());
						matches.add(s.getMirrorSlot());
					}
				}
			}
			// Doing the center line
			for(int x = 0; x <= centerPoint.x; x++) {
				s.setLocation(x, centerPoint.y);
				
				if(s.canPlace()) {
					matches.add(s.clone());
					matches.add(s.getMirrorSlot());
				}
			}
		}
		
		return matches;
	}
	
	@Override
	public StructureSlot placeStructure(String strucPattern) {
		StructureSlot s = super.placeStructure(strucPattern);
		
		if(s != null) {
			StructureSlot mirror = s.getMirrorSlot();
			
			mirror.markStructureTiles();
			structures.add(mirror);
		}
		return s;
	}
	
	@Override
	public StructureSlot excavateRoom(String roomPattern) {
		StructureSlot s = super.excavateRoom(roomPattern);
		
		if(s != null) {
			StructureSlot mirror = s.getMirrorSlot();
			
			mirror.drawStructureTiles();
			mirror.markStructureTiles();
			structures.add(mirror);
		}
		return s;
	}
	
	@Override
	public void knockDownWalls(float openWallPercentage) {
		Point centerPoint = getCenterPoint();
		ArrayList<Point> availableWalls = new ArrayList<Point>();
		
		// Repeating the process from findMatches() but this time to knock down walls
		for(int y = 1; y < centerPoint.y; y++) {
			for(int x = 1 + y % 2; x < width - 1; x+=2) {
				if(!isGround(x, y) && !isStructure(x, y)) {
					availableWalls.add(new Point(x, y));
				}
			}
		}
		
		// Center line
		for(int x = 1 + centerPoint.y % 2; x <= centerPoint.x; x+=2) {
			if(!isGround(x, centerPoint.y) && !isStructure(x, centerPoint.y)) {
				availableWalls.add(new Point(x, centerPoint.y));
			}
		}
		
		int wallsToDestroy = Math.round(availableWalls.size() * openWallPercentage);
		for(int i = 0; i < wallsToDestroy; i++) {
			Point randomWall = availableWalls.get(random.nextInt(availableWalls.size()));
			
			availableWalls.remove(randomWall);
			setGround(randomWall, true);
		}
	}
	
	public void testRooms() {
		excavateRoom("0001000.0111110.0111110.0111110.0000000");
		excavateRoom("00000.01110.01110.01110.01000");
		excavateRoom("0000010.0111110.0111110.0111110.0100000");
		excavateRoom("00000.01110.01110.01110.01110");
	}
	
	public void testStructures() {
		placeStructure("000.010");
		placeStructure("000.010");
		placeStructure("000.010");
		placeStructure("000.010");
		placeStructure("000.010");
		placeStructure("000.010");
		placeStructure("000.010");
		placeStructure("000.010");
		placeStructure("000.010");
		placeStructure("000.010");
		placeStructure("111.101.111");
		placeStructure("111.101.111");
		placeStructure("111.101.111");
		placeStructure("00.10.10.10.10.10");
		placeStructure("11111.10101.11111");
		placeStructure("X0X.101.101.101.101.101.X0X");
	}

	
}
