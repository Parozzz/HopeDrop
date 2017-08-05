/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.parozzz.hopedrop.condition.generic;

import java.util.function.Predicate;
import org.bukkit.Location;

/**
 *
 * @author Stefania
 */
public class CheckYLevel implements GenericCondition
{
    private final Predicate<Integer> level;
    public CheckYLevel(final int minLevel, final int maxLevel)
    {
        level= lv -> lv>=minLevel && lv <=maxLevel;
    }
    
    @Override
    public boolean check(Location l) 
    {
        return level.test(l.getBlockY());
    }
    
}
