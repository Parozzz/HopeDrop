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
import me.parozzz.hopedrop.Utils.FireworkBuilder;
import me.parozzz.hopedrop.reflection.ActionBar;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.entity.Player;

/**
 *
 * @author Paros
 */
public class RewardManager 
{
    public static enum RewardType
    {
        ACTIONBAR, MESSAGE, PLAYERCOMMAND, CONSOLECOMMAND, MONEY;
    }
    
    private final Set<Consumer<Player>> rewards;
    public RewardManager()
    {
        rewards=new HashSet<>();
    }
    
    public void addActionBarReward(final String message)
    {
        rewards.add(p -> ActionBar.send(p, message));
    }
    
    public void addCommandReward(final String cmd, final boolean console)
    {
        rewards.add(console ? p -> Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), cmd.replace("%player%", p.getName())) : p -> p.performCommand(cmd));
    }
    
    public void addMessageReward(final String message)
    {
        rewards.add(p -> p.sendMessage(message));
    }
    
    public void addMoneyReward(final double money)
    {
        if(Dependency.isEconomyEnabled())
        {
            rewards.add(p ->  Dependency.eco.depositPlayer(p, money));
        }
    }
    
    public void executeAll(final Player p)
    {
        rewards.forEach(cns -> cns.accept(p));
    }
}
