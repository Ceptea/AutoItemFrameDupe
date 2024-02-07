package io.github.ceptea.aifd;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.network.packet.c2s.play.PickFromInventoryC2SPacket;

public enum Core {
    INSTANCE;

    public static MinecraftClient mc;

    public void init() {
        mc = MinecraftClient.getInstance();
    }

    public static int findItem(Item target_item) {


        PlayerInventory inv = mc.player.getInventory();
        for (int i = 0; i < inv.main.size(); i++) {
            Item item = inv.main.get(i).getItem();
            if (item == target_item) {
                return i;
            }
        }

        return -1;
    }
}

