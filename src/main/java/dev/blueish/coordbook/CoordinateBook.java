package dev.blueish.coordbook;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import dev.blueish.coordbook.data.Book;
import dev.blueish.coordbook.data.Position;
import dev.blueish.coordbook.gui.CoordOverlay;
import dev.blueish.coordbook.gui.CreateScreen;
import dev.blueish.coordbook.gui.ListScreen;
import dev.blueish.coordbook.util.Config;
import dev.blueish.coordbook.util.TextCreator;
import dev.xpple.clientarguments.arguments.CBlockPosArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public CoordOverlay overlay = new CoordOverlay();

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

        HudRenderCallback.EVENT.register(this::renderHUD);

        ClientReceiveMessageEvents.CHAT.register(this::chatListener);

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
                      if (name.equals("coordinatebook-empty-this-is-too-long")) {
                          MinecraftClient.getInstance().send(() -> MinecraftClient.getInstance().setScreen(new CreateScreen(pos)));
                      } else {
                          MinecraftClient.getInstance().send(() -> MinecraftClient.getInstance().setScreen(new CreateScreen(pos, name)));
                      }
                      return 0;
                    }))));
    }

    private void renderHUD(MatrixStack matrixStack, float v) {
        if (CoordinateBook.lastPage > 0 && Config.overlay) {
            overlay.render(matrixStack);
        }
    }

    private void chatListener(Text text, @Nullable SignedMessage signedMessage, @Nullable GameProfile gameProfile, MessageType.Parameters parameters, Instant instant) {
        String originalMessage = text.getString();

        Matcher initial = Pattern.compile("(.*)Coordinate Book: ").matcher(originalMessage);
        String message = initial.replaceFirst("");
        Matcher anyCoords = Pattern.compile("(.*?)(-?\\d+)[ \\-/]+?(-?\\d+)[ \\-/]+?(-?\\d+)(.*)").matcher(originalMessage);

        initial.reset();

        TextCreator newMessage = null;

        if (initial.find() && Config.chatReplace) {
            Matcher wName = Pattern.compile("(.*) - (-?\\d+)/(-?\\d+)/(-?\\d+)").matcher(message);
            Matcher woName = Pattern.compile("(-?\\d+)/(-?\\d+)/(-?\\d+)").matcher(message);

            if (wName.matches()) {
                newMessage = new TextCreator("[Add coordinate to the book]")
                    .format(Formatting.AQUA)
                    .format(Formatting.BOLD)
                    .hover("Click to add")
                    .command(String.format(
                            "/coordbook %d %d %d %s",
                            Integer.parseInt(wName.group(2)),
                            Integer.parseInt(wName.group(3)),
                            Integer.parseInt(wName.group(4)),
                            wName.group(1)
                        )
                    );
            } else if (woName.matches()) {
                newMessage = new TextCreator("[Add coordinate to the book]")
                    .format(Formatting.AQUA)
                    .format(Formatting.BOLD)
                    .hover("Click to add")
                    .command(String.format(
                            "/coordbook %d %d %d %s",
                            Integer.parseInt(woName.group(1)),
                            Integer.parseInt(woName.group(2)),
                            Integer.parseInt(woName.group(3)),
                            "coordinatebook-empty-this-is-too-long"
                        )
                    );
            }
        } else if (anyCoords.find() && Config.allChatReplace) {
            newMessage = new TextCreator("[Add coordinate to the book]")
                        .format(Formatting.AQUA)
                        .format(Formatting.BOLD)
                        .hover("Click to add")
                        .command(String.format(
                                "/coordbook %d %d %d %s",
                                Integer.parseInt(anyCoords.group(2)),
                                Integer.parseInt(anyCoords.group(3)),
                                Integer.parseInt(anyCoords.group(4)),
                                "coordinatebook-empty-this-is-too-long")
                        );
        }

        if (newMessage != null) {
            text = newMessage.raw();
            MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(newMessage.raw());
        }
    }
}
