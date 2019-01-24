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
package sporemodder.view.ribbons;

import java.util.List;

import emord.javafx.ribbon.Ribbon;
import emord.javafx.ribbon.RibbonCustomMenu;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import sporemodder.GameManager;
import sporemodder.ProjectManager;
import sporemodder.UIManager;
import sporemodder.util.Project;
import sporemodder.view.UIUpdateListener;
import sporemodder.view.dialogs.CreateProjectUI;
import sporemodder.view.dialogs.OpenProjectUI;
import sporemodder.view.dialogs.ProgramSettingsUI;
import sporemodder.view.dialogs.UnpackPackageUI;
import sporemodder.view.dialogs.UnpackPresetsUI;

public class ProgramMenu implements UIUpdateListener {

	/** How many projects are displayed on the "Recent projects" list. */
	private static final int RECENT_COUNT = 10;
	
	private RibbonCustomMenu ribbonMenu;

	private HBox mainNode;
	
	private Button newProjectButton;
	private Button openProjectButton;
	private Button unpackPresetsButton;
	private Button importOldProject;
	
	private Button packButton;
	private Button unpackButton;
	private Button packRunButton;
	private Button runButton;
	private Button packDebugButton;
	
	private Button settingsButton;
	
	private VBox recentProjectsList;
	
	public RibbonCustomMenu getMenu() {
		return ribbonMenu;
	}
	
	private ImageView createIcon(String name) {
		return createIcon(name, 32, 32);
	}
	
	private ImageView createIcon(String name, int width, int height) {
		ImageView view = UIManager.get().loadIcon(name);
		view.setFitWidth(width);
		view.setFitWidth(height);
		return view;
	}
	
	private Button createButton(String text, String accelerator) {
		return createButton(text, accelerator, null);
	}
	
	private Button createButton(String text, String accelerator, Node graphic) {
		if (accelerator == null) {
			Button button = new Button(text, graphic);
			button.getStyleClass().add("artificial-menu-item");
			button.addEventFilter(ActionEvent.ACTION, (event) -> {
				ribbonMenu.hide();
			});
			return button;
		}
		else {
			HBox hbox = new HBox();
			hbox.setAlignment(Pos.CENTER_LEFT);
			if (graphic != null) {
				hbox.getChildren().add(graphic);
			}
			hbox.getChildren().add(new Label(text));
			hbox.setPrefWidth(Double.MAX_VALUE);
			
			BorderPane pane = new BorderPane();
			pane.setCenter(hbox);
			pane.setRight(new Label(accelerator));
			BorderPane.setAlignment(pane.getRight(), Pos.CENTER_RIGHT);
			
			Button button = new Button();
			button.setGraphic(pane);
			button.getStyleClass().add("artificial-menu-item");
			button.addEventFilter(ActionEvent.ACTION, (event) -> {
				ribbonMenu.hide();
			});
			return button;
		}
	}
	
