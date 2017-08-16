/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.parozzz.hopedrop.chance;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import me.parozzz.hopedrop.utilities.Utils;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

/**
 *
 * @author Stefania
 */
public class ChanceManager 
{
    public enum ChanceModifierType
    {
        ENCHANT, POTION, LEVEL, ATTRIBUTE;
    }
    
    private final Set<Function<Player,Double>> modifiers;
    private final double chance;
    public ChanceManager(final double chance)
    {
        this.chance=chance;
        modifiers=new HashSet<>();
    }
    
    public void addPlayerLevelModifier(final double value)
    {
        modifiers.add(p -> p.getLevel()*value);
    }
    
    public void addPlayerAttributeModifier(final Attribute attr, final double value)
    {
        if(!Utils.bukkitVersion("1.8"))
        {
            modifiers.add(p -> p.getAttribute(attr).getValue()*value);
        }
    }
    
    public void addPlayerPotionModifier(final PotionEffectType pet, final double value)
    {
        modifiers.add(p -> p.getActivePotionEffects().stream().filter(pe -> pe.getType()==pet).findFirst().map(pe -> pe.getAmplifier()*value).orElseGet(() -> 0D));
    }
    
    public void addPlayerToolEnchantmentModifier(final Enchantment ench, final double value)
    {
        modifiers.add(p -> Optional.ofNullable(Utils.getMainHand(p.getEquipment())).map(hand -> hand.getEnchantmentLevel(ench)*value).orElseGet(() -> 0D));
    }
    
    public boolean random()
    {
        return ThreadLocalRandom.current().nextDouble(101D)<chance;
    }
    
    public boolean random(final Player p)
    {
        return ThreadLocalRandom.current().nextDouble(101D)<chance+ modifiers.stream().map(pr -> pr.apply(p)).reduce(Double::sum).orElseGet(() -> 0D);
    }
}
