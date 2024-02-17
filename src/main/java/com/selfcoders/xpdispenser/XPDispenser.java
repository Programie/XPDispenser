package com.selfcoders.xpdispenser;

import com.selfcoders.bukkitlibrary.Experience;
import com.selfcoders.bukkitlibrary.SignUtils;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class XPDispenser extends JavaPlugin implements Listener {
    final int SIGN_LINE_INDEX = 0;
    final int SIGN_LINE_INDEX_XP = 1;
    final String SIGN_LINE = "[XPDispenser]";

    public void onEnable() {
        PluginManager pluginManager = getServer().getPluginManager();

        pluginManager.registerEvents(this, this);
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        String line = event.getLine(SIGN_LINE_INDEX);

        if (line == null) {
            return;
        }

        if (!ChatColor.stripColor(line).equalsIgnoreCase(SIGN_LINE)) {
            return;
        }

        Player player = event.getPlayer();

        if (!player.hasPermission("xpdispenser")) {
            player.sendMessage(ChatColor.RED + "You do not have the required permissions to place XPDispenser signs!");
            event.setCancelled(true);
            return;
        }

        event.setLine(SIGN_LINE_INDEX, ChatColor.BLUE + SIGN_LINE);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Block block = event.getClickedBlock();

        if (block == null) {
            return;
        }

        Sign signBlock = SignUtils.getSignFromBlock(block);

        if (signBlock == null) {
            return;
        }

        if (!ChatColor.stripColor(signBlock.getLine(SIGN_LINE_INDEX)).equalsIgnoreCase(SIGN_LINE)) {
            return;
        }

        signBlock.setWaxed(true);
        signBlock.update();

        Player player = event.getPlayer();

        if (!player.hasPermission("xpdispenser")) {
            player.sendMessage(ChatColor.RED + "You do not have the required permissions to use XPDispenser signs!");
            return;
        }

        int xp = 0;
        try {
            xp = Integer.parseInt(ChatColor.stripColor(signBlock.getLine(SIGN_LINE_INDEX_XP)));
        } catch (Exception ignored) {
        }

        if (xp <= 0) {
            xp = 10;
        }

        int totalExp = Experience.getExp(player);
        if (totalExp < xp) {
            xp = totalExp;
        }

        if (xp <= 0) {
            return;
        }

        int realXP = xp;

        player.giveExp(-realXP);
        player.getWorld().spawn(player.getLocation(), ExperienceOrb.class, experienceOrb -> experienceOrb.setExperience(realXP));
    }
}
