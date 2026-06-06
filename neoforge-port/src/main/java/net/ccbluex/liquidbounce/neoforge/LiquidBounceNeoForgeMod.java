package net.ccbluex.liquidbounce.neoforge;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(LiquidBounceNeoForgeMod.MOD_ID)
public final class LiquidBounceNeoForgeMod {

    public static final String MOD_ID = "liquidbounce";

    public LiquidBounceNeoForgeMod(IEventBus modEventBus, ModContainer modContainer) {
        // Port seed only. The real LiquidBounce startup currently lives behind MinecraftClient mixins.
    }

}

