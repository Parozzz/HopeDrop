/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.parozzz.hopedrop.drop.item;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Stefania
 */
public class ItemManager 
{
    private final Function<Location,Item> simpleDrop;
    
    private final ItemStack item;
    public ItemManager(final ItemStack item)
    {
        this.item=item;
        
        simpleDrop = l -> l.getWorld().dropItem(l, item.clone());
    }
    
    private final Set<AmountModifier> modifiers=new HashSet<>();
    public void addAmountModifier(final AmountModifier modifier)
    {
        modifiers.add(modifier);
    }
    
    private BiFunction<Location, Player, Item> modifiersDrop; 
    public void setMaxAndMin(final int min, final int max)
    {
        NumberManager manager=new NumberManager(min, max);
        modifiersDrop = (l,p) -> 
        {
            ItemStack toDrop=new ItemStack(item);
            toDrop.setAmount(manager.getAddedClone(modifiers.stream().map(am -> am.getNumbers(p)).collect(Collectors.toSet())).generateBetween());
            
            return l.getWorld().dropItem(l, toDrop);
        };
    }
    
    public ItemStack getItem()
    {
        return item;
    }
    
    public Item simpleDrop(final Location l)
    {
        return simpleDrop.apply(l);
    }
    
    public boolean hasModifiersDrop()
    {
        return modifiersDrop!=null;
    }
    
    public Item modifiersDrop(final Location l, final Player p)
    {
        return modifiersDrop.apply(l, p);
    }
}
