/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.parozzz.hopedrop.drop.mob;

import java.util.HashSet;
import java.util.Set;
import me.parozzz.hopedrop.chance.ChanceManager;
import me.parozzz.hopedrop.condition.MobCondition;
import me.parozzz.hopedrop.drop.ConditionManager;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Stefania
 */
public class MobConditionManager extends ConditionManager
{
    private final MobCondition mobCondition;
    public MobConditionManager()
    {
        super();
        mobCondition=new MobCondition();
    }
    
    public MobCondition getMobCondition()
    {
        return mobCondition;
    }
    
    public boolean checkAll(final Location l, final Player p, final ItemStack tool, final LivingEntity ent)
    {
        return getGenericCondition().checkAll(l) && getPlayerCondition().checkAll(p) && getToolCondition().checkAll(tool) && mobCondition.checkAll(ent);
    }
}
