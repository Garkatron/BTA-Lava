package deus.lava.mixin;


import deus.lava.api.interfaces.IEntityPlayerLuaCommon;
import deus.lava.signal.LuaSignal;
import net.minecraft.client.entity.player.EntityPlayerSP;
import net.minecraft.core.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayerSP.class)
public class EntityPlayerSPAccessorMixin implements IEntityPlayerLuaCommon {

	//LuaSignal onBreakBlock = new LuaSignal();
	@Unique
	LuaSignal onPickUpItem = new LuaSignal();
	@Unique
	LuaSignal onDropItem = new LuaSignal();

	@Unique LuaSignal onDeath = new LuaSignal();

	@Inject(method = "onItemPickup(Lnet/minecraft/core/entity/Entity;I)V", at=@At("TAIL"), remap = false)
	private void onItemPickUp(Entity entity, int i, CallbackInfo ci) {
		onPickUpItem.emit(null); // Aquí accedes a la señal directamente
	}

}
