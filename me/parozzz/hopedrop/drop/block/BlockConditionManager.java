/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.parozzz.hopedrop.drop.block;

import me.parozzz.hopedrop.chance.ChanceManager;
import me.parozzz.hopedrop.drop.ConditionManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Stefania
 */
public class BlockConditionManager extends ConditionManager
{
    public BlockConditionManager()
    {
        super();
    }
    
    public boolean checkAll(final Player p, final ItemStack tool, final Location l)
    {
        return checkGeneric(l) && checkTool(tool) && checkPlayer(p);
    }
}
