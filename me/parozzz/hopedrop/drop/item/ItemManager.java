/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.parozzz.hopedrop.drop.item;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import me.parozzz.hopedrop.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

/**
 *
 * @author Stefania
 */
public class ItemManager 
{
    public enum AmountModifierType 
    {
        POTION, ENCHANT;
    }
    
    private final Function<Location,Item> simpleDrop;
    
    private final ItemStack item;
    public ItemManager(final ItemStack item)
    {
        this.item=item;
        
        simpleDrop = l -> l.getWorld().dropItemNaturally(l, item.clone());
    }
    
    private final Set<BiConsumer<Player, NumberManager>> modifiers=new HashSet<>();
    
    public void addEnchantModifier(final Enchantment ench, final int min, final int max)
    {
        //NumberManager manager =new NumberManager(min, max);
        modifiers.add((p, manager) -> 
        {
            Optional.ofNullable(Utils.getMainHand(p))
                .map(item -> item.getEnchantmentLevel(ench))
                .ifPresent(multiplier -> 
                {
                    manager.addToMinAdder(min * multiplier);
                    manager.addToMaxAdder(max * multiplier);
                });
        });
    }
    
    public void addPotionModifier(final PotionEffectType pet, final int min, final int max)
    {
        modifiers.add((p, manager) -> 
        {
            p.getActivePotionEffects().stream().filter(pe -> pe.getType()==pet).findFirst().ifPresent(pe -> 
            {
                manager.addToMinAdder(min*(pe.getAmplifier()+1));
                manager.addToMaxAdder(max*(pe.getAmplifier()+1));
            });
        });
    }
    
    private BiFunction<Location, Player, Item> modifiersDrop; 
    public void setMinAndMax(final int min, final int max)
    {
        NumberManager manager=new NumberManager(min, max);
        modifiersDrop = (l,p) -> 
        {
            ItemStack toDrop=new ItemStack(item);
            modifiers.stream().forEach(cns -> cns.accept(p, manager));
            toDrop.setAmount(manager.generateBetweenWithAdders());
            
            return l.getWorld().dropItemNaturally(l, toDrop);
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
