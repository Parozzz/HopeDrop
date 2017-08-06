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
    
    private final Set<Predicate<Player>> conditions;
    public PlayerCondition()
    {
        conditions=new HashSet<>();
    }
    
    public void addPermissionCheck(final String perm)
    {
        conditions.add(p -> p.hasPermission(perm));
    }
    
    public void addOnFireCheck(final boolean onFire)
    {
        conditions.add(onFire? p -> p.getFireTicks()!=-1 : p -> p.getFireTicks()==-1);
    }
    
    public void addLevelCheck(final int level)
    {
        conditions.add(p -> p.getLevel()>=level);
    }
    
    public void addHungerCheck(final int hunger)
    {
        conditions.add(p -> p.getFoodLevel()>=hunger);
    }
    
    public void addHealthCheck(final double health)
    {
        conditions.add(p -> p.getHealth()>=health);
    }
    
    public void addGameModeCheck(final GameMode gm)
    {
        conditions.add(p -> p.getGameMode()==gm);
    }
    
    public boolean checkAll(final Player p)
    {
        return conditions.stream().allMatch(pr -> pr.test(p));
    }
}
