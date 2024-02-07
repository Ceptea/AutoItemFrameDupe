package io.github.ceptea.aifd;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PickFromInventoryC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AutoItemFrameDupe implements ModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("aifd");

	boolean done = false;
	public static boolean active = false;
	public static Item target_item;

	public static MinecraftClient mc;
	public static double cooldown;
	public void item_switch() {
		int target_slot = Core.findItem(target_item);
		if (mc.player.getMainHandStack().getItem() == target_item) {
			return;
		}

		PlayerInventory inv = mc.player.getInventory();

		if (target_slot > 8) {
			mc.player.networkHandler.sendPacket(new PickFromInventoryC2SPacket(target_slot));
		} else {
			inv.selectedSlot = target_slot;
		}
	}
	@Override
	public void onInitialize() {
		mc = MinecraftClient.getInstance();
		Core.INSTANCE.init();
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
			if (Settings.item_switch) {
				item_switch();
			}
			if (Settings.item_switch && !(target_item == Items.AIR)) {
				 mc.player.sendMessage(Text.of(String.format("§aDuping %s", target_item.toString())),true);


			} else {
				mc.player.sendMessage(Text.of("§aAutoItemFrameDupe"),true);
			}

			if (System.currentTimeMillis() > cooldown) {
				cooldown = System.currentTimeMillis()+Settings.cooldown;
				for (Entity entity: mc.world.getEntities()) {
					if (!entity.isInRange(mc.player,3,3)) {
						continue;
					}
					if (entity instanceof ItemFrameEntity) {

						ItemFrameEntity item = (ItemFrameEntity) entity;

						boolean attackable = String.valueOf(item.getHeldItemStack().getItem().toString()).contains("air");
						mc.interactionManager.interactEntity(mc.player,entity, Hand.MAIN_HAND);
						if (!attackable) {
							done = true;
							mc.interactionManager.attackEntity(mc.player,entity);
							((ItemFrameEntity) entity).setHeldItemStack(Items.AIR.getDefaultStack());


						} else {

							mc.interactionManager.interactEntity(mc.player,entity,Hand.MAIN_HAND);

						}
					}
				}
			}

		});
		ClientTickEvents.END_CLIENT_TICK.register(client -> {

			if (bind.wasPressed()) {
				active = !active;
				if (!active) {
					mc.player.sendMessage(Text.of("§cAutoItemFrameDupe"),true);

				} else {
					cooldown = System.currentTimeMillis()+Settings.cooldown;
					if (Settings.item_switch) {
						target_item = mc.player.getMainHandStack().getItem();
						if (target_item == Items.AIR) {
							mc.player.sendMessage(Text.of(String.format("§aHold a item, then enable AutoItemFrameDupe.", target_item.toString())));
							active = false;
						}
					}

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
				}
			}
		});
	}
}