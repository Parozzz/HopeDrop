/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.parozzz.hopedrop.condition;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import me.parozzz.hopedrop.utilities.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Stefania
 */
public class MobCondition 
{
    public enum MobConditionType 
    {
        BABY, EQUIPMENT, KILLREASON, NAME, ONFIRE;
    }
    
    private Predicate<LivingEntity> condition;
    public MobCondition()
    {
        condition=ent -> true;
    }
    
    public void addAgeCheck(final boolean baby)
    {
        condition=condition.and(ent -> ent instanceof Ageable && ((Ageable)ent).isAdult()!=baby);
    }
    
    public void addEquipmentCheck(final EquipmentSlot slot, final Material type)
    {
        switch(slot)
        {
            case HEAD:
                condition=condition.and(ent -> ent.getEquipment().getHelmet()!=null && ent.getEquipment().getHelmet().getType()==type);
                break;
            case CHEST:
                condition=condition.and(ent -> ent.getEquipment().getChestplate()!=null && ent.getEquipment().getChestplate().getType()==type);
                break;
            case LEGS:
                condition=condition.and(ent -> ent.getEquipment().getLeggings()!=null && ent.getEquipment().getLeggings().getType()==type);
                break;
            case FEET:
                condition=condition.and(ent -> ent.getEquipment().getBoots()!=null && ent.getEquipment().getBoots().getType()==type);
                break;
            case HAND:
                condition=condition.and(ent -> Utils.getMainHand(ent.getEquipment())!=null && Utils.getMainHand(ent.getEquipment()).getType()==type);
                break;
            default:
                condition=condition.and(ent -> ent.getEquipment().getItemInOffHand()!=null && ent.getEquipment().getItemInOffHand().getType()==type);
                break;
        }
    }
    
    public enum KillReason
    {
        PLAYER,
        FALL,
        FIRE;
    }
    
    public void addKillReasonCheck(final KillReason reason)
    {
        switch(reason)
        {
            case PLAYER:
                condition=condition.and(ent -> ent.getKiller()!=null);
                break;
            case FALL:
                condition=condition.and(ent -> ent.getLastDamageCause().getCause().equals(EntityDamageEvent.DamageCause.FALL));
                break;
            case FIRE:
                EnumSet<DamageCause> causes=EnumSet.of(DamageCause.FIRE, DamageCause.LAVA, DamageCause.FIRE_TICK);
                condition=condition.and(ent -> causes.contains(ent.getLastDamageCause().getCause()));
                break;
        }
    }
    
    public void addOnFireCheck(final boolean onFire)
    {
        condition=condition.and(onFire? ent -> ent.getFireTicks()!=-1 : ent -> ent.getFireTicks()==-1);
    }
    
    public void addNameCheck(final String name)
    {
        condition=condition.and(ent -> ent.getCustomName()!=null && ent.getCustomName().equals(name));
    }
    
    public boolean checkAll(final LivingEntity ent)
    {
        return condition.test(ent);
    }
}
