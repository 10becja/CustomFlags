package me.becja10.CustomFlags;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TravelAgent;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

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
	
	private HashMap<Location, Block> boneMealBlocks = new HashMap<Location, Block>();
	private HashSet<UUID> droppedEggs = new HashSet<UUID>();
	
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
	
	private double diamondPick; private String _diamondPick = "randomores.Tool Effectiveness.DiamondPickAxe";
	private double goldPick;    private String _goldPick    = "randomores.Tool Effectiveness.GoldPickAxe";
	private double ironPick;    private String _ironPick    = "randomores.Tool Effectiveness.IronPickAxe";
	private double stonePick;   private String _stonePick   = "randomores.Tool Effectiveness.StonePickAxe";
	private double woodPick;    private String _woodPick    = "randomores.Tool Effectiveness.WoodPickAxe";

	private double fort1; private String _fort1 = "randomores.Enchantment Boost.Fortune1";
	private double fort2; private String _fort2 = "randomores.Enchantment Boost.Fortune2";
	private double fort3; private String _fort3 = "randomores.Enchantment Boost.Fortune3";
	private double goldBoost; private String _goldBoost = "randomores.Enchantment Boost.GoldPickAxe";

	
	
	private final String nohunger = "nohunger";
	private final String nomobdamage = "nomobdamage";
	private final String randomores = "randomores";
	private final String noportals = "noportals";
	private final String noteleport = "noteleport";
	private final String noplants = "noplants";
	private final String noanimals = "noanimals";
	
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
		
	
		explodeChance = config.getDouble(_explodeChance, 0.01);
		emeraldChance = config.getDouble(_emeraldChance, 0.1);
		diamondChance = config.getDouble(_diamondChance, 0.5);
		lapisChance   = config.getDouble(_lapisChance, 0.5);
		redstoneChance= config.getDouble(_redstoneChance, 1);
		goldChance    = config.getDouble(_goldChance, 2.5);
		ironChance    = config.getDouble(_ironChance, 5);
		coalChance    = config.getDouble(_coalChance, 10);
		
		goldPick    = config.getDouble(_goldPick, 100);
		diamondPick = config.getDouble(_diamondPick, 20);
		ironPick    = config.getDouble(_ironPick, 0);
		stonePick   = config.getDouble(_stonePick, -10);
		woodPick    = config.getDouble(_woodPick, -20);
		
		fort1 = config.getDouble(_fort1, 33);
		fort2 = config.getDouble(_fort2, 75);
		fort3 = config.getDouble(_fort3, 120);
		goldBoost = config.getDouble(_goldBoost, 100);
		
		outConfig.set(_eligableBlocks, eligableBlocks);
		
		outConfig.set(_explodeChance, explodeChance);
		outConfig.set(_emeraldChance, emeraldChance);
		outConfig.set(_diamondChance, diamondChance);
		outConfig.set(_lapisChance, lapisChance);
		outConfig.set(_redstoneChance, redstoneChance);
		outConfig.set(_goldChance, goldChance);
		outConfig.set(_ironChance, ironChance);
		outConfig.set(_coalChance, coalChance);
		
		outConfig.set(_goldPick, goldPick);
		outConfig.set(_diamondPick, diamondPick);
		outConfig.set(_ironPick, ironPick);
		outConfig.set(_stonePick, stonePick);
		outConfig.set(_woodPick, woodPick);
		
		outConfig.set(_fort1, fort1);
		outConfig.set(_fort2, fort2);
		outConfig.set(_fort3, fort3);
		outConfig.set(_goldBoost, goldBoost);

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
					sender.sendMessage(ChatColor.GREEN + "Flags reloaded");
				}
				break;
			case "addflag":
				if(!sender.hasPermission("customflags.addflag")){
					sender.sendMessage(ChatColor.RED + "You don't have permission.");
					return true;
				}
				else{
					String region = "";
					String flag = "";
					String world = "";
					if(args.length >= 2){
						
						if(!(sender instanceof Player) && args.length < 3){
							sender.sendMessage("Must provide world name from console.");
							return true;
						}
						region = args[0];
						flag = args[1];
						world = (args.length == 3) ? args[2] : ((Player)sender).getWorld().getName();
						
						if(Bukkit.getWorld(world) == null){
							sender.sendMessage(ChatColor.RED + "World does not exist.");
						}
						else if(wg.getRegionManager(Bukkit.getWorld(world)).getRegion(region) == null){
							sender.sendMessage(ChatColor.RED + "That region does not exist in this world.");
						}
						else{
							FlagManager.addFlag(world, region, flag);
							sender.sendMessage(ChatColor.GREEN + "Flag added.");
						}						
					}
					else{
						return false;
					}
				}
				break;
		}
		
		return true;
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onBreak(BlockBreakEvent event){
		Player p = event.getPlayer();
		if(p.getGameMode() == GameMode.CREATIVE)
			return;
		Block block = event.getBlock();
		if(isFlagApplicable(block.getLocation(), randomores) && eligableBlocks.contains(block.getType().toString())){
			ItemStack inHand = p.getInventory().getItemInMainHand();
			ItemStack toDrop = null;
			Location loc = block.getLocation();
			
			boolean isWoodAxe = inHand.getType() == Material.WOOD_PICKAXE;
			boolean isStoneAxe = inHand.getType() == Material.STONE_PICKAXE;
			
			boolean silkTouch = inHand.containsEnchantment(Enchantment.SILK_TOUCH);
			
			//Get the shift of chance caused by tool
			double toolShift = 100;
			switch(inHand.getType()){
				case DIAMOND_PICKAXE:
					toolShift += diamondPick;
					break;
				case GOLD_PICKAXE:
					toolShift += goldPick;
					break;
				case IRON_PICKAXE:
					toolShift += ironPick;
					break;					
				case STONE_PICKAXE:
					toolShift += stonePick;
					break;
				case WOOD_PICKAXE:
					toolShift += woodPick;
					break;
				default:
					break;
			}			
			toolShift = toolShift / 100;
			
			//figure out what block to drop
			Material mat = Material.STONE;
			OreDrop drop = new OreDrop(rng.nextDouble() * 100);
			double ticker = 0;
			if(drop.isBetween(ticker, ticker += explodeChance) && !isWoodAxe){
				p.sendMessage(ChatColor.YELLOW + "Sparks from your tool ignited built up gas!!");
				block.getWorld().createExplosion(loc, 5.0f, true);
				return;
			}
			else if(drop.isBetween(ticker, ticker += emeraldChance * toolShift)){
				mat = Material.EMERALD_ORE;
			}
			else if(drop.isBetween(ticker, ticker += diamondChance* toolShift) && !isWoodAxe && !isStoneAxe){
				mat = Material.DIAMOND_ORE; 
			}
			else if(drop.isBetween(ticker, ticker += lapisChance * toolShift)){
				mat = Material.LAPIS_ORE;
			}
			else if(drop.isBetween(ticker, ticker += redstoneChance * toolShift)){
				mat = Material.REDSTONE_ORE;
			}
			else if(drop.isBetween(ticker, ticker += goldChance * toolShift) && !isWoodAxe){
				mat = Material.GOLD_ORE;
			}
			else if(drop.isBetween(ticker, ticker += ironChance * toolShift)){
				mat = Material.IRON_ORE;
			}
			else if(drop.isBetween(ticker, ticker += coalChance * toolShift)){
				mat = Material.COAL_ORE;
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
			
			if(inHand.getType() == Material.GOLD_PICKAXE)
				enchantBonus += goldBoost;
									
			int numDrops = 0;
			double luckLeft = enchantBonus/100;;
			
			//figure out how many extra drops we should add
			while(luckLeft > 0){
				double luckThisRound = Math.min(1.00, luckLeft);
				luckLeft -= luckThisRound;
				
				if(luckThisRound > rng.nextDouble())
					numDrops++;
			}
			if(numDrops == 0 || mat == Material.STONE)
				return;
			
			//Get durability loss
			double chanceOfLoss = 1;
			if(inHand.containsEnchantment(Enchantment.DURABILITY)){
				chanceOfLoss += inHand.getEnchantmentLevel(Enchantment.DURABILITY); 
			}
			
			chanceOfLoss = (100/chanceOfLoss)/100;
			if(rng.nextDouble() <= chanceOfLoss){
				inHand.setDurability((short) (inHand.getDurability() + 1));
			}
			
			event.setCancelled(true);
			
			if(!silkTouch){
				block.setType(mat);
				
				ItemStack fake = new ItemStack(inHand);
				fake.setType(Material.DIAMOND_PICKAXE);
				
				Collection<ItemStack> items = block.getDrops(fake);
				numDrops *= items.size();
				for(ItemStack item : items){
					toDrop = new ItemStack(item);
					break;
				}
				toDrop.setAmount(numDrops);
			}
			
			if(toDrop == null)
				toDrop= new ItemStack(mat, numDrops);
			block.setType(Material.AIR);
			block.getWorld().dropItem(loc.add(0.5, 0.5, 0.5), toDrop).setVelocity(
					new Vector(rng.nextDouble()/10, rng.nextDouble()/10, rng.nextDouble()/10));
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
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInteract(PlayerInteractEvent event){
		Block block = event.getClickedBlock();
		if(block != null && isFlagApplicable(block.getLocation(), noplants)){
			ItemStack item = event.getItem();
			if(item != null && item.getType() == Material.INK_SACK && item.getDurability() == (short)15){ //bonemeal
				boneMealBlocks.put(block.getLocation(), block);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onGrow(BlockGrowEvent event){
		Block block = event.getBlock();
		if(isFlagApplicable(block.getLocation(), noplants)){
			Block b = boneMealBlocks.remove(block.getLocation());
			if(b != null && b.getType() == event.getBlock().getType()){
				return; //don't cancel bonemeal from growing blocks if used by the player
			}
			
			Material newMat = event.getNewState().getType();
			List<Material> good = Arrays.asList(Material.LONG_GRASS, Material.RED_ROSE, Material.YELLOW_FLOWER);
					
			if(good.contains(newMat)){
				return;
			}
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onSpread(BlockSpreadEvent event){
		if(isFlagApplicable(event.getBlock().getLocation(), noplants)){
			Material newMat = event.getNewState().getType();
			List<Material> bad = Arrays.asList(Material.CHORUS_FLOWER);
			
			if(bad.contains(newMat)){
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onFeed(PlayerInteractEntityEvent event){
		Entity thing = event.getRightClicked();
		if(thing != null && isFlagApplicable(thing.getLocation(), noanimals)){
			if(thing instanceof Ageable){
				Ageable age = (Ageable) thing;
				if(age.canBreed())
					age.setBreed(false); //don't allow animals in regions to breed, try to be nice and don't let players feed them
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBreed(CreatureSpawnEvent event){
		if(event.getSpawnReason() == SpawnReason.BREEDING && isFlagApplicable(event.getLocation(), noanimals)){
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onLayEgg(ItemSpawnEvent event){
		Item item = event.getEntity();
		if(droppedEggs.contains(item.getUniqueId())){
			droppedEggs.remove(item.getUniqueId()); //don't cancel dropped items. Will make players mad
			return;
		}
		ItemStack stack = item.getItemStack();
		if(stack.getType() == Material.EGG && isFlagApplicable(event.getLocation(), noanimals)){
			List<Entity> nearby = item.getNearbyEntities(1, 1, 1);
			for(Entity near : nearby){
				if(near instanceof Chicken){
					event.setCancelled(true);
					break;
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDrop(PlayerDropItemEvent event){
		Item item = event.getItemDrop();
		ItemStack stack = item.getItemStack();
		if(stack.getType() == Material.EGG && isFlagApplicable(item.getLocation(), noanimals)){
			droppedEggs.add(item.getUniqueId());
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
