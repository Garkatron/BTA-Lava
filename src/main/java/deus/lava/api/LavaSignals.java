package deus.lava.api;

import deus.lava.signal.LuaSignal;

public class LavaSignals {
	public static LuaSignal onInit = new LuaSignal();
	public static LuaSignal beforeGameStart = new LuaSignal();
	public static LuaSignal afterGameStart = new LuaSignal();
	public static LuaSignal onRecipesReady = new LuaSignal();
	public static LuaSignal initNamespaces = new LuaSignal();
}
