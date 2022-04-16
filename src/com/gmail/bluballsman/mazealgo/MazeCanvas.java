package com.gmail.bluballsman.mazealgo;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

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
	
	public void paintTile(Graphics g, Point p, Color color) {
		paintTile(g, p.x, p.y, color);
	}
	
	public void paintGridLayer(Graphics g) {
		for (int y = 0; y < maze.getHeight(); y++) {
			for (int x = 0; x < maze.getWidth(); x++) {		
				boolean isGuaranteedWall = x % 2 == 0 && y % 2 == 0;
				boolean isGuaranteedGround = x % 2 == 1 && y % 2 == 1;
			
				if (isGuaranteedWall) {
					paintTile(g, x, y, new Color(255, 255, 255, 50));
				}				
				else if (isGuaranteedGround) {
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
		maze = new SymmetricMaze(mazeWidth, mazeHeight);
		String centerBlueprint = """
				XXXXXXXXXXXXXXXXX
				XXXXXXXXXXXXXXXXX
				XX1111111111111XX
				XX1111111111111XX
				XX1111111111111XX
				XX1111111111111XX
				XX1111111111111XX
				XX1111111111111XX
				XX1111111111111XX
				XX1111111111111XX				
				XX1111111111111XX				
				XXXXXXXXXXXXXXXXX								
				XXXXXXXXXXXXXXXXX
				""";

		centerBlueprint = """
				1111111111111
				1111111111111
				1111111111111
				1111111111111
				1111111111111
				1111111111111
				1111111111111
				1111111111111			
				1111111111111				
				""";		
		
		Structure center = new Structure(centerBlueprint);
		Structure room = new Structure("01000.01110.01110.01110.00010");
		Structure deadEnd = new Structure("000.010.010");
		Structure hall = new Structure("000.011.010.010.010.010.010.010.010");
		
		Point centerPoint = maze.getCenterPoint();
		Point centerStrucCorner = new Point((centerPoint.x  - center.getCenter().x), (centerPoint.y - center.getCenter().y));
		
		maze.placeStructure(centerStrucCorner, center);
		
		for (int i = 0; i < 3; i++) {
			maze.placeStructure(room);
		}		
		for (int i = 0; i < 16; i++) {
			maze.placeStructure(deadEnd);
		}
		for (int i = 0; i < 3; i++) {
			maze.placeStructure(hall);
		}
		
		maze.fillMaze(new Point(1, 1));
		//maze.knockDownWalls(45);
		
		System.out.println("Time: " + (System.currentTimeMillis() - millis));

		for (int y = 0; y < maze.getHeight(); y++) {
			for (int x = 0; x < maze.getWidth(); x++) {
				Tile t = maze.getTile(x, y);
				Color color = null;
				
				if (t.isStructure()) {
					color = t.isGround() ? new Color(255, 255, 255) : new Color(255, 0, 0);
					//color = t.isGround() ? new Color(255, 255, 255) : new Color(0, 0, 0);
				}
				else {
					color = t.isGround() ? new Color(255, 255, 255) : new Color(0, 0, 0);
				}
				
				paintTile(g, x, y, color);
			}
		}
		
		paintTile(g, maze.getCenterPoint(), new Color(255, 0, 255));
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
