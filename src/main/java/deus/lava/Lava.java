package deus.lava;

import deus.lava.api.LavaSignals;
import deus.lava.command.EnvironmentCommand;
import deus.lava.command.ExecuteCommand;
import deus.lava.command.LavaCommand;
import deus.lava.setup.EnvironmentManager;
import deus.lava.setup.LuaSandbox;
import deus.lava.tool.ClassToTable;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.Minecraft;
import org.luaj.vm2.LuaTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import turniplabs.halplibe.helper.CommandHelper;
import turniplabs.halplibe.util.GameStartEntrypoint;
import turniplabs.halplibe.util.RecipeEntrypoint;

import java.io.File;
import java.io.IOException;


public class Lava implements ModInitializer, GameStartEntrypoint, RecipeEntrypoint {
	public static final String MOD_ID = "lava";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	private static Minecraft mc;
	private static Thread luaThread;
	public static File lavaFolder;

	@Override
	public void onInitialize() {
		LOGGER.info("Lava initialized.");


		CommandHelper.createCommand(new ExecuteCommand());
		CommandHelper.createCommand(new EnvironmentCommand());
		CommandHelper.createCommand(new LavaCommand());
 		LuaSandbox.init();

		LavaSignals.onInit.emit("null");

		File mcdir = Minecraft.getMinecraft(this).getMinecraftDir();

		File lavaFolder = new File(mcdir, "lava");

		try {
			if (lavaFolder.exists()) {
				System.out.println("The 'lava' folder already exists at: " + lavaFolder.getAbsolutePath());
			} else {
				if (lavaFolder.mkdirs()) {
					System.out.println("The 'lava' folder was successfully created at: " + lavaFolder.getAbsolutePath());
				} else {
					System.err.println("Unable to create the 'lava' folder. Please check permissions or the path.");
				}
			}
		} catch (Exception e) {
			System.err.println("An error occurred while creating the 'lava' folder: " + e.getMessage());
		}

		Lava.lavaFolder = lavaFolder;

		EnvironmentManager.loadFilesFromLavaFolder();

	}

	@Override
	public void beforeGameStart() {
		LavaSignals.beforeGameStart.emit("null");
	}

	@Override
	public void afterGameStart() {
		LavaSignals.afterGameStart.emit("null");
	}

	@Override
	public void onRecipesReady() {
		LavaSignals.onRecipesReady.emit("null");
	}

	@Override
	public void initNamespaces() {
		LavaSignals.initNamespaces.emit("null");
	}
}
