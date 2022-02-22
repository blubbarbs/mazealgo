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
	
	public void paintTile(Graphics g, int x, int y, Color color) {
		int paintX = tileSize * x;
		int paintY = tileSize * y;
		
		g.setColor(color);
		g.fillRect(paintX, paintY, tileSize, tileSize);		
	}
	
	public void paintGridLayer(Graphics g) {
		for(int y = 0; y < maze.getHeight(); y++) {
			for(int x = 0; x < maze.getWidth(); x++) {		
				boolean isGuaranteedWall = x % 2 == 0 && y % 2 == 0;
				boolean isGuaranteedGround = x % 2 == 1 && y % 2 == 1;
			
				if(isGuaranteedWall) {
					paintTile(g, x, y, new Color(255, 255, 255, 50));
				}				
				else if(isGuaranteedGround) {
					paintTile(g, x, y, new Color(255, 255, 255, 210));					
				}
				else {
					paintTile(g, x, y, new Color(255, 255, 255, 127));					
				}
			}
		}
		
	}
	
	@Override
	public void paint(Graphics g) {
		long millis = System.currentTimeMillis();
		maze = new Maze(mazeWidth, mazeHeight);
		Structure testStructure = new Structure("01000.01110.01110.01110.00000");
		Structure testStructure2 = new Structure("000.010.010");
		Structure testStructure3 = new Structure("000.011.010.010.010.010.010.010.010");
		maze.placeStructure(testStructure);
		maze.placeStructure(testStructure);
		maze.placeStructure(testStructure2);
		maze.placeStructure(testStructure2);
		maze.placeStructure(testStructure2);
		maze.placeStructure(testStructure2);
		maze.placeStructure(testStructure2);
		maze.placeStructure(testStructure2);
		maze.placeStructure(testStructure2);
		maze.placeStructure(testStructure2);
		maze.placeStructure(testStructure2);
		maze.placeStructure(testStructure2);
		maze.placeStructure(testStructure2);
		maze.placeStructure(testStructure2);		
		maze.placeStructure(testStructure2);
		maze.placeStructure(testStructure2);
		maze.placeStructure(testStructure2);
		maze.placeStructure(testStructure2);				
		maze.placeStructure(testStructure3);
		
		maze.fillMaze(new Point(1, 1));
		maze.knockDownWalls(45);
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
				Tile t = maze.getTile(x, y);
				Color color = null;
				
				if(t.isStructure()) {
					color = t.isGround() ? new Color(255, 255, 255) : new Color(255, 0, 0);
				}
				else {
					color = t.isGround() ? new Color(255, 255, 255) : new Color(0, 0, 0);
				}
				
				paintTile(g, x, y, color);
			}
		}
		
		paintGridLayer(g);
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
