/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.parozzz.hopedrop.condition;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

/**
 *
 * @author Stefania
 */
public class PlayerCondition
{
    public enum PlayerConditionType 
    {
        GAMEMODE, HEALTH, HUNGER, LEVEL, ONFIRE, PERMISSION;
    }
    
    private Predicate<Player> condition;
    public PlayerCondition()
    {
        condition=p -> true;
    }
    
    public void addPermissionCheck(final String perm)
    {
        condition=condition.and(p -> p.hasPermission(perm));
    }
    
    public void addOnFireCheck(final boolean onFire)
    {
        condition=condition.and(onFire? p -> p.getFireTicks()!=-1 : p -> p.getFireTicks()==-1);
    }
    
    public void addLevelCheck(final int level)
    {
        condition=condition.and(p -> p.getLevel()>=level);
    }
    
    public void addHungerCheck(final int hunger)
    {
        condition=condition.and(p -> p.getFoodLevel()>=hunger);
    }
    
    public void addHealthCheck(final double health)
    {
        condition=condition.and(p -> p.getHealth()>=health);
    }
    
    public void addGameModeCheck(final GameMode gm)
    {
        condition=condition.and(p -> p.getGameMode()==gm);
    }
    
    public boolean checkAll(final Player p)
    {
        return condition.test(p);
    }
}
