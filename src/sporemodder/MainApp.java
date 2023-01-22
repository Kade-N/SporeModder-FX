/****************************************************************************
* Copyright (C) 2019 Eric Mor
*
* This file is part of SporeModder FX.
*
* SporeModder FX is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program.  If not, see <http://www.gnu.org/licenses/>.
****************************************************************************/

package sporemodder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.MetalTheme;
//import javax.swing.plaf.multi.MultiLookAndFeel;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
//import javax.swing.plaf.synth.SynthLookAndFeel;
/*import org.pushingpixels.substance.api.SubstanceCortex;
import org.pushingpixels.substance.api.SubstanceSkin;
import org.pushingpixels.substance.api.skin.AutumnSkin;
import org.pushingpixels.substance.api.skin.BusinessBlackSteelSkin;
import org.pushingpixels.substance.api.skin.BusinessBlueSteelSkin;
import org.pushingpixels.substance.api.skin.BusinessSkin;
import org.pushingpixels.substance.api.skin.CremeCoffeeSkin;
import org.pushingpixels.substance.api.skin.CremeSkin;
import org.pushingpixels.substance.api.skin.GraphiteGlassSkin;
import org.pushingpixels.substance.api.skin.GraphiteSkin;
import org.pushingpixels.substance.api.skin.MagellanSkin;
import org.pushingpixels.substance.api.skin.MistAquaSkin;
import org.pushingpixels.substance.api.skin.ModerateSkin;
import org.pushingpixels.substance.api.skin.NebulaBrickWallSkin;
import org.pushingpixels.substance.api.skin.NebulaSkin;
import org.pushingpixels.substance.api.skin.OfficeSilver2007Skin;
import org.pushingpixels.substance.api.skin.RavenSkin;
import org.pushingpixels.substance.api.skin.SaharaSkin;*/

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import sporemodder.MessageManager.MessageType;
import sporemodder.file.shaders.FXCompiler;
import sporemodder.file.shaders.ShaderData;
import sporemodder.userinterface.smfxlook.*;

/**
 * The main class of the program.
 */
public class MainApp extends Application {
	
	private static MainApp instance;
	private UIManager uiManager;
	private MessageManager messageManager;
	private EditorManager editorManager;
	private PathManager pathManager;
	private HashManager hashManager;
	private ProjectManager projectManager;
	private PluginManager pluginManager;
	private GameManager gameManager;
	private DocumentationManager documentationManager;
	private FormatManager formatManager;
	private FileManager fileManager;
	private UpdateManager updateManager;
	private FXCompiler fxCompiler;
	
	private Properties settings;
	
	/**
	 * Returns the class that manages and controls the user interface of SporeModder.
	 * @return The UI manager.
	 */
	public UIManager getUIManager() {
		return uiManager;
	}
	
	/**
	 * Returns the class that manages internal messages and events.
	 * @return The message manager.
	 */
	public MessageManager getMessageManager() {
		return messageManager;
	}
	
	/**
	 * Returns the class that manages the supported file editors and syntax highlighting formats.
	 * @return The editor manager.
	 */
	public EditorManager getEditorManager() {
		return editorManager;
	}
	
	/**
	 * Returns the class that manages the program and projects path.
	 * @return The path manager.
	 */
	public PathManager getPathManager() {
		return pathManager;
	}
	
	/**
	 * Returns the class that controls the names and hashes, number formatting, etc.
	 * @return The hash manager.
	 */
	public HashManager getHashManager() {
		return hashManager;
	}
	
	/**
	 * Returns the class that manages the projects in the program.
	 * @return The project manager.
	 */
	public ProjectManager getProjectManager() {
		return projectManager;
	}
	
	/**
	 * Returns the class that manages the SporeModder plugins.
	 * @return The plugin manager.
	 */
	public PluginManager getPluginManager() {
		return pluginManager;
	}
	
	/**
	 * Returns the class that manages the available Spore games.
	 * @return The game manager
	 */
	public GameManager getGameManager() {
		return gameManager;
	}
	
	/**
	 * Returns the class that controls the documentation of the program.
	 * @return The documentation manager
	 */
	public DocumentationManager getDocumentationManager() {
		return documentationManager;
	}
	
	/**
	 * Returns the class that keeps track of all the convertible file formats.
	 * @return The format manager
	 */
	public FormatManager getFormatManager() {
		return formatManager;
	}
	
	/**
	 * Returns the class that contains multiple utilities to work with files and folders.
	 * @return The file manager.
	 */
	public FileManager getFileManager() {
		return fileManager;
	}
	
	public UpdateManager getUpdateManager() {
		return updateManager;
	}
	
	public FXCompiler getFXCompiler() {
		return fxCompiler;
	}
	
	/**
	 * Returns the current instance of the MainApp class.
	 */
	public static MainApp get() {
		return MainApp.instance;
	}
	
	public static void testInit() {
		new MainApp().init(true);
	}
	
