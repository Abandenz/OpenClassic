package ch.spacebase.openclassic.game.io;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;


import ch.spacebase.openclassic.api.OpenClassic;
import ch.spacebase.openclassic.api.Position;
import ch.spacebase.openclassic.api.level.Level;
import ch.spacebase.openclassic.api.level.LevelInfo;
import ch.spacebase.openclassic.api.level.generator.NormalGenerator;
import ch.spacebase.openclassic.api.util.io.IOUtils;
import ch.spacebase.openclassic.game.level.ClassicLevel;

import ch.spacebase.opennbt.TagBuilder;
import ch.spacebase.opennbt.stream.NBTInputStream;
import ch.spacebase.opennbt.stream.NBTOutputStream;
import ch.spacebase.opennbt.tag.ByteArrayTag;
import ch.spacebase.opennbt.tag.ByteTag;
import ch.spacebase.opennbt.tag.CompoundTag;
import ch.spacebase.opennbt.tag.DoubleTag;
import ch.spacebase.opennbt.tag.FloatTag;
import ch.spacebase.opennbt.tag.IntTag;
import ch.spacebase.opennbt.tag.LongTag;
import ch.spacebase.opennbt.tag.ShortTag;
import ch.spacebase.opennbt.tag.StringTag;

public class OpenClassicLevelFormat {
	
	public static Level load(ClassicLevel level, String name, boolean create) throws IOException {
		if(!(new File(OpenClassic.getGame().getDirectory(), "levels/" + name + ".map").exists())) {
			if(new File(OpenClassic.getGame().getDirectory(), "levels/" + name + ".mclevel").exists()) {
				OpenClassic.getLogger().info(String.format(OpenClassic.getGame().getTranslator().translate("level.detected-format"), "Minecraft Indev"));
				IndevLevelFormat.read(level, "levels/" + name + ".mclevel");
				save(level);
				return level;
			}
			
			if(new File(OpenClassic.getGame().getDirectory(), "levels/" + name + ".mine").exists()) {
				OpenClassic.getLogger().info(String.format(OpenClassic.getGame().getTranslator().translate("level.detected-format"), "Minecraft Classic"));
				MinecraftLevelFormat.read(level, "levels/" + name + ".mine");
				save(level);
				return level;
			}
			
			if(new File(OpenClassic.getGame().getDirectory(), "levels/" + name + ".dat").exists()) {
				OpenClassic.getLogger().info(String.format(OpenClassic.getGame().getTranslator().translate("level.detected-format"), "Minecraft Classic"));
				MinecraftLevelFormat.read(level, "levels/" + name + ".dat");
				save(level);
				return level;
			}
			
			if(new File(OpenClassic.getGame().getDirectory(), "levels/" + name + ".lvl").exists()) {
				OpenClassic.getLogger().info(String.format(OpenClassic.getGame().getTranslator().translate("level.detected-format"), "MCSharp"));
				MCSharpLevelFormat.load(level, "levels/" + name + ".lvl");
				save(level);
				return level;
			}
			
			if(new File(OpenClassic.getGame().getDirectory(), "levels/" + name + ".oclvl").exists()) {
				OpenClassic.getLogger().info(String.format(OpenClassic.getGame().getTranslator().translate("level.detected-format"), "Old OpenClassic"));
				readOld(level, "levels/" + name + ".oclvl");
				save(level);
				return level;
			}
		}

		File levelFile = new File(OpenClassic.getGame().getDirectory(), "levels/" + name + ".map");
		if(!levelFile.exists()) {
			if(create) {
				OpenClassic.getLogger().info(String.format(OpenClassic.getGame().getTranslator().translate("level.auto-create"), name));
				return OpenClassic.getGame().createLevel(new LevelInfo(name, new Position(null, 0, 65, 0), (short) 256, (short) 64, (short) 256), new NormalGenerator());
			} else {
				return null;
			}
		}
		
		FileInputStream in = new FileInputStream(levelFile);
		NBTInputStream nbt = new NBTInputStream(in);
		CompoundTag root = (CompoundTag) nbt.readTag();
		CompoundTag info = (CompoundTag) root.get("Info");
		if(info.get("Version") == null) {
			readOldNbt(level, root, info);
		} else {
			int version = ((IntTag) info.get("Version")).getValue();
			if(version == 1) {
				CompoundTag spawn = (CompoundTag) root.get("Spawn");
				CompoundTag map = (CompoundTag) root.get("Map");
				
				level.setName(((StringTag) info.get("Name")).getValue());
				level.setAuthor(((StringTag) info.get("Author")).getValue());
				level.setCreationTime(((LongTag) info.get("CreationTime")).getValue());
				
				float x = ((FloatTag) spawn.get("x")).getValue();
				float y = ((FloatTag) spawn.get("y")).getValue();
				float z = ((FloatTag) spawn.get("z")).getValue();
				float yaw = ((FloatTag) spawn.get("yaw")).getValue();
				float pitch = ((FloatTag) spawn.get("pitch")).getValue();
				level.setSpawn(new Position(level, x, y, z, yaw, pitch));
				
				short width = ((ShortTag) map.get("Width")).getValue();
				short height = ((ShortTag) map.get("Height")).getValue();
				short depth = ((ShortTag) map.get("Depth")).getValue();
				byte blocks[] = ((ByteArrayTag) map.get("Blocks")).getValue();
				level.setData(width, height, depth, blocks);
			} else {
				nbt.close();
				throw new IOException("Unknown OpenClassic map version: " + version);
			}
		}

		nbt.close();
		return level;
	}
	
