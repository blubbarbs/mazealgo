package com.gmail.bluballsman.mazealgo;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Collection;

import com.gmail.bluballsman.mazealgo.maze.Maze;
import com.gmail.bluballsman.mazealgo.maze.SymmetricMaze;
import com.gmail.bluballsman.mazealgo.maze.Tile;
import com.gmail.bluballsman.mazealgo.structure.Structure;

public class MazeCanvas extends Canvas {
	private static final long serialVersionUID = 1L;
	
	private int mazeWidth;
	private int mazeHeight;
	private int tileSize;
	private Maze maze;
	
	public MazeCanvas(int mazeWidth, int mazeHeight, int tileSize) {
		this.mazeWidth = mazeWidth;
		this.mazeHeight = mazeHeight;
		this.tileSize = tileSize;
		addMouseListener(new CanvasListener());
		setSize(mazeWidth * tileSize, mazeHeight * tileSize);
	}
	
	@Override
	public void paint(Graphics g) {
		long millis = System.currentTimeMillis();
		maze = new SymmetricMaze(mazeWidth, mazeHeight);
		Structure testStructure = new Structure("00000.01110.01110.01110.00000");
		Structure testStructure2 = new Structure("000.010.010");
		maze.placeStructure(testStructure);
		maze.placeStructure(testStructure2);
		maze.placeStructure(testStructure2);
		maze.placeStructure(testStructure2);
		maze.placeStructure(testStructure2);
		maze.placeStructure(testStructure2);
		maze.placeStructure(testStructure2);
		maze.placeStructure(testStructure2);
		maze.placeStructure(testStructure2);		
		maze.fillMaze(new Point(1, 1));
		//maze.knockDownWalls(45);
		Collection<Point> path = maze.findPath(new Point(1, 1), maze.getCenterPoint());
		
		if(path != null) {
			System.out.println("Path: " + path.toString());
		}
		else {
			System.out.println("No Path");
		}
		
		System.out.println("Time: " + (System.currentTimeMillis() - millis));

		for(int y = 0; y < maze.getHeight(); y++) {
			for(int x = 0; x < maze.getWidth(); x++) {
				int paintX = tileSize * x;
				int paintY = tileSize * y;
				Tile t = maze.getTile(x, y);
				boolean isGuaranteedWall = x % 2 == 0 && y % 2 == 0;
				boolean isGuaranteedGround = x % 2 == 1 && y % 2 == 1;
				Color color = null;
				
				if(!t.isGround()) {
					if(isGuaranteedWall) {
						color = new Color(50, 50, 50);
					}
					else if(isGuaranteedGround) {
						color = new Color(210, 210, 210);
					}
					else {
						color = new Color(127, 127, 127);
					}
				}
				else {
					color = new Color(255, 255, 255);
				}
				
				
				g.setColor(color);
				g.fillRect(paintX, paintY, tileSize, tileSize);
			}
		}
		Point centerPoint = maze.getCenterPoint();
		g.setColor(new Color(255, 0, 0));
		g.fillRect(centerPoint.x * tileSize, centerPoint.y * tileSize, tileSize, tileSize);
	}
	
	
	private class CanvasListener implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {}

		@Override
		public void mousePressed(MouseEvent e) {}

		@Override
		public void mouseReleased(MouseEvent e) {
			repaint();
		}

		@Override
		public void mouseEntered(MouseEvent e) {}

		@Override
		public void mouseExited(MouseEvent e) {}
		
	}
	
}
