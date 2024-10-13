package deus.lava.api;

import deus.lava.Lava;
import deus.lava.api.interfaces.IMinecraftLua;
import deus.lava.signal.LuaSignal;
import net.minecraft.client.Minecraft;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;

public class MinecraftAccessor {

	private final Globals luaGlobals;
	private final Minecraft minecraft;

	public static LuaSignal onWorldStarted = new LuaSignal();
	public static LuaSignal onWorldChanged = new LuaSignal();

	public MinecraftAccessor(Globals luaGlobals, Minecraft minecraft) {
		this.luaGlobals = luaGlobals;
		this.minecraft = minecraft;
	}

	public void whenTickRun(String luaCode) {

		LuaValue result = luaGlobals.get(luaCode);
		if (result.isfunction()) {
			IMinecraftLua luamc = (IMinecraftLua) minecraft;
			luamc.lava$setCodeInRunTick(result);
		} else {
			Lava.LOGGER.error("It's not a function");
		}
	}

	public Minecraft getMinecraft() {
		return minecraft;
	}
}
