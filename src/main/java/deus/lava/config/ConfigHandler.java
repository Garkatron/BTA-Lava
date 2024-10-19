package deus.lava.config;

import turniplabs.halplibe.util.TomlConfigHandler;
import turniplabs.halplibe.util.toml.Toml;

import static deus.lava.Lava.MOD_ID;

public class ConfigHandler {

	private static final TomlConfigHandler config;

	static {
		Toml toml = new Toml("LavaConfig");

		toml.addCategory("libs")
			.addEntry("OS", false)
			.addEntry("IO", false)
			.addEntry("LuaJava", false)
			.addEntry("Debug", false)
			.addEntry("Coroutine", false);


		config = new TomlConfigHandler(null, MOD_ID, toml);
	}


	public TomlConfigHandler getConfig() {
		return config;
	}
}
