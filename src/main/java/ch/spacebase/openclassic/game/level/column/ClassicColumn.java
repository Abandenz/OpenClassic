package ch.spacebase.openclassic.game.level.column;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ch.spacebase.openclassic.api.OpenClassic;
import ch.spacebase.openclassic.api.block.BlockType;
import ch.spacebase.openclassic.api.block.Blocks;
import ch.spacebase.openclassic.api.level.column.Column;
import ch.spacebase.openclassic.api.util.Constants;
import ch.spacebase.openclassic.game.level.ClassicLevel;
import ch.spacebase.openclassic.game.level.LightType;

public class ClassicColumn implements Column {

	private ClassicLevel level;
	private int x;
	private int z;
	private ClassicChunk chunks[] = new ClassicChunk[Constants.COLUMN_HEIGHT >> 4];
	
	public ClassicColumn(ClassicLevel level, int x, int z) {
		this.level = level;
		this.x = x;
		this.z = z;
		for(int index = 0; index < this.chunks.length; index++) {
			this.chunks[index] = new ClassicChunk(this, index);
		}
	}
	
	public ClassicLevel getLevel() {
		return this.level;
	}
	
	public int getX() {
		return this.x;
	}
	
	public int getZ() {
		return this.z;
	}
	
	public int getWorldX() {
		return this.x << 4;
	}
	
	public int getWorldZ() {
		return this.z << 4;
	}
	
	public byte getBlockAt(int x, int y, int z) {
		if(x < this.getWorldX() || z < this.getWorldZ() || x >= this.getWorldX() + Constants.CHUNK_WIDTH || z >= this.getWorldZ() + Constants.CHUNK_DEPTH) {
			if(this.level.isColumnLoaded(x >> 4, z >> 4)) {
				return this.level.getBlockIdAt(x, y, z);
			}

			return 0;
		}
		
		if(this.getChunkFromBlock(y) == null) return 0;
		return this.getChunkFromBlock(y).getBlockAt(x, y, z);
	}
	
	public void setBlockAt(int x, int y, int z, byte id) {
		if(x < this.getWorldX() || z < this.getWorldZ() || x >= this.getWorldX() + Constants.CHUNK_WIDTH || z >= this.getWorldZ() + Constants.CHUNK_DEPTH) {
			if(this.level.isColumnLoaded(x >> 4, z >> 4)) {
				this.level.setBlockIdAt(x, y, z, id);
			}
			
			return;
		}
		
		if(this.getChunkFromBlock(y) == null) return;
		this.getChunkFromBlock(y).setBlockAt(x, y, z, id);
	}
	
	public int getHighestOpaque(int x, int z) {
		if(x < this.getWorldX() || z < this.getWorldZ() || x >= this.getWorldX() + Constants.CHUNK_WIDTH || z >= this.getWorldZ() + Constants.CHUNK_DEPTH)
			return -1;
		
		for(int y = Constants.COLUMN_HEIGHT - 1; y >= 0; y--) {
			BlockType type = this.getBlockAt(x, y, z) >= 0 ? Blocks.fromId(this.getBlockAt(x, y, z)) : null;
			if(type != null && type.isOpaque()) return y;
		}
		
		return -1;
	}
	
	public ClassicChunk getChunk(int y) {
		if(y < 0 || y >= this.chunks.length) return null;
		return this.chunks[y];
	}
	
	public ClassicChunk getChunkFromBlock(int y) {
		return this.getChunk(y >> 4);
	}
	
	public List<ClassicChunk> getChunks() {
		return new ArrayList<ClassicChunk>(Arrays.asList(this.chunks));
	}
	
	public void save() {
		try {
			this.level.getFormat().save(this);
		} catch (IOException e) {
			OpenClassic.getLogger().severe("Failed to save column (" + this.x + ", " + this.z + ")");
			e.printStackTrace();
		}
	}
	
	public void dispose() {
		for(ClassicChunk chunk : this.chunks) {
			chunk.dispose();
		}
	}
	
	public void update() {
		for(ClassicChunk chunk : this.chunks) {
			chunk.update();
		}
		
		// TODO
	}
	
	@Override
	public String toString() {
		return "Column{x=" + this.x + ",z=" + this.z + "}";
	}

	public float getBrightness(int x, int y, int z) {
		if(x < this.getWorldX() || z < this.getWorldZ() || x >= this.getWorldX() + Constants.CHUNK_WIDTH || z >= this.getWorldZ() + Constants.CHUNK_DEPTH)
			return this.level.getBrightness(x, y, z);
		
		ClassicChunk chunk = this.getChunkFromBlock(y);
		if(chunk == null) return 1;
		return chunk.getBrightness(x, y, z);
	}

	public int getLightLevel(int x, int y, int z) {
		if(x < this.getWorldX() || z < this.getWorldZ() || x >= this.getWorldX() + Constants.CHUNK_WIDTH || z >= this.getWorldZ() + Constants.CHUNK_DEPTH)
			return this.level.getLightLevel(x, y, z);
		
		ClassicChunk chunk = this.getChunkFromBlock(y);
		if(chunk == null) return 15;
		return chunk.getLightLevel(x, y, z);
	}
	
	public int getLightLevel(int x, int y, int z, LightType type) {
		if(x < this.getWorldX() || z < this.getWorldZ() || x >= this.getWorldX() + Constants.CHUNK_WIDTH || z >= this.getWorldZ() + Constants.CHUNK_DEPTH)
			return this.level.getLightLevel(x, y, z, type);
		
		ClassicChunk chunk = this.getChunkFromBlock(y);
		if(chunk == null) return type == LightType.SKY ? 15 : 0;
		return chunk.getLightLevel(x, y, z, type);
	}
	
}