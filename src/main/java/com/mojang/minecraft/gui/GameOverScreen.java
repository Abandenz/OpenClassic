package com.mojang.minecraft.gui;

import ch.spacebase.openclassic.api.OpenClassic;
import ch.spacebase.openclassic.api.gui.GuiScreen;
import ch.spacebase.openclassic.api.gui.widget.Button;
import ch.spacebase.openclassic.api.render.RenderHelper;
import ch.spacebase.openclassic.client.util.GeneralUtils;

import com.mojang.minecraft.Minecraft;

import org.lwjgl.opengl.GL11;

public final class GameOverScreen extends GuiScreen {

	public final void onOpen() {
		this.clearWidgets();
		this.attachWidget(new Button(0, this.getWidth() / 2 - 100, this.getHeight() / 4 + 72, this, true, "Respawn"));
		this.attachWidget(new Button(1, this.getWidth() / 2 - 100, this.getHeight() / 4 + 96, this, true, "Main Menu"));
	}

	public final void onButtonClick(Button button) {
		Minecraft mc = GeneralUtils.getMinecraft();
		
		if (button.getId() == 0) {
			mc.player.inventory.count = new int[9];
			mc.player.inventory.slots = new int[9];
			mc.player.inventory.popTime = new int[9];
			mc.player.airSupply = 20;
			mc.player.arrows = 20;
			mc.player.deathTime = 0;
			mc.player.resetPos();
			
			OpenClassic.getClient().setCurrentScreen(null);
		}

		if (button.getId() == 1) {
			mc.stopGame(true);
		}
	}

	public final void render() {
		RenderHelper.getHelper().color(0, 0, this.getWidth(), this.getHeight(), 1615855616, -1602211792);
		
		GL11.glPushMatrix();
		GL11.glScalef(2.0F, 2.0F, 2.0F);
		RenderHelper.getHelper().renderText("Game over!", this.getWidth() / 2 / 2, 30);
		GL11.glPopMatrix();
		RenderHelper.getHelper().renderText("Score: &e" + GeneralUtils.getMinecraft().player.getScore(), this.getWidth() / 2, 100);
		super.render();
	}
}
