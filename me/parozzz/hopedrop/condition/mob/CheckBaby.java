/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.parozzz.hopedrop.condition.mob;

import java.util.function.Predicate;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.LivingEntity;

/**
 *
 * @author Stefania
 */
public class CheckBaby implements MobCondition
{

    private final Predicate<LivingEntity> check;
    public CheckBaby(final boolean baby)
    {
        check = ent -> ent instanceof Ageable && ((Ageable)ent).isAdult()!=baby;
    }
    
    @Override
    public boolean check(LivingEntity ent) 
    {
        return check.test(ent);
    }
    
}
