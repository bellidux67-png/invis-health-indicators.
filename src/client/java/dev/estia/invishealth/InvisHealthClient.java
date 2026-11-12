package dev.estia.invishealth;

import com.mojang.blaze3d.platform.InputConstants;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;

public class InvisHealthClient implements ClientModInitializer {
	public static final String MOD_ID = "invishealth";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitializeClient() {
		InvisHealthConfig.load();

		KeyMapping.Category category = KeyMapping.Category.register(
				Identifier.fromNamespaceAndPath(MOD_ID, "main"));

		KeyMapping toggle = KeyBindingHelper.registerKeyBinding(new KeyMapping(
				"key.invishealth.toggle", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_H, category));
		KeyMapping toggleMobs = KeyBindingHelper.registerKeyBinding(new KeyMapping(
				"key.invishealth.toggle_mobs", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_J, category));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			InvisHealthConfig cfg = InvisHealthConfig.INSTANCE;
			while (toggle.consumeClick()) {
				cfg.enabled = !cfg.enabled;
				InvisHealthConfig.save();
				if (client.player != null) {
					client.player.displayClientMessage(Component.translatable(
							cfg.enabled ? "invishealth.msg.on" : "invishealth.msg.off"), true);
				}
			}
			while (toggleMobs.consumeClick()) {
				cfg.showMobs = !cfg.showMobs;
				InvisHealthConfig.save();
				if (client.player != null) {
					client.player.displayClientMessage(Component.translatable(
							cfg.showMobs ? "invishealth.msg.mobs_on" : "invishealth.msg.mobs_off"), true);
				}
			}
		});

		WorldRenderEvents.BEFORE_TRANSLUCENT.register(HeartRenderer::render);
	}
}
