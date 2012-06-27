package ch.spacebase.openclassic.client.gui;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import ch.spacebase.openclassic.api.Color;
import ch.spacebase.openclassic.api.OpenClassic;
import ch.spacebase.openclassic.api.gui.GuiScreen;
import ch.spacebase.openclassic.api.gui.widget.Button;
import ch.spacebase.openclassic.api.gui.widget.ButtonList;
import ch.spacebase.openclassic.api.render.RenderHelper;
import ch.spacebase.openclassic.client.util.GeneralUtils;
import ch.spacebase.openclassic.client.util.HTTPUtil;
import ch.spacebase.openclassic.client.util.Server;

import com.mojang.minecraft.Minecraft;
import com.mojang.minecraft.SessionData;

public class ServerListScreen extends GuiScreen {

	private GuiScreen parent;
	private String title = "Select a server.";

	private boolean select = false;

	public ServerListScreen(GuiScreen parent) {
		this.parent = parent;
	}

	public void onOpen() {
		this.clearWidgets();
		this.attachWidget(new ButtonList(0, this.getWidth(), this.getHeight(), this, true));
		this.getWidget(0, ButtonList.class).setContents(SessionData.serverInfo);

		this.attachWidget(new Button(1, this.getWidth() / 2 - 206, this.getHeight() / 6 + 144, 100, 20, this, true, "Favorites"));
		this.attachWidget(new Button(2, this.getWidth() / 2 - 102, this.getHeight() / 6 + 144, 100, 20, this, true, "Add Favorite"));
		this.attachWidget(new Button(3, this.getWidth() / 2 + 2, this.getHeight() / 6 + 144, 100, 20, this, true, "Enter a URL..."));
		this.attachWidget(new Button(4, this.getWidth() / 2 + 106, this.getHeight() / 6 + 144, 100, 20, this, true, "Back to Menu"));
	}

	public final void onButtonClick(Button button) {
		if (button.getId() == 1) {
			OpenClassic.getClient().setCurrentScreen(new FavoriteServersScreen(this));
		}

		if (button.getId() == 2) {
			if (this.select) {
				this.title = "Select a server.";
				this.select = false;
			} else {
				this.title = Color.GREEN + "Select a server to favorite.";
				this.select = true;
			}
		}

		if (button.getId() == 3) {
			OpenClassic.getClient().setCurrentScreen(new ServerURLScreen(this));
		}
		
		if (button.getId() == 4) {
			OpenClassic.getClient().setCurrentScreen(this.parent);
		}
	}

	@Override
	public void onButtonListClick(ButtonList list, Button button) {
		Server server = SessionData.servers.get(list.getCurrentPage() * 5 + button.getId());
		
		if (this.select) {
			this.title = "Select a server.";
			this.select = false;
			
			SessionData.favorites.put(server.name, server.getUrl());
		} else {
			this.joinServer(server);
		}
	}

	private void joinServer(Server server) {
		if(server != null) {
			Minecraft mc = GeneralUtils.getMinecraft();
			
			OpenClassic.getClient().getProgressBar().setTitle("Connecting...");
			OpenClassic.getClient().getProgressBar().setText("Getting server info...");
			OpenClassic.getClient().getProgressBar().setProgress(0);

			mc.data = new SessionData(server.username, "-1");
			mc.data.key = server.mppass;

			try {
				mc.data.haspaid = Boolean.valueOf(HTTPUtil.fetchUrl("http://www.minecraft.net/haspaid.jsp", "user=" + URLEncoder.encode(server.username, "UTF-8")));
			} catch(UnsupportedEncodingException e) {
			}

			mc.server = server.ip;
			mc.port = server.port;

			mc.initGame();
			mc.setCurrentScreen(null);
		}
	}

	public void render() {
		RenderHelper.getHelper().drawDirtBG();
		RenderHelper.getHelper().renderText(this.title, this.getWidth() / 2, 15);

		super.render();
	}
}
