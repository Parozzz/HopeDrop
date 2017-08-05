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
public class CheckMobOnFire implements MobCondition
{
    private final Predicate<LivingEntity> check;
    public CheckMobOnFire(final boolean onFire)
    {
        check = onFire? ent -> ent.getFireTicks()!=-1 : ent -> ent.getFireTicks()==-1;
    }
    
    @Override
    public boolean check(LivingEntity ent) 
    {
        return check.test(ent);
    }
    
}
