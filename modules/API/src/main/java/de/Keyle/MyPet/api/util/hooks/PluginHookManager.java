/*
 * This file is part of mypet-api
 *
 * Copyright (C) 2011-2016 Keyle
 * mypet-api is licensed under the GNU Lesser General Public License.
 *
 * mypet-api is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * mypet-api is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.Keyle.MyPet.api.util.hooks;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class PluginHookManager implements Listener {

    private static PluginManager pluginManager = null;
    private static Map<String, Plugin> pluginInstances = new HashMap<>();
    private static Map<String, String> pluginNames = new HashMap<>();
    private static Map<String, Boolean> pluginFound = new HashMap<>();


    public static <T extends JavaPlugin> T getPluginInstance(Class<T> clazz) {
        if (pluginManager == null) {
            pluginManager = Bukkit.getPluginManager();
        }
        if (pluginInstances.containsKey(clazz.getName())) {
            return clazz.cast(pluginInstances.get(clazz.getName()));
        }
        try {
            T plugin = JavaPlugin.getPlugin(clazz);
            if (plugin != null) {
                pluginInstances.put(clazz.getName(), plugin);
                pluginFound.put(clazz.getName(), true);
                pluginNames.put(plugin.getName(), clazz.getName());
                //DebugLogger.info("Plugin " + plugin.getName() + " (" + clazz.getName() + ") found!");
                return plugin;
            }
        } catch (NoSuchMethodError e) {
            for (Plugin p : pluginManager.getPlugins()) {
                if (clazz.isInstance(p)) {
                    T plugin = clazz.cast(p);
                    pluginInstances.put(clazz.getName(), plugin);
                    pluginFound.put(clazz.getName(), true);
                    pluginNames.put(plugin.getName(), clazz.getName());
                    //DebugLogger.info("Plugin " + plugin.getName() + " (" + clazz.getName() + ") found!");
                    return plugin;
                }
            }
        }
        pluginFound.put(clazz.getName(), false);
        return null;
    }

    public static boolean isPluginUsable(String pluginName) {
        if (pluginManager == null) {
            pluginManager = Bukkit.getPluginManager();
        }
        if (pluginFound.containsKey(pluginName)) {
            return pluginFound.get(pluginName);
        }
        if (!pluginNames.containsKey(pluginName)) {
            JavaPlugin plugin = (JavaPlugin) pluginManager.getPlugin(pluginName);
            if (plugin != null && plugin.isEnabled()) {
                return getPluginInstance(plugin.getClass()) != null;
            } else {
                pluginFound.put(pluginName, false);
            }
            return false;
        } else {
            return true;
        }
    }

    public static boolean isPluginUsable(String pluginName, String className) {
        if (pluginManager == null) {
            pluginManager = Bukkit.getPluginManager();
        }
        if (pluginFound.containsKey(className)) {
            return pluginFound.get(className);
        }
        if (!pluginNames.containsKey(pluginName)) {
            JavaPlugin plugin = (JavaPlugin) pluginManager.getPlugin(pluginName);
            if (plugin != null && plugin.isEnabled() && plugin.getClass().getName().equals(className)) {
                return getPluginInstance(plugin.getClass()) != null;
            } else {
                pluginFound.put(className, false);
            }
            return false;
        } else {
            return true;
        }
    }

    public static void reset() {
        pluginFound.clear();
        pluginInstances.clear();
        pluginNames.clear();
        pluginManager = null;
    }

    @EventHandler
    public void onPluginDisable(final PluginDisableEvent event) {
        String pluginName = event.getPlugin().getName();
        String className = event.getPlugin().getClass().getName();

        pluginNames.remove(pluginName);
        pluginFound.remove(className);
        pluginInstances.remove(className);
    }
}