package com.gmail.bluballsman.mazealgo.maze;

import java.util.HashMap;

public class Tile {
	protected boolean isGround = false;
	protected boolean structureFlag = false;
	
	public boolean isGround() {
		return isGround;
	}
	
	public boolean isStructure() {
		return structureFlag;
	}	
			
	public static enum Type {
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
			for(Type type: values()) {
				int dummyCode = type.typeCode + (type.typeCode << 4);
				for(int rotations = 0; rotations < 4; rotations++) {
					int rotatedTypeCode = (dummyCode >> rotations) & 0b00001111;
					codeMap.putIfAbsent(rotatedTypeCode, type);
					rotationMap.putIfAbsent(rotatedTypeCode, rotations);
				}
			}
		}
		
		protected static Type getType(int typeCode) {
			return codeMap.get(typeCode);
		}
		
		protected static int getRotations(int typeCode) {
			return rotationMap.get(typeCode);
		}
		
		private final int typeCode;
		
		Type(int typeCode) {
			this.typeCode = typeCode;
		}
	}	
}
