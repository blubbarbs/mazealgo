package com.gmail.bluballsman.mazealgo.maze;

import java.util.HashMap;

public class Tile {
    private final Maze maze;
    public final int x;
    public final int y;
    private Structure structure = null;
    private boolean isGround = false;
    private boolean isEditable = true;

    public Tile(Maze maze, int x, int y) {
        this.maze = maze;
        this.x = x;
        this.y = y;
    }

    public Tile getNeighbor(Direction direction) {
        return maze.getTile(x + direction.X_OFFSET, y + direction.Y_OFFSET);
    }

    public Structure getStructure() {
        return structure;
    }

    public boolean isEdge() {
        return x == 0 || x == maze.width - 1 || y == 0 || y == maze.height - 1;
    }

    public boolean isGround() {
        return isGround;
    }

    public boolean isStructure() {
        return structure != null;
    }

    public boolean isEditable() {
        return isEditable;
    }

    public void setUneditable() {
        this.isEditable = false;
    }

    public void setGround(boolean isGround) {
        this.isGround = isGround;
    }

    public void setStructure(Structure structure) {
        this.structure = structure;
    }

    // Returns the "type code" for this tile. The type code is a representation of the surrounding tiles on the
    // north, east, south, and west sides of this tile. If a surrounding tile match this tile's ground flag, it is
    // represented as a "1", otherwise as "0". These bits are represented in the type code from left to right as north,
    // east, south, west. So a type code of 1010 means that the ground flag of the surrounding tiles matched this tile
    // in the north and south positions. Out of bounds tiles are counted as ground.
    public int getTypeCode() {
        Tile north = getNeighbor(Direction.NORTH);
        Tile east = getNeighbor(Direction.EAST);
        Tile south = getNeighbor(Direction.SOUTH);
        Tile west = getNeighbor(Direction.WEST);

        int typeCode = 0;
        typeCode += north != null && north.isGround == isGround ? 0b1000 : 0;
        typeCode += east != null && east.isGround == isGround ? 0b0100 : 0;
        typeCode += south != null && south.isGround == isGround ? 0b0010 : 0;
        typeCode += west != null && west.isGround == isGround ? 0b0001 : 0;

        return typeCode;
    }

    public Type getTileType() {
        return Type.getType(this.getTypeCode());
    }

    public int getRotations() {
        return Type.getRotations(this.getTypeCode());
    }

    public enum Type {
        T(0b1101),
        CORNER(0b1100),
        STRAIGHT(0b1010),
        END(0b0010),
        CROSS(0b1111),
        ALONE(0b0000);

        static HashMap<Integer, Type> codeMap = new HashMap<Integer, Type>();
        static HashMap<Integer, Integer> rotationMap = new HashMap<Integer, Integer>();

        // Setting up the default type codes for each of the tile types. All rotations from this type code
        // are stored in a separate map for easy access.
        static {
            for (Type type : values()) {
                int dummyCode = type.typeCode + (type.typeCode << 4);
                for (int rotations = 0; rotations < 4; rotations++) {
                    int rotatedTypeCode = (dummyCode >> rotations) & 0b00001111;
                    codeMap.putIfAbsent(rotatedTypeCode, type);
                    rotationMap.putIfAbsent(rotatedTypeCode, rotations);
                }
            }
        }

        static Type getType(int typeCode) {
            return codeMap.get(typeCode);
        }

        static int getRotations(int typeCode) {
            return rotationMap.get(typeCode);
        }

        private final int typeCode;

        Type(int typeCode) {
            this.typeCode = typeCode;
        }
    }
}
