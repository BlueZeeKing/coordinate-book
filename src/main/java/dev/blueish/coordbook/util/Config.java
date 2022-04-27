package dev.blueish.coordbook.util;

import eu.midnightdust.lib.config.MidnightConfig;

public class Config extends MidnightConfig {
    @Entry public static boolean overlay = true;
    @Comment public static Comment chatText;
    @Entry public static boolean chatReplace = true;
    @Entry public static boolean allChatReplace = true;

    public static enum XPosition {                               // Enums allow the user to cycle through predefined options
        LEFT, RIGHT
    }
    public static enum YPosition {                               // Enums allow the user to cycle through predefined options
        TOP, BOTTOM
    }

    @Comment public static Comment positionText;
    @Entry public static XPosition xPos = XPosition.LEFT;
    @Entry public static YPosition yPos = YPosition.TOP;
}