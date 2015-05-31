package com.mtgames.lvlr;

import com.mtgames.platformer.Game;
import com.mtgames.platformer.debug.Command;
import com.mtgames.platformer.gfx.Font;
import com.mtgames.utils.Debug;
import org.lwjgl.BufferUtils;
import org.lwjgl.Sys;
import org.lwjgl.opengl.GLContext;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class Editor extends Game {

	public static void main(String[] args) {
		if (Integer.getInteger("com.mtgames.scale") != null) {
			Game.scale = Integer.getInteger("com.mtgames.scale");
		} else {
			Game.scale = 1;
		}

		if (Integer.getInteger("com.mtgames.debug") == 0) {
			debug = true;
		}

		new Editor().start();
	}

	protected void init() {
		super.init();
		level = new LevelE();
		Command.set(level, screen);
		Command.exec("load debug_level");
	}

	private synchronized void start() {
		Thread main = new Thread(this);
		main.setName(NAME + " - Main");
		main.start();
	}

	public void run() {
		Debug.log("LWGJL version: " + Sys.getVersion(), Debug.DEBUG);

		try {
			init();
			loop();

			glfwDestroyWindow(window);
			keyCallback.release();
		} finally {
			glfwTerminate();
			errorCallback.release();
		}

		System.exit(0);
	}

	private void loop() {
		long lastTime = System.nanoTime();
		double nsPerTick = 1000000000d / TPS;

		int ticks = 0;
		int frames = 0;

		long lastTimer = System.currentTimeMillis();
		long lastTimerShort = System.currentTimeMillis();
		double delta = 0;

		GLContext.createFromCurrent();

		glClearColor (0.0f, 0.0f, 0.0f, 0.5f);
		glClearDepth (1.0f);
		glDepthFunc(GL_LEQUAL);
		glEnable(GL_DEPTH_TEST);
		glShadeModel(GL_SMOOTH);
		glHint (GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);

		screen.initLight();

		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0, WIDTH * scale, HEIGHT * scale, 0, 1, -1);
		glMatrixMode(GL_MODELVIEW);
		glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		Font.init();

		Debug.log("OpenGL version: " + glGetString(GL_VERSION), Debug.DEBUG);

		while (glfwWindowShouldClose(window) == GL_FALSE) {
			long now = System.nanoTime();
			delta += (now - lastTime) / nsPerTick;
			lastTime = now;
			boolean shouldRender = FPSUNLOCK;

			while (delta >= 1) {
				if (!paused) {
					tick();
				}
				ticks++;
				delta -= 1;
				shouldRender = true;
			}

			// try{
			// Thread.sleep(2);
			// } catch (InterruptedException e) {
			// e.printStackTrace();
			// }

			if (shouldRender) {
				frames++;
				render();
			}

			if (input.isPressed(GLFW_KEY_P)) {
				Command.exec("pause");
				input.set(GLFW_KEY_P, false);
			}

			if (input.isPressed(GLFW_KEY_F3) && debug) {
				showDebug = !showDebug;
				input.set(GLFW_KEY_F3, false);
			}

			/* Determine current fps */
			if (System.currentTimeMillis() - lastTimerShort >= 100) {
				lastTimerShort += 100;
				fps = (int) ((frames) / ((System.currentTimeMillis() - lastTimer) / 1000d) + 0.5d);
			}

			if (System.currentTimeMillis() - lastTimer >= 1000) {
				lastTimer += 1000;
				Debug.log(frames + " Frames, " + ticks + " Ticks", Debug.INFO);
				frames = 0;
				ticks = 0;
			}
		}
	}

	private void tick() {
		DoubleBuffer x = BufferUtils.createDoubleBuffer(1);
		DoubleBuffer y = BufferUtils.createDoubleBuffer(1);
		glfwGetCursorPos(window, x, y);

		x.rewind();
		y.rewind();

		int xPos = (int) x.get() / 2 + screen.xOffset;
		int yPos = (int) y.get() / 2 + screen.yOffset;

		int xTile = xPos >> 4;
		int yTile = yPos >> 4;

//		Debug.log(xTile + " " + yTile, Debug.DEBUG);
		if (input.isPressed(GLFW_KEY_SPACE)) {
			Command.exec("tile 2 " + xTile + " " + yTile);
		}

		if (input.isPressed(GLFW_KEY_L)) {
			Command.exec("lighting");
			input.set(GLFW_KEY_L, false);
		}

		level.tick();
	}
}