package deus.lava.mixin;

import deus.lava.api.MinecraftAccessor;
import deus.lava.api.interfaces.IMinecraftLua;
import net.minecraft.client.Minecraft;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.world.World;
import org.luaj.vm2.LuaValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;

@Mixin(Minecraft.class)
public class MinecraftMixin implements IMinecraftLua {

	@Unique LuaValue luaCode;

	@Inject(method = "runTick", at = @At("TAIL"), remap = false)
	private void modifiedJump(CallbackInfo ci) {
		if (luaCode != null) {
			luaCode.call();
		}
	}

	@Inject(method = "startWorld(Ljava/lang/String;Ljava/lang/String;J)V", at = @At("TAIL"), remap = false)
	private void onStartWorld(String worldDirName, String worldName, long seed, CallbackInfo ci) {
		HashMap<String, Object> data = new HashMap<>();
		data.put("WorldDirName", worldDirName);
		data.put("WorldName", worldName);
		data.put("Seed", seed);
		MinecraftAccessor.onWorldStarted.emit(data);
	}

	@Inject(method = "changeWorld(Lnet/minecraft/core/world/World;)V", at = @At("TAIL"), remap = false)
	private void onChangeWorld(World world, CallbackInfo ci) {
		HashMap<String, Object> data = new HashMap<>();
		data.put("World", world);
		MinecraftAccessor.onWorldChanged.emit(data);
	}

	@Inject(method = "changeWorld(Lnet/minecraft/core/world/World;Ljava/lang/String;Lnet/minecraft/core/entity/player/EntityPlayer;)V", at = @At("TAIL"), remap = false)
	private void onChangeWorld(World world, String loadingTitle, EntityPlayer player, CallbackInfo ci) {
		HashMap<String, Object> data = new HashMap<>();
		data.put("World", world);
		data.put("LoadingTitle", loadingTitle);
		data.put("Player", player);
		MinecraftAccessor.onWorldChanged.emit(data);
	}

	@Inject(method = "changeWorld(Lnet/minecraft/core/world/World;Ljava/lang/String;)V", at = @At("TAIL"), remap = false)
	private void onChangeWorld(World world, String loadingTitle, CallbackInfo ci) {
		HashMap<String, Object> data = new HashMap<>();
		data.put("World", world);
		data.put("LoadingTitle", loadingTitle);
		MinecraftAccessor.onWorldChanged.emit(data);
	}

	@Override
	public void lava$setCodeInRunTick(LuaValue codeBlock) {
		this.luaCode = codeBlock;
	}
}
