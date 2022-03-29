package dev.blueish.coordbook;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.fabricmc.api.ClientModInitializer;

import java.util.ArrayList;
import java.util.Arrays;

import org.lwjgl.glfw.GLFW;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.gui.screen.ingame.BookScreen;
import net.minecraft.text.StringVisitable;

import dev.blueish.coordbook.gui.ListScreen;
import dev.blueish.coordbook.util.Contents;


public class CoordinateBook implements ClientModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("modid");

	@Override
	public void onInitializeClient() {
		KeyBinding open = KeyBindingHelper
				.registerKeyBinding(new KeyBinding("key.coordbook.open",
						InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_O, "category.coordbook.coordbook"));
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (open.wasPressed()) {
				//client.player.sendMessage(new LiteralText("Open was pressed!"), false);
				client.setScreen(new ListScreen(new Contents(new ArrayList<StringVisitable>(Arrays.asList(StringVisitable.plain("hi"), 
						StringVisitable.plain("2"))))));
			}
		});

		LOGGER.info("Hello Fabric world!");
	}
}
