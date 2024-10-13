package deus.lava;

import deus.lava.api.*;
import deus.lava.api.player.PlayerUtils;
import deus.lava.command.ExecuteCommand;
import deus.lava.setup.LuaSandbox;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.core.block.Block;
import net.minecraft.core.item.Item;
import net.minecraft.core.sound.SoundCategory;
import org.luaj.vm2.*;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import turniplabs.halplibe.helper.CommandHelper;
import turniplabs.halplibe.util.GameStartEntrypoint;
import turniplabs.halplibe.util.RecipeEntrypoint;



public class Lava implements ModInitializer, GameStartEntrypoint, RecipeEntrypoint {
	public static final String MOD_ID = "lava";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	private static Minecraft mc;

	@Override
	public void onInitialize() {
		LOGGER.info("Lava initialized.");


		CommandHelper.createCommand(new ExecuteCommand());

		mc = Minecraft.getMinecraft(this);

		LuaSandbox.init();
		LuaSandbox.exposeUserClasses(globals -> {

			// Utils
			globals.set("PlayerUtils", CoerceJavaToLua.coerce(new PlayerUtils()));

			// Accessors
			LuaValue minecraft = CoerceJavaToLua.coerce(new MinecraftAccessor(globals, mc));
			globals.set("Minecraft", minecraft);

			// Vanilla classes
			globals.set("Item", CoerceJavaToLua.coerce(Item.class));
			globals.set("SoundCategory", CoerceJavaToLua.coerce(SoundCategory.class));
			globals.set("Block", CoerceJavaToLua.coerce(Block.class));
			globals.set("SoundManager", CoerceJavaToLua.coerce(mc.sndManager));
			globals.set("Gui", CoerceJavaToLua.coerce(Gui.class));
			globals.set("Lava", CoerceJavaToLua.coerce(LavaSignals.class));
		});

		LuaSandbox.runFileSandbox("script.lua");

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
