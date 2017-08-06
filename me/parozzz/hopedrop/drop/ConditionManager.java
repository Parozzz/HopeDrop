/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.parozzz.hopedrop.drop;

import me.parozzz.hopedrop.condition.GenericCondition;
import me.parozzz.hopedrop.condition.PlayerCondition;
import me.parozzz.hopedrop.condition.ToolCondition;

/**
 *
 * @author Stefania
 */
      
public abstract class ConditionManager
{
    public enum ConditionManagerType 
    {
        MOB, BLOCK;
    }

    
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