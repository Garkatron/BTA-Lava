package deus.lava.mixin;

import deus.lava.api.interfaces.IEntityPlayerLua;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
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

	@Shadow
	protected abstract void setPlayerSleeping(int x, int y, int z);

	@Inject(method = "jump()V", at=@At("TAIL"), remap = false)
	private void modifiedJump(CallbackInfo ci) {
		EntityPlayer player = (EntityPlayer) (Object) this;
		onJump.emit(player.username);
	}

	@Inject(method = "onDeath(Lnet/minecraft/core/entity/Entity;)V", at=@At("TAIL"), remap = false)
	private void modifiedDeath(CallbackInfo ci) {
		EntityPlayer player = (EntityPlayer) (Object) this;
		onDeath.emit(player.username);
	}

	@Inject(method = "onItemPickup(Lnet/minecraft/core/entity/Entity;I)V", at=@At("TAIL"), remap = false)
	private void onItemPickUp(Entity entity, int i, CallbackInfo ci) {
		// Crear un HashMap para almacenar la información
		HashMap<String, Object> data = new HashMap<>();
		data.put("Entity", entity);
		data.put("i", i);

		// Emitir el HashMap como un único objeto
		onPickUpItem.emit(data);
	}
	@Inject(method = "dropCurrentItem(Z)V", at=@At("TAIL"), remap = false)
	private void onDropCurrentItem(boolean dropFullStack, CallbackInfo ci) {
		onDropItem.emit(dropFullStack);
	}

	// * ERROR

	@Override
	public void lava$sleepPlayer() {
		EntityPlayer player = (EntityPlayer) (Object) this;

		setPlayerSleeping((int) player.x, (int) player.y, (int) player.z);
	}

	@Override
	public void lava$sleepPlayerAtCoords(int x, int y, int z) {
		setPlayerSleeping(x, y, z);
	}
}
