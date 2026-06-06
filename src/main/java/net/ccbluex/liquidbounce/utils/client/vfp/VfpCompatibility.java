/*
 * This file is part of LiquidBounce (https://github.com/CCBlueX/LiquidBounce)
 *
 * Copyright (c) 2024 CCBlueX
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
 *
 *
 */

package net.ccbluex.liquidbounce.utils.client.vfp;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.api.protocol.version.VersionType;
import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.utils.client.ClientProtocolVersion;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import org.apache.commons.lang3.ArrayUtils;

/**
 * Compatibility layer for ViaFabricPlus
 * <p>
 * DO NOT CALL ANY OF THESE METHODS WITHOUT CHECKING IF VIAFABRICPLUS IS LOADED
 */
public enum VfpCompatibility {

    INSTANCE;

    public void unsafeDsableConflictingVfpOptions() {
        try {
            var visualSettings = Class.forName("de.florianmichael.viafabricplus.settings.impl.VisualSettings")
                    .getMethod("global")
                    .invoke(null);

            // 1 == off, 0 == on
            setSettingValue(visualSettings, "enableSwordBlocking", 1);
            setSettingValue(visualSettings, "enableBlockHitAnimation", 1);
        } catch (Throwable throwable) {
            LiquidBounce.INSTANCE.getLogger().error("Failed to disable conflicting options", throwable);
        }
    }

    public ClientProtocolVersion unsafeGetProtocolVersion() {
        try {
            ProtocolVersion version = getTargetVersion();
            return new ClientProtocolVersion(version.getName(), version.getVersion());
        } catch (Throwable throwable) {
            LiquidBounce.INSTANCE.getLogger().error("Failed to get protocol version", throwable);
            return null;
        }
    }

    public ClientProtocolVersion[] unsafeGetProtocolVersions() {
        try {
            var protocols = ProtocolVersion.getProtocols()
                    .stream()
                    .filter(version -> version.getVersionType() == VersionType.RELEASE)
                    .map(version -> new ClientProtocolVersion(version.getName(), version.getVersion()))
                    .toArray(ClientProtocolVersion[]::new);

            ArrayUtils.reverse(protocols);
            return protocols;
        } catch (Throwable throwable) {
            LiquidBounce.INSTANCE.getLogger().error("Failed to get protocol versions", throwable);
            return new ClientProtocolVersion[0];
        }
    }

    public void unsafeOpenVfpProtocolSelection() {
        try {
            var currentScreen = MinecraftClient.getInstance().currentScreen;
            if (currentScreen == null) {
                currentScreen = new TitleScreen();
            }

            var selectionScreenClass = Class.forName(
                    "de.florianmichael.viafabricplus.screen.base.ProtocolSelectionScreen");
            var instance = selectionScreenClass.getField("INSTANCE").get(null);

            for (var method : selectionScreenClass.getMethods()) {
                if (method.getName().equals("open") && method.getParameterCount() == 1) {
                    method.invoke(instance, currentScreen);
                    return;
                }
            }
        } catch (Throwable throwable) {
            LiquidBounce.INSTANCE.getLogger().error("Failed to open ViaFabricPlus screen", throwable);
        }
    }

    public void unsafeSelectProtocolVersion(int protocolId) {
        try {
            if (!ProtocolVersion.isRegistered(protocolId)) {
                throw new IllegalArgumentException("Protocol version is not registered");
            }

            ProtocolVersion version = ProtocolVersion.getProtocol(protocolId);
            setTargetVersion(version);
        } catch (Throwable throwable) {
            LiquidBounce.INSTANCE.getLogger().error("Failed to select protocol version", throwable);
        }
    }

    public boolean isEqual1_8() {
        try {
            var version = getTargetVersion();

            // Check if the version is equal to 1.8
            return version.equalTo(ProtocolVersion.v1_8);
        } catch (Throwable throwable) {
            LiquidBounce.INSTANCE.getLogger().error("Failed to check if old combat", throwable);
            return false;
        }
    }

    public boolean isOlderThanOrEqual1_8() {
        try {
            var version = getTargetVersion();

            // Check if the version is older or equal than 1.8
            return version.olderThanOrEqualTo(ProtocolVersion.v1_8);
        } catch (Throwable throwable) {
            LiquidBounce.INSTANCE.getLogger().error("Failed to check if old combat", throwable);
            return false;
        }
    }

    public boolean isOlderThanOrEqual1_7_10() {
        try {
            var version = getTargetVersion();

            // Check if the version is older or equal than 1.7.10
            return version.olderThanOrEqualTo(ProtocolVersion.v1_7_6);
        } catch (Throwable throwable) {
            LiquidBounce.INSTANCE.getLogger().error("Failed to check if old combat", throwable);
            return false;
        }
    }

    private static ProtocolVersion getTargetVersion() throws ReflectiveOperationException {
        return (ProtocolVersion) protocolTranslatorClass()
                .getMethod("getTargetVersion")
                .invoke(null);
    }

    private static void setTargetVersion(ProtocolVersion version) throws ReflectiveOperationException {
        protocolTranslatorClass()
                .getMethod("setTargetVersion", ProtocolVersion.class)
                .invoke(null, version);
    }

    private static Class<?> protocolTranslatorClass() throws ClassNotFoundException {
        return Class.forName("de.florianmichael.viafabricplus.protocoltranslator.ProtocolTranslator");
    }

    private static void setSettingValue(Object settings, String fieldName, int value) throws ReflectiveOperationException {
        var option = settings.getClass().getField(fieldName).get(settings);

        for (var method : option.getClass().getMethods()) {
            if (method.getName().equals("setValue") && method.getParameterCount() == 1) {
                method.invoke(option, value);
                return;
            }
        }
    }

}
