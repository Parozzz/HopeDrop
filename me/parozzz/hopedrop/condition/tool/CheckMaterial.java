/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.parozzz.hopedrop.condition.tool;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Stefania
 */
public class CheckMaterial implements ToolCondition
{
    private final Material type;
    public CheckMaterial(final Material type)
    {
        this.type=type;
    }
    
    @Override
    public boolean check(ItemStack item) 
    {
        return item.getType()==type;
    }
    
}
