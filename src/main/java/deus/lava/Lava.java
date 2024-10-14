package deus.lava;

import deus.lava.api.*;
import deus.lava.api.player.PlayerUtils;
import deus.lava.command.EnvironmentCommand;
import deus.lava.command.ExecuteCommand;
import deus.lava.command.LavaCommand;
import deus.lava.setup.LuaSandbox;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.core.Global;
import net.minecraft.core.block.Block;
import net.minecraft.core.item.Item;
import net.minecraft.core.sound.SoundCategory;
import org.luaj.vm2.*;
import org.luaj.vm2.compiler.LuaC;
import org.luaj.vm2.lib.CoroutineLib;
import org.luaj.vm2.lib.PackageLib;
import org.luaj.vm2.lib.jse.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import turniplabs.halplibe.helper.CommandHelper;
import turniplabs.halplibe.util.GameStartEntrypoint;
import turniplabs.halplibe.util.RecipeEntrypoint;

import java.util.HashMap;


public class Lava implements ModInitializer, GameStartEntrypoint, RecipeEntrypoint {
	public static final String MOD_ID = "lava";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	private static Minecraft mc;
	private static Thread luaThread;

	@Override
	public void onInitialize() {
		LOGGER.info("Lava initialized.");


		CommandHelper.createCommand(new ExecuteCommand());
		CommandHelper.createCommand(new EnvironmentCommand());
		CommandHelper.createCommand(new LavaCommand());
////
 		LuaSandbox.init();
//		LuaSandbox.exposeUserClasses(globals -> {
//
//			// Utils
//
//		});

		//LuaSandbox.runFileSandbox("script.lua");

		LavaSignals.onInit.emit("null");
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
