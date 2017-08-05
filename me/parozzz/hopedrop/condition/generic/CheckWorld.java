/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.parozzz.hopedrop.condition.generic;

import java.util.function.Predicate;
import org.bukkit.Location;
import org.bukkit.World;

/**
 *
 * @author Stefania
 */
public class CheckWorld implements GenericCondition
{
    private final Predicate<World> check;
    public CheckWorld(final World w)
    {
        check= wrld -> wrld.equals(w);
    }

    @Override
    public boolean check(Location l) 
    {
        return check.test(l.getWorld());
    }
}
