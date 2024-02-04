package io.github.ceptea.aifd;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AutoItemFrameDupe implements ModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("aifd");

	boolean done = false;
	public static boolean active;
	@Override
	public void onInitialize() {
		MinecraftClient mc = MinecraftClient.getInstance();
		active = false;
		KeyBinding bind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"Enable AutoItemFrameDupe",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_H,
				"AIFD"

		));
		ClientTickEvents.START_CLIENT_TICK.register(client -> {
			if (client.player == null || client.world == null) {
				return;
			}
			if (!active) {

				return;
			}
			if (done) {
				done = false;
				return;
			}
			mc.player.sendMessage(Text.of("§aAutoItemFrameDupe is Enabled"),true);

			Iterable<Entity> entities = mc.world.getEntities();

			for (Entity entity: entities) {
				if (entity instanceof ItemFrameEntity) {

					ItemFrameEntity item = (ItemFrameEntity) entity;
					boolean attackable = String.valueOf(item.getHeldItemStack().getItem().toString()).contains("air");
					mc.interactionManager.interactEntity(mc.player,entity, Hand.MAIN_HAND);
					if (!attackable) {
						done = true;
						mc.interactionManager.attackEntity(mc.player,entity);


					} else {
						//BlockPos bp = entity.getBlockPos();
//						mc.interactionManager.interactEntity(mc.player,entity,Hand.MAIN_HAND);
						mc.interactionManager.interactEntity(mc.player,entity,Hand.MAIN_HAND);
//						mc.interactionManager.interactEntity(mc.player,entity,Hand.MAIN_HAND);

					}
				}
			}
		});
		ClientTickEvents.END_CLIENT_TICK.register(client -> {

			if (bind.wasPressed()) {
				active = !active;
				if (!active) {
					mc.player.sendMessage(Text.of("§cAutoItemFrameDupe is Disabled"),true);

				}
			}

			if (client.player == null || client.world == null) {
				return;
			}
			if (!active) {
				return;
			}


			Iterable<Entity> entities = mc.world.getEntities();

			for (Entity entity: entities) {
				if (entity instanceof ItemFrameEntity) {

					ItemFrameEntity item = (ItemFrameEntity) entity;
					boolean attackable = String.valueOf(item.getHeldItemStack().getItem().toString()).contains("air");
					mc.interactionManager.interactEntity(mc.player,entity, Hand.MAIN_HAND);
					if (attackable) {
						mc.interactionManager.interactEntity(mc.player,entity,Hand.MAIN_HAND);


					}
					//nw.send(String.valueOf(item));
				}
			}
		});
	}
}