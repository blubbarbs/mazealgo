package com.gmail.bluballsman.mazealgo.maze;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.function.Predicate;

import com.gmail.bluballsman.mazealgo.pathfinding.AStarComparator;
import com.gmail.bluballsman.mazealgo.structure.Structure;

public class Maze {
	protected int width;
	protected int height;
	protected Tile[][] tiles;
	protected HashMap<Point, Structure> structuresNew = new HashMap<Point, Structure>();
	protected Random random = new Random();
	
	public Maze(int width, int height) {
	   this.width = width;
	   this.height = height;
		tiles = new Tile[width][height];
		
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				tiles[x][y] = new Tile();
			}
		}
	}
	
	public Maze(int width, int height, long randomSeed) {
		this(width, height);
		random.setSeed(randomSeed);
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public Point getCenterPoint() {
		return new Point((width - 1) / 2, (height - 1) / 2);
	}
	
	public Point getMirrorPoint(int x, int y) {
		return new Point(width - x - 1, height - y - 1);
	}
	
	public Point getMirrorPoint(Point p) {
		return getMirrorPoint(p.x, p.y);
	}
	
	public boolean isGround(int x, int y) {
		Tile t = getTile(x, y);
		
		return t == null || t.isGround();
	}
	
	public boolean isGround(Point p) {
		return isGround(p.x, p.y);
	}
	
	public boolean isValidPoint(int x, int y) {
		return x >= 0 && x < width && y >= 0 && y < height;
	}
	
	public boolean isEdge(int x, int y) {
		return x == 0 || x == width - 1 || y == 0 || y == height - 1;
	}
	
	public boolean isEdge(Point p) {
		return isEdge(p.x, p.y);
	}
	
	public boolean isValidPoint(Point p) {
		return isValidPoint(p.x, p.y);
	}
	
	public boolean isStructure(int x, int y) {
		Tile t = getTile(x, y);
		
		return t != null && t.isStructure();
	}
	
	// Returns the "type code" for this point. The type code is a representation of the surrounding tiles on the north, east, south, and west sides
	// of this tile. If a surrounding tile match this tile's ground flag, it is represented as a "1", otherwise as "0". 
	// These bits are represented in the type code from left to right as north, east, south, west. So a type code of 1010 means that the ground flag
	// of the surrounding tiles matched this tile in the north and south positions. Out of bounds tiles are counted as ground.	
	public int getTypeCode(Point p) {
		Point north = new Point(p.x, p.y + 1);
		Point east = new Point(p.x + 1, p.y);
		Point south = new Point(p.x, p.y - 1);
		Point west = new Point(p.x - 1, p.y);
		
		boolean tileIsGround = isValidPoint(p) && isGround(p);
		boolean northIsGround = isValidPoint(north) && isGround(north);
		boolean eastIsGround = isValidPoint(east) && isGround(east);
		boolean southIsGround = isValidPoint(south) && isGround(south);
		boolean westIsGround = isValidPoint(west) && isGround(west);
		
		int typeCode = 0;
		typeCode += northIsGround == tileIsGround ? 0b1000 : 0;
		typeCode += eastIsGround == tileIsGround ? 0b0100 : 0;
		typeCode += southIsGround == tileIsGround ? 0b0010 : 0;
		typeCode += westIsGround == tileIsGround ? 0b0001 : 0;
		
		return typeCode;		
	}
	
	public Tile.Type getTileType(Point p) {
		return Tile.Type.getType(getTypeCode(p));
	}
	
	public int getTileRotations(Point p) {
		return Tile.Type.getRotations(getTypeCode(p));
	}
		
	public boolean isStructure(Point p) {
		return isStructure(p.x, p.y);
	}
	
	public Tile getTile(int x, int y) {
		return isValidPoint(x, y) ? tiles[x][y] : null;
	}
	
	public Tile getTile(Point p) {
		return getTile(p.x, p.y);
	}
	
	public ArrayList<Point> getSurroundingPoints(Point p, int offset, Predicate<Point> predicate) {
		predicate = predicate != null ? predicate : (dummy) -> true;
		ArrayList<Point> points = new ArrayList<Point>();
		Point north = new Point(p.x, p.y + offset);
		Point east = new Point(p.x + offset, p.y);
		Point south = new Point(p.x, p.y - offset);
		Point west = new Point(p.x - offset, p.y);	
		
		if (isValidPoint(north) && predicate.test(north)) {
			points.add(north);
		}
		
		if (isValidPoint(east) && predicate.test(east)) {
			points.add(east);
		}

		if (isValidPoint(south) && predicate.test(south)) {
			points.add(south);
		}

		if (isValidPoint(west) && predicate.test(west)) {
			points.add(west);
		}

		return points;
	}
	
	public ArrayList<Point> getSurroundingPoints(Point p, Predicate<Point> predicate) {
		return getSurroundingPoints(p, 1, predicate);
	}
	
	public ArrayList<Point> getSurroundingPoints(Point p) {
		return getSurroundingPoints(p, 1, null);
	}
	
	public boolean canPlaceStructure(Point p, Structure s) {
		for (int y = 0; y < s.height; y++) {
			for (int x = 0; x < s.width; x++) {
				Point rel = new Point(p.x + x, p.y + y);
				char symbol = s.blueprint[x][y];
				boolean overwrites = (symbol == '1' && !isGround(rel)) || (symbol == '0' && isGround(rel));				
				
				if (isStructure(rel) || (isEdge(rel) && overwrites)) {
					return false;
				}
			}
		}
		
		return true;
	}
	
	public ArrayList<StructureSlot> findValidStructureSlots(Structure s) {
		ArrayList<StructureSlot> matches = new ArrayList<StructureSlot>();
		
		for (int rotations = 0; rotations < 4; rotations++) {
			for (int y = 0; y <= height - s.height; y+=2) {
				for (int x = 0; x <= width - s.width; x+=2) {
					Point p = new Point(x, y);
					
					if (canPlaceStructure(p, s)) {
						matches.add(new StructureSlot(p, s));
					}
				}
			}			
		
			s = s.rotate(1);
		}
		
		return matches;
	}
	
	public void placeStructure(Point p, Structure s) {
		for (int y = 0; y < s.height; y++) {
			for (int x = 0; x < s.width; x++) {
				Point rel = new Point(p.x + x, p.y + y);
				char symbol = s.blueprint[x][y];
				
				switch(symbol) {
				case '0':
					setGround(rel, false);
					setStructureFlag(rel, true);
					break;
				case '1':
					setGround(rel, true);
					setStructureFlag(rel, true);
					break;
				case '?':
					setGround(rel, random.nextBoolean());
					setStructureFlag(rel, true);
					break;
				default:
					break;
				}
			}
		}
	}
	
	public void placeStructure(Structure s) {
		ArrayList<StructureSlot> slots = findValidStructureSlots(s);
		
		if (slots.isEmpty()) {
			return;
		}
		
		int randomIndex = random.nextInt(slots.size());
		StructureSlot randomEntry = slots.get(randomIndex);
		Structure selectedStructure = randomEntry.structure;
		Point selectedPoint = randomEntry.point;
		
		placeStructure(selectedPoint, selectedStructure);
	}
		
	public void setGround(int x, int y, boolean isGround) {
		tiles[x][y].isGround = isGround;
	}
	
	public void setGround(Point p, boolean isGround) {
		setGround(p.x, p.y, isGround);
	}
	
	public void setStructureFlag(int x, int y, boolean isStructure) {
		tiles[x][y].structureFlag = isStructure;
	}
	
	public void setStructureFlag(Point p, boolean isStructure) {
		setStructureFlag(p.x, p.y, isStructure);
	}
	
	public void fillMaze(Point start) {
		Stack<Point> path = new Stack<Point>();
		
		path.push(start);
		setGround(start, true);
		
		while(!path.isEmpty()) {
			Point currentPoint = path.peek();
			ArrayList<Point> availablePoints = getSurroundingPoints(currentPoint, 2, point -> !isGround(point));
			
			if (!availablePoints.isEmpty()) {
				int chosenIndex = random.nextInt(availablePoints.size());
				Point chosenPoint = availablePoints.get(chosenIndex);
				
				forEach(chosenPoint, currentPoint, point -> setGround(point, true));
				path.push(chosenPoint);
			}
			else {
				currentPoint = path.pop();
			}
		}
	}
	
	public Stack<Point> findPath(Point start, Point finish) {
		// Tiles available to explore, order determined by A* heuristic
		PriorityQueue<Point> openNodes = new PriorityQueue<Point>(new AStarComparator(start, finish));
		// Previous point before current point is discovered, used to reconstruct the final path. Keys are nodes that have been discovered
		HashMap<Point, Point> nodeParents = new HashMap<Point, Point>();
		openNodes.add(start);
		nodeParents.put(start, null);
		
		while (!openNodes.isEmpty()) {
			Point current = openNodes.poll();
			
			if (current.equals(finish)) {
				break;
			}

			ArrayList<Point> validNeighbors = getSurroundingPoints(current, 1, (p) -> isGround(p) && !nodeParents.containsKey(p));
			
			for (Point p : validNeighbors) {
				openNodes.add(p);
				nodeParents.put(p, current);
			}
		}
		
		// Reconstructing path after A* search is done
		if (nodeParents.containsKey(finish)) {
			Stack<Point> path = new Stack<Point>();
			
			Point current = finish;
			while (current != start) {
				path.add(current);
				current = nodeParents.get(current);
			}

			path.add(start);
			return path;
		}
		else {
			return null;
		}
	}
	
	
	public ArrayList<Point> getDeletableWalls() {
		ArrayList<Point> deletableWalls = new ArrayList<Point>();
		
		for (int y = 1; y < height - 1; y++) {
			for (int x = 1 + (y % 2); x < width - 1; x+=2) {
				if (!isGround(x, y) && !isStructure(x, y)) {
					deletableWalls.add(new Point(x, y));
				}
			}
		}		
		
		return deletableWalls;
	}
	
	public ArrayList<Point> knockDownWalls(int cutoffLength) {
		ArrayList<Point> deletableWalls = getDeletableWalls();
		ArrayList<Point> deletedWalls = new ArrayList<Point>();
		
		Collections.shuffle(deletableWalls);
		
		for (Point wall : deletableWalls) {
			ArrayList<Point> neighbors = getSurroundingPoints(wall, (p) -> isGround(p));
			Stack<Point> shortestPath = findPath(neighbors.get(0), neighbors.get(1));
			
			if (shortestPath.size() >= cutoffLength) {
				setGround(wall, true);
				deletedWalls.add(wall);
			}
		}
		
		return deletedWalls;
	}	
	
	// Performs an action for each point within a point range [start, end]. Both inclusive.
	public void forEach(Point start, Point end, Consumer<Point> action) {
		int minX = Math.min(start.x, end.x);
		int minY = Math.min(start.y, end.y);
		int maxX = Math.max(start.x, end.x);
		int maxY = Math.max(start.y, end.y);
		
		for (int x = minX; x <= maxX; x++) {
			for (int y = minY; y <= maxY; y++) {
				Point p = new Point(x, y);
				
				action.accept(p);
			}
		}
	}
}