/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.parozzz.hopedrop.condition.generic;

import java.util.function.Predicate;
import org.bukkit.Location;
import org.bukkit.block.Biome;

/**
 *
 * @author Stefania
 */
public class CheckBiome implements GenericCondition
{
    private final Predicate<Location> check;
    public CheckBiome(final Biome biome)
    {
        check = l -> l.getBlock().getBiome()==biome;
    }
    
    @Override
    public boolean check(Location l) 
    {
        return check.test(l);
    }
    
}
