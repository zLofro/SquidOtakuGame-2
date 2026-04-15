package me.lofro.game.global.listeners;

import io.papermc.paper.event.entity.EntityLoadCrossbowEvent;
import lombok.Getter;
import me.lofro.game.SquidGame;
import me.lofro.game.global.enums.PvPState;
import me.lofro.game.global.events.SquidParticipantChangeRoleEvent;
import me.lofro.game.global.item.CustomItems;
import me.lofro.game.global.utils.Sounds;
import me.lofro.game.global.utils.credits.Credits;
import me.lofro.game.global.utils.scoreboards.Scoreboards;
import me.lofro.game.players.enums.Role;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.block.data.Rotatable;
import org.bukkit.block.data.type.Door;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.lofro.game.games.GameManager;
import me.lofro.game.global.utils.text.HexFormatter;
import me.lofro.game.global.utils.datacontainers.Data;
import me.lofro.game.global.utils.datacontainers.PlayerIsNotOnlineException;
import me.lofro.game.players.PlayerManager;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

public class GlobalListener implements Listener {

    private final PlayerManager pManager;
    private final GameManager gManager;

    private final @Getter HashMap<String, Boolean> hasSeenCredits = new HashMap<>();

    public GlobalListener(PlayerManager pManager, GameManager gManager) {
        this.pManager = pManager;
        this.gManager = gManager;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        var player = e.getPlayer();
        var name = player.getName();

        if (!player.hasPlayedBefore()) player.teleport(new Location(Bukkit.getWorlds().get(0), -23,-49,-19));

        e.joinMessage(null);

        pManager.guardMessage(HexFormatter.hexFormat("&7El jugador &6" + name + " &7ha entrado al servidor."));
        player.playSound(player.getLocation(), "sfx.server_join", 1, 1);

        if (!hasSeenCredits.containsKey(player.getName())) hasSeenCredits.put(name, false);

        if (pManager.pData().getParticipant(name) == null) {
            if (player.isOp()) {
                pManager.pData().addGuard(name);
                player.sendMessage(HexFormatter.hexFormatWithPrefix("&bTu rol ha sido asignado automáticamente a &3GUARDIA&b debido a que tienes permisos de administrador."));
            } else {
                pManager.pData().addPlayer(name);
                player.setGameMode(GameMode.ADVENTURE);
            }
        }

        setupRole(player, name);

        var timer = gManager.getTimer();
        if (timer.isActive())
            timer.addPlayer(player);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        var player = e.getPlayer();
        var name = player.getName();

        e.quitMessage(null);

        pManager.guardMessage(HexFormatter.hexFormat("&7El jugador &6" + name + " &7ha abandonado el servidor."));

        var timer = gManager.getTimer();
        timer.removePlayer(player);
    }

    @EventHandler
    public void onParticipantChangeRole(SquidParticipantChangeRoleEvent e) {
        var player = e.getPlayer();
        var name = player.getName();

        setupRole(player, name);
    }

