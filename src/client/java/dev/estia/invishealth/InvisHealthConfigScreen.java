package dev.estia.invishealth;

import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
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

		this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, b -> this.onClose())
				.bounds(x, y + SPACING * 4, BUTTON_WIDTH, BUTTON_HEIGHT)
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
