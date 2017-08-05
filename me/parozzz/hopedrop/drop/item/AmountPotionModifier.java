/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.parozzz.hopedrop.drop.item;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

/**
 *
 * @author Paros
 */
public class AmountPotionModifier implements AmountModifier
{
    
    private final NumberManager manager;
    private final PotionEffectType pet;
    public AmountPotionModifier(final PotionEffectType pet, final int min, final int max)
    {
        this.pet=pet;
        
        manager=new NumberManager(min, max);
    }
    
    @Override
    public NumberManager getNumbers(Player p) 
    {
        return p.getActivePotionEffects().stream()
                .filter(pe -> pe.getType()==pet)
                .findFirst().map(pe -> manager.getMultipliedClone(pe.getAmplifier()+1))
                .orElseGet(() -> NumberManager.getEmptyManager());
    }
    
}
