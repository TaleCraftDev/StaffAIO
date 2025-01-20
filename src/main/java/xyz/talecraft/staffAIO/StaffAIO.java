package xyz.talecraft.staffAIO;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.talecraft.staffAIO.xrayDetector.MiningEvents;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public final class StaffAIO extends JavaPlugin {

	ConfigManager xrayConfig;

	@Override
	public void onEnable() {
		// Saving plugin config
		saveDefaultConfig();

		// Initializing component configs
		try {
			String dataPath = getDataFolder().getAbsolutePath();

			xrayConfig = new ConfigManager("xray_config.yml", dataPath);
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
	}

	@Override
	public void onDisable() { }
}
