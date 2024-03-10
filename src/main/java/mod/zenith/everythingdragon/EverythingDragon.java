package mod.zenith.everythingdragon;


import org.bukkit.World;
import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EnderDragonChangePhaseEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.world.EntitiesUnloadEvent;


import java.security.Permission;
import java.util.Arrays;
import java.util.List;

import static org.bukkit.Bukkit.*;


public final class EverythingDragon extends JavaPlugin implements Listener {

    private int overworldDragLimit=20;
    private int netherDragLimit=10;
    private int endDragLimit=50;
    private int dragCount = 0;
    @Override
    public void onEnable() {
        getLogger().info("EverythingDragon plugin has been enabled!");
        getServer().getPluginManager().registerEvents(this, this);
        Bukkit.getScheduler().runTaskTimer(this, this::killEntities, 20L, 20L * 60);

    }

    @Override
    public void onDisable() {
        getLogger().info("EverythingDragon plugin has been disabled!");
    }

    @EventHandler
    public void onPlayerJoin (PlayerJoinEvent e){
        e.getPlayer().getLocation().getWorld().spawnEntity(e.getPlayer().getLocation(), EntityType.ENDER_DRAGON);
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent e) {
        World.Environment env = e.getLocation().getWorld().getEnvironment();
        if(e.getEntity() instanceof LivingEntity) {
            LivingEntity entity = (LivingEntity) e.getEntity();
            if (env == World.Environment.NORMAL) {
                if (this.countDragon(e.getLocation().getWorld())<=overworldDragLimit) {
                    if (entity.getType() != EntityType.ENDER_DRAGON && entity.getType() != EntityType.BAT) {
                        // Replace every spawned mob with Ender Dragon
                        entity.remove();
                        entity.getWorld().spawnEntity(entity.getLocation(), EntityType.ENDER_DRAGON);
                    }
                    if (entity.getType()==EntityType.ENDER_DRAGON) {
                        getLogger().info("-----Dragon Spawned at "+entity.getLocation()+(++dragCount));
                        EnderDragon dragon = (EnderDragon) entity;
                        Player nearestPlayer = findNearestPlayer(entity.getLocation());
                        dragon.setAI(true);
                        dragon.setPhase(EnderDragon.Phase.BREATH_ATTACK);
                        dragon.setTarget(nearestPlayer);
                        getLogger().info("Nearest Player :"+nearestPlayer);
                    }
                }
                else{
                    e.getEntity().remove();
                }
            } else if (env == World.Environment.NETHER) {
                if (this.countDragon(e.getLocation().getWorld())<=netherDragLimit) {
                    if (entity.getType() != EntityType.ENDER_DRAGON && entity.getType() != EntityType.BLAZE && entity.getType()!=EntityType.ENDERMAN) {
                        // Replace every spawned mob with Ender Dragon
                        entity.remove();
                        entity.getWorld().spawnEntity(entity.getLocation(), EntityType.ENDER_DRAGON);
                    }
                    if (entity.getType()==EntityType.ENDER_DRAGON) {
                        getLogger().info("-----Dragon Spawned at "+entity.getLocation()+(++dragCount));
                        EnderDragon dragon = (EnderDragon) entity;
                        Player nearestPlayer = findNearestPlayer(entity.getLocation());
                        dragon.setAI(true);
                        dragon.setPhase(EnderDragon.Phase.BREATH_ATTACK);
                        dragon.setTarget(nearestPlayer);
                        getLogger().info("Nearest Player :"+nearestPlayer);
                    }
                }
                else{
                    e.getEntity().remove();
                }

            } else if (env == World.Environment.THE_END) {
                if (this.countDragon(e.getLocation().getWorld())<=endDragLimit) {
                    if(e.getEntity().getType()!=EntityType.ENDER_DRAGON) {
                        entity.remove();
                        entity.getWorld().spawnEntity(entity.getLocation(), EntityType.ENDER_DRAGON);
                    }
                    if (entity.getType()==EntityType.ENDER_DRAGON) {
                        EnderDragon dragon = (EnderDragon) entity;
                        dragon.setAI(true);
                        dragon.setPhase(EnderDragon.Phase.CIRCLING);
                        dragon.setTarget(null);
                    }

                }
                else {
                    e.getEntity().remove();
                }
            }
        }
    }



    @EventHandler
    public void onEnderDragonChangePhase(EnderDragonChangePhaseEvent e){
            getLogger().info("Phase Change Triggered:"+e.getEntity().getWorld().getEnvironment()+"::"+ e.getCurrentPhase() + "-->" + e.getNewPhase());
            if (e.getEntity().getWorld().getEnvironment() != World.Environment.THE_END) {
                getLogger().info("Phase Change Triggered:" + e.getCurrentPhase() + "-->" + e.getNewPhase());
                if (e.getNewPhase() == EnderDragon.Phase.FLY_TO_PORTAL || e.getCurrentPhase() == EnderDragon.Phase.FLY_TO_PORTAL) {
                    e.setNewPhase(EnderDragon.Phase.BREATH_ATTACK);
                }
            }
    }

    private void killEntities() {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "/kill @e[type=!minecraft:player,type=!minecraft:cow,type=!minecraft:sheep,type=!minecraft:blaze,type=!minecraft:enderman]");
        getLogger().info("===================Killed===================");
    }

    private  int countDragon(World w){
        int count = 0;
        for (Entity e: w.getLivingEntities()){
            if(e.getType()==EntityType.ENDER_DRAGON){
                count++;
            }
        }
//        getLogger().info("Dragon Count :"+count);
        return count;
    }

    private Player findNearestPlayer(org.bukkit.Location location) {
        double nearestDistanceSquared = Double.MAX_VALUE;
        Player nearestPlayer = null;
        for (Player player : getOnlinePlayers()) {
            double distanceSquared = player.getLocation().distanceSquared(location);
            if (distanceSquared < nearestDistanceSquared) {
                nearestDistanceSquared = distanceSquared;
                nearestPlayer = player;
            }
        }
        return nearestPlayer;
    }

    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent e){
        if(e.getPlayer().getWorld().getEnvironment() == World.Environment.THE_END){
            Bukkit.getScheduler().cancelTasks(this);
        }
        if(e.getPlayer().getWorld().getEnvironment() != World.Environment.THE_END){
            Bukkit.getScheduler().runTaskTimer(this, this::killEntities, 20L, 20L * 60);
        }
    }
}




