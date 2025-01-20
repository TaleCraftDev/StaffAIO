package xyz.talecraft.staffAIO.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.talecraft.staffAIO.StaffAIO;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Alerts implements CommandExecutor, TabCompleter {

	Plugin plugin;

	public Alerts() {
		this.plugin = JavaPlugin.getPlugin(StaffAIO.class);
	}

	@Override
	public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
		if(!(commandSender instanceof Player player)) return false;

		String alertPrefix = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getConfig().getString("alert_prefix")));

		if(args.length == 0 || args.length == 1) return false;

		if(Objects.equals(args[0], "toggle")) {
			switch (args[1]) {
				case "mining":
					boolean enabled = StaffAIO.staffManager.toggleMiningAlert(player);

					if(enabled) {
						player.sendMessage(alertPrefix + "Mining alerts have been enabled");
					} else {
						player.sendMessage(alertPrefix + "Mining alerts have been disabled");
					}

					return true;

				default:
					return false;
			}
		}

		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender commandSender, Command command, String label, String[] args) {
		List<String> retArgs = new ArrayList<>();

		if(args.length == 1) {
			retArgs.add("toggle");
		}

		if(args.length == 2) {
			if (args[0].equals("toggle")) {
				retArgs.add("mining");
			}
		}

		return retArgs;
	}
}
