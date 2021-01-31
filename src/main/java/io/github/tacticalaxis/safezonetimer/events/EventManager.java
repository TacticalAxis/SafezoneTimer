package io.github.tacticalaxis.safezonetimer.events;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import io.github.tacticalaxis.safezonetimer.SafezoneTimer;
import io.github.tacticalaxis.safezonetimer.util.ConfigurationManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class EventManager implements Listener {

    public static boolean isSafezone(Location location) {
        if (SafezoneTimer.getWorldGuard() != null) {
            RegionManager regionManager = SafezoneTimer.getWorldGuard().getRegionManager(location.getWorld());
            ApplicableRegionSet set = regionManager.getApplicableRegions(location);
            for (ProtectedRegion pr : set) {
                for (String name : ConfigurationManager.getInstance().getSafezones()) {
                    if (pr.getId().equalsIgnoreCase(name)) { // get from config
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @EventHandler
    public void leaveRegion(PlayerMoveEvent event) {
        if (!(isSafezone(event.getFrom()) && isSafezone(event.getTo()))) {
            if (isSafezone(event.getFrom())) {
                event.getPlayer().sendMessage(ChatColor.RED + "Exited safezone");
                try {
                    SafezoneTimer.leftPlayers.put(event.getPlayer(), System.currentTimeMillis() + (ConfigurationManager.getInstance().getMainConfiguration().getInt("safezone-leave-time") * 1000)); // get value from config
                } catch (Exception ignored) {}
            } else if (isSafezone(event.getTo())) {
                event.getPlayer().sendMessage(ChatColor.GREEN + "Entered safezone");
                SafezoneTimer.leftPlayers.remove(event.getPlayer());
            }
        }
    }

    @EventHandler
    public void damage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (isSafezone(player.getLocation())) {
                event.setCancelled(true);
            } else {
                if (SafezoneTimer.leftPlayers.containsKey(player)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void damage(EntityDamageByEntityEvent event) {
        if (isSafezone(event.getEntity().getLocation()) || isSafezone(event.getDamager().getLocation())) {
            event.setCancelled(true);
        } else {
            if (event.getEntity() instanceof Player) {
                Player player = (Player) event.getEntity();
                if (SafezoneTimer.leftPlayers.containsKey(player)) {
                    event.setCancelled(true);
                }
            }

            if (event.getDamager() instanceof Player) {
                Player player = (Player) event.getDamager();
                if (SafezoneTimer.leftPlayers.containsKey(player)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void damage(EntityDamageByBlockEvent event) {
        if (isSafezone(event.getEntity().getLocation()) || isSafezone(event.getDamager().getLocation())) {
            event.setCancelled(true);
        } else {
            if (event.getEntity() instanceof Player) {
                Player player = (Player) event.getEntity();
                if (SafezoneTimer.leftPlayers.containsKey(player)) {
                    event.setCancelled(true);
                }
            }

            if (event.getDamager() instanceof Player) {
                Player player = (Player) event.getDamager();
                if (SafezoneTimer.leftPlayers.containsKey(player)) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
