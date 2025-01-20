package xyz.talecraft.staffAIO;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class ConfigManager {

	File file;
	FileConfiguration config;

	Plugin plugin;

	/**
	 * Initializes the config manager.
	 * @param name Name of the config file. Must exist in resources to be copied in case it doesn't exist.
	 * @param dataFolder Path to the plugin's config folder.
	 */
	public void init(String name, String dataFolder) throws IOException, InvalidConfigurationException {
		// Variable initialization
		this.plugin = JavaPlugin.getPlugin(StaffAIO.class);
		this.file = new File(dataFolder, name);

		// Saving config file
		plugin.saveResource(file.getName(), false);

		// Setting up file config
		config = new YamlConfiguration();
		config.load(file);
	}

	/**
	 * Gets the configuration
	 * @return File config
	 */
	public FileConfiguration getConfig() {
		return config;
	}

}
