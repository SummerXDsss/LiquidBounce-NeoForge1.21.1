package net.ccbluex.liquidbounce.injection.mixins.minecraft.client;

import net.ccbluex.liquidbounce.utils.client.VanillaTranslationRecognizer;
import net.minecraft.client.resource.language.TranslationStorage;
import net.minecraft.resource.Resource;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mixin(TranslationStorage.class)
public abstract class MixinTranslationStorage {

    @Shadow
    private static void appendFrom(String langCode, List<Resource> resourceRefs, Map<String, String> translations,
                                   Map<String, Text> componentStorage) {
    }

    @Redirect(method = "load(Lnet/minecraft/resource/ResourceManager;Ljava/util/List;Z)Lnet/minecraft/client/resource/language/TranslationStorage;", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resource/language/TranslationStorage;appendFrom(Ljava/lang/String;Ljava/util/List;Ljava/util/Map;Ljava/util/Map;)V"))
    private static void injectShit(String langCode, List<Resource> resourceRefs, Map<String, String> translations,
                                   Map<String, Text> componentStorage) {
        List<Resource> vanillaResources = new ArrayList<>();
        List<Resource> loadedResources = new ArrayList<>();

        for (Resource res : resourceRefs) {
            if (VanillaTranslationRecognizer.INSTANCE.isPackLegit(res.getPack())) {
                vanillaResources.add(res);
            } else if (!VanillaTranslationRecognizer.INSTANCE.shouldPreventLoad(res.getPack())) {
                loadedResources.add(res);
            }
        }

        appendFrom(langCode, loadedResources, translations, componentStorage);

        var map = new HashMap<String, String>();
        var componentMap = new HashMap<String, Text>();

        appendFrom(langCode, vanillaResources, map, componentMap);

        VanillaTranslationRecognizer.INSTANCE.getVanillaTranslations().addAll(map.keySet());

        translations.putAll(map);
        componentStorage.putAll(componentMap);
    }

}
