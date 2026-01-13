package com.gmail.bluballsman.mazealgo.maze;

import java.awt.*;
import java.util.*;
import java.util.function.Predicate;

import com.gmail.bluballsman.mazealgo.loc.Direction;
import com.gmail.bluballsman.mazealgo.pathfinding.AStarComparator;
import com.gmail.bluballsman.mazealgo.structure.Structure;

public class Maze {
    protected int width;
    protected int height;
    protected Tile[][] tiles;
    protected Random random = new Random();

    public Maze(int width, int height) {
		assert width % 2 == 1;
		assert height % 2 == 1;

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
        if (x < 0 || x + s.width - 1 >= width || y < 0 ||  y + s.height - 1 >= height)
            return false;

        for (int sy = 0; sy < s.height; sy++) {
            for (int sx = 0; sx < s.width; sx++) {
                Tile tile = this.getTile(x + sx, y + sy);

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
        ArrayList<StructureSlot> matches = new ArrayList<>();

        for (int rotations = 0; rotations < 4; rotations++) {
            for (int y = 0; y < height; y += 2) {
                for (int x = 0; x < width; x += 2) {
                    if (doesStructureFit(x, y, s))
                        matches.add(new StructureSlot(x, y, s));
                }
            }

            s = s.rotate(1);
        }

        return matches;
    }

    public void placeStructure(int topLeftX, int topLeftY, Structure s) {
        for (int sy = 0; sy < s.height; sy++) {
            for (int sx = 0; sx < s.width; sx++) {
                Tile tile = this.getTile(topLeftX + sx, topLeftY + sy);
                char symbol = s.blueprint[sx][sy];

                switch (symbol) {
                    case '0':
                        tile.setStructure(s);
                        tile.setGround(false);
                        tile.setUneditable();
                        break;
                    case '1':
                        tile.setStructure(s);
                        tile.setGround(true);
                        tile.setUneditable();
                        break;
                    case '?':
                        tile.setStructure(s);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    public void placeCenterStructure(Structure s) {
        Tile center = getCenterTile();
        int topLeftX = center.x - s.radiusX;
        int topLeftY = center.y - s.radiusY;

        topLeftX -= topLeftX % 2;
        topLeftY -= topLeftY % 2;

        placeStructure(topLeftX, topLeftY, s);
    }

    public void generateStructure(Structure s) {
        ArrayList<StructureSlot> slots = findValidStructureSlots(s);

        if (slots.isEmpty()) {
            return;
        }

        int randomIndex = random.nextInt(slots.size());
        StructureSlot structureSlot = slots.get(randomIndex);
        Structure selectedStructure = structureSlot.structure;

        placeStructure(structureSlot.topLeftX, structureSlot.topLeftY, selectedStructure);
    }

    public void generateStructure(Structure s, int amt) {
        for (int i = 0; i < amt; i++) {
            generateStructure(s);
        }
    }

    public void fillMaze(int xStart, int yStart) {
        Tile start = this.getTile(xStart, yStart);

        if (start == null)
            return;

        HashSet<Tile> explored = new HashSet<>();
        Stack<Tile> path = new Stack<>();
        explored.add(start);
        path.push(start);
        start.setGround(true);

        while (!path.isEmpty()) {
            Tile current = path.peek();
            ArrayList<Direction> availableDirections = new ArrayList<>();

            for (Direction direction : Direction.values()) {
                Tile between = current.getNeighbor(direction);
                if (between == null || (!between.isGround() && !between.isEditable()))
                    continue;

                Tile next = between.getNeighbor(direction);
                if (next == null || explored.contains(next) || (!next.isGround() && !next.isEditable()))
                    continue;

                availableDirections.add(direction);
            }

            if (!availableDirections.isEmpty()) {
                int chosenIndex = random.nextInt(availableDirections.size());
                Direction chosenDirection = availableDirections.get(chosenIndex);
                Tile between = current.getNeighbor(chosenDirection);
                Tile next = between.getNeighbor(chosenDirection);

                between.setGround(true);
                next.setGround(true);

                path.push(next);
                explored.add(next);
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

    public ArrayList<Tile> knockDownWalls(int cutoffLength, double percentage) {
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
        int numToDelete = (int) (deletableWalls.size() * percentage);

        for (int i = 0; i < numToDelete; i++) {
            Tile wall = deletableWalls.get(i);
            Tile start, end;

            if (wall.y % 2 == 0) {
                start = this.getTile(wall.x, wall.y - 1);
                end = this.getTile(wall.x, wall.y + 1);
            }
            else {
                start = this.getTile(wall.x - 1, wall.y);
                end = this.getTile(wall.x + 1, wall.y);
            }

            if (start == null || end == null || !start.isGround() || !end.isGround())
                continue;

            Stack<Tile> shortestPath = findPath(start.x, start.y, end.x, end.y);

            if (shortestPath == null || shortestPath.size() >= cutoffLength) {
                wall.setGround(true);
                deletedWalls.add(wall);
            }
        }

        return deletedWalls;
    }
}