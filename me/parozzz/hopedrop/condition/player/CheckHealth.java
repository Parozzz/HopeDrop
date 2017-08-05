/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.parozzz.hopedrop.condition.player;

import org.bukkit.entity.Player;

/**
 *
 * @author Stefania
 */
public class CheckHealth implements PlayerCondition
{
    private final double health;
    public CheckHealth(final double health)
    {
        this.health=health;
    }
    
    
    @Override
    public boolean check(Player p) 
    {
        return p.getHealth()>=health;
    }
    
}
