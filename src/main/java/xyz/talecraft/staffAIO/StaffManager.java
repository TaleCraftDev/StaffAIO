package xyz.talecraft.staffAIO;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Objects;

public class StaffManager {
	Plugin plugin;

	// List of staff members
	private ArrayList<Player> staffMembers = new ArrayList<>();

	// Reports Enabled
	private ArrayList<Player> miningReportsEnabled = new ArrayList<>();

	public void init() {
		this.plugin = JavaPlugin.getPlugin(StaffAIO.class);
	}

	// Adding and Removing Members

	/**
	 * Adds a player to the staff list and subscribes them for reports.
	 * @param player Staff member to add.
	 */
	public void addStaffMember(Player player, boolean subscribeAlerts) {
		staffMembers.add(player);

		if(subscribeAlerts) {
			// Subscribing the player for all reports
			miningReportsEnabled.add(player);
		}
	}

	public void removeStaffMember(Player player) {
		staffMembers.remove(player);

		miningReportsEnabled.remove(player);
	}

	// Report Togglers

	/**
	 * Toggles if the player has mining alerts enabled.
	 * If the player is not a valid staff member, they will be added.
	 * @param player Player to check.
	 * @return If the player has the mining alerts toggled.
	 */
	public boolean toggleMiningAlert(Player player) {
		if(!staffMembers.contains(player)) {
			addStaffMember(player, true);
		}

		if(miningReportsEnabled.contains(player)) {
			miningReportsEnabled.remove(player);
		} else {
			miningReportsEnabled.add(player);
		}

		return miningReportsEnabled.contains(player);
	}

	// Report Senders
	/**
	 * Sends a mining report to subscribed staff members.
	 * @param target The suspicious player.
	 * @param block The block being mined.
	 */
	public void sendMiningReport(Player target, Block block, int amount) {
		ConfigManager xrayConfig = StaffAIO.xrayConfig;

		boolean realtimeAlerts = xrayConfig.getConfig().getBoolean("alerts.enabled");
		if(!realtimeAlerts) return;

		String alertPrefix = plugin.getConfig().getString("alert_prefix");
		String alertMessage = ChatColor.translateAlternateColorCodes('&', alertPrefix + Objects.requireNonNull(xrayConfig.getConfig().getString("alerts.message")));

		// Replacing placeholders
		Location blockLocation = block.getLocation();

		alertMessage = alertMessage.replaceAll("%player%", target.getName())
				.replaceAll("%count%", String.valueOf(amount))
				.replaceAll("%block%", block.getBlockData().getMaterial().toString())
				.replaceAll("%x%", String.valueOf(blockLocation.getBlockX()))
				.replaceAll("%y%", String.valueOf(blockLocation.getBlockY()))
				.replaceAll("%z%", String.valueOf(blockLocation.getBlockZ()))
				.replaceAll("%world%", Objects.requireNonNull(blockLocation.getWorld()).getName());

		for(Player player : miningReportsEnabled) {
			player.sendMessage(alertMessage);
		}
	}
}
