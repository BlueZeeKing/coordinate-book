package dev.blueish.coordbook;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import dev.blueish.coordbook.data.Book;
import dev.blueish.coordbook.data.Position;
import dev.blueish.coordbook.gui.CreateScreen;
import dev.blueish.coordbook.gui.ListScreen;
import dev.blueish.coordbook.util.Config;
import dev.xpple.clientarguments.arguments.CBlockPosArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.text.Text;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;


public class CoordinateBook implements ClientModInitializer {
    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger("Coordbook");
    public static int lastPage = -1;
    public static Book book;

    public static String clientToName() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.isInSingleplayer()) {
            return Objects.requireNonNull(client.getServer()).getSavePath(WorldSavePath.ROOT).getParent().getFileName().toString();
        } else if (client.getCurrentServerEntry() != null) {
            return client.getCurrentServerEntry().address;
        } else {
            return "global";
        }
    }

    @Override
    public void onInitializeClient() {
        Config.init("coordbook", Config.class);

        KeyBinding open = KeyBindingHelper
                .registerKeyBinding(new KeyBinding("key.coordbook.open",
                        InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_O, "category.coordbook.coordbook"));

        KeyBinding create = KeyBindingHelper
                .registerKeyBinding(new KeyBinding("key.coordbook.create",
                        InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_K, "category.coordbook.coordbook"));

        KeyBinding open_last = KeyBindingHelper
                .registerKeyBinding(new KeyBinding("key.coordbook.open_last",
                        InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_R, "category.coordbook.coordbook"));

        KeyBinding send_coords = KeyBindingHelper
                .registerKeyBinding(new KeyBinding("key.coordbook.send_coords",
                        InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_MINUS, "category.coordbook.coordbook"));
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        ClientCommandRegistrationCallback.EVENT.register(this::registerCommands);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (book == null) {
                book = new Book();
            }

            while (open.wasPressed()) {
                client.setScreen(new ListScreen(book, 0));
            }

            while (create.wasPressed()) {
                if (client.player != null) {
                    client.setScreen(new CreateScreen(new Position(client.player.getX(), client.player.getY(), client.player.getZ())));
                }
            }

            while (open_last.wasPressed()) {
                client.setScreen(new ListScreen(book, lastPage == -1 ? 0 : lastPage));
            }

            while (send_coords.wasPressed()) {
                if (client.player != null) {
                    client.player.networkHandler.sendChatMessage(String.format("Coordinate Book: %d/%d/%d", (int) client.player.getX(), (int) client.player.getY(), (int) client.player.getZ()));
                }
            }
        });
    }

    private void registerCommands(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {

        dispatcher.register(literal("coordbook")
            .then(argument("pos", CBlockPosArgumentType.blockPos())
                .then(argument("name", StringArgumentType.greedyString())
                    .executes(ctx -> {
                      String name = StringArgumentType.getString(ctx, "name");
                      BlockPos pos = CBlockPosArgumentType.getCBlockPos(ctx, "pos");
                      LOGGER.info(pos.toString());
                      if (name.equals("coordinatebook-empty-this-is-too-long")) {
                          MinecraftClient.getInstance().send(() -> MinecraftClient.getInstance().setScreen(new CreateScreen(pos)));
                      } else {
                          MinecraftClient.getInstance().send(() -> MinecraftClient.getInstance().setScreen(new CreateScreen(pos, name)));
                      }
                      return 0;
                    }))));
    }
}
