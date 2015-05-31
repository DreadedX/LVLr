package com.mtgames.lvlr;

import com.mtgames.platformer.debug.Command;
import com.mtgames.platformer.level.Level;
import com.mtgames.platformer.level.LevelLoader;
import com.mtgames.utils.Debug;

import java.io.File;

public class LevelE extends Level {
	public void tick() {
		if (reload) {
			load(path);
			reload = false;
		}

		entities.get(0).tick();
	}

	private void load(String path) {
		boolean external = false;

		if (ClassLoader.getSystemResource(path) == null) {
			if (new File("platformer/" + path).exists()) {
				external = true;
				path = "platformer/" + path;
			} else {
				Debug.log("The file '" + path + "' does not exist", Debug.WARNING);
				return;
			}
		}

		if (this.entities.size() > 0) {
			this.entities.clear();
		}

		if (this.backgrounds.size() > 0) {
			this.backgrounds.clear();
		}

		if (this.lightSources.size() > 0) {
			this.lightSources.clear();
		}

		Command.exec("spawn freeCamera 0 0");

		try {
			LevelLoader loader = new LevelLoader(this, path, external);
			width = loader.getWidth();
			height = loader.getHeight();

			tiles = loader.getTiles();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