	private void init(boolean testInit) {
		MainApp.instance = this;
		
		messageManager = new MessageManager();
		gameManager = new GameManager();
		pluginManager = new PluginManager();
		pathManager = new PathManager();
		uiManager = new UIManager();
		if (!testInit) editorManager = new EditorManager();
		hashManager = new HashManager();
		projectManager = new ProjectManager();
		if (!testInit) documentationManager = new DocumentationManager();
		formatManager = new FormatManager();
		fileManager = new FileManager();
		updateManager = new UpdateManager();
		fxCompiler = new FXCompiler();
		
		// Initialize it first, otherwise we can't get the settings
		pathManager.initialize(null);
		
		settings = new Properties();
		File settingsFile = pathManager.getProgramFile("config.properties");
		if (settingsFile.exists()) {
			try (InputStream stream = new FileInputStream(settingsFile)) {
				settings.load(stream);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		// The path managers might be used in other manager initialization methods,
		// so we ensure we initialize them first.
		messageManager.initialize(settings);
		gameManager.initialize(settings);
		fileManager.initialize(settings);
		
		if (!testInit) uiManager.initialize(settings);
		if (!testInit) editorManager.initialize(settings);
		hashManager.initialize(settings);
		projectManager.initialize(settings);
		if (!testInit) documentationManager.initialize(settings);
		formatManager.initialize(settings);
		fxCompiler.initialize(settings);
		
		ShaderData.initialize();
		
		// The plugin must go last, as plugins will use the other managers
		pluginManager.initialize(settings);
		
		messageManager.postMessage(MessageType.OnSettingsLoad, settings);
		prepareSwingTheme();
	}
	
	public void saveSettings() {
		messageManager.saveSettings(settings);
		gameManager.saveSettings(settings);
		pluginManager.saveSettings(settings);
		pathManager.saveSettings(settings);
		uiManager.saveSettings(settings);
		editorManager.saveSettings(settings);
		hashManager.saveSettings(settings);
		projectManager.saveSettings(settings);
		documentationManager.saveSettings(settings);
		formatManager.saveSettings(settings);
		fileManager.saveSettings(settings);
		fxCompiler.saveSettings(settings);
		
		messageManager.postMessage(MessageType.OnSettingsSave, settings);
		
		try (OutputStream stream = new FileOutputStream(pathManager.getProgramFile("config.properties"))) {
			settings.store(stream, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void init() {
		init(false);
	}
	
	@Override
	public void stop() {
		// The plugin must go first, as plugins will use the other managers
		pluginManager.dispose();
		
		fxCompiler.dispose();
		documentationManager.dispose();
		formatManager.dispose();
		editorManager.dispose();
		projectManager.dispose();
		hashManager.dispose();
		uiManager.dispose();
		
		fileManager.dispose();
		pathManager.dispose();
		gameManager.dispose();
	}

	@Override
	public void start(Stage primaryStage) {
		
		uiManager.start(primaryStage);
		
		// Before showing, notify
		uiManager.notifyUIUpdate(true);
		
		uiManager.show();
		
		if (!updateManager.checkUpdate()) {
			Platform.exit();
			return;
		}
		
		if (uiManager.isFirstTime()) {
			gameManager.showFirstTimeDialog();
			
			projectManager.showFirstTimeDialog();
			
			uiManager.setOverlay(false);
			
			uiManager.setFirstTime(false);
			MainApp.get().saveSettings();
		}

	}
	
	
	/*
	//TODO: Hopefully don't lock Swing GUI elements to a binary dark/light choice
	
	 * probably a stretch, but hey...I can dream
	*/
	static void prepareSwingTheme() {
		try {
			LookAndFeel laf = createSwingTheme();
			javax.swing.UIManager.setLookAndFeel(laf);
			/*if (laf instanceof MetalLookAndFeel) {
				MetalLookAndFeel.setCurrentTheme(theme);
			}*/
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	static LookAndFeel createSwingTheme() {
		LookAndFeel laf = new MetalLookAndFeel();
		MetalTheme theme = isDarkTheme()
			? createDarkSwingTheme()
			: createLightSwingTheme()
		;
		MetalLookAndFeel.setCurrentTheme(theme);
		return laf;
	}
	
	static boolean isDarkTheme() {
		//TODO: Don't rely on dumb name guessing games
		String currentStyle = UIManager.get().getCurrentStyle().toLowerCase();
		return currentStyle.contains("dark") || currentStyle.contains("darcula");
	}
	
	
	static MetalTheme createLightSwingTheme() {
		return new DefaultMetalTheme();
	}
	static MetalTheme createDarkSwingTheme() {
		if (true) {
			//TODO: Don't use light theme as dark theme
			return createLightSwingTheme();
			}
		else {
			return new 
					//PatheticExcuseForADarkTheme
					//DarkMetalTheme
					DarkMetalTheme2
			();
		}
	}
	
	
	public static void main(String[] args) {
		launch(args);
	}
	
	//TODO: Stub for a function that existed in original SporeModder...but what did it do?
	public static List<String> getSearchStrings(String what) {
		return new ArrayList<String>();
	}
}
