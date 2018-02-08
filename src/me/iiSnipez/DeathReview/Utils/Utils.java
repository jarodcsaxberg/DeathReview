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

package me.iiSnipez.DeathReview.Utils;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import me.iiSnipez.DeathReview.DeathReview;
import me.iiSnipez.DeathReview.Listeners.EntityDamageByEntityListener;
import me.iiSnipez.DeathReview.Listeners.EntityDamageListener;
import me.iiSnipez.DeathReview.Listeners.EntityRegainHealthListener;
import me.iiSnipez.DeathReview.Listeners.PlayerDeathListener;
import me.iiSnipez.DeathReview.Listeners.PlayerRespawnListener;

public class Utils {

	private DeathReview plugin;

	public Utils(DeathReview plugin) {
		this.plugin = plugin;
	}

	public void logMsg(Level level, String message) {
		Logger.getLogger("Minecraft").log(level, message);
	}

	public String colorText(String message) {
		return ChatColor.translateAlternateColorCodes('&', message);
	}

	public void registerAllEvents(DeathReview dr) {
		Bukkit.getServer().getPluginManager().registerEvents(new EntityDamageByEntityListener(dr), plugin);
		Bukkit.getServer().getPluginManager().registerEvents(new EntityRegainHealthListener(dr), plugin);
		Bukkit.getServer().getPluginManager().registerEvents(new EntityDamageListener(dr), plugin);
		Bukkit.getServer().getPluginManager().registerEvents(new PlayerDeathListener(dr), plugin);
		Bukkit.getServer().getPluginManager().registerEvents(new PlayerRespawnListener(dr), plugin);
	}

	public void getValues() {
		plugin.updateCheck = plugin.co.getConfig().getBoolean("UpdateCheck");
		plugin.alertDuration = plugin.co.getConfig().getInt("AlertDuration");
		plugin.maxDataAmount = plugin.co.getConfig().getInt("MaximumDataStored");
		plugin.header = plugin.co.getConfig().getString("Header");
		plugin.recapHeader = plugin.co.getConfig().getString("RecapHeader");
		plugin.damageMessage = plugin.co.getConfig().getString("DamageMessage");
		plugin.damagePreventedMessage = plugin.co.getConfig().getString("DamagePreventedMessage");
		plugin.healingMessage = plugin.co.getConfig().getString("HealingMessage");
		plugin.footer = plugin.co.getConfig().getString("Footer");
		plugin.useMetrics = plugin.co.getConfig().getBoolean("Metrics");
	}

	public void enableTimer() {
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			public void run() {
				Iterator<Map.Entry<String, Long>> iter = plugin.taggedPlayers.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry<String, Long> c = iter.next();
					if ((getTime() - c.getValue().longValue()) >= plugin.alertDuration) {
						iter.remove();
						plugin.recapDamage.remove(c.getKey());
						plugin.recapHeal.remove(c.getKey());
					}
				}
			}
		}, 0L, 20L);
	}

	public Long getTime() {
		return System.currentTimeMillis() / 1000;
	}

	public void tagPlayer(Player player) {
		String name = player.getName();
		if (!plugin.taggedPlayers.containsKey(name)) {
			plugin.taggedPlayers.put(name, getTime());
		} else {
			plugin.taggedPlayers.remove(name);
			plugin.taggedPlayers.put(name, getTime());
		}
	}

	public void addDamageData(long time, String name, String source, double amount, double originalAmount) {
		if (plugin.taggedPlayers.containsKey(name)) {
			if (plugin.recapDamage.containsKey(name)) {
				ArrayList<String> data = plugin.recapDamage.get(name);
				data.add(time + ":" + source + ":" + amount + ":" + originalAmount);
				plugin.recapDamage.remove(name);
				plugin.recapDamage.put(name, data);
			} else {
				ArrayList<String> start = new ArrayList<String>();
				start.add(time + ":" + source + ":" + amount + ":" + originalAmount);
				plugin.recapDamage.put(name, start);
			}
			if(plugin.recapDamage.get(name).size() > plugin.maxDataAmount) {
				plugin.recapDamage.get(name).remove(0);
			}
		}
	}

	public void addHealData(long time, String name, String source, double amount) {
		if (plugin.taggedPlayers.containsKey(name)) {
			if (plugin.recapHeal.containsKey(name)) {
				ArrayList<String> data = plugin.recapHeal.get(name);
				data.add(time + ":" + source + ":" + amount);
				plugin.recapHeal.remove(name);
				plugin.recapHeal.put(name, data);
			} else {
				ArrayList<String> start = new ArrayList<String>();
				start.add(time + ":" + source + ":" + amount);
				plugin.recapHeal.put(name, start);
			}
			if(plugin.recapHeal.get(name).size() > plugin.maxDataAmount) {
				plugin.recapHeal.get(name).remove(0);
			}
		}
	}
	
	public void buildRecap(Player player) {
		String name = player.getName();
		ArrayList<String> messages = new ArrayList<String>();
		
		if (plugin.recapDamage.containsKey(name)) {
			float total = 0;
			float totalOriginal = 0;
			ArrayList<String> list = plugin.recapDamage.get(name);
			HashMap<String, Float> sources = new HashMap<String, Float>();
			
			for (String entry : list) {
				String[] data = entry.split(":");
				total += Float.parseFloat(data[2]);
				totalOriginal += Float.parseFloat(data[3]);
				if (sources.containsKey(data[1])) {
					float totalPerSource = sources.get(data[1]);
					float runningTotal = totalPerSource + Float.parseFloat(data[2]);
					sources.remove(data[1]);
					sources.put(data[1], runningTotal);
				} else {
					sources.put(data[1], Float.parseFloat(data[2]));
				}
			}
			if(100 - (total / totalOriginal * 100) > 0)
				messages.add(plugin.damagePreventedMessage.replaceAll("<percent>", formatDec(100 - (total / totalOriginal * 100)) + ""));
			for (Entry<String, Float> entry : sources.entrySet()) {
				float percent = (entry.getValue()) / total * 100;
				messages.add(plugin.damageMessage.replaceAll("<source>", entry.getKey())
						.replaceAll("<amount>", formatDec(entry.getValue()))
						.replaceAll("<total>", formatDec(total))
						.replaceAll("<percent>", formatDec(percent)));
			}
			plugin.recapDamage.remove(name);
		}
		
		if(plugin.recapHeal.containsKey(name)) {
			float total = 0;
			ArrayList<String> list = plugin.recapHeal.get(name);
			HashMap<String, Float> sources = new HashMap<String, Float>();
			
			for (String entry : list) {
				String[] data = entry.split(":");
				total += Float.parseFloat(data[2]);
				
				if (sources.containsKey(data[1])) {
					float totalPerSource = sources.get(data[1]);
					float runningTotal = totalPerSource + Float.parseFloat(data[2]);
					sources.remove(data[1]);
					sources.put(data[1], runningTotal);
				} else {
					sources.put(data[1], Float.parseFloat(data[2]));
				}
			}
			for (Entry<String, Float> entry : sources.entrySet()) {
				float percent = (entry.getValue()) / total * 100;
				messages.add(plugin.healingMessage.replaceAll("<source>", entry.getKey())
						.replaceAll("<amount>", formatDec(entry.getValue()))
						.replaceAll("<total>", formatDec(total))
						.replaceAll("<percent>", formatDec(percent)));
			}
		}
		plugin.recapMessage.put(name, messages);
	}

	public String formatDec(float i) {
		DecimalFormat df = new DecimalFormat("#.##");
		df.setRoundingMode(RoundingMode.CEILING);
		return df.format(i);
	}
	
}
