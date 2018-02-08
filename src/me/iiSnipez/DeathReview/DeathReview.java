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

package me.iiSnipez.DeathReview;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import me.iiSnipez.DeathReview.Commands.CommandExec;
import me.iiSnipez.DeathReview.Config.ConfigOptions;
import me.iiSnipez.DeathReview.Utils.Metrics;
import me.iiSnipez.DeathReview.Utils.Updater;
import me.iiSnipez.DeathReview.Utils.Utils;

public class DeathReview extends JavaPlugin {

	public ConfigOptions co;
	public Utils utils;
	public CommandExec commandExec;
	public Metrics m;
	public Updater updater;

	public HashMap<String, Long> taggedPlayers = new HashMap<String, Long>();
	public HashMap<String, ArrayList<String>> recapDamage = new HashMap<String, ArrayList<String>>();
	public HashMap<String, ArrayList<String>> recapHeal = new HashMap<String, ArrayList<String>>();
	public HashMap<String, ArrayList<String>> recapMessage = new HashMap<String, ArrayList<String>>();

	public boolean updateCheck = true;
	public int alertDuration = 0;
	public int maxDataAmount = 0;
	public String header = "";
	public String recapHeader = "";
	public String damageMessage = "";
	public String damagePreventedMessage = "";
	public String healingMessage = "";
	public String footer = "";
	public boolean useMetrics = true;
	public boolean updateAvailable = false;

	@Override
	public void onEnable() {
		initClasses();
		loadConfig();
		utils.getValues();
		utils.registerAllEvents(this);
		utils.enableTimer();
		getCommand("deathreview").setExecutor(commandExec);
		if (useMetrics) {
			m = new Metrics(this);
		}
		utils.logMsg(Level.INFO, "[DeathReview] Started!");
		if (updateCheck) {
			updater = new Updater(this);
			updater.updateCheck();
		}
	}

	public void onDisable() {
		utils.logMsg(Level.INFO, "[DeathReview] Disabled!");
		Bukkit.getScheduler().cancelAllTasks();
	}

	private void initClasses() {
		co = new ConfigOptions(this);
		utils = new Utils(this);
		commandExec = new CommandExec(this);
	}

	public void loadConfig() {
		co.getConfig().options().copyDefaults(true);
		co.saveDefault();
		co.reloadConfig();
		utils.getValues();
	}

}
