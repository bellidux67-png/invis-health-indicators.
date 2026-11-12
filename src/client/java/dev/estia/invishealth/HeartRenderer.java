package dev.estia.invishealth;

import com.mojang.blaze3d.vertex.PoseStack;
import org.joml.Matrix4f;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;

/**
 * Draws a row of hearts above every living entity in range.
 *
 * Unlike vanilla name tags (and most health-bar mods), this deliberately does NOT
 * check {@code isInvisible()} / {@code isInvisibleTo()}, so players with an
 * Invisibility potion still get their hearts drawn.
 */
public final class HeartRenderer {
	private static final double MAX_DISTANCE = 48.0;
	private static final int MAX_HEARTS_SHOWN = 10;
	private static final float TEXT_SCALE = 0.025f;
	private static final int FULL_BRIGHT = 0xF000F0;

	private static final String HEART = "❤"; // ❤
	private static final int COLOR_FULL = 0xFFFF2B2B;
	private static final int COLOR_HALF = 0xFF8C1A1A;
	private static final int COLOR_EMPTY = 0xFF3A3A3A;
	private static final int COLOR_ABSORB = 0xFFFFD02B;
	private static final int COLOR_TEXT = 0xFFFFFFFF;
	private static final int COLOR_INVIS = 0xFFB18CFF;

	private HeartRenderer() { }

	public static void render(WorldRenderContext context) {
		InvisHealthConfig cfg = InvisHealthConfig.INSTANCE;
		if (!cfg.enabled) return;

		Minecraft mc = Minecraft.getInstance();
		if (mc.level == null || mc.player == null || mc.options.hideGui) return;

		Camera camera = mc.gameRenderer.getMainCamera();
		Vec3 camPos = context.worldState().cameraRenderState.pos;
		float partialTick = mc.getDeltaTracker().getGameTimeDeltaPartialTick(false);
		PoseStack matrices = context.matrices();
		MultiBufferSource consumers = context.consumers();
		Font font = mc.font;
		double maxDistSq = MAX_DISTANCE * MAX_DISTANCE;

		for (Entity entity : mc.level.entitiesForRendering()) {
			if (!(entity instanceof LivingEntity living)) continue;
			if (living instanceof ArmorStand || !living.isAlive()) continue;
			if (living instanceof Player p && p.isSpectator()) continue;
			if (!cfg.showMobs && !(living instanceof Player)) continue;
			if (!cfg.showInvisible && living.isInvisible()) continue;
			// Don't draw your own hearts in first person.
			if (living == mc.getCameraEntity() && !camera.isDetached()) continue;

			Vec3 pos = living.getPosition(partialTick);
			if (pos.distanceToSqr(camPos) > maxDistSq) continue;

			Component line = buildLine(living);

			// Sit above the vanilla name-tag slot so the two never overlap.
			double yOffset = living.getBbHeight() + 0.5;
			if (living instanceof Player || living.hasCustomName()) yOffset += 0.3;
			yOffset += Mth.clamp(cfg.heightOffset, InvisHealthConfig.MIN_HEIGHT, InvisHealthConfig.MAX_HEIGHT);

			matrices.pushPose();
			matrices.translate(pos.x - camPos.x, pos.y - camPos.y + yOffset, pos.z - camPos.z);
			matrices.mulPose(camera.rotation());
			matrices.scale(TEXT_SCALE, -TEXT_SCALE, TEXT_SCALE);

			Matrix4f pose = matrices.last().pose();
			float x = -font.width(line) / 2f;
			font.drawInBatch(line, x, 0, COLOR_TEXT, true, pose, consumers,
					Font.DisplayMode.NORMAL, 0, FULL_BRIGHT);

			matrices.popPose();
		}
	}

	private static Component buildLine(LivingEntity living) {
		float health = Math.max(0f, living.getHealth());
		float maxHealth = Math.max(1f, living.getMaxHealth());
		float absorption = living.getAbsorptionAmount();

		MutableComponent line = Component.empty();

		if (living.isInvisible()) {
			line.append(Component.literal("[Invis] ").withColor(COLOR_INVIS));
		}

		int maxHearts = Mth.ceil(maxHealth / 2f);
		if (maxHearts <= MAX_HEARTS_SHOWN) {
			// One heart per 2 HP, exactly like the vanilla HUD.
			int halfPoints = Mth.ceil(health);
			for (int i = 0; i < maxHearts; i++) {
				int remaining = halfPoints - i * 2;
				int color = remaining >= 2 ? COLOR_FULL : remaining == 1 ? COLOR_HALF : COLOR_EMPTY;
				line.append(Component.literal(HEART).withColor(color));
			}
		} else {
			// Big mobs: scale to 10 hearts so the row stays readable.
			float fraction = health / maxHealth;
			int filled = Mth.ceil(fraction * MAX_HEARTS_SHOWN);
			for (int i = 0; i < MAX_HEARTS_SHOWN; i++) {
				line.append(Component.literal(HEART).withColor(i < filled ? COLOR_FULL : COLOR_EMPTY));
			}
		}

		if (absorption > 0f) {
			int absorbHearts = Math.min(MAX_HEARTS_SHOWN, Mth.ceil(absorption / 2f));
			line.append(Component.literal(" "));
			for (int i = 0; i < absorbHearts; i++) {
				line.append(Component.literal(HEART).withColor(COLOR_ABSORB));
			}
		}

		line.append(Component.literal(" " + format(health + absorption) + "/" + format(maxHealth))
				.withColor(COLOR_TEXT));
		return line;
	}

	private static String format(float value) {
		return value == Math.floor(value) ? String.valueOf((int) value) : String.format(java.util.Locale.ROOT, "%.1f", value);
	}
}
