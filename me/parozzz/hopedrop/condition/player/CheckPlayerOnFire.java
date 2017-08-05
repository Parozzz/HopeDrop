/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.parozzz.hopedrop.condition.player;

import java.util.function.Predicate;
import org.bukkit.entity.Player;

/**
 *
 * @author Stefania
 */
public class CheckPlayerOnFire implements PlayerCondition
{
    private final Predicate<Player> fire;
    public CheckPlayerOnFire(final boolean onFire)
    {
        fire=onFire?p -> p.getFireTicks()!=-1:p->p.getFireTicks()==-1;
    }
    
    @Override
    public boolean check(Player p) 
    {
        return fire.test(p);
    }
    
}
