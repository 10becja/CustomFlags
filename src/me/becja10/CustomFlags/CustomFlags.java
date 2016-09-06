package me.becja10.CustomFlags;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TravelAgent;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldguard.bukkit.RegionQuery;
import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class CustomFlags extends JavaPlugin implements Listener{

	public static CustomFlags instance;
	public final static Logger logger = Logger.getLogger("Minecraft");
	
	private WorldGuardPlugin wg = WGBukkit.getPlugin();
	private Random rng = new Random(System.currentTimeMillis());
	
	private String configPath;
	private FileConfiguration config;
	private FileConfiguration outConfig;
	
	private List<String> eligableBlocks; private String _eligableBlocks = "randomores.Blocks eligable for random ores";
	
	private double explodeChance; private String _explodeChance = "randomores.Chance ore will drop.Explosion";
	private double emeraldChance; private String _emeraldChance = "randomores.Chance ore will drop.Emerald";
	private double diamondChance; private String _diamondChance = "randomores.Chance ore will drop.Diamond";
	private double lapisChance;   private String _lapisChance   = "randomores.Chance ore will drop.Lapis";
	private double redstoneChance;private String _redstoneChance= "randomores.Chance ore will drop.Redstone";
	private double goldChance;    private String _goldChance    = "randomores.Chance ore will drop.Gold";
	private double ironChance;    private String _ironChance    = "randomores.Chance ore will drop.Iron";
	private double coalChance;    private String _coalChance    = "randomores.Chance ore will drop.Coal";
	
	private double diamondAxe; private String _diamondAxe = "randomores.Tool Effectiveness.DiamondPickAxe";
	private double goldAxe;    private String _goldAxe    = "randomores.Tool Effectiveness.GoldPickAxe";
	private double ironAxe;    private String _ironAxe    = "randomores.Tool Effectiveness.IronPickAxe";
	private double stoneAxe;   private String _stoneAxe   = "randomores.Tool Effectiveness.StonePickAxe";
	private double woodAxe;    private String _woodAxe    = "randomores.Tool Effectiveness.WoodPickAxe";

	private double fort1; private String _fort1 = "randomores.Enchantment Boost.Fortune1";
	private double fort2; private String _fort2 = "randomores.Enchantment Boost.Fortune2";
	private double fort3; private String _fort3 = "randomores.Enchantment Boost.Fortune3";


	
	
	private final String nohunger = "nohunger";
	private final String nomobdamage = "nomobdamage";
	private final String randomores = "randomores";
	private final String noportals = "noportals";
	private final String noteleport = "noteleport";
	
	private void loadConfig(){
		configPath = this.getDataFolder().getAbsolutePath() + File.separator + "config.yml";
		config = YamlConfiguration.loadConfiguration(new File(configPath));
		outConfig = new YamlConfiguration();	
		
		List<String> tempBlocks = new ArrayList<String>();
		tempBlocks.add("STONE");		
		if(config.contains(_eligableBlocks))
			eligableBlocks = config.getStringList(_eligableBlocks);
		else
			eligableBlocks = tempBlocks;
		
	
		explodeChance = config.getDouble(_explodeChance, 0.1);
		emeraldChance = config.getDouble(_emeraldChance, 1);
		diamondChance = config.getDouble(_diamondChance, 5);
		lapisChance   = config.getDouble(_lapisChance, 5);
		redstoneChance= config.getDouble(_redstoneChance, 10);
		goldChance    = config.getDouble(_goldChance, 10);
		ironChance    = config.getDouble(_ironChance, 15);
		coalChance    = config.getDouble(_coalChance, 20);
		
		goldAxe    = config.getDouble(_goldAxe, 100);
		diamondAxe = config.getDouble(_diamondAxe, 20);
		ironAxe    = config.getDouble(_ironAxe, 0);
		stoneAxe   = config.getDouble(_stoneAxe, -10);
		woodAxe    = config.getDouble(_woodAxe, -20);
		
		fort1 = config.getDouble(_fort1, 33);
		fort2 = config.getDouble(_fort2, 75);
		fort3 = config.getDouble(_fort3, 120);
		
		outConfig.set(_eligableBlocks, eligableBlocks);
		
		outConfig.set(_explodeChance, explodeChance);
		outConfig.set(_emeraldChance, emeraldChance);
		outConfig.set(_diamondChance, diamondChance);
		outConfig.set(_lapisChance, lapisChance);
		outConfig.set(_redstoneChance, redstoneChance);
		outConfig.set(_goldChance, goldChance);
		outConfig.set(_ironChance, ironChance);
		outConfig.set(_coalChance, coalChance);
		
		outConfig.set(_goldAxe, goldAxe);
		outConfig.set(_diamondAxe, diamondAxe);
		outConfig.set(_ironAxe, ironAxe);
		outConfig.set(_stoneAxe, stoneAxe);
		outConfig.set(_woodAxe, woodAxe);
		
		outConfig.set(_fort1, fort1);
		outConfig.set(_fort2, fort2);
		outConfig.set(_fort3, fort3);

		saveConfig(outConfig, configPath);
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
					loadConfig();
				}					
		}
		
		return true;
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onBreak(BlockBreakEvent event){
		Block block = event.getBlock();
		if(isFlagApplicable(block.getLocation(), randomores) && eligableBlocks.contains(block.getType().toString())){
			Player p = event.getPlayer();
			ItemStack inHand = p.getInventory().getItemInMainHand();
			ItemStack toDrop = null;
			
			//figure out what block to drop
			Material mat = Material.STONE;
			OreDrop drop = new OreDrop(rng.nextDouble() * 100);
			double ticker = 0;
			if(drop.isBetween(ticker, ticker += explodeChance)){
				p.sendMessage(ChatColor.YELLOW + "Sparks from your tool ignited built up gas!!");
				//block.getWorld().createExplosion(block.getLocation(), 5.0f, true);
				return;
			}
			else if(drop.isBetween(ticker, ticker += emeraldChance)){
				mat = Material.EMERALD_ORE;
			}
			else if(drop.isBetween(ticker, ticker += diamondChance)){
				mat = Material.DIAMOND_ORE; 
			}
			else if(drop.isBetween(ticker, ticker += lapisChance)){
				mat = Material.LAPIS_ORE;
			}
			else if(drop.isBetween(ticker, ticker += redstoneChance)){
				mat = Material.REDSTONE_ORE;
			}
			else if(drop.isBetween(ticker, ticker += goldChance)){
				mat = Material.GOLD_ORE;
			}
			else if(drop.isBetween(ticker, ticker += ironChance)){
				mat = Material.IRON_ORE;
			}
			else if(drop.isBetween(ticker, ticker += coalChance)){
				mat = Material.COAL_ORE;
			}
						
			double toolBonus = 100;
			switch(inHand.getType()){
				case DIAMOND_AXE:
					toolBonus += diamondAxe;
					break;
				case GOLD_AXE:
					toolBonus += goldAxe;
					break;
				case IRON_AXE:
					toolBonus += ironAxe;
					break;					
				case STONE_AXE:
					toolBonus += stoneAxe;
					break;
				case WOOD_AXE:
					toolBonus += woodAxe;
					break;
				default:
					break;
			}
						
			double enchantBonus = 100;			
			if(inHand.containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS)){
				switch(inHand.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS)){
					case 1:
						enchantBonus += fort1;
						break;
					case 2:
						enchantBonus += fort2;
						break;
					case 3:
						enchantBonus += fort3;
						break;
					default: 
						break;
				}
			}
			
			double toolLuck = toolBonus /100, enchantLuck = enchantBonus/100;
			
			double luck = toolLuck * enchantLuck;
			
			int numDrops = 0;
			double luckLeft = luck;
			
			//figure out how many extra drops we should add
			while(luckLeft > 0){
				double luckThisRound = Math.min(1.00, luckLeft);
				luckLeft -= luckThisRound;
				
				if(luckThisRound > rng.nextDouble())
					numDrops++;
			}
			System.out.println(drop.value + " " + mat + " " + numDrops);			
			if(numDrops == 0 || mat == Material.STONE)
				return;
			
			toDrop = new ItemStack(mat, numDrops);
			block.setType(Material.AIR);
			block.getWorld().dropItemNaturally(block.getLocation(), toDrop);
		}
	}
	
	
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
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onTravelByPortal(PlayerPortalEvent event){
		if(isFlagApplicable(event.getTo(), noportals)){
			TravelAgent agent = event.getPortalTravelAgent();
			Location to = agent.findPortal(event.getTo());
			if(to == null){//it can't find a portal, so it's going to try and create one.
				event.setTo(event.getTo().getWorld().getSpawnLocation());
				agent.setCanCreatePortal(false);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onTeleport(PlayerTeleportEvent event){
		if(event.getPlayer().hasPermission("customflags.bypass.noteleport"))
			return;
		TeleportCause cause = event.getCause();
		if(cause == TeleportCause.COMMAND || cause == TeleportCause.PLUGIN){
			Location to = event.getTo();
			Location from = event.getFrom();
			if(isFlagApplicable(to, noteleport) || isFlagApplicable(from, noteleport)){
				event.getPlayer().sendMessage(ChatColor.RED + "Teleporting to/from this area is not allowed!");
				event.setCancelled(true);
				return;
			}
		}
	}
	
	private boolean isFlagApplicable(Location loc, String flag){
		if(FlagManager.hasFlag(loc.getWorld().getName(), "__global__", flag))
			return true;
		
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
	
	
	class OreDrop{
		double value;
		public OreDrop(double value){
			this.value = value;
		}
		public boolean isBetween(double one, double two){
			return value >= one && value < two;
		}
	}
}
