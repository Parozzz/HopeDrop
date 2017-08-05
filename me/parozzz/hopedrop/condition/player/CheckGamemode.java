/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.parozzz.hopedrop.condition.player;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

/**
 *
 * @author Stefania
 */
public class CheckGamemode implements PlayerCondition
{
    private final GameMode gm;
    public CheckGamemode(final GameMode gm)
    {
        this.gm=gm;
    }
    
    
    @Override
    public boolean check(Player p) 
    {
        return p.getGameMode()==gm;
    }
    
}
