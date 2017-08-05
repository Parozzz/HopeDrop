/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.parozzz.hopedrop.drop.item;

import org.bukkit.entity.Player;

/**
 *
 * @author Paros
 */
public interface AmountModifier 
{
    NumberManager getNumbers(final Player p);
}
