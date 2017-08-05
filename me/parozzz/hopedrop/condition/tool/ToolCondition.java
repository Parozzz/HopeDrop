/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.parozzz.hopedrop.condition.tool;

import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Stefania
 */
public interface ToolCondition 
{
    public boolean check(final ItemStack item);
}
