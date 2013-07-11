package ch.spacebase.openclassic.client.gui;

import org.lwjgl.input.Keyboard;

import ch.spacebase.openclassic.api.OpenClassic;
import ch.spacebase.openclassic.api.gui.GuiScreen;
import ch.spacebase.openclassic.api.gui.widget.Button;
import ch.spacebase.openclassic.api.render.RenderHelper;

public final class ErrorScreen extends GuiScreen {

	private String title;
	private String message;

	public ErrorScreen(String title, String message) {
		this.title = title;
		this.message = message;
	}

	public void onOpen() {
		this.clearWidgets();
		this.attachWidget(new Button(0, this.getWidth() / 2 - 200, this.getHeight() / 6 + 264, this, OpenClassic.getGame().getTranslator().translate("gui.error.main-menu")));
	}

	public void onButtonClick(Button button) {
		if(button.getId() == 0) {
			if(OpenClassic.getClient().isInGame()) {
				OpenClassic.getClient().exitLevel();
			} else {
				OpenClassic.getClient().setCurrentScreen(new MainMenuScreen());
			}
		}
	}

	public void render() {
		RenderHelper.getHelper().drawDefaultBG();
		RenderHelper.getHelper().renderScaledText(this.title, this.getWidth() / 2, 180);
		RenderHelper.getHelper().renderText(this.message, this.getWidth() / 2, 220);
		super.render();
	}

	public void onKeyPress(char c, int key) {
		if(key != Keyboard.KEY_ESCAPE) {
			super.onKeyPress(c, key);
		}
	}
}
