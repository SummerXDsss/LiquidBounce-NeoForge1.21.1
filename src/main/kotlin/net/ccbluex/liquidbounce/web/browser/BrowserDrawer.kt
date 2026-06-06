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
package net.ccbluex.liquidbounce.web.browser

import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.systems.RenderSystem
import net.ccbluex.liquidbounce.event.Listenable
import net.ccbluex.liquidbounce.event.events.*
import net.ccbluex.liquidbounce.event.handler
import net.ccbluex.liquidbounce.utils.client.mc
import net.ccbluex.liquidbounce.utils.kotlin.EventPriorityConvention
import net.ccbluex.liquidbounce.web.browser.supports.IBrowser
import net.ccbluex.liquidbounce.web.browser.supports.tab.ITab
import net.ccbluex.liquidbounce.web.browser.supports.tab.JcefTab
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.BufferRenderer.drawWithGlobalProgram
import net.minecraft.client.render.GameRenderer
import net.minecraft.client.render.Tessellator
import net.minecraft.client.render.VertexFormat
import net.minecraft.client.render.VertexFormats

class BrowserDrawer(val browser: () -> IBrowser?) : Listenable {

    private val tabs
        get() = browser()?.getTabs() ?: emptyList()

    @Suppress("unused")
    val preRenderHandler = handler<GameRenderEvent> {
        browser()?.drawGlobally()

        for (tab in tabs) {
            tab.drawn = false
        }
    }

    @Suppress("unused")
    val windowResizeWHandler = handler<FrameBufferResizeEvent> { ev ->
        for (tab in tabs) {
            tab.resize(ev.width, ev.height)
        }
    }

    @Suppress("unused")
    val onScreenRender = handler<ScreenRenderEvent> { event ->
        for (tab in tabs) {
            if (tab.drawn) {
                continue
            }

            renderTab(tab, event.context)
            tab.drawn = true
        }
    }

    private var shouldReload = false

    @Suppress("unused")
    val onReload = handler<ResourceReloadEvent> {
        shouldReload = true
    }

    @Suppress("unused")
    val onOverlayRender = handler<OverlayRenderEvent>(priority = EventPriorityConvention.READ_FINAL_STATE) {
        if (this.shouldReload) {
            for (tab in tabs) {
                tab.forceReload()
            }

            this.shouldReload = false
        }

        for (tab in tabs) {
            if (tab.drawn) {
                continue
            }

            if (tab.preferOnTop && mc.currentScreen != null) {
                continue
            }

            renderTab(tab, it.context)
            tab.drawn = true
        }
    }

    private fun renderTab(tab: ITab, context: DrawContext?) {
        val scaleFactor = mc.window.scaleFactor.toFloat()
        val x = tab.position.x.toFloat() / scaleFactor
        val y = tab.position.y.toFloat() / scaleFactor
        val w = tab.position.width.toFloat() / scaleFactor
        val h = tab.position.height.toFloat() / scaleFactor
        val jcefTab = tab as? JcefTab

        if (context != null && jcefTab != null && jcefTab.isTextureReady()) {
            renderTexture(context, x, y, w, h, jcefTab)
        } else {
            renderTexture(x, y, w, h, tab.getTexture())
        }
    }

    private fun renderTexture(context: DrawContext, x: Float, y: Float, width: Float, height: Float, tab: JcefTab) {
        val drawWidth = width.toInt().coerceAtLeast(1)
        val drawHeight = height.toInt().coerceAtLeast(1)
        val textureWidth = tab.getTextureWidth().coerceAtLeast(tab.position.width.coerceAtLeast(1))
        val textureHeight = tab.getTextureHeight().coerceAtLeast(tab.position.height.coerceAtLeast(1))

        RenderSystem.disableDepthTest()
        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()
        context.drawTexture(
            tab.getTextureLocation(),
            x.toInt(),
            y.toInt(),
            drawWidth,
            drawHeight,
            0f,
            0f,
            textureWidth,
            textureHeight,
            textureWidth,
            textureHeight
        )
        RenderSystem.enableDepthTest()
        RenderSystem.defaultBlendFunc()
        RenderSystem.enableBlend()
    }

    private fun renderTexture(x: Float, y: Float, width: Float, height: Float, texture: Int) {
        RenderSystem.disableDepthTest()
        RenderSystem.enableBlend()
        RenderSystem.blendFunc(GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA)
        RenderSystem.setShader { GameRenderer.getPositionTexColorProgram() }
        RenderSystem.setShaderTexture(0, texture)
        val tessellator = Tessellator.getInstance()
        val buffer = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR)
        buffer.vertex(x, y + height, 0.0f)
            .texture(0.0f, 1.0f)
            .color(255, 255, 255, 255)
        buffer.vertex(x + width, y + height, 0.0f)
            .texture(1.0f, 1.0f)
            .color(255, 255, 255, 255)
        buffer.vertex(x + width, y, 0.0f)
            .texture(1.0f, 0.0f)
            .color(255, 255, 255, 255)
        buffer.vertex(x, y, 0.0f)
            .texture(0.0f, 0.0f)
            .color(255, 255, 255, 255)
        drawWithGlobalProgram(buffer.end())
        RenderSystem.setShaderTexture(0, 0)
        RenderSystem.enableDepthTest()
        RenderSystem.defaultBlendFunc()
        RenderSystem.enableBlend()
    }

}
