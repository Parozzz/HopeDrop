/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.parozzz.hopedrop.chance;

import org.bukkit.entity.Player;

/**
 *
 * @author Stefania
 */
public interface ChanceModifier 
{
    double getAdder(final Player p);
}