    private void setupRole(Player player, String name) {
        if (pManager.isGuard(player)) {
            Scoreboards.removeDisplayName(player);
            Scoreboards.setGuard(player);

            player.playerListName(HexFormatter.hexFormat("&7&k#1zzz"));

            player.getEquipment().setChestplate(null);
            player.getEquipment().setLeggings(null);

            player.getEquipment().setChestplate(CustomItems.Decoration.GUARD_CHESTPLATE.get());
            player.getEquipment().setLeggings(CustomItems.Decoration.GUARD_LEGGINGS.get());
        } else {
            var id = pManager.pData().getPlayer(name).getId();

            Scoreboards.removeGuard(player);
            Scoreboards.setDisplayName(HexFormatter.hexFormat("&3#" + id + "&b "), NamedTextColor.AQUA, player);

            player.playerListName(HexFormatter.hexFormat("&3#" + id + "&b " + name));

            player.getEquipment().setHelmet(null);

            player.getEquipment().setChestplate(CustomItems.Decoration.PLAYER_CHESTPLATE.get());
            player.getEquipment().setLeggings(CustomItems.Decoration.PLAYER_LEGGINGS.get());
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getClickedInventory() instanceof PlayerInventory) {
            if (e.getSlotType().equals(InventoryType.SlotType.ARMOR)) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onChat(AsyncChatEvent e) {
        var player = e.getPlayer();

        e.setCancelled(true);

        if (pManager.isGuard(player))
            pManager.guardMessage(HexFormatter.hexFormat("&cGUARDS &8| &7" + player.getName() + " &8| &8&l>> &7"
                    + PlainTextComponentSerializer.plainText().serialize(e.message())));
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        var player = e.getPlayer();
        var name = player.getName();

        var deathLocation = player.getLocation();

        try {
            PersistentDataContainer dataContainer = Data.getData(player);
            Data.set(dataContainer, "DEATH_LOCATION", gManager.getSquidInstance(), PersistentDataType.INTEGER_ARRAY,
                    new int[] { deathLocation.getBlockX(), deathLocation.getBlockY(), deathLocation.getBlockZ() });
            Data.set(dataContainer, "DEATH_LOCATION_ROTATION", gManager.getSquidInstance(), PersistentDataType.FLOAT,
                    player.getLocation().getYaw());
        } catch (PlayerIsNotOnlineException ex) {
            SquidGame.getInstance().getLogger().info(ex.getMessage());
        }

        e.deathMessage(null);

        if (!pManager.isPlayer(player) || player.getGameMode().equals(GameMode.SPECTATOR))
            return;

        var squidPlayer = pManager.pData().getPlayer(name);
        int playerID = squidPlayer.getId();

        player.setGameMode(GameMode.SPECTATOR);
        if (squidPlayer.isDead()) return;

        squidPlayer.setDead(true);

        Bukkit.getOnlinePlayers().forEach(online -> online.playSound(online.getLocation(), "sfx.elimination", 1, 1));

        Bukkit.broadcast(HexFormatter.hexFormat("&bEl jugador &3#" + playerID + " " + name + " &bha sido eliminado."));

        setSkullOnGround(player);
    }

    private void setSkullOnGround(Player player) {
        var location = player.getLocation();

        for (int y = location.getBlockY(); y > -64; y--) {

            if(location.clone().subtract(0, 1, 0).getBlock().getType() == Material.AIR) continue;

            Block skullBlock = location.getBlock();
            skullBlock.setType(Material.PLAYER_HEAD);

            BlockState state = skullBlock.getState();
            Skull skull = (Skull) state;
            UUID uuid = player.getUniqueId();

            skull.setOwningPlayer(Bukkit.getServer().getOfflinePlayer(uuid));

            Rotatable skullRotation = (Rotatable) skull.getBlockData();
            skullRotation.setRotation(getCardinalDirectionFace(player.getLocation()).getOppositeFace());
            skull.setBlockData(skullRotation);

            skull.update();

            break;
        }
    }

    private BlockFace getCardinalDirectionFace(Location location) {
        var yaw = location.getYaw();
        double rotation = (yaw) % 360.0F;

        if (rotation < 0.0D) rotation += 360.0D;

        int index = (int) (rotation / 45);

        ArrayList<BlockFace> allFaces = new ArrayList<>(
                Arrays.asList(BlockFace.SOUTH, BlockFace.SOUTH_WEST,
                        BlockFace.WEST, BlockFace.NORTH_WEST, BlockFace.NORTH, BlockFace.NORTH_EAST, BlockFace.EAST, BlockFace.SOUTH_EAST));

        return allFaces.get(index);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        var player = e.getPlayer();

        player.setGameMode(GameMode.SPECTATOR);

        try {
            PersistentDataContainer persistentDataContainer = Data.getData(player);
            int[] locationBlocks = Data.get(persistentDataContainer, "DEATH_LOCATION", gManager.getSquidInstance(), PersistentDataType.INTEGER_ARRAY);
            float yaw = Data.get(persistentDataContainer, "DEATH_LOCATION_ROTATION", gManager.getSquidInstance(), PersistentDataType.FLOAT);

            Location respawnLocation = new Location(player.getWorld(), locationBlocks[0], locationBlocks[1], locationBlocks[2]);

            respawnLocation.setYaw(yaw);

            e.setRespawnLocation(respawnLocation);
        } catch (PlayerIsNotOnlineException ex) {
            SquidGame.getInstance().getLogger().info(ex.getMessage());
        }
            Bukkit.getScheduler().runTaskLater(gManager.getSquidInstance(), () -> Credits.showCredits(player), 1);
            hasSeenCredits.put(player.getName(), true);
    }

    @EventHandler
    public void onFood(FoodLevelChangeEvent e) {
        var player = (Player) e.getEntity();
        if (!player.hasPotionEffect(PotionEffectType.SATURATION) && !player.hasPotionEffect(PotionEffectType.HUNGER)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        var entity = e.getEntity();
        var damager = e.getDamager();

        var pvPState = gManager.gameData().getPvPState();

        if (entity instanceof Player player) {
            var name = player.getName();

            var pSquidParticipant = pManager.pData().getParticipant(name);

            var pRole = pManager.pData().getRole(pSquidParticipant);

            if (damager instanceof Player playerDamager) {
                var playerDamagerName = playerDamager.getName();

                var dSquidParticipant = pManager.pData().getParticipant(playerDamagerName);

                var dRole = pManager.pData().getRole(dSquidParticipant);

                executeDamageByPVP(e, pvPState, pRole, dRole);
            } else if (damager instanceof Projectile projectile) {
                if (projectile.getShooter() instanceof Player pShooter) {
                    String pShooterName = pShooter.getName();

                    var pShooterSquidParticipant = pManager.pData().getParticipant(pShooterName);

                    var pShooterRole = pManager.pData().getRole(pShooterSquidParticipant);

                    executeDamageByPVP(e, pvPState, pRole, pShooterRole);
                    e.setDamage(1000);
                } else if (projectile.getShooter() == null) {
                    e.setDamage(6);
                }
            }
        } else if (entity instanceof ItemFrame itemFrame && damager instanceof Player player) {
            if (pManager.isPlayer(player)) {
                if (itemFrame.getItem().getType().equals(Material.BRICK)) {
                    e.setCancelled(true);
                }
            }
        }
    }

    private void executeDamageByPVP(EntityDamageByEntityEvent e, PvPState pvPState, Role pRole, Role dRole) {
        switch (pvPState) {
            case ONLY_GUARDS -> {
                //Player to player.
                if (pRole == Role.PLAYER && dRole == pRole) {
                    e.setCancelled(true);
                    break;
                }
                //Guard to guard.
                if (pRole == Role.GUARD && dRole == pRole) {
                    e.setCancelled(true);
                    break;
                }
                //Player to guard.
                if (dRole == Role.PLAYER && pRole == Role.GUARD) {
                    e.setCancelled(true);
                }
            }
            case ALL -> {
                //Player to guard.
                if (dRole == Role.PLAYER && pRole == Role.GUARD) {
                    e.setCancelled(true);
                }
            }
            case NONE -> e.setCancelled(true);
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent e) {
        var entity = e.getEntity();

        if (entity instanceof Snowball snowball) {
            Sounds.playSoundDistance(snowball.getLocation(), 15, "sfx.bottle_break", 1f, 1f);

            if (entity.getShooter() == null) {
                snowball.getWorld().dropItemNaturally(snowball.getLocation(), CustomItems.Weapons.BROKEN_BOTTLE.get());
            }
        }
    }

    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent e) {
        var player = e.getPlayer();
        var entity = e.getRightClicked();

        if (entity instanceof ArmorStand armorStand) {
            if (armorStand.getEquipment().getHelmet() != null && armorStand.getEquipment().getHelmet().equals(CustomItems.Decoration.FOOD.get())) {
                if (player.getInventory().contains(CustomItems.Decoration.BOTTLE.get()) || player.getInventory().contains(Material.GOLDEN_APPLE)) return;

                player.getInventory().addItem(CustomItems.Decoration.BOTTLE.get());
                player.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE));
            }
        }
    }

    @EventHandler
    public void onClick(PlayerInteractEvent e) {
        var player = e.getPlayer();
        var name = player.getName();

        var block = e.getClickedBlock();

        var squidParticipant = pManager.pData().getParticipant(name);

        var role = pManager.pData().getRole(squidParticipant);

        if (role == Role.GUARD) {
            if (e.getHand() == EquipmentSlot.HAND) {
                if (block != null && block.getType().equals(Material.IRON_DOOR)) {
                    // 1ms delay fixing visual bug.
                    Bukkit.getScheduler().runTask(SquidGame.getInstance(), task -> openDoors(block));
                }
            }
        }

        if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (e.getHand() == EquipmentSlot.HAND || e.getHand() == EquipmentSlot.OFF_HAND) {
                var item = e.getItem();

                if (item == null || !gManager.gameData().getPvPState().equals(PvPState.ALL)) return;

                if (item.equals(CustomItems.Weapons.BROKEN_BOTTLE.get())) {
                    var throwableItem = player.getWorld().spawnEntity(player.getEyeLocation(), EntityType.SNOWBALL);
                    ((Projectile) throwableItem).setShooter(player);
                    throwableItem.setVelocity(player.getEyeLocation().getDirection());

                    player.getEquipment().getItemInMainHand().setAmount(0);
                } else if (item.equals(CustomItems.Decoration.BOTTLE.get())) {
                    var throwableItem = player.getWorld().spawnEntity(player.getEyeLocation(), EntityType.SNOWBALL);
                    throwableItem.setVelocity(player.getEyeLocation().getDirection());

                    player.getEquipment().getItemInMainHand().setAmount(0);
                }
            }
        }
    }