	public static void readOldNbt(ClassicLevel level, CompoundTag root, CompoundTag info) {
		CompoundTag spawn = (CompoundTag) root.get("Spawn");
		CompoundTag map = (CompoundTag) root.get("Map");
		
		level.setName(((StringTag) info.get("Name")).getValue());
		level.setAuthor(((StringTag) info.get("Author")).getValue());
		level.setCreationTime(((LongTag) info.get("CreationTime")).getValue());
		
		double x = ((DoubleTag) spawn.get("x")).getValue();
		double y = ((DoubleTag) spawn.get("y")).getValue();
		double z = ((DoubleTag) spawn.get("z")).getValue();
		byte yaw = ((ByteTag) spawn.get("yaw")).getValue();
		byte pitch = ((ByteTag) spawn.get("pitch")).getValue();
		level.setSpawn(new Position(level, (float) x + 0.5f, (float) y, (float) z + 0.5f, yaw, pitch));
		
		short width = ((ShortTag) map.get("Width")).getValue();
		short height = ((ShortTag) map.get("Height")).getValue();
		short depth = ((ShortTag) map.get("Depth")).getValue();
		byte blocks[] = ((ByteArrayTag) map.get("Blocks")).getValue();
		level.setData(width, height, depth, blocks);
	}
	
	public static Level readOld(ClassicLevel level, String file) throws IOException {
		File f = new File(OpenClassic.getGame().getDirectory(), file);
		FileInputStream in = new FileInputStream(new File(OpenClassic.getGame().getDirectory(), file));
		GZIPInputStream gzipIn = new GZIPInputStream(in);
		DataInputStream data = new DataInputStream(gzipIn);
		
		level.setName(IOUtils.readString(data));
		level.setAuthor(IOUtils.readString(data));
		level.setCreationTime(data.readLong());
		
		double x = data.readDouble();
		double y = data.readDouble();
		double z = data.readDouble();
		byte yaw = data.readByte();
		byte pitch = data.readByte();
		level.setSpawn(new Position(level, (float) x, (float) y, (float) z, yaw, pitch));
		
		short width = data.readShort();
		short height = data.readShort();
		short depth = data.readShort();
		
		byte[] blocks = new byte[width * depth * height];
		data.read(blocks);

		level.setData(width, depth, height, blocks);
		
		data.close();
		
		try {
			f.delete();
		} catch(SecurityException e) {
			e.printStackTrace();
		}
		
		return level;
	}
	
	public static void save(Level level) throws IOException {
		FileOutputStream out = new FileOutputStream(new File(OpenClassic.getGame().getDirectory(), "levels/" + level.getName() + ".map"));
		NBTOutputStream nbt = new NBTOutputStream(out);
		
		TagBuilder root = new TagBuilder("Level");
		
		TagBuilder info = new TagBuilder("Info");
		info.append("Version", 1);
		info.append("Name", level.getName());
		info.append("Author", level.getAuthor());
		info.append("CreationTime", level.getCreationTime());
		root.append(info);
		
		TagBuilder spawn = new TagBuilder("Spawn");
		spawn.append("x", level.getSpawn().getX());
		spawn.append("y", level.getSpawn().getY());
		spawn.append("z", level.getSpawn().getZ());
		spawn.append("yaw", level.getSpawn().getYaw());
		spawn.append("pitch", level.getSpawn().getPitch());
		root.append(spawn);
		
		TagBuilder map = new TagBuilder("Map");
		map.append("Width", level.getWidth());
		map.append("Height", level.getHeight());
		map.append("Depth", level.getDepth());
		map.append("Blocks", level.getBlocks());
		root.append(map);
		
		nbt.writeTag(root.toCompoundTag());
		nbt.close();
	}
	
	private OpenClassicLevelFormat() {
	}
	
}
