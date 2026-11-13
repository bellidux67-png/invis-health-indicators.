package com.neegy.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import org.lwjgl.glfw.GLFW;

public final class NeegyClient implements ClientModInitializer {
    private static KeyMapping flightKey;
    private static KeyMapping boatBoostKey;
    private static boolean flightEnabled;

    @Override
    public void onInitializeClient() {
        flightKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.neegy.toggle_flight", InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_G, KeyMapping.Category.MISC));
        boatBoostKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.neegy.boat_boost", InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_LEFT_ALT, KeyMapping.Category.MISC));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (flightKey.consumeClick()) {
                if (client.player == null) continue;
                // Only toggle flight where the game/server has already granted flight permission.
                if (client.player.getAbilities().mayfly) {
                    flightEnabled = !flightEnabled;
                    client.player.getAbilities().flying = flightEnabled;
                    client.player.displayClientMessage(Component.literal(
                            "Neegy Flight: " + (flightEnabled ? "ON" : "OFF")), true);
                } else {
                    client.player.displayClientMessage(Component.literal(
                            "Neegy Flight requires creative/spectator flight permission."), true);
                }
            }

            if (client.player == null || client.level == null || !boatBoostKey.isDown()) return;
            if (client.player.getVehicle() instanceof AbstractBoat boat) {
                // Small local boost for permitted/private worlds; server authority may override it.
                var look = client.player.getLookAngle();
                var velocity = boat.getDeltaMovement();
                boat.setDeltaMovement(velocity.x + look.x * 0.035,
                        velocity.y + Math.max(0.0, look.y) * 0.015,
                        velocity.z + look.z * 0.035);
            }
        });
    }
}
