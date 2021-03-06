/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.parozzz.hopedrop.condition;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;

/**
 *
 * @author Stefania
 */
public class GenericCondition 
{
    public static enum GenericConditionType 
    {
        BIOME,
        WORLD,
        YLEVEL;
    }

    private Predicate<Location> condition;
    public GenericCondition()
    {
        condition=l -> true;
    }
    
    public void addBiomeCheck(final Biome biome)
    {
        condition=condition.and(l -> l.getBlock().getBiome()==biome);
    }
    
    public void addWorldCheck(final World w)
    {
        condition=condition.and(l -> l.getWorld().equals(w));
    }
    
    public void addYCheck(final int minLevel, final int maxLevel)
    {
       condition=condition.and(l -> l.getY() >=minLevel && l.getY() <=maxLevel);
    }
    
    public boolean checkAll(final Location l)
    {
        return condition.test(l);
    }
}
