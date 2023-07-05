package dev.steph.sittingplugin.Commands;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.spigotmc.event.entity.EntityDismountEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class SitCommand implements CommandExecutor, Listener {
    private static final Set<UUID> sitting = new HashSet<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can execute this command!");
            return true;
        }

        Player player = (Player) sender;

        if (sitting.contains(player.getUniqueId())) {
            sitting.remove(player.getUniqueId());

            if (player.getVehicle() != null && player.getVehicle() instanceof ArmorStand) {
                Entity vehicle = player.getVehicle();
                vehicle.eject();
                vehicle.remove();
            }

            return true;
        }

        if (!player.isOnGround()) {
            player.sendMessage("§cYou need to be on the ground to be able to sit!");
            return true;
        }

        Location location = player.getLocation();
        World world = location.getWorld();
        ArmorStand chair = (ArmorStand) world.spawnEntity(location.subtract(0, 1.6, 0), EntityType.ARMOR_STAND);

        chair.setGravity(false);
        chair.setVisible(false);
        chair.setInvulnerable(true);
        chair.addPassenger(player);

        sitting.add(player.getUniqueId());

        return true;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (sitting.contains(player.getUniqueId())) {
            sitting.remove(player.getUniqueId());

            if (player.getVehicle() != null && player.getVehicle() instanceof ArmorStand) {
                Entity vehicle = player.getVehicle();
                vehicle.eject();
                vehicle.remove();
            }
        }
    }

    @EventHandler
    public void onDismount(EntityDismountEvent event) {
        Entity entity = event.getEntity();

        if (!(entity instanceof Player))
            return;

        Player player = (Player) entity;

        if (!(event.getDismounted() instanceof ArmorStand))
            return;

        if (!sitting.contains(player.getUniqueId()))
            return;

        Entity vehicle = event.getDismounted();
        vehicle.eject();
        vehicle.remove();
        sitting.remove(player.getUniqueId());
    }
}
