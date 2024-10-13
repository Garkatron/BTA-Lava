package deus.lava.api.interfaces;

import org.luaj.vm2.LuaValue;

public interface IMinecraftLua {
	void lava$setCodeInRunTick(LuaValue codeBlock);
}
