package me.becja10.CustomFlags;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class FlagManager {

	private static Logger logger;
	private static FileConfiguration config = null;
	private static File Flags = null;
	private static String path;
	
	public static FileConfiguration getFlags() {
		/*
		 *    worldname:
		 *      regionname: list of flags
		 */
		if (config == null)
			reloadFlags();
		return config;
	}

	public static void reloadFlags() {
		if (Flags == null)
			Flags = new File(path);
		config = YamlConfiguration.loadConfiguration(Flags);
	}
	
	public static void saveFlags() {
		if ((config == null) || (Flags == null))
			return;
		try {
			getFlags().save(Flags);
		} catch (IOException ex) {
			logger.warning("Unable to write to the file \"" + path + "\"");
		}
	}
	
	public static void setUpManager(JavaPlugin plugin, Logger log){
		path = plugin.getDataFolder().getAbsolutePath()	+ File.separator + "Flags.yml".toLowerCase();
		reloadFlags();
		
		String header = "Flags available\n\n";
		header += "nomobdamage: Prevent taking damage from mobs\n";
		header += "nohunger: Prevent losing hunger\n";
		header += "noportals: Prevent nether portals from being generated\n";
		header += "noteleport: Prevent any form of teleporting into or out of an area. Add customflags.bypass.noteleport to bypass\n";
		header += "noplants: Prevent plants from growing by themselves\n";
		header += "noanimals: Prevents animals from breeding or laying eggs\n";
		config.options().header(header);
		config.options().copyHeader(true);
		
		saveFlags();
	}
	
	public static boolean hasFlag(String world, String region, String flag){
		List<String> flags = config.getStringList(world + "." + region);
		return flags.contains(flag);
	}
	
	public static void addFlag(String world, String region, String flag){
		List<String> flags = config.getStringList(world + "." + region);
		flags.add(flag);
		config.set(world + "." + region, flags);
		saveFlags();
	}
}
