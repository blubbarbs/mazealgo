package com.gmail.bluballsman.mazealgo.maze;

import com.gmail.bluballsman.mazealgo.structure.Structure;

public class StructureSlot {
    public final int x;
    public final int y;
    public final Structure structure;

    public StructureSlot(int x, int y, Structure structure) {
        this.x = x;
        this.y = y;
        this.structure = structure;
    }

}
