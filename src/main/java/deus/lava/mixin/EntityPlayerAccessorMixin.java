package deus.lava.mixin;

import deus.lava.api.interfaces.IEntityPlayerLua;
import deus.lava.signal.LuaSignal;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;

@Mixin(EntityPlayer.class)
public abstract class EntityPlayerAccessorMixin implements IEntityPlayerLua {

	@Shadow
	public double xdO;
	@Shadow
	public double ydO;
	@Shadow
	public double zd0;
	@Unique
	LuaSignal onPickUpItem = new LuaSignal();
	@Unique
	LuaSignal onDropItem = new LuaSignal();

	@Shadow
	protected abstract void setPlayerSleeping(int x, int y, int z);

	@Inject(method = "onItemPickup(Lnet/minecraft/core/entity/Entity;I)V", at = @At("TAIL"), remap = false)
	private void onItemPickUp(Entity entity, int i, CallbackInfo ci) {
		HashMap<String, Object> data = new HashMap<>();
		data.put("Entity", entity);
		data.put("i", i);

		onPickUpItem.emit(data);
	}

	@Inject(method = "dropCurrentItem(Z)V", at = @At("TAIL"), remap = false)
	private void onDropCurrentItem(boolean dropFullStack, CallbackInfo ci) {
		onDropItem.emit(dropFullStack);
	}
}
