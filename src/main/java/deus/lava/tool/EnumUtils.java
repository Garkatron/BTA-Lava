package deus.lava.tool;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

public class EnumUtils {
	public static LuaTable enumToLuaTable(Class<? extends Enum<?>> clazz) {
		LuaTable types = new LuaTable();

		Enum<?>[] enumConstants = clazz.getEnumConstants();
		for (Enum<?> value : enumConstants) {
			types.set(value.name(), CoerceJavaToLua.coerce(value.ordinal()));
		}

		return types;
	}
}
