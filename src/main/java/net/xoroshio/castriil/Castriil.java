package net.xoroshio.castriil;

import com.mojang.logging.LogUtils;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.xoroshio.castriil.keys.KeyBindings;
import org.slf4j.Logger;

import java.util.List;

@Mod(Castriil.MOD_ID)
@Mod.EventBusSubscriber(modid = Castriil.MOD_ID)
public class Castriil {

    public static final String MOD_ID = "castriil";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static boolean CANCEL_SERVER_VELOCITY = true;
    public static boolean SPAM_ATTACK = true;
    public static int ATTACK_DELAY = 1;
    private static long TIME_SINCE_LAST_ATTACK = 0;

    public Castriil() {
        // Register EventBus
        MinecraftForge.EVENT_BUS.register(this);
    }

    public static boolean shouldGlowFor(Player player, Entity entity){
        return entity instanceof Player && entity.distanceTo(player) < 100;
    }

    public static boolean shouldCancelServerSetClientMotion(Player player, Vec3 motion){
        return CANCEL_SERVER_VELOCITY;
    }

    @SubscribeEvent
    public static void tick(TickEvent.ClientTickEvent e){

        Player player = Minecraft.getInstance().player;
        if(player == null)return;
        TIME_SINCE_LAST_ATTACK++;
        if(KeyBindings.OPEN_GUI.consumeClick()){
            Minecraft.getInstance().setScreen(new CastriilScreen());
        }

        if(Minecraft.getInstance().hitResult != null && Minecraft.getInstance().hitResult.getType() == HitResult.Type.ENTITY){
            if((SPAM_ATTACK && KeyBindings.ATTACK.isDown()) || (!SPAM_ATTACK && KeyBindings.ATTACK.consumeClick())){
                if(!SPAM_ATTACK || TIME_SINCE_LAST_ATTACK > ATTACK_DELAY){
                    Inventory inventory = player.getInventory();
                    inventory.selected = Util.getBestSwordIndex(inventory);
                    KeyMapping.click(Minecraft.getInstance().options.keyAttack.getKey());
                    TIME_SINCE_LAST_ATTACK = 0;
                }

            }

            if((SPAM_ATTACK && KeyBindings.ATTACK_THOUGH_WALLS.isDown()) || (!SPAM_ATTACK && KeyBindings.ATTACK_THOUGH_WALLS.consumeClick())){
                if(!SPAM_ATTACK || TIME_SINCE_LAST_ATTACK > ATTACK_DELAY) {
                    Inventory inventory = player.getInventory();
                    inventory.selected = Util.getBestSwordIndex(inventory);
                    MultiPlayerGameMode gameMode = Minecraft.getInstance().gameMode;
                    if (gameMode != null) {
                        Entity target = getAttack(player.level(), player);
                        if (target != null) {
                            gameMode.attack(player, target);
                        }
                    }
                    TIME_SINCE_LAST_ATTACK = 0;
                }
            }
        }
    }

    public static Entity getAttack(Level level, Player player){
        Entity entity = null;

        List<Entity> entities = level.getEntities(player, Util.around(player.position(), 6.0f), e -> {
            if(e instanceof Player target){
                if(player.getTeam() != null){
                    if(target.getTeam() != null){
                        return !target.getTeam().equals(player.getTeam());
                    }
                }
                return true;
            }
            return false;
        });

        float dis = Float.MAX_VALUE;
        for(Entity e : entities){
            float d = (float) e.distanceToSqr(player);
            if(d < dis){
                dis = d;
                entity = e;
            }
        }

        return entity;
    }

    public static void toggleAttackDelay(){
        if(ATTACK_DELAY++ >= 10)ATTACK_DELAY = 1;
    }

}
