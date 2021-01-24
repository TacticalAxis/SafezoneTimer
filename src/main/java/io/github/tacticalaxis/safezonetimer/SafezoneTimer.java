package io.github.tacticalaxis.safezonetimer;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import io.github.tacticalaxis.safezonetimer.events.EventManager;
import io.github.tacticalaxis.safezonetimer.util.ConfigurationManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class SafezoneTimer extends JavaPlugin implements CommandExecutor {

    private static SafezoneTimer instance;

    public static HashMap<Player, Long> leftPlayers;

    public void onEnable() {
        leftPlayers = new HashMap<>();
        instance = this;
        init();
        getServer().getPluginManager().registerEvents(new EventManager(), this);

        new BukkitRunnable() {
            public void run() {
                for(Player p : Bukkit.getOnlinePlayers()) {
                    if (EventManager.isSafezone(p.getLocation())) {
                        p.setHealth(20);
                        p.setFoodLevel(20);
                    }
                }
                for(Player p : leftPlayers.keySet()) {
                    p.setHealth(20);
                    p.setFoodLevel(20);
                }
            }
        }.runTaskTimer(SafezoneTimer.getInstance(), 20,5);

        new BukkitRunnable() {
            public void run() {
                for(Player p : leftPlayers.keySet()) {
                    if (System.currentTimeMillis() >= leftPlayers.get(p)) {
                        leftPlayers.remove(p);
                    } else {
                        if (((leftPlayers.get(p) - System.currentTimeMillis()) / 1000) < ConfigurationManager.getInstance().getMainConfiguration().getInt("safezone-leave-time")) { // gets time value from config
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', ConfigurationManager.getInstance().getMainConfiguration().getString("safezone-leave-message").replace("%seconds%",String.valueOf(Math.abs((int)((System.currentTimeMillis() - SafezoneTimer.leftPlayers.get(p)) / 1000))))));
                        }
                    }
                }
            }
        }.runTaskTimer(SafezoneTimer.getInstance(), 20,20);

        ConfigurationManager.getInstance().setupConfiguration();

        getCommand("szt").setExecutor(this);
    }

    public static SafezoneTimer getInstance() {
        return instance;
    }

    private void init() {
        if (Bukkit.getPluginManager().getPlugin("WorldGuard") == null) {
            Bukkit.getLogger().severe("Disabled, as WorldGuard dependency could not be found!");
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    public static WorldGuardPlugin getWorldGuard() {
        Plugin plugin = instance.getServer().getPluginManager().getPlugin("WorldGuard");
        if (!(plugin instanceof WorldGuardPlugin)) {
            return null;
        }
        return (WorldGuardPlugin) plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        ConfigurationManager.getInstance().reloadConfigurations();
        ConfigurationManager.getInstance().setupConfiguration();
        sender.sendMessage(ChatColor.GREEN + "Safezone Timer config reloaded");
        return true;
    }
}