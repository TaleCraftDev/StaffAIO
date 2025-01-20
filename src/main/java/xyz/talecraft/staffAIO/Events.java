package xyz.talecraft.staffAIO;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class Events implements Listener {

	Plugin plugin;

	public Events() {
		this.plugin = JavaPlugin.getPlugin(StaffAIO.class);
	}

	// Events
	/**
	 * Player join event.
	 * Adds player to the staff list if they have given permission.
	 * @param event Join event
	 */
	@EventHandler
	public void joinEvent(PlayerJoinEvent event) {
		// Checking if player is staff
		// If so, add them to the staff list
		String staffPermission = plugin.getConfig().getString("staff_permission");
		String alertPermission = plugin.getConfig().getString("alert_permission");

		if(staffPermission == null) {
			plugin.getLogger().log(Level.SEVERE, "Staff permission unset");
			return;
		}

		if(alertPermission == null) {
			plugin.getLogger().log(Level.SEVERE, "Alert permission unset");
			return;
		}

		Player player = event.getPlayer();

		if(player.hasPermission(staffPermission)) {
			StaffAIO.staffManager.addStaffMember(event.getPlayer(), player.hasPermission(alertPermission));
		}
	}

	/**
	 * Player quit event.
	 * Removes staff from the staff list
	 * @param event Quit event
	 */
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		// Checking if player is staff
		// If so, add them to the staff list
		String staffPermission = plugin.getConfig().getString("staff_permission");

		if(staffPermission == null) {
			plugin.getLogger().log(Level.SEVERE, "Staff permission unset");
			return;
		}

		Player player = event.getPlayer();

		if(player.hasPermission(staffPermission)) {
			StaffAIO.staffManager.removeStaffMember(player);
		}
	}

}
