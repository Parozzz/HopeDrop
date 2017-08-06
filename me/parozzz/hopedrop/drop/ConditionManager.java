/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.parozzz.hopedrop.drop;

import java.util.HashSet;
import java.util.Set;
import me.parozzz.hopedrop.chance.ChanceManager;
import me.parozzz.hopedrop.condition.GenericCondition;
import me.parozzz.hopedrop.condition.PlayerCondition;
import me.parozzz.hopedrop.condition.ToolCondition;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Stefania
 */
      
public abstract class ConditionManager
{
    private final GenericCondition genericCondition;
    private final ToolCondition toolCondition;
    private final PlayerCondition playerCondition;
  
    public ConditionManager()
    {
        genericCondition=new GenericCondition();
        toolCondition=new ToolCondition();
        playerCondition=new PlayerCondition();
    }

    public GenericCondition getGenericCondition()
    {
        return genericCondition;
    }

    public ToolCondition getToolCondition()
    {
        return toolCondition;
    }
    
    public PlayerCondition getPlayerCondition()
    {
        return playerCondition;
    }

}