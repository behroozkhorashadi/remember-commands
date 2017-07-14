package com.khorashadi;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import static javafx.scene.input.KeyEvent.*;

public class LearnIt extends Application {
    private static final KeyCombination SHIFT_ENTER
            = new KeyCodeCombination(KeyCode.ENTER, KeyCombination.SHIFT_DOWN);
    private static final KeyCombination COMMAND_S
            = new KeyCodeCombination(KeyCode.S, KeyCombination.META_DOWN);
    private static final int INSTRUCTIONS = 0;
    private static final int KEY_WORDS = INSTRUCTIONS + 1;
    private static final int TEXT_AREA = KEY_WORDS + 1;
    private Stage stage;
    private Label instructions
            = new Label("Ctrl-R for Remember, Ctrl-F for Find, Ctrl-P for Person, "
            + "Ctrl-N for Note, Ctrl-T for Tasks");

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        stage.setTitle("Memorize: Info");
        StackPane root = new StackPane();
        Scene scene = new Scene(root);
        setupUi(root, scene);
        stage.setScene(scene);
        stage.show();
    }

    private void setupKeyboardShortcuts(Scene scene, GridPane gridPane) {
        final KeyCombination commandR = new KeyCodeCombination(KeyCode.R, KeyCombination.META_DOWN);
        scene.addEventHandler(KEY_RELEASED, event -> {
            if (commandR.match(event)) {
                System.out.println("Remember");
                setupRemember(gridPane);
            }
        });
        final KeyCombination commandF = new KeyCodeCombination(KeyCode.F, KeyCombination.META_DOWN);
        scene.addEventHandler(KEY_RELEASED, event -> {
            if (commandF.match(event)) {
                System.out.println("Find");
                showFindDialog();
            }
        });
        final KeyCombination commandP = new KeyCodeCombination(KeyCode.P, KeyCombination.META_DOWN);
        scene.addEventHandler(KEY_RELEASED, event -> {
            if (commandP.match(event)) {
                System.out.println("Person");
                setupNameRemember(gridPane);
            }
        });
        final KeyCombination commandT = new KeyCodeCombination(KeyCode.T, KeyCombination.META_DOWN);
        scene.addEventHandler(KEY_RELEASED, event -> {
            if (commandT.match(event)) {
                System.out.println("Task");
                setupTask(gridPane);
            }
        });
    }

    private void setupSearchKeyboardShortcuts(Scene scene, GridPane gridPane) {
        final KeyCombination commandR = new KeyCodeCombination(KeyCode.R, KeyCombination.META_DOWN);
        scene.addEventHandler(KEY_RELEASED, event -> {
            if (commandR.match(event)) {
                System.out.println("Search Memory");
                //TODO:
            }
        });
        final KeyCombination commandP = new KeyCodeCombination(KeyCode.P, KeyCombination.META_DOWN);
        scene.addEventHandler(KEY_RELEASED, event -> {
            if (commandP.match(event)) {
                System.out.println("Find Person");
                //TODO:
            }
        });
        final KeyCombination commandT = new KeyCodeCombination(KeyCode.T, KeyCombination.META_DOWN);
        scene.addEventHandler(KEY_RELEASED, event -> {
            if (commandT.match(event)) {
                System.out.println("Find Task");
                //TODO:
            }
        });
    }

    private void setupTask(GridPane gridPane) {
    }

    private void showFindDialog() {
        Stage searchStage = new Stage();
        searchStage.setTitle("Learn It");
        StackPane root = new StackPane();
        Scene scene = new Scene(root);
        searchStage.setScene(scene);
        final GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.setVgap(5);
        gridPane.setHgap(5);

        root.getChildren().add(gridPane);

        setStageTitle("Search");
        reset(gridPane);
        // maybe add a label.
        final TextField keyWords = new TextField();
        keyWords.setPromptText("Search");
        final Runnable action = new Runnable() {
            @Override
            public void run() {
                System.out.println("Start Search");
            }
        };
        final Button mainButton = new Button();
        mainButton.setText("Start Search");
        setupKeyActions(action, mainButton, keyWords);
        gridPane.add(keyWords, 0, KEY_WORDS);
        gridPane.add(mainButton, 1, KEY_WORDS);
        searchStage.show();
    }

    private void setupUi(StackPane root, Scene scene) {
        final GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.setVgap(5);
        gridPane.setHgap(5);
        setupKeyboardShortcuts(scene, gridPane);
        gridPane.add(instructions, 0, INSTRUCTIONS);
        setupNameRemember(gridPane);
        root.getChildren().add(gridPane);
    }

    private void setupNameRemember(GridPane gridPane) {
        setStageTitle("Remember Person");
        reset(gridPane);
        // maybe add a label.
        final TextField keyWords = new TextField();
        keyWords.setPromptText("Person's Name");
        gridPane.add(keyWords, 0, KEY_WORDS);
        final TextArea remember = new TextArea();
        remember.setPromptText("The stuff you want to remember about this person.");
        gridPane.add(remember, 0, TEXT_AREA);
        // setup save actions
        final Button mainButton = new Button();
        mainButton.setText("Save Name Info");
        final Runnable action = new Runnable() {
            @Override
            public void run() {
                System.out.println("Saved name");
            }
        };
        setupKeyActions(action, mainButton, remember, keyWords);
        gridPane.add(mainButton, 0, TEXT_AREA + 1);
    }

    private void setupRemember(GridPane gridPane) {
        setStageTitle("Remember");
        reset(gridPane);
        final TextField keyWords = new TextField();
        keyWords.setPromptText("Key words for tagged for search");
        gridPane.add(keyWords, 0, KEY_WORDS);

        final TextArea remember = new TextArea();
        remember.setPromptText("The stuff you want to remember.");
        gridPane.add(remember, 0, TEXT_AREA);
        final Runnable action = new Runnable() {
            @Override
            public void run() {
                System.out.println("Saved");
            }
        };
        final Button mainButton = new Button();
        mainButton.setText("Save Info");
        setupKeyActions(action, mainButton, remember, keyWords);
        gridPane.add(mainButton, 0, TEXT_AREA + 1);
    }

    private static void setupKeyActions(
            Runnable action,
            Button button,
            TextInputControl... textInputControls) {
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                action.run();
            }
        });
        for (TextInputControl textInputControl : textInputControls) {
            textInputControl.addEventHandler(KEY_RELEASED, event -> {
                if (SHIFT_ENTER.match(event) || COMMAND_S.match(event)) {
                    action.run();
                }
            });
        }
    }

    private void reset(GridPane gridPane) {
        gridPane.getChildren().retainAll(instructions);
    }

    private void setStageTitle(String stageTitle) {
        if (stage != null) {
            stage.setTitle(stageTitle);
        }
    }
}
