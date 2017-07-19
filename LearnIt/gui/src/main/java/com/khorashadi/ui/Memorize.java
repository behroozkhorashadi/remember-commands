package com.khorashadi.ui;

import com.khorashadi.main.Interactor;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import static com.khorashadi.main.Interactor.SearchCategory.GENERAL;
import static com.khorashadi.main.Interactor.SearchCategory.PEOPLE;
import static com.khorashadi.main.Interactor.SearchCategory.TASKS;
import static com.khorashadi.ui.UiUtils.setupSaveKeyActions;
import static javafx.scene.input.KeyEvent.KEY_RELEASED;

public class Memorize extends Application {
    private static final KeyCode[] KEY_CODES = {};
    private static final KeyCombination[] KEY_COMBINATIONS
            = {UiUtils.COMMAND_S, UiUtils.SHIFT_ENTER};
    private static final int INSTRUCTIONS = 0;
    private static final int KEY_WORDS = INSTRUCTIONS + 1;
    private static final int TEXT_AREA = KEY_WORDS + 1;
    private Stage stage;
    private Interactor interactor;
    private Label instructions = new Label("Cmc-R for General Note, Cmd-F for Find, " +
            "Cmd-P for Person, Cmd-N for Note, Cmd-T for Tasks");
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
        KeyComboActionPair commandR = new KeyComboActionPair(
                new KeyCodeCombination(KeyCode.R, KeyCombination.META_DOWN),
                () -> setupGeneralRecord(gridPane));
        KeyComboActionPair commandP = new KeyComboActionPair(
                new KeyCodeCombination(KeyCode.P, KeyCombination.META_DOWN),
                () -> setupNameRecord(gridPane));
        KeyComboActionPair commandT = new KeyComboActionPair(
                new KeyCodeCombination(KeyCode.T, KeyCombination.META_DOWN),
                () -> setupTaskRecord(gridPane));
        KeyComboActionPair commandF = new KeyComboActionPair(
                new KeyCodeCombination(KeyCode.F, KeyCombination.META_DOWN),
                () -> search.showFindDialog());
        UiUtils.setupKeyboardShortcuts(scene, commandR, commandP, commandT, commandF);
    }

    private void setupUi(StackPane root, Scene scene) {
        final GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.setVgap(5);
        gridPane.setHgap(5);
        setupKeyboardShortcuts(scene, gridPane);
        gridPane.add(instructions, 0, INSTRUCTIONS);
        setupGeneralRecord(gridPane);
        root.getChildren().add(gridPane);
    }

    private void setupTaskRecord(GridPane gridPane) {

    }

    private void setupNameRecord(GridPane gridPane) {
        setStageTitle("Remember Name");
        reset(gridPane);
        // maybe add a label.
        final TextField keyWords = new TextField();
        keyWords.setPromptText("Person's Name");
        gridPane.add(keyWords, 0, KEY_WORDS);
        final TextArea remember = new TextArea();
        remember.setPromptText("The stuff you want to remember about this person.");
        gridPane.add(remember, 0, TEXT_AREA);
        // setup save actions
        final Button mainButton = new Button("Save Name Info");
        final Runnable action = () -> System.out.println("Saved name");

        setupSaveKeyActions(action, mainButton, KEY_CODES, KEY_COMBINATIONS, remember, keyWords);
        gridPane.add(mainButton, 0, TEXT_AREA + 1);
    }

    private void setupGeneralRecord(GridPane gridPane) {
        setStageTitle("General Record");
        reset(gridPane);
        final TextField keyWords = new TextField();
        keyWords.setPromptText("Key words for tagged for search");
        gridPane.add(keyWords, 0, KEY_WORDS);

        final TextArea remember = new TextArea();
        remember.setPromptText("The stuff you want to remember.");
        gridPane.add(remember, 0, TEXT_AREA);
        final Runnable action = () -> {
            interactor.createGeneralNote(keyWords.getText(), remember.getText());
            keyWords.clear();
            remember.clear();
            System.out.println("Saved General note");
        };
        final Button mainButton = new Button("Save Info");
        setupSaveKeyActions(action, mainButton, KEY_CODES, KEY_COMBINATIONS, remember, keyWords);
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
