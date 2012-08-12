package ch.spacebase.openclassic.client.gui;

import ch.spacebase.openclassic.api.OpenClassic;
import ch.spacebase.openclassic.api.block.model.Texture;
import ch.spacebase.openclassic.api.gui.GuiScreen;
import ch.spacebase.openclassic.api.gui.widget.Button;
import ch.spacebase.openclassic.api.render.RenderHelper;
import ch.spacebase.openclassic.client.util.GeneralUtils;

import com.mojang.minecraft.gui.LoadLevelScreen;
import com.mojang.minecraft.gui.OptionsScreen;

/**
 * @author Steveice10 <Steveice10@gmail.com>
 */
public class MainMenuScreen extends GuiScreen {

	private static final Texture logo = new Texture("/logo.png", true, 251, 48);
	
	public void onOpen() {
		this.clearWidgets();
		this.attachWidget(new Button(0, this.getWidth() / 2 - 100, this.getHeight() / 4 + 24, this, "Singleplayer"));
		this.attachWidget(new Button(1, this.getWidth() / 2 - 100, this.getHeight() / 4 + 48, this, "Multiplayer"));
		this.attachWidget(new Button(2, this.getWidth() / 2 - 100, this.getHeight() / 4 + 72, this, "Options"));
		this.attachWidget(new Button(3, this.getWidth() / 2 - 100, this.getHeight() / 4 + 96, this, "Texture Packs"));
		this.attachWidget(new Button(4, this.getWidth() / 2 - 102, this.getHeight() / 4 + 144, 100, 20, this, "About"));
		this.attachWidget(new Button(5, this.getWidth() / 2 + 2, this.getHeight() / 4 + 144, 100, 20, this, "Quit"));
	
		if(!OpenClassic.getClient().getAudioManager().isPlaying("menu")) OpenClassic.getClient().getAudioManager().playMusic("menu", true);
	}

	public void onButtonClick(Button button) {
		if (button.getId() == 0) {
			OpenClassic.getClient().setCurrentScreen(new LoadLevelScreen(this));
		}

		if (button.getId() == 1) {
			OpenClassic.getClient().setCurrentScreen(new ServerListScreen(this));
		}

		if (button.getId() == 2) {
			OpenClassic.getClient().setCurrentScreen(new OptionsScreen(this, GeneralUtils.getMinecraft().settings));
		}
		
		if (button.getId() == 3) {
			OpenClassic.getClient().setCurrentScreen(new TexturePackScreen(this));
		}

		if (button.getId() == 4) {
			OpenClassic.getClient().setCurrentScreen(new AboutScreen(this));
		}

		if (button.getId() == 5) {
			OpenClassic.getClient().shutdown();
		}
	}

	public void render() {
		RenderHelper.getHelper().drawDirtBG();
		RenderHelper.getHelper().drawTexture(logo, this.getWidth() / 2 - logo.getWidth() / 2, 20);
		super.render();
	}
}
