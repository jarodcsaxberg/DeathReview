/*
 *  Minecraft plugin that shows players the damage they took before dying.
 *  
 *  Copyright (C) 2018 Jarod Saxberg (iiSnipez)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package me.iiSnipez.DeathReview.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import me.iiSnipez.DeathReview.DeathReview;

public class CommandExec implements CommandExecutor {

	private DeathReview plugin;

	public CommandExec(DeathReview plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
		if (cmdLabel.equalsIgnoreCase("deathreview") || cmdLabel.equalsIgnoreCase("dr")) {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				if (args.length == 0) {
					player.sendMessage(
							plugin.utils.colorText("&8================&6[&cDeathReview&6]&8================"));
					player.sendMessage(plugin.utils.colorText("&cDeveloped by &6iiSnipez&c."));
					player.sendMessage(plugin.utils.colorText("&cVersion: &6" + plugin.getDescription().getVersion()));
					if (player.isOp() && plugin.updateAvailable)
						player.sendMessage(plugin.utils.colorText("&aUpdate available!"));
					player.sendMessage(plugin.utils.colorText("&cFor more information, visit the &6Spigot &cpage."));
					player.sendMessage(plugin.utils.colorText("&6" + plugin.getDescription().getWebsite()));
					player.sendMessage(plugin.utils.colorText("&8==========================================="));
				} else if (args[0].equalsIgnoreCase("reload") && args.length == 1) {
					if (player.isOp() || player.hasPermission("deathreview.reload")) {
						plugin.loadConfig();
						player.sendMessage(plugin.utils.colorText("&6[&cDeathReview&6] &aConfig reloaded successfully."));
					}
				}
			} else if (sender instanceof ConsoleCommandSender) {
				if (args.length == 0) {
					sender.sendMessage(
							plugin.utils.colorText("&8================&6[&cDeathReview&6]&8================"));
					sender.sendMessage(plugin.utils.colorText("&cDeveloped by &6iiSnipez&c."));
					sender.sendMessage(plugin.utils.colorText("&cVersion: &6" + plugin.getDescription().getVersion()));
					if (plugin.updateAvailable)
						sender.sendMessage(plugin.utils.colorText("&aUpdate available!"));
					sender.sendMessage(plugin.utils.colorText("&cFor more information, visit the &6Spigot &cpage."));
					sender.sendMessage(plugin.utils.colorText("&6" + plugin.getDescription().getWebsite()));
					sender.sendMessage(plugin.utils.colorText("&8============================================="));
				} else if (args[0].equalsIgnoreCase("reload") && args.length == 1) {
					plugin.loadConfig();
					sender.sendMessage(plugin.utils.colorText("&6[&cDeathReview&6] &aConfig reloaded successfully."));
				}
			}
		}
		return false;
	}

}