	public void initialize(Ribbon ribbon) {
		
		final double width = 650;
		final double menuWidth = 325;
		final double height = RECENT_COUNT*24 + 10;
		
		recentProjectsList = new VBox(5);
		
		VBox vbox = new VBox(5);
		
		Separator separator = new Separator(Orientation.VERTICAL);
		separator.setPrefHeight(height - 10);
		
		mainNode = new HBox();
		mainNode.getStyleClass().add("context-menu");
		mainNode.getChildren().addAll(
				vbox,
				new Separator(Orientation.VERTICAL),
				recentProjectsList);
		
		
		HBox.setMargin(vbox, new Insets(5));
		HBox.setHgrow(recentProjectsList, Priority.ALWAYS);
		
		vbox.setMinWidth(menuWidth);
		vbox.setMaxWidth(menuWidth);
		
		mainNode.setMaxWidth(width);
		mainNode.setMaxHeight(height);
		
		newProjectButton = createButton("New project", null);
		newProjectButton.setOnAction((event) -> {
			CreateProjectUI.show();
		});
		
		openProjectButton = createButton("Open project...", "Ctrl+O");
		openProjectButton.setOnAction((event) -> {
			OpenProjectUI.show();
		});
		
		unpackPresetsButton = createButton("Unpack Presets", null);
		unpackPresetsButton.setOnAction((event) -> {
			UnpackPresetsUI.show(null, false);
			UIManager.get().setOverlay(false);
		});
		
		importOldProject = createButton("Import old project", null);
		importOldProject.setOnAction((event) -> {
			ProjectManager.get().importOldProject();
		}); 
		
		vbox.getChildren().addAll(newProjectButton, openProjectButton, unpackPresetsButton, importOldProject);
		
		vbox.getChildren().add(new Separator(Orientation.HORIZONTAL));
		
		packRunButton = createButton("Pack mod and run game", "F9", createIcon("pack-and-run.png", 48, 32));
		packRunButton.setOnAction(event -> {
			if (ProjectManager.get().packActive(false)) {
				UIManager.get().tryAction(() -> {
					GameManager.get().runGame();
				}, "Cannot run game.");
			}
		});
		
		packButton = createButton("Pack mod", "Ctrl+P", createIcon("pack.png"));
		packButton.setOnAction((event) -> {
			ProjectManager.get().packActive(false);
		});
		
		runButton = createButton("Run game", "F7", createIcon("run-without-pack.png"));
		runButton.setOnAction(event -> {
			UIManager.get().tryAction(() -> {
				GameManager.get().runGame();
			}, "Cannot run game.");
		});
		
		unpackButton = createButton("Unpack mod", "Ctrl+U", createIcon("unpack.png"));
		unpackButton.setOnAction((event) -> {
			UnpackPackageUI.showChooser();
		});
		
		packDebugButton = createButton("Pack mod with debug info", null, createIcon("debug-pack.png"));
		packDebugButton.setOnAction((event) -> {
			ProjectManager.get().packActive(true);
		});
		
		vbox.getChildren().addAll(packRunButton, packButton, runButton, unpackButton, packDebugButton);
		
		settingsButton = createButton("Settings", null, createIcon("config.png"));
		settingsButton.setOnAction(event -> {
			ProgramSettingsUI.show();
		});
		
		vbox.getChildren().add(new Separator(Orientation.HORIZONTAL));
		
		vbox.getChildren().addAll(settingsButton);
		
		
		UIManager.get().addListener(this);
		
		
		ribbonMenu = new RibbonCustomMenu(ribbon, mainNode);
		ribbonMenu.setDefaultStyling();
	}

	@Override
	public void onUIUpdate(boolean isFirstUpdate) {
		if (isFirstUpdate) {
			// Accelerators
			Platform.runLater(() -> {
				Scene scene = UIManager.get().getScene();
				
				scene.getAccelerators().put(new KeyCodeCombination(KeyCode.F7), () -> {
		        	runButton.fire();
		        });
				
				scene.getAccelerators().put(new KeyCodeCombination(KeyCode.F9), () -> {
		        	packRunButton.fire();
		        });
				
				scene.getAccelerators().put(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN), () -> {
		        	openProjectButton.fire();
		        });
				
				scene.getAccelerators().put(new KeyCodeCombination(KeyCode.U, KeyCombination.CONTROL_DOWN), () -> {
		        	unpackButton.fire();
		        });
		        
				scene.getAccelerators().put(new KeyCodeCombination(KeyCode.P, KeyCombination.CONTROL_DOWN), () -> {
		        	packButton.fire();
		        });
		    });
		}
		
		boolean hasProjects = !ProjectManager.get().getProjects().isEmpty();
		
		openProjectButton.setDisable(!hasProjects);
		
		boolean hasActiveProject = ProjectManager.get().getActive() != null;
		boolean hasGame = GameManager.get().canRunGame();
		boolean isReadOnly = hasActiveProject ? ProjectManager.get().getActive().isReadOnly() : true;
		
		packButton.setDisable(!hasActiveProject || isReadOnly);
		packRunButton.setDisable(!hasActiveProject || !hasGame || isReadOnly);
		packDebugButton.setDisable(!hasActiveProject || isReadOnly);
		runButton.setDisable(!hasGame);
		
		List<Project> recentProjects = ProjectManager.get().getRecentProjects(RECENT_COUNT);
		recentProjectsList.getChildren().clear();
		for (Project project : recentProjects) {
			Button button = createButton(project.getName(), null);
			button.setMnemonicParsing(false);  // will mess up with '_' symbols in the name otherwise
			button.setOnAction((event) -> {
				ProjectManager.get().setActive(project);
			});
			recentProjectsList.getChildren().add(button);
		}
	}
}
