/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.parozzz.hopedrop.chance;

import java.util.function.Function;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

/**
 *
 * @author Stefania
 */
public class ToolEnchantmentModifier implements ChanceModifier
{
    
    private final Function<Player,Double> getModifier;
    public ToolEnchantmentModifier(final Enchantment ench, final double modifier)
    {
        getModifier = p -> p.getInventory().getItemInMainHand().getEnchantmentLevel(ench)*modifier;
    }
    
    @Override
    public double getAdder(Player p) 
    {
        return getModifier.apply(p);
    }
    
}
