/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.parozzz.hopedrop.drop.item;

import java.util.Optional;
import me.parozzz.hopedrop.Utils;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

/**
 *
 * @author Paros
 */
public class AmountEnchantModifier implements AmountModifier
{
    private final NumberManager manager;
    private final Enchantment ench;
    public AmountEnchantModifier(final Enchantment ench, final int min, final int max)
    {
        this.ench=ench;
        
        manager=new NumberManager(min, max);
    }

    @Override
    public NumberManager getNumbers(Player p) 
    {
        return Optional.ofNullable(Utils.getHand(p))
                .map(item -> item.getEnchantmentLevel(ench))
                .map(level -> manager.getMultipliedClone(level))
                .orElseGet(() -> NumberManager.getEmptyManager());
    }
}
