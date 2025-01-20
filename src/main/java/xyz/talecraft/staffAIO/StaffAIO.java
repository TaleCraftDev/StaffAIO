package xyz.talecraft.staffAIO;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.talecraft.staffAIO.commands.Alerts;
import xyz.talecraft.staffAIO.xrayDetector.MiningEvents;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.logging.Level;

public final class StaffAIO extends JavaPlugin {

	public static StaffManager staffManager = new StaffManager();
	public static ConfigManager xrayConfig = new ConfigManager();

	@Override
	public void onEnable() {
		// Saving plugin config
		saveDefaultConfig();

		// Permissions
		String staffPermission = getConfig().getString("staff_permission");
		String alertPermission = getConfig().getString("alert_permission");

		if(staffPermission == null) {
			getLogger().log(Level.SEVERE, "Staff permission unset");
			return;
		}

		if(alertPermission == null) {
			getLogger().log(Level.SEVERE, "Alert permission unset");
			return;
		}

		// Initializing staff manager
		staffManager.init();

		// Adding valid players to staff manager
		// Only done plugin is actively reloaded
		for(Player player : getServer().getOnlinePlayers()) {
			if(player.hasPermission(staffPermission)) staffManager.addStaffMember(player, player.hasPermission(alertPermission));
		}

		// Initializing component configs
		try {
			String dataPath = getDataFolder().getAbsolutePath();

			xrayConfig.init("xray_config.yml", dataPath);
		} catch (IOException | InvalidConfigurationException e) {
			throw new RuntimeException(e);
		}

		// Xray detection
		// Getting timeframe to check
		int miningTimeframe = xrayConfig.getConfig().getInt("timeframe");

		// Getting the blocks to check and thresholds
		ArrayList<String> blocksToCheck = (ArrayList<String>) xrayConfig.getConfig().getStringList("ores");
		HashMap<String, Integer> thresholds = new HashMap<>();

		for(String blockName : blocksToCheck) {
			thresholds.put(blockName, xrayConfig.getConfig().getInt("thresholds." + blockName));
		}

		// Registering xray events
		getServer().getPluginManager().registerEvents(new MiningEvents(miningTimeframe, thresholds), this);

		// Registering commands
		Objects.requireNonNull(getCommand("alerts")).setExecutor(new Alerts());
		Objects.requireNonNull(getCommand("alerts")).setTabCompleter(new Alerts());
	}

	@Override
	public void onDisable() { }
}
