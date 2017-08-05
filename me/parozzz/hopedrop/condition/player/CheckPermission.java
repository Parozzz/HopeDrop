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
public class CheckPermission implements PlayerCondition
{
    private final String perm;
    public CheckPermission(final String perm)
    {
       this.perm=perm; 
    }
    
    @Override
    public boolean check(Player p) 
    {
        return p.hasPermission(perm);
    }
    
}
