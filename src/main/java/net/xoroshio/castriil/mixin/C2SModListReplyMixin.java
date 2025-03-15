package net.xoroshio.castriil.mixin;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.HandshakeMessages;
import net.xoroshio.castriil.Castriil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@OnlyIn(Dist.CLIENT)
@Mixin(HandshakeMessages.C2SModListReply.class)
public class C2SModListReplyMixin {



    @Inject(method = "<init>*", at = @At("RETURN"))
    public void onConstructed(CallbackInfo ci){
        List<String> mods = getModList();
        Castriil.LOGGER.debug("C2SModListReply: {}", mods.toString());
        mods.remove(Castriil.MOD_ID);
        Castriil.LOGGER.debug("C2SModListReply (after modification/spoof): {}", mods);
    }

    @Shadow(remap = false)
    public List<String> getModList(){
        throw new IllegalStateException("Mixin failed to shadow getModList()");
    }
}
