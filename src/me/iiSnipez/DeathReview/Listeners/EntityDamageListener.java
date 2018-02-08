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

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import me.iiSnipez.DeathReview.DeathReview;

public class EntityDamageListener implements Listener {

	private DeathReview plugin;

	public EntityDamageListener(DeathReview plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			if (!event.getCause().equals(DamageCause.ENTITY_ATTACK)
					&& !event.getCause().equals(DamageCause.ENTITY_EXPLOSION)
					&& !event.getCause().equals(DamageCause.ENTITY_SWEEP_ATTACK)
					&& !event.getCause().equals(DamageCause.PROJECTILE)
					&& !event.getCause().equals(DamageCause.MAGIC)
					&& !event.getCause().equals(DamageCause.THORNS)) {
				Player player = (Player) event.getEntity();
				String name = player.getName();
				String cause = event.getCause().toString().toLowerCase().replaceAll("_", " ");
				String formattedCause = cause.substring(0, 1).toUpperCase() + cause.substring(1);

				plugin.utils.tagPlayer(player);
				plugin.utils.addDamageData(plugin.utils.getTime(), name, formattedCause, event.getFinalDamage(), event.getDamage());
			}
		}
	}

}
