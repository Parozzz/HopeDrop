/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.parozzz.hopedrop.condition;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import me.parozzz.hopedrop.Utils;
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
    
    private final Set<Predicate<LivingEntity>> conditions;
    public MobCondition()
    {
        conditions=new HashSet<>();
    }
    
    public void addAgeCheck(final boolean baby)
    {
        conditions.add(ent -> ent instanceof Ageable && ((Ageable)ent).isAdult()!=baby);
    }
    
    public void addEquipmentCheck(final EquipmentSlot slot, final Material type)
    {
        switch(slot)
        {
            case HEAD:
                conditions.add(ent -> ent.getEquipment().getHelmet()!=null && ent.getEquipment().getHelmet().getType()==type);
                break;
            case CHEST:
                conditions.add(ent -> ent.getEquipment().getChestplate()!=null && ent.getEquipment().getChestplate().getType()==type);
                break;
            case LEGS:
                conditions.add(ent -> ent.getEquipment().getLeggings()!=null && ent.getEquipment().getLeggings().getType()==type);
                break;
            case FEET:
                conditions.add(ent -> ent.getEquipment().getBoots()!=null && ent.getEquipment().getBoots().getType()==type);
                break;
            case HAND:
                conditions.add(ent -> 
                { 
                    ItemStack hand=Utils.getMainHand(ent.getEquipment());
                    return hand!=null && hand.getType()==type;
                });
                break;
            default:
                conditions.add(ent -> ent.getEquipment().getItemInOffHand()!=null && ent.getEquipment().getItemInOffHand().getType()==type);
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
                conditions.add(ent -> ent.getKiller()!=null);
                break;
            case FALL:
                conditions.add(ent -> ent.getLastDamageCause().getCause().equals(EntityDamageEvent.DamageCause.FALL));
                break;
            case FIRE:
                EnumSet<DamageCause> causes=EnumSet.of(DamageCause.FIRE, DamageCause.LAVA, DamageCause.FIRE_TICK);
                conditions.add(ent -> causes.contains(ent.getLastDamageCause().getCause()));
                break;
        }
    }
    
    public void addOnFireCheck(final boolean onFire)
    {
        conditions.add(onFire? ent -> ent.getFireTicks()!=-1 : ent -> ent.getFireTicks()==-1);
    }
    
    public void addNameCheck(final String name)
    {
        conditions.add(ent -> ent.getCustomName()!=null && ent.getCustomName().equals(name));
    }
    
    public boolean checkAll(final LivingEntity ent)
    {
        return conditions.stream().allMatch(pr -> pr.test(ent));
    }
}
