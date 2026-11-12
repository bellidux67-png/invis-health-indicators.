package dev.estia.invishealth;

import com.mojang.blaze3d.platform.InputConstants;
import org.lwjgl.glfw.GLFW;

import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;

public class InvisHealthClient implements ClientModInitializer {
	public static final String MOD_ID = "invishealth";

	/** Master switch for all indicators. */
	public static boolean enabled = true;
	/** When false, only players get hearts. */
	public static boolean showMobs = true;

	@Override
	public void onInitializeClient() {
		KeyMapping.Category category = KeyMapping.Category.register(
				Identifier.fromNamespaceAndPath(MOD_ID, "main"));

		KeyMapping toggle = KeyBindingHelper.registerKeyBinding(new KeyMapping(
				"key.invishealth.toggle", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_H, category));
		KeyMapping toggleMobs = KeyBindingHelper.registerKeyBinding(new KeyMapping(
				"key.invishealth.toggle_mobs", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_J, category));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (toggle.consumeClick()) {
				enabled = !enabled;
				if (client.player != null) {
					client.player.displayClientMessage(Component.translatable(
							enabled ? "invishealth.msg.on" : "invishealth.msg.off"), true);
				}
			}
			while (toggleMobs.consumeClick()) {
				showMobs = !showMobs;
				if (client.player != null) {
					client.player.displayClientMessage(Component.translatable(
							showMobs ? "invishealth.msg.mobs_on" : "invishealth.msg.mobs_off"), true);
				}
			}
		});

		WorldRenderEvents.BEFORE_TRANSLUCENT.register(HeartRenderer::render);
	}
}
