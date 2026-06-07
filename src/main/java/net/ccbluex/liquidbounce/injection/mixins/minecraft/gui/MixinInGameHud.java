/*
 * This file is part of LiquidBounce (https://github.com/CCBlueX/LiquidBounce)
 *
 * Copyright (c) 2015 - 2024 CCBlueX
 *
 * LiquidBounce is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * LiquidBounce is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with LiquidBounce. If not, see <https://www.gnu.org/licenses/>.
 */
package net.ccbluex.liquidbounce.injection.mixins.minecraft.gui;

import net.ccbluex.liquidbounce.event.EventManager;
import net.ccbluex.liquidbounce.event.events.OverlayMessageEvent;
import net.ccbluex.liquidbounce.features.module.modules.render.ModuleAntiBlind;
import net.ccbluex.liquidbounce.features.module.modules.render.ModuleFreeCam;
import net.ccbluex.liquidbounce.render.engine.UIRenderer;
import net.ccbluex.liquidbounce.web.theme.component.ComponentOverlay;
import net.ccbluex.liquidbounce.web.theme.component.FeatureTweak;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class MixinInGameHud {

    @Final
    @Shadow
    private static Identifier PUMPKIN_BLUR;

    @Final
    @Shadow
    private static Identifier POWDER_SNOW_OUTLINE;

    /**
     * Hook render hud event at the top layer
     */
    @Inject(method = "renderMainHud", at = @At("HEAD"))
    private void hookRenderEventStart(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        UIRenderer.INSTANCE.startUIOverlayDrawing(context, tickCounter.getTickDelta(false));
    }

    @Inject(method = "renderOverlay", at = @At("HEAD"), cancellable = true)
    private void injectPumpkinBlur(DrawContext context, Identifier texture, float opacity, CallbackInfo callback) {
        ModuleAntiBlind module = ModuleAntiBlind.INSTANCE;
        if (!module.getEnabled()) {
            return;
        }

        if (module.getPumpkinBlur() && PUMPKIN_BLUR.equals(texture)) {
            callback.cancel();
            return;
        }

        if (module.getPowerSnowFog() && POWDER_SNOW_OUTLINE.equals(texture)) {
            callback.cancel();
        }
    }

    @Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
    private void hookFreeCamRenderCrosshairInThirdPerson(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if ((ModuleFreeCam.INSTANCE.getEnabled() && ModuleFreeCam.INSTANCE.shouldDisableCrosshair())
                || ComponentOverlay.isTweakEnabled(FeatureTweak.DISABLE_CROSSHAIR)) {
            ci.cancel();
        }
    }


    @Inject(method = "renderScoreboardSidebar", at = @At("HEAD"), cancellable = true)
    private void renderScoreboardSidebar(CallbackInfo ci) {
        if (ComponentOverlay.isTweakEnabled(FeatureTweak.DISABLE_SCOREBOARD)) {
            ci.cancel();
        }
    }

    @Inject(method = "renderHeldItemTooltip", at = @At("HEAD"), cancellable = true)
    private void hookRenderHeldItemTooltip(CallbackInfo ci) {
        if (ComponentOverlay.isTweakEnabled(FeatureTweak.DISABLE_HELD_ITEM_TOOL_TIP)) {
            ci.cancel();
        }
    }

    @Inject(method = "setOverlayMessage", at = @At("HEAD"), cancellable = true)
    private void hookSetOverlayMessage(Text message, boolean tinted, CallbackInfo ci) {
        EventManager.INSTANCE.callEvent(new OverlayMessageEvent(message, tinted));

        if (ComponentOverlay.isTweakEnabled(FeatureTweak.DISABLE_OVERLAY_MESSAGE)) {
            ci.cancel();
        }
    }

    @Inject(method = "renderStatusEffectOverlay", at = @At("HEAD"), cancellable = true)
    private void hookRenderStatusEffectOverlay(CallbackInfo ci) {
        if (ComponentOverlay.isTweakEnabled(FeatureTweak.DISABLE_STATUS_EFFECT_OVERLAY)) {
            ci.cancel();
        }
    }

}
