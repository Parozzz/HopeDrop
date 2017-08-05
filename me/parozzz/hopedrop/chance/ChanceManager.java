/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.parozzz.hopedrop.chance;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.entity.Player;

/**
 *
 * @author Stefania
 */
public class ChanceManager 
{
    private final Set<ChanceModifier> modifiers;
    private final double chance;
    public ChanceManager(final double chance)
    {
        this.chance=chance;
        modifiers=new HashSet<>();
    }
    
    public void addChanceModifier(final ChanceModifier cm)
    {
        modifiers.add(cm);
    }
    
    public boolean random()
    {
        return ThreadLocalRandom.current().nextDouble(101D)<=chance;
    }
    
    public boolean random(final Player p)
    {
        return ThreadLocalRandom.current().nextDouble(101D)<=(chance+ modifiers.stream().map(cm -> cm.getAdder(p)).reduce(Double::sum).orElseGet(() -> 0D));
    }
}
