package com.gmail.bluballsman.mazealgo.pathfinding;

import java.awt.Point;
import java.util.Comparator;

public class AStarComparator implements Comparator<Point> {
	private Point start;
	private Point finish;
	
	public AStarComparator(Point start, Point finish) {
		this.start = start;
		this.finish = finish;
	}
	
	public double calculateManhattan(Point p1, Point p2) {
		return Math.abs(p1.x - p2.x) + Math.abs(p1.y - p2.y);
	}
	
	@Override
	public int compare(Point p1, Point p2) {
		double toStartP1 = calculateManhattan(p1, start);
		double toFinishP1 = calculateManhattan(p1, finish);
		double combinedP1 = toStartP1 + toFinishP1;
		
		double toStartP2 = calculateManhattan(p2, start);
		double toFinishP2 = calculateManhattan(p2, start);
		double combinedP2 = toStartP1 + toFinishP2;

		if (combinedP1 > combinedP2) {
			return 1;
		} 
		else if (combinedP1 < combinedP2) {
			return -1;
		}
		else {
			return toStartP1 > toStartP2 ? 1 : -1;
		}		
	}

}
