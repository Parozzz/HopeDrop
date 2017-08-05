/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.parozzz.hopedrop.condition.mob;

import org.bukkit.entity.LivingEntity;

/**
 *
 * @author Stefania
 */
public interface MobCondition 
{
    boolean check(final LivingEntity ent);
}
