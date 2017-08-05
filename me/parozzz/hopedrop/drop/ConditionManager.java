/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.parozzz.hopedrop.drop;

import java.util.HashSet;
import java.util.Set;
import me.parozzz.hopedrop.chance.ChanceManager;
import me.parozzz.hopedrop.condition.generic.GenericCondition;
import me.parozzz.hopedrop.condition.player.PlayerCondition;
import me.parozzz.hopedrop.condition.tool.ToolCondition;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Stefania
 */
      
public abstract class ConditionManager
{
    private final Set<GenericCondition> genericCondition;
    private final Set<ToolCondition> toolCondition;
    private final Set<PlayerCondition> playerCondition;
  
    public ConditionManager()
    {
        genericCondition=new HashSet<>();
        toolCondition=new HashSet<>();
        playerCondition=new HashSet<>();
    }

    public void addGenericCondition(final GenericCondition cond)
    {
        genericCondition.add(cond);
    }

    public void addToolCondition(final ToolCondition cond)
    {
        toolCondition.add(cond);
    }

    public void addPlayerCondition(final PlayerCondition cond)
    {
        playerCondition.add(cond);
    }

    public boolean checkGeneric(final Location l)
    {
        return genericCondition.stream().allMatch(gc -> gc.check(l));
    }

    public boolean checkTool(final ItemStack tool)
    {
        return toolCondition.stream().allMatch(tc -> tc.check(tool));
    }

    public boolean checkPlayer(final Player p)
    {
        return playerCondition.stream().allMatch(pc -> pc.check(p));
    }
}