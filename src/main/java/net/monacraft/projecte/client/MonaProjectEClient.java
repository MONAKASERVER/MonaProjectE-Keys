package net.monacraft.projecte.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

public final class MonaProjectEClient implements ClientModInitializer {
    private static KeyMapping charge;
    private static KeyMapping extraFunction;
    private static KeyMapping fireProjectile;
    private static KeyMapping mode;
    private static KeyMapping bootsOrHelmet;
    private static KeyMapping helmetDirect;

    @Override
    public void onInitializeClient() {
        PayloadTypeRegistry.serverboundPlay().register(AbilityPayload.TYPE, AbilityPayload.CODEC);
        KeyMapping.Category category = KeyMapping.Category.register(Identifier.fromNamespaceAndPath("monaprojectekeys", "main"));
        charge = register("charge", GLFW.GLFW_KEY_V, category);
        extraFunction = register("extra_function", GLFW.GLFW_KEY_C, category);
        fireProjectile = register("fire_projectile", GLFW.GLFW_KEY_R, category);
        mode = register("mode", GLFW.GLFW_KEY_G, category);
        bootsOrHelmet = register("boots_toggle", GLFW.GLFW_KEY_X, category);
        helmetDirect = register("helmet_toggle", GLFW.GLFW_KEY_UNKNOWN, category);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            drain(charge, "charge");
            drain(extraFunction, "extra_function");
            drain(fireProjectile, "fire_projectile");
            drain(mode, "mode");
            while (bootsOrHelmet.consumeClick()) {
                long window = client.getWindow().handle();
                boolean shift = GLFW.glfwGetKey(window, GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS
                        || GLFW.glfwGetKey(window, GLFW.GLFW_KEY_RIGHT_SHIFT) == GLFW.GLFW_PRESS;
                send(shift ? "helmet_toggle" : "boots_toggle");
            }
            drain(helmetDirect, "helmet_toggle");
        });
    }

    private static KeyMapping register(String id, int key, KeyMapping.Category category) {
        return KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.monaprojectekeys." + id,
                InputConstants.Type.KEYSYM,
                key,
                category
        ));
    }

    private static void drain(KeyMapping mapping, String action) {
        while (mapping.consumeClick()) send(action);
    }

    private static void send(String action) {
        if (ClientPlayNetworking.canSend(AbilityPayload.TYPE)) {
            ClientPlayNetworking.send(new AbilityPayload(action));
        }
    }
}
