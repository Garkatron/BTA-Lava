package deus.lava;

import com.naef.jnlua.LuaState;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import turniplabs.halplibe.util.GameStartEntrypoint;
import turniplabs.halplibe.util.RecipeEntrypoint;

import java.io.File;


public class LavaMod implements ModInitializer, GameStartEntrypoint, RecipeEntrypoint {
    public static final String MOD_ID = "lava";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    @Override
	public void onInitialize() {
		// Detectar la arquitectura del sistema operativo
		// Determinar la arquitectura del sistema
		String arch = System.getProperty("os.arch").contains("64") ? "Win32-x64" : "Win32-x86";

		// Construir la ruta a la biblioteca nativa
		String nativePath = new File("src/main/resources/natives/jnlua-1.0.4/" + arch + "/jnlua52.dll").getAbsolutePath();

		try {
			// Cargar la biblioteca nativa
			System.load(nativePath);
			LOGGER.info("Biblioteca nativa cargada desde: " + nativePath);
		} catch (UnsatisfiedLinkError e) {
			LOGGER.error("Error al cargar la biblioteca nativa. Asegúrate de que la DLL exista y sea accesible.", e);
			return;
		}


		// Inicialización de JNLua y carga de scripts Lua
		try {
			LuaState lua = new LuaState();
			lua.openLibs();  // Abrir las bibliotecas estándar de Lua
			lua.load("print('Hello from Lua in Fabric Mod!')", "script");
			lua.call(0, 0);
			lua.close();
		} catch (Exception e) {
			LOGGER.error("Error ejecutando script Lua.", e);
		}

		LOGGER.info("Mod lava inicializado.");
	}

	@Override
	public void beforeGameStart() {

	}

	@Override
	public void afterGameStart() {

	}

	@Override
	public void onRecipesReady() {

	}

	@Override
	public void initNamespaces() {

	}
}
