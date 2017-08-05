/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.parozzz.hopedrop.condition.mob;

import java.util.function.Predicate;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;

/**
 *
 * @author Stefania
 */
public class CheckEquipment implements MobCondition
{
    
    private final Predicate<EntityEquipment> check;
    public CheckEquipment(final EquipmentSlot slot, final Material type)
    {
        switch(slot)
        {
            case HEAD:
                check = equip -> equip.getHelmet()!=null && equip.getHelmet().getType()==type;
                break;
            case CHEST:
                check = equip -> equip.getChestplate()!=null && equip.getChestplate().getType()==type;
                break;
            case LEGS:
                check = equip -> equip.getLeggings()!=null && equip.getLeggings().getType()==type;
                break;
            case FEET:
                check = equip -> equip.getBoots()!=null && equip.getBoots().getType()==type;
                break;
            case HAND:
                check = equip -> equip.getItemInMainHand()!=null && equip.getItemInMainHand().getType()==type;
                break;
            default:
                check = equip -> equip.getItemInOffHand()!=null && equip.getItemInOffHand().getType()==type;
                break;
        }
    }
    
    @Override
    public boolean check(LivingEntity ent) 
    {
        return check.test(ent.getEquipment());
    }
    
}
