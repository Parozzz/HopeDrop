/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.parozzz.hopedrop.condition.mob;

import java.util.function.Predicate;
import org.bukkit.entity.LivingEntity;

/**
 *
 * @author Stefania
 */
public class CheckName implements MobCondition
{
    private final Predicate<LivingEntity> check;
    public CheckName(final String name)
    {
        check= ent -> name.equals(ent.getCustomName());
    }
    
    @Override
    public boolean check(LivingEntity ent) 
    {
        return check.test(ent);
    }
    
}
