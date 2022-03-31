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
import net.minecraft.util.Formatting;
import net.minecraft.text.StringVisitable;

import dev.blueish.coordbook.gui.ListScreen;
import dev.blueish.coordbook.gui.CreateScreen;
import dev.blueish.coordbook.util.Book;
import dev.blueish.coordbook.util.TextCreator;
import dev.blueish.coordbook.util.Coord;
import dev.blueish.coordbook.util.Position;


public class CoordinateBook implements ClientModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("Coordbook");

	@Override
	public void onInitializeClient() {
		KeyBinding open = KeyBindingHelper
				.registerKeyBinding(new KeyBinding("key.coordbook.open",
						InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_O, "category.coordbook.coordbook"));

		KeyBinding create = KeyBindingHelper
				.registerKeyBinding(new KeyBinding("key.coordbook.create",
						InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_K, "category.coordbook.coordbook"));
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (open.wasPressed()) {
				//client.player.sendMessage(new LiteralText("Open was pressed!"), false);
				new Coord(new Position(0, 100, 0), "hi", Formatting.AQUA, "overworld");
				client.setScreen(new ListScreen(new Book(1)));
			}

			while (create.wasPressed()) {
				client.setScreen(new CreateScreen(new Position(client.player.getX(), client.player.getY(), client.player.getZ())));
			}
		});

		LOGGER.info("Hello Fabric world!");
	}
}
