/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.parozzz.hopedrop.chance;

import java.util.function.Function;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

/**
 *
 * @author Stefania
 */
public class PlayerPotionModifier implements ChanceModifier
{
    private final Function<Player, Double> getModifier;
    public PlayerPotionModifier(final PotionEffectType pet, final double modifier)
    {
        getModifier = p -> p.getActivePotionEffects().stream().filter(pe -> pe.getType()==pet).findFirst().map(pe -> pe.getAmplifier()).orElseGet(() -> 0)*modifier;
    }

    @Override
    public double getAdder(Player p) 
    {
        return getModifier.apply(p);
    }
    
}
