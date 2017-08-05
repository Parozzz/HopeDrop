/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.parozzz.hopedrop.condition.mob;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

/**
 *
 * @author Stefania
 */
public class CheckKillReason implements MobCondition
{
    public enum KillReason
    {
        PLAYER,
        FALL,
        FIRE;
    }
    
    private final Predicate<LivingEntity> check;
    public CheckKillReason(final KillReason reason)
    {
        switch(reason)
        {
            case PLAYER:
                check = ent -> ent.getKiller()!=null;
                break;
            case FALL:
                check = ent -> ent.getLastDamageCause().getCause().equals(DamageCause.FALL);
                break;
            case FIRE:
                Set<DamageCause> causes=new HashSet<>(Arrays.asList(DamageCause.FIRE, DamageCause.LAVA, DamageCause.FIRE_TICK));
                check = ent -> causes.contains(ent.getLastDamageCause().getCause());
                break;
            default:
                check=null;
                break;
        }
    }
    
    @Override
    public boolean check(LivingEntity ent) 
    {
        return check.test(ent);
    }
    
}
