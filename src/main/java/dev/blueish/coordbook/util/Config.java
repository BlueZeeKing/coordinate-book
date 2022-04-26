package dev.blueish.coordbook.util;

import eu.midnightdust.lib.config.MidnightConfig;

public class Config extends MidnightConfig {
    @Entry public static boolean overlay = true;
    @Comment public static Comment chatText;
    @Entry public static boolean chatReplace = true;
    @Entry public static boolean allChatReplace = true;
}