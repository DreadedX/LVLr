package com.mtgames.lvlr;

import com.mtgames.platformer.Game;
import com.mtgames.platformer.debug.Command;
import com.mtgames.utils.LauncherBase;
import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;

public class Launcher extends LauncherBase{

	static private ComboBox<Integer> scale;

	public static void main(String[] args) {
		consoleEnabled = true;

		scale = new ComboBox<>(FXCollections.observableArrayList(1, 2));
		scale.setValue(2);

		addOption("Scale", scale);

		launch(args);
	}

	@Override protected void run() {
		System.setProperty("com.mtgames.scale", String.valueOf(scale.getValue()));

		Editor.main(new String[] { "" });
	}

	@Override protected void consoleCommand(String s) {
		Command.exec(s);
	}
}
