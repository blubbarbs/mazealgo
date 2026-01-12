package com.gmail.bluballsman.mazealgo.maze;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Stack;
import java.util.function.Predicate;

import com.gmail.bluballsman.mazealgo.pathfinding.AStarComparator;
import com.gmail.bluballsman.mazealgo.structure.Structure;

public class Maze {
    protected int width;
    protected int height;
    protected Tile[][] tiles;
    protected Random random = new Random();

    public Maze(int width, int height) {
		assert width % 4 == 3;
		assert height % 4 == 3;

        this.width = width;
        this.height = height;
        tiles = new Tile[width][height];

        for (int y = 0; y < height; y++) {
            boolean isEdgeY = y == 0 || y == height - 1;

            for (int x = 0; x < width; x++) {
                boolean isEdgeX = x == 0 || x == width - 1;

                Tile tile = new Tile(this, x, y);

                if (isEdgeX || isEdgeY)
                    tile.setUneditable();

                this.tiles[x][y] = tile;
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

    public boolean isValidPoint(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    public Tile getCenterTile() {
        return this.getTile((width - 1) / 2, (height - 1) / 2);
    }

    public Tile getTile(int x, int y) {
        return isValidPoint(x, y) ? tiles[x][y] : null;
    }

    public ArrayList<Tile> getSurroundingTiles(int x, int y, int offset, Predicate<Tile> predicate) {
        Tile[] surroundingTiles = {
                this.getTile(x, y + offset),
                this.getTile(x + offset, y),
                this.getTile(x, y - offset),
                this.getTile(x - offset, y)
        };
        ArrayList<Tile> filtered = new ArrayList<>();

        for (Tile tile : surroundingTiles) {
            if (tile == null)
                continue;

            if (predicate == null || predicate.test(tile))
                filtered.add(tile);
        }

        return filtered;
    }

    public boolean doesStructureFit(int x, int y, Structure s) {
        if (x - s.radiusX == 0 || x + s.radiusX >= width || y - s.radiusY == 0 ||  y + s.radiusY >= height)
            return false;

        int topLeftX = x - s.radiusX;
        int topLeftY = y - s.radiusY;

        for (int sy = 0; sy < s.height; sy += 2) {
            for (int sx = 0; sx < s.width; sx += 2) {
                Tile tile = this.getTile(topLeftX + sx, topLeftY + sy);

                if (tile == null || tile.isStructure())
                    return false;

                char symbol = s.blueprint[sx][sy];
                boolean overwrites = (symbol == '1' && !tile.isGround()) || (symbol == '0' && tile.isGround());

                if (!tile.isEditable() && overwrites)
                    return false;
            }
        }

        return true;
    }

    public ArrayList<StructureSlot> findValidStructureSlots(Structure s) {
        ArrayList<StructureSlot> matches = new ArrayList<StructureSlot>();

        for (int rotations = 0; rotations < 4; rotations++) {
            for (int y = 1; y < height; y += 2) {
                for (int x = 1; x < width; x += 2) {
                    if (doesStructureFit(x, y, s))
                        matches.add(new StructureSlot(x, y, s));
                }
            }

            s = s.rotate(1);
        }

        return matches;
    }

    public void placeStructure(int x, int y, Structure s) {
        int topLeftX = x - s.radiusX;
        int topLeftY = y - s.radiusY;

        for (int sy = 0; sy < s.height; sy++) {
            for (int sx = 0; sx < s.width; sx++) {
                Tile tile = this.getTile(topLeftX + sx, topLeftY + sy);
                char symbol = s.blueprint[sx][sy];

                tile.setStructure(true);

                switch (symbol) {
                    case '0':
                        tile.setGround(false);
                        tile.setUneditable();
                        break;
                    case '1':
                        tile.setGround(true);
                        tile.setUneditable();
                        break;
                    case '?':
                        tile.setGround(random.nextBoolean());
                        tile.setUneditable();
                        break;
                    default:
                        break;
                }
            }
        }
    }

    public void generateStructure(Structure s) {
        ArrayList<StructureSlot> slots = findValidStructureSlots(s);

        if (slots.isEmpty()) {
            return;
        }

        int randomIndex = random.nextInt(slots.size());
        StructureSlot structureSlot = slots.get(randomIndex);
        Structure selectedStructure = structureSlot.structure;

        placeStructure(structureSlot.x, structureSlot.y, selectedStructure);
    }

    public void fillMaze(int xStart, int yStart) {
        Tile start = this.getTile(xStart, yStart);

        if (start == null)
            return;

        Stack<Tile> path = new Stack<>();

        path.push(start);
        start.setGround(true);

        while (!path.isEmpty()) {
            Tile current = path.peek();
            ArrayList<Tile> available = getSurroundingTiles(current.x, current.y, 2, t -> !t.isGround());

            if (!available.isEmpty()) {
                int chosenIndex = random.nextInt(available.size());
                Tile next = available.get(chosenIndex);
                int dx = (next.x - current.x) / 2;
                int dy = (next.y - current.y) / 2;
                Tile inBetween = this.getTile(current.x + dx, current.y + dy);

                inBetween.setGround(true);
                next.setGround(true);
                path.push(next);
            }
            else {
                path.pop();
            }
        }
    }

    public Stack<Tile> findPath(int x1, int y1, int x2, int y2) {
        Tile start = this.getTile(x1, y1);
        Tile end = this.getTile(x2, y2);

        PriorityQueue<Tile> open = new PriorityQueue<>(new AStarComparator(start, end));
        HashMap<Tile, Tile> history = new HashMap<>();
        open.add(start);
        history.put(start, null);

        while (!open.isEmpty()) {
            Tile current = open.poll();

            ArrayList<Tile> neighbors = getSurroundingTiles(current.x, current.y, 1, t -> t.isGround() && !history.containsKey(t));

            for (Tile t : neighbors) {
                open.add(t);
                history.put(t, current);
            }

            if (history.containsKey(end))
                break;
        }

        // Reconstructing path after A* search is done
        if (history.containsKey(end)) {
            Stack<Tile> path = new Stack<>();

            Tile current = end;
            while (current != start) {
                path.add(current);
                current = history.get(current);
            }

            path.add(start);

            return path;
        }
        else {
            return null;
        }
    }

    public ArrayList<Tile> knockDownWalls(int cutoffLength) {
        ArrayList<Tile> deletableWalls = new ArrayList<>();
        ArrayList<Tile> deletedWalls = new ArrayList<>();

        for (int y = 1; y < height - 1; y++) {
            for (int x = 1 + (y % 2); x < width - 1; x += 2) {
                Tile tile = this.getTile(x, y);

                if (!tile.isGround() && tile.isEditable())
                    deletableWalls.add(tile);
            }
        }

        Collections.shuffle(deletableWalls);

        for (Tile wall : deletableWalls) {
            ArrayList<Tile> neighbors = getSurroundingTiles(wall.x, wall.y, 1, Tile::isGround);
            Tile start = neighbors.get(0);
            Tile end = neighbors.get(1);

            Stack<Tile> shortestPath = findPath(start.x, start.y, end.x, end.y);

            if (shortestPath == null || shortestPath.size() >= cutoffLength) {
                wall.setGround(true);
                deletedWalls.add(wall);
            }
        }

        return deletedWalls;
    }
}