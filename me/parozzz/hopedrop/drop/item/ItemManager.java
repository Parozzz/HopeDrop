/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.parozzz.hopedrop.drop.item;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import me.parozzz.hopedrop.utilities.Utils;
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
    
    private final ItemStack item;
    public ItemManager(final ItemStack item)
    {
        this.item=item;
    }
    
    private final Set<BiConsumer<Player, NumberManager>> modifiers=new HashSet<>();
    
    public void addEnchantModifier(final Enchantment ench, final int min, final int max)
    {
        //NumberManager manager =new NumberManager(min, max);
        modifiers.add((p, manager) -> 
        {
            Optional.ofNullable(Utils.getMainHand(p.getEquipment()))
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
    
    private Function<Player, ItemStack> modifiedDrop; 
    public void setMinAndMax(final int min, final int max)
    {
        NumberManager manager=new NumberManager(min, max);
        modifiedDrop = p -> 
        {
            ItemStack toDrop=item.clone();
            modifiers.stream().forEach(cns -> cns.accept(p, manager));
            toDrop.setAmount(manager.generateBetweenWithAdders());
            
            return toDrop;
        };
    }
    
    public ItemStack getItem()
    {
        return item.clone();
    }
    
    public boolean hasModifiedItem()
    {
        return modifiedDrop!=null;
    }
    
    public ItemStack getModifiedItem(final Player p)
    {
        return modifiedDrop.apply(p);
    }
}
