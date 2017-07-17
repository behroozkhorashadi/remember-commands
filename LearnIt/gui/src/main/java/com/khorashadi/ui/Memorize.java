package com.khorashadi.ui;

import com.khorashadi.main.Interactor;

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

import static com.khorashadi.ui.UiUtils.setupKeyActions;
import static javafx.scene.input.KeyEvent.*;

public class Memorize extends Application {

    private static final int INSTRUCTIONS = 0;
    private static final int KEY_WORDS = INSTRUCTIONS + 1;
    private static final int TEXT_AREA = KEY_WORDS + 1;
    private Stage stage;
    private Interactor interactor;
    private Label instructions
            = new Label("Ctrl-R for Remember, Ctrl-F for Find, Ctrl-P for Person, "
            + "Ctrl-N for Note, Ctrl-T for Tasks");
    private Search search;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        interactor = new Interactor(getParameters());
        search = new Search(interactor);
        stage.setTitle("Memorize: Info");
        StackPane root = new StackPane();
        Scene scene = new Scene(root);
        setupUi(root, scene);
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() {
        System.out.println("Stop");
    }

    private void setupKeyboardShortcuts(Scene scene, GridPane gridPane) {
        final KeyCombination commandR = new KeyCodeCombination(KeyCode.R, KeyCombination.META_DOWN);
        scene.addEventHandler(KEY_RELEASED, event -> {
            if (commandR.match(event)) {
                System.out.println("GeneralNote");
                setupGeneralNote(gridPane);
            }
        });
        final KeyCombination commandF = new KeyCodeCombination(KeyCode.F, KeyCombination.META_DOWN);
        scene.addEventHandler(KEY_RELEASED, event -> {
            if (commandF.match(event)) {
                System.out.println("Find");
                search.showFindDialog();
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

    private void setupTask(GridPane gridPane) {
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

    private void setupGeneralNote(GridPane gridPane) {
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
                interactor.createGeneralNote(keyWords.getText(), remember.getText());
                keyWords.clear();
                remember.clear();
                System.out.println("Saved General note");
            }
        };
        final Button mainButton = new Button();
        mainButton.setText("Save Info");
        setupKeyActions(action, mainButton, remember, keyWords);
        gridPane.add(mainButton, 0, TEXT_AREA + 1);
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
