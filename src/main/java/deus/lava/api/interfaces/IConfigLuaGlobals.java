package deus.lava.api.interfaces;

import org.luaj.vm2.Globals;

@FunctionalInterface
public interface IConfigLuaGlobals {

	void execute(Globals global);
}
