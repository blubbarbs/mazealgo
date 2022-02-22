package com.gmail.bluballsman.mazealgo.maze;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Stack;

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
	
	// Identical to normal maze generation except that because of the symmetry, the two symmetrical paths
	// must be forcibly reunited. The first time they meet, the path between them will open.
	@Override
	public void fillMaze(Point start) {
		HashSet<Point> allVisitedPoints = new HashSet<Point>();
		Stack<Point> path = new Stack<Point>();
		boolean hasConnected = false;
		
		path.push(start);
		allVisitedPoints.add(start);
		setGround(start, true);
		
		while(!path.isEmpty()) {
			Point currentPoint = path.peek();
			ArrayList<Point> availablePoints = getSurroundingPoints(currentPoint, 2, point -> !isGround(point));
			
			if(!availablePoints.isEmpty()) {
				int chosenIndex = random.nextInt(availablePoints.size());
				Point chosenPoint = availablePoints.get(chosenIndex);
				
				forEach(chosenPoint, currentPoint, point -> setGround(point, true));
				path.push(chosenPoint);
				allVisitedPoints.add(chosenPoint);
			}
			else {
				currentPoint = path.pop();
			}
		
			if(!hasConnected) {
				ArrayList<Point> intersectingPoints = getSurroundingPoints(currentPoint, 2, point -> isGround(point) && !allVisitedPoints.contains(point));
				
				if(!intersectingPoints.isEmpty()) {
					Point deletedPoint = intersectingPoints.get(0);
					
					forEach(currentPoint, deletedPoint, point -> setGround(point, true));
					hasConnected = true;
				}
			}
		}
	}
	
}
