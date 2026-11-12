package dev.estia.invishealth;

import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.util.Mth;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

/** Simple on/off settings screen, opened from Mod Menu. */
public class InvisHealthConfigScreen extends Screen {
	private static final int BUTTON_WIDTH = 220;
	private static final int BUTTON_HEIGHT = 20;
	private static final int SPACING = 24;

	private final Screen parent;

	public InvisHealthConfigScreen(Screen parent) {
		super(Component.translatable("invishealth.config.title"));
		this.parent = parent;
	}

	@Override
	protected void init() {
		int x = this.width / 2 - BUTTON_WIDTH / 2;
		int y = this.height / 4 + 10;
		InvisHealthConfig cfg = InvisHealthConfig.INSTANCE;

		addToggle(x, y, "invishealth.config.enabled",
				() -> cfg.enabled, v -> cfg.enabled = v);
		addToggle(x, y + SPACING, "invishealth.config.show_mobs",
				() -> cfg.showMobs, v -> cfg.showMobs = v);
		addToggle(x, y + SPACING * 2, "invishealth.config.show_invisible",
				() -> cfg.showInvisible, v -> cfg.showInvisible = v);

		HeightSlider slider = this.addRenderableWidget(new HeightSlider(x, y + SPACING * 3, BUTTON_WIDTH - 54, BUTTON_HEIGHT));
		this.addRenderableWidget(Button.builder(Component.translatable("invishealth.config.reset"), b -> slider.reset())
				.bounds(x + BUTTON_WIDTH - 50, y + SPACING * 3, 50, BUTTON_HEIGHT)
				.build());

		this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, b -> this.onClose())
				.bounds(x, y + SPACING * 5, BUTTON_WIDTH, BUTTON_HEIGHT)
				.build());
	}

	private void addToggle(int x, int y, String key, Supplier<Boolean> getter, Consumer<Boolean> setter) {
		Button button = Button.builder(label(key, getter.get()), b -> {
					setter.accept(!getter.get());
					b.setMessage(label(key, getter.get()));
					InvisHealthConfig.save();
				})
				.bounds(x, y, BUTTON_WIDTH, BUTTON_HEIGHT)
				.build();
		this.addRenderableWidget(button);
	}

	private static Component label(String key, boolean on) {
		return Component.translatable(key).append(": ").append(on
				? Component.literal("ON").withStyle(ChatFormatting.GREEN)
				: Component.literal("OFF").withStyle(ChatFormatting.RED));
	}

	/** Slider for how high the hearts sit, in steps of 0.1 blocks. */
	private static class HeightSlider extends AbstractSliderButton {
		private static final double RANGE = InvisHealthConfig.MAX_HEIGHT - InvisHealthConfig.MIN_HEIGHT;

		HeightSlider(int x, int y, int width, int height) {
			super(x, y, width, height, Component.empty(), toSlider(InvisHealthConfig.INSTANCE.heightOffset));
			this.updateMessage();
		}

		private static double toSlider(double offset) {
			return Mth.clamp((offset - InvisHealthConfig.MIN_HEIGHT) / RANGE, 0.0, 1.0);
		}

		private double offset() {
			return Math.round((InvisHealthConfig.MIN_HEIGHT + this.value * RANGE) * 10.0) / 10.0;
		}

		void reset() {
			this.value = toSlider(0.0);
			this.applyValue();
			this.updateMessage();
		}

		@Override
		protected void updateMessage() {
			double offset = offset();
			String text = (offset > 0 ? "+" : "") + String.format(java.util.Locale.ROOT, "%.1f", offset);
			this.setMessage(Component.translatable("invishealth.config.height").append(": " + text));
		}

		@Override
		protected void applyValue() {
			InvisHealthConfig.INSTANCE.heightOffset = offset();
			InvisHealthConfig.save();
		}
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		super.render(graphics, mouseX, mouseY, partialTick);
		graphics.drawCenteredString(this.font, this.title, this.width / 2, this.height / 4 - 20, 0xFFFFFFFF);
	}

	@Override
	public void onClose() {
		InvisHealthConfig.save();
		this.minecraft.setScreen(parent);
	}
}
