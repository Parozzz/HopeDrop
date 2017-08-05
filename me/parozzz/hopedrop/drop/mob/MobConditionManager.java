/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.parozzz.hopedrop.drop.mob;

import java.util.HashSet;
import java.util.Set;
import me.parozzz.hopedrop.chance.ChanceManager;
import me.parozzz.hopedrop.condition.mob.MobCondition;
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
    private final Set<MobCondition> mobCondition;
    public MobConditionManager()
    {
        super();
        mobCondition=new HashSet<>();
    }
    
    public void addMobCondition(final MobCondition cond)
    {
        mobCondition.add(cond);
    }
    
    public boolean checkMob(final LivingEntity ent)
    {
        return mobCondition.stream().allMatch(mc -> mc.check(ent));
    } 
    
    public boolean checkAll(final Player p, final ItemStack tool, final Location l, final LivingEntity ent)
    {
        return checkPlayer(p) && checkTool(tool) && checkGeneric(l) && checkMob(ent);
    }
}
