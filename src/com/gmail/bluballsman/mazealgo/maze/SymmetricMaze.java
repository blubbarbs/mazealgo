package com.gmail.bluballsman.mazealgo.maze;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;

public class SymmetricMaze extends Maze {

	public SymmetricMaze(int width, int height) {
		super(width, height);
	}
	
	public SymmetricMaze(int width, int height, long randomSeed) {
		super(width, height, randomSeed);
	}
	
	@Override
	public ArrayList<Point> getDeletableWalls() {
		Point center = getCenterPoint();
		ArrayList<Point> deletableWalls = new ArrayList<Point>();
		
		for(int y = 1; y < center.y; y++) {
			for(int x = 1 + (y % 2); x < width - 1; x+=2) {
				if(!isGround(x, y) && !isStructure(x, y)) {
					deletableWalls.add(new Point(x, y));
				}
			}
		}		
		
		for(int x = 1 + (center.y % 2); x < center.x; x+=2) {
			if(!isGround(x, center.y) && !isStructure(x, center.y)) {
				deletableWalls.add(new Point(x, center.y));
			}
		}
		
		return deletableWalls;
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
	public void fillMaze(Point start) {
		super.fillMaze(start);
		
		if(findPath(new Point(1, 1), getMirrorPoint(1, 1)) == null) {
			connectSymmetricalHalves();
		}
		
	}
	
	public void connectSymmetricalHalves() {
		Point center = getCenterPoint();
		boolean isGuaranteedWall = center.x % 2 == 0 && center.y % 2 == 0;
		boolean isGroundOrWall = center.x % 2 != center.y % 2;
		
		System.out.println("Center: " + center.toString());
		
		if(isGuaranteedWall) {
			ArrayList<Point> deletableWalls = getDeletableWalls();
			
			Collections.shuffle(deletableWalls);
			
			for(Point wall : deletableWalls) {
				ArrayList<Point> neighbors = getSurroundingPoints(wall, (p) -> isGround(p));
				Stack<Point> shortestPath = findPath(neighbors.get(0), neighbors.get(1));
				
				if(shortestPath == null) {
					setGround(wall, true);
					System.out.println("Deleted " + wall.toString());
					return;
				}
			}
		}
		else if(isGroundOrWall) {
			setGround(center, true);
		}	
	}
	
}
