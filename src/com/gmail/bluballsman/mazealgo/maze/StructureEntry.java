package com.gmail.bluballsman.mazealgo.maze;

import java.awt.Point;

import com.gmail.bluballsman.mazealgo.structure.Structure;

public class StructureEntry {
	public final Point point;
	public final Structure structure;
	
	public StructureEntry(Point point, Structure structure) {
		this.point = point;
		this.structure = structure;
	}
	
}
