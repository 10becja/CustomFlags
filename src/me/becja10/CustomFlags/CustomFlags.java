package me.becja10.CustomFlags;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.entity.ShulkerBullet;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.sk89q.worldguard.bukkit.RegionQuery;
import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class CustomFlags extends JavaPlugin implements Listener{

	public static CustomFlags instance;
	public final static Logger logger = Logger.getLogger("Minecraft");
	
	private WorldGuardPlugin wg = WGBukkit.getPlugin();
	
	private String configPath;
	private FileConfiguration config;
	private FileConfiguration outConfig;
	
	
//	private final String notntignition = "notntignition";
	private final String nohunger = "nohunger";
	private final String nomobdamage = "nomobdamage";
	private final String randomores = "randomores";
	
	private void loadConfig(){
		configPath = this.getDataFolder().getAbsolutePath() + File.separator + "config.yml";
		config = YamlConfiguration.loadConfiguration(new File(configPath));
		outConfig = new YamlConfiguration();	
		
		
	}
	
	private void saveConfig(FileConfiguration config, String path)
	{
        try{config.save(path);}
        catch(IOException exception){logger.info("Unable to write to the configuration file at \"" + path + "\"");}
	}
	
	@Override
	public void onEnable(){
		PluginDescriptionFile pdfFile = this.getDescription();
		PluginManager manager = getServer().getPluginManager();

		logger.info(pdfFile.getName() + " Version " + pdfFile.getVersion() + " has been enabled!");
		instance = this;		
		
		manager.registerEvents(this, this);
		
		FlagManager.setUpManager(this, logger);
		
		loadConfig();		
	}
		
	@Override
	public void onDisable(){
		PluginDescriptionFile pdfFile = this.getDescription();
		logger.info(pdfFile.getName() + " Has Been Disabled!");
		saveConfig(outConfig, configPath);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		
		switch(cmd.getName().toLowerCase()){
			case "loadflags":
				if(!sender.hasPermission("customflags.admin")){
					sender.sendMessage(ChatColor.RED + "You don't have permission.");
					return true;
				}
				else{
					FlagManager.reloadFlags();
				}					
		}
		
		return true;
	}
	
//	@EventHandler(priority = EventPriority.LOWEST)
//	public void onEntityChangeBlock(EntityChangeBlockEvent event){
//		System.out.println("change");
//		if(shouldCancelForTnt(event.getBlock())){
//			event.setCancelled(true);
//		}	
//	}
//	
//	@EventHandler(priority = EventPriority.LOWEST)
//	public void onPlayerInteract(PlayerInteractEvent event)
//	{
//		System.out.println("interact");
//		
//		if(event.getItem() != null && event.getItem().getType() == Material.FLINT_AND_STEEL && shouldCancelForTnt(event.getClickedBlock())){
//			event.setCancelled(true);
//		}
//	}
//	
//	@EventHandler(priority = EventPriority.LOWEST)
//	public void onPowered(BlockRedstoneEvent event){
//		System.out.println("powered");
//
//	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onLoseHunger(FoodLevelChangeEvent event){
		if(event.getEntity() instanceof Player){
			Player p = (Player) event.getEntity();
			//if their food is going down
			if(event.getFoodLevel() < p.getFoodLevel()){
				if(isFlagApplicable(p.getLocation(), nohunger)){
					event.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerDamage(EntityDamageByEntityEvent event){
		if(event.getEntity() instanceof Player){
			Player p = (Player) event.getEntity();
			if(isFlagApplicable(p.getLocation(), nomobdamage)){
				event.setDamage(0);
			}
		}
	}
	
	private boolean isFlagApplicable(Location loc, String flag){
		RegionQuery quary = wg.getRegionContainer().createQuery();
		ApplicableRegionSet set = quary.getApplicableRegions(loc);
		
		for(ProtectedRegion region : set){
			if(FlagManager.hasFlag(loc.getWorld().getName(), region.getId(), flag))
				return true;
		}
		
		return false;
	}
	
//	private boolean shouldCancelForTnt(Block b){
//		if(b.getType() != Material.TNT){
//			return false;
//		}
//		Location loc = b.getLocation();
//		if(isFlagApplicable(loc, notntignition)){
//			return true;
//		}
//		return false;
//	}
}
