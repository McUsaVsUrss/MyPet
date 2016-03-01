/*
 * This file is part of mypet
 *
 * Copyright (C) 2011-2016 Keyle
 * mypet is licensed under the GNU Lesser General Public License.
 *
 * mypet is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * mypet is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.Keyle.MyPet.listeners;

import de.Keyle.MyPet.MyPetApi;
import de.Keyle.MyPet.api.WorldGroup;
import de.Keyle.MyPet.api.util.configuration.ConfigurationYAML;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;

import java.io.File;

public class WorldListener implements Listener {
    @EventHandler
    public void onWorldInit(final WorldInitEvent event) {
        if (WorldGroup.getGroupByWorld(event.getWorld().getName()) == null) {
            WorldGroup defaultGroup = WorldGroup.getGroupByName("default");
            if (defaultGroup == null) {
                defaultGroup = new WorldGroup("default");
                defaultGroup.registerGroup();
            }
            if (defaultGroup.addWorld(event.getWorld().getName())) {
                File groupsFile = new File(MyPetApi.getPlugin().getDataFolder().getPath() + File.separator + "worldgroups.yml");
                ConfigurationYAML yamlConfiguration = new ConfigurationYAML(groupsFile);
                FileConfiguration config = yamlConfiguration.getConfig();
                config.set("Groups.default", defaultGroup.getWorlds());
                yamlConfiguration.saveConfig();
                MyPetApi.getLogger().info("added " + ChatColor.YELLOW + event.getWorld().getName() + ChatColor.RESET + " to '" + ChatColor.YELLOW + "default" + ChatColor.RESET + "' group.");
            } else {
                MyPetApi.getLogger().warning("An error occured while adding " + ChatColor.YELLOW + event.getWorld().getName() + ChatColor.RESET + " to '" + ChatColor.YELLOW + "default" + ChatColor.RESET + "' group. Please restart the server.");
            }
        }
    }
}