package com.gmail.bluballsman.mazealgo.maze;

import java.awt.Point;

import com.gmail.bluballsman.mazealgo.structure.Structure;

public class StructureSlot {
	public final Point point;
	public final Structure structure;
	
	public StructureSlot(Point point, Structure structure) {
		this.point = point;
		this.structure = structure;
	}
	
}
