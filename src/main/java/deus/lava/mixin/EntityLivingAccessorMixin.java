package deus.lava.mixin;

import deus.lava.api.interfaces.IEntityLivingLua;
import deus.lava.signal.LuaSignal;
import net.minecraft.core.entity.EntityLiving;
import net.minecraft.core.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityLiving.class)
public class EntityLivingAccessorMixin implements IEntityLivingLua {

	@Unique LuaSignal onIsInWater = new LuaSignal();
	@Unique LuaSignal onIsInLava = new LuaSignal();
	@Unique LuaSignal onJump = new LuaSignal();
	@Unique LuaSignal onLabelled = new LuaSignal();
	@Unique LuaSignal onEatFood = new LuaSignal();
	@Unique LuaSignal onDropFewItems = new LuaSignal();

	@Inject(method = "jump()V", at=@At("TAIL"), remap = false)
	private void modifiedJump(CallbackInfo ci) {
		onJump.emit("null");
	}

	@Inject(method = "onLabelled", at=@At("TAIL"), remap = false)
	private void modifiedOnLabelled(CallbackInfo ci) {
		onLabelled.emit("null");
	}

	@Inject(method = "eatFood", at=@At("TAIL"), remap = false)
	private void modifiedOnEatFood(CallbackInfo ci) {
		onEatFood.emit("null");
	}

	@Inject(method = "dropFewItems()V", at=@At("TAIL"), remap = false)
	private void modifiedOnSropFewItems(CallbackInfo ci) {
		onDropFewItems.emit("null");
	}
}
