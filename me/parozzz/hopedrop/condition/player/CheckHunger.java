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
public class CheckHunger implements PlayerCondition
{
    private final int hunger;
    public CheckHunger(final int hunger)
    {
        this.hunger=hunger;
    }

    @Override
    public boolean check(Player p) 
    {
        return p.getFoodLevel()>=hunger;
    }
    
}
