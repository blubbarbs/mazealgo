package com.gmail.bluballsman.mazealgo.maze;

import com.gmail.bluballsman.mazealgo.structure.Structure;

public class StructureSlot {
    public final int topLeftX;
    public final int topLeftY;
    public final Structure structure;

    public StructureSlot(int x, int y, Structure structure) {
        this.topLeftX = x;
        this.topLeftY = y;
        this.structure = structure;
    }

}
