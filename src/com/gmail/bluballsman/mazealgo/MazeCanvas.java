package com.gmail.bluballsman.mazealgo;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import com.gmail.bluballsman.mazealgo.maze.Maze;
import com.gmail.bluballsman.mazealgo.maze.Tile;

public class MazeCanvas extends Canvas {
    private static final long serialVersionUID = 1L;
    private final int tileSize;
    private Maze maze;

    public MazeCanvas(Maze maze, int tileSize) {
        this.maze = maze;
        this.tileSize = tileSize;
        setSize(maze.getWidth() * tileSize, maze.getHeight() * tileSize);
    }

    public void setMaze(Maze maze) {
        this.maze = maze;
        setSize(maze.getWidth() * tileSize, maze.getHeight() * tileSize);

        this.repaint();
    }

    private void paintTile(Graphics g, int x, int y, Color color) {
        int paintX = tileSize * x;
        int paintY = tileSize * y;

        g.setColor(color);
        g.fillRect(paintX, paintY, tileSize, tileSize);
    }

    private void paintTile(Graphics g, Point p, Color color) {
        paintTile(g, p.x, p.y, color);
    }

    private void paintGridLayer(Graphics g) {
        for (int y = 0; y < maze.getHeight(); y++) {
            for (int x = 0; x < maze.getWidth(); x++) {
                boolean isGuaranteedWall = x % 2 == 0 && y % 2 == 0;
                boolean isGuaranteedGround = x % 2 == 1 && y % 2 == 1;

                if (isGuaranteedWall) {
                    paintTile(g, x, y, new Color(255, 255, 255, 50));
                } else if (isGuaranteedGround) {
                    paintTile(g, x, y, new Color(255, 255, 255, 210));
                } else {
                    paintTile(g, x, y, new Color(255, 255, 255, 127));
                }
            }
        }
    }

    @Override
    public void paint(Graphics g) {
        for (int y = 0; y < maze.getHeight(); y++) {
            for (int x = 0; x < maze.getWidth(); x++) {
                Tile t = maze.getTile(x, y);
                Color color = null;

                if (t.isStructure()) {
                    color = t.isGround() ? new Color(127, 255, 255) : new Color(255, 0, 0);
                    //color = t.isGround() ? new Color(255, 255, 255) : new Color(0, 0, 0);
                } else {
                    color = t.isGround() ? new Color(255, 255, 255) : new Color(0, 0, 0);
                }

                paintTile(g, x, y, color);
            }
        }

        paintGridLayer(g);
    }
}
