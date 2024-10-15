package deus.lava.mixin;

import deus.lava.signal.LuaSignal;
import net.minecraft.core.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityAccessorMixin {

	@Unique
	public LuaSignal onIsInWater = new LuaSignal();
	@Unique
	public LuaSignal onIsInLava = new LuaSignal();

	@Inject(method = "isInLava()Z", at = @At("TAIL"), remap = false)
	private void modifiedIsInLava(CallbackInfoReturnable<Boolean> cir) {
		if (cir.getReturnValue())
			onIsInLava.emit("null");

	}

	@Inject(method = "isInWater()Z", at = @At("TAIL"), remap = false)
	private void modifiedIsInWater(CallbackInfoReturnable<Boolean> cir) {
		if (cir.getReturnValue())
			onIsInWater.emit("null");
	}

}
