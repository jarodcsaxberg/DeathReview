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

package me.iiSnipez.DeathReview.Listeners;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;

import me.iiSnipez.DeathReview.DeathReview;

public class EntityRegainHealthListener implements Listener {

	private DeathReview plugin;

	public EntityRegainHealthListener(DeathReview plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityRegainHealth(EntityRegainHealthEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			String name = event.getEntity().getName();
			double health = player.getHealth();
			double healing = event.getAmount();
			double maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue();
			if (plugin.taggedPlayers.containsKey(name)) {
				plugin.utils.tagPlayer(player);
				if(player.getHealth() == 20) {
					return;
				}
				if(health + healing >= maxHealth) {
					healing = maxHealth - health;
				}
				if (event.getRegainReason().equals(RegainReason.CUSTOM)) {
					plugin.utils.addHealData(plugin.utils.getTime(), name, "Custom", healing);
				} else if (event.getRegainReason().equals(RegainReason.MAGIC)) {
					plugin.utils.addHealData(plugin.utils.getTime(), name, "Potion", healing);
				} else if (event.getRegainReason().equals(RegainReason.MAGIC_REGEN)) {
					plugin.utils.addHealData(plugin.utils.getTime(), name, "Potion", healing);
				} else if (event.getRegainReason().equals(RegainReason.SATIATED)) {
					plugin.utils.addHealData(plugin.utils.getTime(), name, "Nourished", healing);
				} else {
					String source = event.getRegainReason().toString().toLowerCase().replaceAll("_", " ");
					String formattedSource = source.substring(0, 1).toUpperCase() + source.substring(1);
					plugin.utils.addHealData(plugin.utils.getTime(), name, formattedSource, healing);
				}
			}
		}

	}
}
