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

package me.iiSnipez.DeathReview.Config;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import me.iiSnipez.DeathReview.DeathReview;

public class ConfigOptions {

	private DeathReview plugin;
	
	public FileConfiguration config = null;
	public File configFile = null;

	public ConfigOptions(DeathReview plugin) {
		this.plugin = plugin;
	}

	public void reloadConfig() {
		if (configFile == null) {
			configFile = new File(plugin.getDataFolder(), "config.yml");
		}
		config = YamlConfiguration.loadConfiguration(configFile);
	}
	
	public FileConfiguration getConfig() {
		if (config == null) {
			reloadConfig();
		}
		return config;
	}

	public void saveConfig() {
		if ((config == null) || (configFile == null)) {
			return;
		}
		try {
			getConfig().save(configFile);
		} catch (IOException ex) {
			plugin.utils.logMsg(Level.SEVERE, "Could not save config to " + configFile);
			plugin.utils.logMsg(Level.SEVERE, ex.getMessage());
		}
	}

	public void saveDefault() {
		if (!configFile.exists()) {
			plugin.saveResource("config.yml", false);
		}
	}
}
