package net.xoroshio.castriil;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.function.Consumer;

public class Util {

    public static void forEachItemStack(Inventory inventory, Consumer<ItemStack> consumer){
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if(!stack.isEmpty()){
                consumer.accept(stack);
            }
        }
    }

    public static AABB around(Vec3 pos, float radius){
        return new AABB(pos.x - radius, pos.y - radius, pos.z - radius, pos.x + radius, pos.y + radius, pos.z + radius);
    }

    public static int getBestSwordIndex(Inventory inventory){
        int j = inventory.selected;
        float d = 0.0f;
        for(int i = 0; i < 9; i++){
            ItemStack stack = inventory.getItem(i);
            if(stack.getItem() instanceof SwordItem sword){
                float s = sword.getDamage();
                if(s > d){
                    d = s;
                    j = i;
                }
            }
        }
        return j;
    }
}
