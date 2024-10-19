package deus.lava.setup;

import deus.lava.Lava;
import deus.lava.api.LavaSignals;
import deus.lava.api.MinecraftAccessor;
import deus.lava.api.interfaces.IConfigLuaGlobals;
import deus.lava.api.player.PlayerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.core.block.Block;
import net.minecraft.core.item.Item;
import net.minecraft.core.sound.SoundCategory;
import org.luaj.vm2.*;
import org.luaj.vm2.compiler.LuaC;
import org.luaj.vm2.lib.*;
import org.luaj.vm2.lib.jse.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static deus.lava.Lava.MOD_CONFIG;

public class LuaSandbox {
	private static final ExecutorService executor = Executors.newFixedThreadPool(10);
	private static final HashMap<String, CancellableTask> user_tasks = new HashMap<>();
	private static final Map<String, Future<?>> user_futures = new HashMap<>();
	private static Globals lua;

	public static void executeTask(String name, CancellableTask task) {
		user_tasks.put(name, task);

		Future<?> future = executor.submit(() -> {
			try {
				task.run();
			} catch (Exception e) {
				Lava.LOGGER.error("Error on task: {}", e);
			}
		});
		user_futures.put(name, future);
		Lava.LOGGER.info("Executing task in thread: {}", name);
	}

	public static boolean stopTask(String name) {
		if (user_tasks.containsKey(name)) {
			CancellableTask task = user_tasks.get(name);
			task.stop();

			Future<?> future = user_futures.remove(name);
			if (future != null) {
				future.cancel(true);
				Lava.LOGGER.info("Stopped and removed task: {}", name);
				return true;
			} else {
				Lava.LOGGER.warn("Task not found: {}", name);
				return false;
			}
		}
		return false;
	}

	public static void init() {
		lua = new Globals();

		lua.load(new JseBaseLib());
		lua.load(new PackageLib());
		lua.load(new JseStringLib());
		lua.load(new JseMathLib());
		LoadState.install(lua);
		LuaC.install(lua);

		LuaString.s_metatable = new ReadOnlyLuaTable(LuaString.s_metatable);
	}

	public static void exposeClasses(IConfigLuaGlobals lambda) {
		lambda.execute(lua);
	}

	public static void runFileFromPath(String path, Globals environment) {
		File file = new File(path);
		if (!file.exists()) {
			Lava.LOGGER.error("File not found: {}", path);
			return;
		}
		try (InputStream luaScript = Files.newInputStream(file.toPath())) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(luaScript));
			LuaValue chunk = lua.load(reader, path, environment);
			executeLuaThread(environment, chunk, path);
		} catch (Exception e) {
			Lava.LOGGER.error("Error when load the file: {}", e);
		}
	}


	public static void runScriptSandbox(String script, Globals environment) {

		LuaValue chunk = lua.load(script, "main", environment);
		executeLuaThread(environment, chunk, script);
	}

	public static Globals createUserGlobals(Object caller) {

		Minecraft mc = Minecraft.getMinecraft(caller);

		Globals user_globals = new Globals();
		user_globals.load(new JseBaseLib());
		user_globals.load(new PackageLib());
		user_globals.load(new Bit32Lib());
		user_globals.load(new TableLib());
		user_globals.load(new JseStringLib());
		user_globals.load(new JseMathLib());

		if (MOD_CONFIG.getConfig().getBoolean("libs.Coroutine"))
			user_globals.load(new CoroutineLib());

		if (MOD_CONFIG.getConfig().getBoolean("libs.OS"))
			user_globals.load(new OsLib());

		if (MOD_CONFIG.getConfig().getBoolean("libs.IO"))
			user_globals.load(new JseIoLib());

		if (MOD_CONFIG.getConfig().getBoolean("libs.LuaJava"))
			user_globals.load(new LuajavaLib());

		if (MOD_CONFIG.getConfig().getBoolean("libs.Debug"))
			user_globals.load(new DebugLib());

		user_globals.set("PlayerUtils", CoerceJavaToLua.coerce(new PlayerUtils()));

		// Accessors
		LuaValue minecraft = CoerceJavaToLua.coerce(new MinecraftAccessor(user_globals, mc));
		user_globals.set("Minecraft", minecraft);

		// Vanilla classes
		user_globals.set("Item", CoerceJavaToLua.coerce(Item.class));
		user_globals.set("SoundCategory", CoerceJavaToLua.coerce(SoundCategory.class));
		user_globals.set("Block", CoerceJavaToLua.coerce(Block.class));
		user_globals.set("SoundManager", CoerceJavaToLua.coerce(mc.sndManager));
		user_globals.set("Gui", CoerceJavaToLua.coerce(Gui.class));
		user_globals.set("Lava", CoerceJavaToLua.coerce(LavaSignals.class));

		return user_globals;
	}

	private static void executeLuaThread(Globals user_globals, LuaValue chunk, String scriptIdentifier) {

		LuaThread thread = new LuaThread(user_globals, chunk);

		try {
			Varargs result = thread.resume(LuaValue.NIL);
			System.out.println("[[" + scriptIdentifier + "]] -> " + result);
		} catch (NullPointerException e) {
			Lava.LOGGER.error("Error on executeLuaThread: {}", e.getMessage());
		}

	}

	static class ReadOnlyLuaTable extends LuaTable {
		public ReadOnlyLuaTable(LuaValue table) {
			presize(table.length(), 0);
			for (Varargs n = table.next(LuaValue.NIL); !n.arg1().isnil(); n = table
				.next(n.arg1())) {
				LuaValue key = n.arg1();
				LuaValue value = n.arg(2);
				super.rawset(key, value.istable() ? new ReadOnlyLuaTable(value) : value);
			}
		}

		public LuaValue setmetatable(LuaValue metatable) {
			return error("table is read-only");
		}

		public void set(int key, LuaValue value) {
			error("table is read-only");
		}

		public void rawset(int key, LuaValue value) {
			error("table is read-only");
		}

		public void rawset(LuaValue key, LuaValue value) {
			error("table is read-only");
		}

		public LuaValue remove(int pos) {
			return error("table is read-only");
		}
	}
}
