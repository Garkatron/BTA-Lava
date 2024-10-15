package deus.lava.mixin;

import deus.lava.api.interfaces.IEntityLivingLua;
import deus.lava.signal.LuaSignal;
import net.minecraft.core.entity.EntityLiving;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityLiving.class)
public class EntityLivingAccessorMixin implements IEntityLivingLua {

	@Unique
	public LuaSignal onJump = new LuaSignal();
	@Unique
	public LuaSignal onLabelled = new LuaSignal();
	@Unique
	public LuaSignal onEatFood = new LuaSignal();
	@Unique
	public LuaSignal onDropFewItems = new LuaSignal();
	@Unique
	public LuaSignal onDeath = new LuaSignal();

	@Inject(method = "onDeath(Lnet/minecraft/core/entity/Entity;)V", at = @At("TAIL"), remap = false)
	private void modifiedDeath(CallbackInfo ci) {
		EntityLiving entityLiving = (EntityLiving) (Object) this;
		onDeath.emit("null");
	}

	@Inject(method = "jump()V", at = @At("TAIL"), remap = false)
	private void modifiedJump(CallbackInfo ci) {
		onJump.emit("null");
	}

	@Inject(method = "onLabelled", at = @At("TAIL"), remap = false)
	private void modifiedOnLabelled(CallbackInfo ci) {
		onLabelled.emit("null");
	}

	@Inject(method = "eatFood", at = @At("TAIL"), remap = false)
	private void modifiedOnEatFood(CallbackInfo ci) {
		onEatFood.emit("null");
	}

	@Inject(method = "dropFewItems()V", at = @At("TAIL"), remap = false)
	private void modifiedOnSropFewItems(CallbackInfo ci) {
		onDropFewItems.emit("null");
	}
}