    @EventHandler
    public void onSpawn(CreatureSpawnEvent e) {
        if (e.getSpawnReason().toString().contains("NATURAL")) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onArrowPickup(PlayerPickupArrowEvent e) {
        var player = e.getPlayer();

        if (pManager.isPlayer(player)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onCrossbowLoad(EntityLoadCrossbowEvent e) {
        var crossBow = e.getCrossbow();
        var loc = e.getEntity().getLocation();

        var itemMeta = crossBow.getItemMeta();

        if (!itemMeta.hasCustomModelDataComponent()) return;

        var modelData = itemMeta.getCustomModelDataComponent();

        if (modelData == CustomItems.Weapons.REVOLVER.get().getItemMeta().getCustomModelDataComponent() || modelData == CustomItems.Weapons.SHOTGUN.get().getItemMeta().getCustomModelDataComponent()) {
            e.setConsumeItem(false);
            Sounds.playSoundDistance(loc, 30, "sfx.reload_gun", 1f, 1f);
        }
    }

    @EventHandler
    @SuppressWarnings("deprecation")
    public void onProjectileLaunch(EntityShootBowEvent e) {
        var entity = e.getEntity();

        if (entity instanceof Player player) {

            var loc = player.getLocation();

            if (e.getBow() == null) return;

            var bow = e.getBow();
            var bMeta = bow.getItemMeta();

            if (!bMeta.hasCustomModelDataComponent()) return;

            var modelData = bMeta.getCustomModelDataComponent();

            if (modelData == CustomItems.Weapons.REVOLVER.get().getItemMeta().getCustomModelDataComponent()) {
                e.setConsumeItem(false);
                player.updateInventory();
                Sounds.playSoundDistance(loc, 30, "sfx.gun", 1f, 1f);
            } else if (modelData == CustomItems.Weapons.SHOTGUN.get().getItemMeta().getCustomModelDataComponent()) {
                e.setConsumeItem(false);
                player.updateInventory();
                Sounds.playSoundDistance(loc, 30, "sfx.shotgun", 1f, 1f);
            }
        }
    }

    /**
     * Function that opens both door hinges at the same time.
     *
     * @param block Block to open.
     */
    private void openDoors(Block block) {
        if (block.getBlockData() instanceof Door door) {
            ArrayList<BlockFace> mainFaces = new ArrayList<>(Arrays.asList(BlockFace.WEST, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH));

            door.setOpen(!door.isOpen());
            block.setBlockData(door);
            Sounds.playSoundDistance(block.getLocation(), 10, Sound.BLOCK_IRON_DOOR_OPEN, 1f, 1f);

            var hinge = door.getHinge();
            var index = mainFaces.indexOf(door.getFacing());
            var face = mainFaces.get(hinge == Door.Hinge.RIGHT ? ( index == 0 ? 3 : index -1 ) : (index == 3 ? 0 : index + 1));

            var relative = block.getRelative(face);
            if (relative.getBlockData() instanceof Door secondDoor) {
                if (secondDoor.isOpen() == door.isOpen()) return;
                if (hinge == secondDoor.getHinge()) return;
                secondDoor.setOpen(!secondDoor.isOpen());
                Sounds.playSoundDistance(block.getLocation(), 10, Sound.BLOCK_IRON_DOOR_OPEN, 1f, 1f);
                relative.setBlockData(secondDoor);
            }
        }
    }

}
