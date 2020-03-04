package com.week1.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.week1.game.GameController;
import org.apache.commons.cli.*;

public class DesktopLauncher {
	public static void main (String[] args) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "RTS";
		config.width = 800;
		config.height = 800;
		CommandLineParser parser = new DefaultParser();
		Options options = createOptions();
		try {
			CommandLine commandLine = parser.parse(createOptions(), args);
			if (validate(commandLine)) {
				new LwjglApplication(new GameController(commandLine), config);
			} else {
				new HelpFormatter().printHelp("game", options);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	private static Options createOptions() {
		return new Options()
			.addOption(
				Option.builder("l")
					.longOpt("log")
					.hasArg().argName("i|e|d")
					. desc("set the logging level to (i)nfo, (e)rror, (d)ebug")
					.build()
			);
	}

	private static boolean validate(CommandLine commandLine) {
		return true;
	}
}
