package com.gmail.bluballsman.mazealgo.pathfinding;

import com.gmail.bluballsman.mazealgo.maze.Tile;

import java.util.Comparator;

public class AStarComparator implements Comparator<Tile> {
    private final Tile start;
    private final Tile finish;

    public AStarComparator(Tile start, Tile finish) {
        this.start = start;
        this.finish = finish;
    }

    public double calculateManhattan(Tile p1, Tile p2) {
        return Math.abs(p1.x - p2.x) + Math.abs(p1.y - p2.y);
    }

    @Override
    public int compare(Tile p1, Tile p2) {
        double toStartP1 = calculateManhattan(p1, start);
        double toFinishP1 = calculateManhattan(p1, finish);
        double combinedP1 = toStartP1 + toFinishP1;

        double toStartP2 = calculateManhattan(p2, start);
        double toFinishP2 = calculateManhattan(p2, finish);
        double combinedP2 = toStartP1 + toFinishP2;

        if (combinedP1 > combinedP2) {
            return 1;
        } else if (combinedP1 < combinedP2) {
            return -1;
        } else {
            return toStartP1 > toStartP2 ? 1 : -1;
        }
    }

}
