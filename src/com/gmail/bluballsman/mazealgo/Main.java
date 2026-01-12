package com.gmail.bluballsman.mazealgo;

import com.gmail.bluballsman.mazealgo.maze.Maze;
import com.gmail.bluballsman.mazealgo.maze.Tile;
import com.gmail.bluballsman.mazealgo.structure.Structure;

import java.awt.Canvas;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;

public class Main {
    static final int tileSize = 15;
    static final int width = 75;
    static final int height = 75;

    public static Maze createMaze() {
        long millis = System.currentTimeMillis();

        Structure center = new Structure(
                """
                        XXXXXXXXXXXXXXX
                        X1111111111111X
                        X1111111111111X
                        X1111111111111X
                        X1111111111111X
                        X1111111111111X
                        X1111111111111X
                        X1111111111111X
                        X1111111111111X
                        X1111111111111X
                        XXXXXXXXXXXXXXX
                        """);
        Structure room = new Structure(
                """
                        XXXXXXX
                        X11111X
                        X11111X
                        X11111X
                        X11111X
                        X11111X
                        XXXXXXX
                        """);
        Structure deadEnd = new Structure(
                """
                        000
                        010
                        010
                        """);
        Structure hall = new Structure(
                """
                        000
                        011
                        010
                        010
                        010
                        010
                        010
                        """);

        Maze maze = new Maze(width, height, millis);

        Tile centerTile = maze.getCenterTile();

        maze.placeStructure(centerTile.x, centerTile.y, center);

        for (int i = 0; i < 3; i++) {
            maze.generateStructure(room);
        }
        for (int i = 0; i < 16; i++) {
            maze.generateStructure(deadEnd);
        }
        for (int i = 0; i < 3; i++) {
            maze.generateStructure(hall);
        }

        maze.fillMaze(1, 1);
        maze.knockDownWalls(12);

        return maze;
    }

    private static class CanvasListener implements MouseListener {
        private MazeCanvas mazeCanvas;

		public CanvasListener(MazeCanvas canvas) {
        	this.mazeCanvas = canvas;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
        }

        @Override
        public void mousePressed(MouseEvent e) {
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            mazeCanvas.setMaze(createMaze());
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }

    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Maze");
        MazeCanvas canvas = new MazeCanvas(createMaze(), tileSize);
        canvas.addMouseListener(new CanvasListener(canvas));
        frame.add(canvas);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
