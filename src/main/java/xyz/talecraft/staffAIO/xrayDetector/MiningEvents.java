package xyz.talecraft.staffAIO.xrayDetector;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.talecraft.staffAIO.StaffAIO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

public class MiningEvents implements Listener {

	Plugin plugin;

	public HashMap<Player, Long> miningTimestamps = new HashMap<>(); // Map of unix timestamps for each player
	public HashMap<Player, HashMap<String, Integer>> oresMined = new HashMap<>(); // Map of each block the player has broken

	private final int miningTimeframe;
	private final HashMap<String, Integer> blocksToCheck;

	public MiningEvents(int miningTimeframe, HashMap<String, Integer> blocksToCheck) {
		// Variable initialization
		this.plugin = JavaPlugin.getPlugin(StaffAIO.class);
		this.miningTimeframe = miningTimeframe;
		this.blocksToCheck = blocksToCheck;

		// Adding all players to the hash maps
		for(Player player : Bukkit.getServer().getOnlinePlayers()) {
			addPlayer(player);
		}
	}

	// Events
	/**
	 * Player join event.
	 * Adds player to the respective hash maps.
	 * @param event Join Event
	 */
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		addPlayer(player);
	}

	/**
	 * Player quit event.
	 * Removes player from the respective hash maps.
	 * @param event Quit Event
	 */
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();

		removePlayer(player);
	}

	/**
	 * Block break event.
	 * Checks the amount of blocks a player has broken in a predefined timeframe.
	 * If it is greater than the threshold, it will be logged
	 * @param event Break Event
	 */
	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();
		String materialName = block.getBlockData().getMaterial().name().toLowerCase();

		// Checking if the block is part of the blocks to check
		if(!blocksToCheck.containsKey(materialName)) return;

		// Getting unix timestamps for checking
		long currentTime = System.currentTimeMillis() / 1000L;
		long lastMined = miningTimestamps.get(player);

		// Checking if the mined time is greater than the threshold
		// If so, it's reset to the current time and the blocks mined is cleared
		long minedTime = currentTime - lastMined;
		if(minedTime > miningTimeframe) {
			miningTimestamps.put(player, currentTime);
			oresMined.get(player).clear();
		}

		// Adding the block to the blocks the player has broken
		if(!oresMined.get(player).containsKey(materialName)) {
			oresMined.get(player).put(materialName, 1);
		} else {
			oresMined.get(player).compute(materialName, (name, mined) -> mined + 1);
		}

		// Checking if the mined ore goes above the threshold
		int oreMinedAmount = oresMined.get(player).get(materialName);

		if(oreMinedAmount >= blocksToCheck.get(materialName)) {
			plugin.getLogger().log(Level.INFO, player.getName() + " has reached the mining threshold for " + materialName);
			StaffAIO.staffManager.sendMiningReport(player, block, oreMinedAmount);
		}
	}

	// Methods
	public void addPlayer(Player player) {
		miningTimestamps.put(player, 0L);
		oresMined.put(player, new HashMap<>());
	}

	public void removePlayer(Player player) {
		miningTimestamps.remove(player);
		oresMined.remove(player);
	}
}
