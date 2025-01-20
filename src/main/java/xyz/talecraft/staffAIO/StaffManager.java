package xyz.talecraft.staffAIO;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Objects;

public class StaffManager {
	// List of staff members
	private ArrayList<Player> staffMembers = new ArrayList<>();

	// Reports Enabled
	private ArrayList<Player> miningReportsEnabled = new ArrayList<>();

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

		String alertMessage = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(xrayConfig.getConfig().getString("alerts.message")));

		// Replacing placeholders
		Location blockLocation = block.getLocation();

		alertMessage = alertMessage.replaceAll("%player%", target.getName());
		alertMessage = alertMessage.replaceAll("%count%", String.valueOf(amount));
		alertMessage = alertMessage.replaceAll("%block%", block.getBlockData().getMaterial().toString());
		alertMessage = alertMessage.replaceAll("%x%", String.valueOf(blockLocation.getBlockX()));
		alertMessage = alertMessage.replaceAll("%y%", String.valueOf(blockLocation.getBlockY()));
		alertMessage = alertMessage.replaceAll("%z%", String.valueOf(blockLocation.getBlockZ()));
		alertMessage = alertMessage.replaceAll("%world%", blockLocation.getWorld().getName());

		for(Player player : miningReportsEnabled) {
			player.sendMessage(alertMessage);
		}
	}
}
