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

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.entity.TippedArrow;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import me.iiSnipez.DeathReview.DeathReview;

public class EntityDamageByEntityListener implements Listener {

	private DeathReview plugin;

	public EntityDamageByEntityListener(DeathReview plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDamage(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			String pName = player.getName();
			Entity damager = event.getDamager();
			String dName = "";
			if (damager instanceof Projectile || damager instanceof TippedArrow 
					|| damager instanceof Arrow || damager instanceof ThrownPotion) {
				dName = ((Projectile) damager).getShooter().toString().replaceAll("Craft", "");
				if (dName.startsWith("Player{name=")) {
					dName = dName.substring(12, dName.length() - 1);
				}
			} else {
				dName = damager.getName();
			}
			plugin.utils.tagPlayer(player);
			plugin.utils.addDamageData(plugin.utils.getTime(), pName, dName, event.getFinalDamage(), event.getDamage());
		}
	}

}
