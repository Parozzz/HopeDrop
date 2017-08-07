/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.parozzz.hopedrop.drop;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import me.parozzz.hopedrop.Dependency;
import me.parozzz.hopedrop.HopeDrop;
import me.parozzz.hopedrop.Utils;
import me.parozzz.hopedrop.Utils.FireworkBuilder;
import me.parozzz.hopedrop.reflection.ActionBar;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

/**
 *
 * @author Paros
 */
public class RewardManager 
{
    public static enum RewardType
    {
        ACTIONBAR, MESSAGE, PLAYERCOMMAND, CONSOLECOMMAND, MONEY,
        FIREWORK, WORLDSOUND, PLAYERSOUND;
    }
    
    private final Set<Consumer<Player>> playerRewards;
    private final Set<Consumer<Location>> locRewards;
    public RewardManager()
    {
        playerRewards=new HashSet<>();
        locRewards=new HashSet<>();
    }
    
    public void addActionBarReward(final String message)
    {
        playerRewards.add(p -> ActionBar.send(p, message));
    }
    
    public void addCommandReward(final String cmd, final boolean console)
    {
        playerRewards.add(console ? p -> Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), cmd.replace("%player%", p.getName())) : p -> p.performCommand(cmd));
    }
    
    public void addMessageReward(final String message)
    {
        playerRewards.add(p -> p.sendMessage(message));
    }
    
    public void addMoneyReward(final double money)
    {
        if(Dependency.isEconomyEnabled())
        {
            playerRewards.add(p ->  Dependency.eco.depositPlayer(p, money));
        }
    }
    
    public void addFireworkReward(final Color... colors)
    {
        FireworkBuilder builder=new FireworkBuilder(HopeDrop.instance).addColor(colors).build();
        locRewards.add(l -> builder.spawn(l));
    }
    
    public void addSoundReward(final Sound sound, final float volume, final float pitch, final boolean onlyPlayer)
    {
        if(onlyPlayer)
        {
            playerRewards.add(p -> p.playSound(p.getLocation(), sound, volume, pitch));
        }
        else
        {
            locRewards.add(l -> l.getWorld().playSound(l, sound, volume, pitch));
        }
    }
    
    public void executeAll(final Location l)
    {
        locRewards.forEach(cns -> cns.accept(l));
    }
    
    public void executeAll(final Player p)
    {
        playerRewards.forEach(cns -> cns.accept(p));
    }
}
