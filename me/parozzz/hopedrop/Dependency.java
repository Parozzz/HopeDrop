/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.parozzz.hopedrop;

import java.util.Optional;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;

/**
 *
 * @author Paros
 */
public class Dependency 
{
    public static Economy eco;
    protected static boolean setupEconomy()
    {
        if(Bukkit.getPluginManager().isPluginEnabled("Vault"))
        {
            eco = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class).getProvider();
        }
        
        return eco != null;
    }
    
    public static boolean isEconomyEnabled()
    {
        return eco!=null;
    }
}
