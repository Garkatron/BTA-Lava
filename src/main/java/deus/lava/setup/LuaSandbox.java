package deus.lava.setup;

import deus.lava.Lava;
import deus.lava.api.interfaces.IConfigLuaGlobals;
import org.luaj.vm2.*;
import org.luaj.vm2.compiler.LuaC;
import org.luaj.vm2.lib.*;
import org.luaj.vm2.lib.jse.CoerceLuaToJava;
import org.luaj.vm2.lib.jse.JseBaseLib;
import org.luaj.vm2.lib.jse.JseMathLib;
import org.luaj.vm2.lib.jse.JseStringLib;

import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class LuaSandbox {
	private static Globals lua;
	private static final Globals user_globals = new Globals();
	//private static final ExecutorService executor = Executors.newFixedThreadPool(10);
	//private static final HashMap<String, Future<?>> luaThreads = new HashMap<>();

//	public static boolean stopLuaTask(String name) {
//		// Array to hold active threads
//		if (!luaThreads.containsKey(name)) {
//			Lava.LOGGER.error("Task: {} not found", name);
//			return false;
//		}
//
//		Future<?> future = luaThreads.get(name);
//
//		if (future==null) {
//			Lava.LOGGER.error("Task: {} future null", name);
//			return false;
//		}
//
//		future.cancel(true);
//		Lava.LOGGER.info("Task: {} canceled", name);
//
//		return true;
//	}
//
//	public static void executeTask(String name, Runnable task) {
//		Future<?> future = executor.submit(()->{
//			try {
//				task.run();
//			} catch (Exception e) {
//				Lava.LOGGER.error("Error on task: {}", e);
//			}
//		}); // Enviar el hilo al executorç
//		luaThreads.put(name, future); // Guarda el Future en vez del Thread
//		Lava.LOGGER.info("Executing task in thread: {}", name);
//	}

	public static void init() {
		lua = new Globals();

		lua.load(new JseBaseLib());
		lua.load(new PackageLib());
		lua.load(new JseStringLib());
		lua.load(new JseMathLib());
		LoadState.install(lua);
		LuaC.install(lua);

		// Set up the LuaString metatable to be read-only since it is shared across all scripts.
		LuaString.s_metatable = new ReadOnlyLuaTable(LuaString.s_metatable);
	}

	public static void exposeClasses(IConfigLuaGlobals lambda) {
		lambda.execute(lua);
	}

	public static void exposeUserClasses(IConfigLuaGlobals lambda) {
		lambda.execute(user_globals);
	}


	// Método para detener un LuaThread específico
	public static boolean stopLuaThread(String name) {
		if (!name.isEmpty()) {

			//if (!user_globals.get("running").isnil())
			LuaValue greetFunction = user_globals.get("stop");

			if (greetFunction.isfunction()) {
				greetFunction.call();
			} else {
				System.err.println("La función 'stop' no se encontró o no es una función.");
			}

			return true;
		}

		Lava.LOGGER.error("Name is empty");
		return false;
	}



	public static void runFileSandbox(String scriptPath) {
		// Cada script tendrá su propio conjunto de globals, lo que debería
		// prevenir la fuga entre scripts que se ejecutan en el mismo servidor.
		Globals user_globals = createUserGlobals();

		// Cargar el script Lua desde un archivo.
		try (InputStream luaScript = LuaSandbox.class.getClassLoader().getResourceAsStream(scriptPath)) {
			if (luaScript == null) {
				Lava.LOGGER.error("No se pudo encontrar el archivo script.lua");
				return;
			}

			// Usar BufferedReader para leer el InputStream
			BufferedReader reader = new BufferedReader(new InputStreamReader(luaScript));
			LuaValue chunk = lua.load(reader, scriptPath, user_globals);  // Cargar y ejecutar el script
			executeLuaThread(user_globals, chunk, scriptPath);
		} catch (Exception e) {
			Lava.LOGGER.error("Error al cargar el script Lua: ", e);
		}
	}

	public static void runFileFromPath(String path, Globals environment) {
		File file = new File(path);
		if (!file.exists()) {
			Lava.LOGGER.error("No se pudo encontrar el archivo {}", path);
			return;
		}
		try (InputStream luaScript = Files.newInputStream(file.toPath())) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(luaScript));
			//Globals user_globals = createUserGlobals();
			LuaValue chunk = lua.load(reader, path, environment);
			executeLuaThread(environment, chunk, path);
		} catch (Exception e) {
			Lava.LOGGER.error("Error al cargar el script Lua: ", e);
		}
	}


	public static void runScriptSandbox(String script, Globals environment) {
		// Cada script tendrá su propio conjunto de globals, lo que debería
		// prevenir la fuga entre scripts que se ejecutan en el mismo servidor.
		//Globals user_globals = createUserGlobals();

		// Cargar y ejecutar el script desde la cadena.
		LuaValue chunk = lua.load(script, "main", environment);
		executeLuaThread(environment, chunk, script);
	}

	public static Globals createUserGlobals() {

		user_globals.load(new JseBaseLib());
		user_globals.load(new PackageLib());
		user_globals.load(new Bit32Lib());
		user_globals.load(new TableLib());
		user_globals.load(new JseStringLib());
		user_globals.load(new JseMathLib());
		user_globals.load(new CoroutineLib());


		return user_globals;
	}

	private static void executeLuaThread(Globals user_globals, LuaValue chunk, String scriptIdentifier) {

		LuaThread thread = new LuaThread(user_globals, chunk);
		// Configurar la función hook para lanzar un Error inmediatamente, que no será
		// manejado por ningún código Lua excepto por la coroutine.
		LuaValue hookfunc = new ZeroArgFunction() {
			public LuaValue call() {
				// Un error Lua simple puede ser atrapado por el script, pero un
				// Error de Java pasará al nivel superior y detendrá el script.
				throw new Error("Script overran resource limits.");
			}
		};

		final int instruction_count = 30000;  // Limitar a 30,000 instrucciones
		// La biblioteca de depuración debe ser cargada para que las funciones hook funcionen,
		// lo que nos permite limitar la ejecución de scripts a un cierto número de instrucciones a la vez.
		// Sin embargo, no deseamos exponer la biblioteca en los globals del usuario,
		// por lo que se elimina inmediatamente una vez creada.
		user_globals.load(new DebugLib());

		LuaValue sethook = user_globals.get("debug").get("sethook");

		user_globals.set("debug", LuaValue.NIL);
		sethook.invoke(LuaValue.varargsOf(new LuaValue[] { thread, hookfunc,
			LuaValue.EMPTYSTRING, LuaValue.valueOf(instruction_count) }));

		// Cuando reanudemos el hilo, ejecutará hasta 'instruction_count' instrucciones
		// y luego llamará a la función hook que provocará un error y detendrá el script.
		Varargs result = thread.resume(LuaValue.NIL);

		System.out.println("[[" + scriptIdentifier + "]] -> " + result);
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
		public LuaValue setmetatable(LuaValue metatable) { return error("table is read-only"); }
		public void set(int key, LuaValue value) { error("table is read-only"); }
		public void rawset(int key, LuaValue value) { error("table is read-only"); }
		public void rawset(LuaValue key, LuaValue value) { error("table is read-only"); }
		public LuaValue remove(int pos) { return error("table is read-only"); }
	}
}
