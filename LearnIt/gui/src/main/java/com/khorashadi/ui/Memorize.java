package com.khorashadi.ui;

import com.khorashadi.main.Interactor;
import com.khorashadi.models.BaseRecord;

import java.io.IOException;

import io.reactivex.disposables.CompositeDisposable;
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
import javafx.scene.web.HTMLEditor;
import javafx.stage.Stage;

import static com.khorashadi.ui.UiUtils.setupSaveKeyActions;

public class Memorize extends Application {
    private static final KeyCode[] KEY_CODES = {};
    private static final KeyCombination[] KEY_COMBINATIONS
            = {UiUtils.COMMAND_S, UiUtils.SHIFT_ENTER};
    private static final int INSTRUCTIONS = 0;
    private static final int KEY_WORDS = INSTRUCTIONS + 1;
    private static final int TEXT_AREA = KEY_WORDS + 1;
    private Stage stage;
    private Interactor interactor;
    private Label instructions = new Label("Cmd-F for Find");
    private CompositeDisposable currentSubscriptions = null;
    private GridPane gridPane;


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        try {
            interactor = new Interactor(getParameters().getRaw().get(0), this);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public void editRecord(BaseRecord baseRecord) {
        switch (baseRecord.getSaveType()) {
            case GENERAL_RECORD:
                setupGeneralRecord(baseRecord);
                break;
        }
    }

    public void setFocus() {
        stage.requestFocus();
    }

    private void setupKeyboardShortcuts(final Scene scene) {
        KeyComboActionPair commandR = new KeyComboActionPair(
                new KeyCodeCombination(KeyCode.R, KeyCombination.META_DOWN),
                () -> setupGeneralRecord(null));
        KeyComboActionPair commandP = new KeyComboActionPair(
                new KeyCodeCombination(KeyCode.P, KeyCombination.META_DOWN),
                () -> setupNameRecord());
        KeyComboActionPair commandT = new KeyComboActionPair(
                new KeyCodeCombination(KeyCode.T, KeyCombination.META_DOWN),
                () -> setupGeneralRecord(null));
        KeyComboActionPair commandF = new KeyComboActionPair(
                new KeyCodeCombination(KeyCode.F, KeyCombination.META_DOWN),
                () -> interactor.showFindDialog());
        // Intentionally don't do anything with the returned disposable
        UiUtils.setupKeyboardShortcuts(scene, commandR, commandP, commandT, commandF);
    }

    private void setupUi(StackPane root, Scene scene) {
        this.gridPane = new GridPane();
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.setVgap(5);
        gridPane.setHgap(5);
        setupKeyboardShortcuts(scene);
        gridPane.add(instructions, 0, INSTRUCTIONS);
        setupGeneralRecord(null);
        root.getChildren().add(gridPane);
    }

    private void setupTaskRecord() {
        reset();
    }

    private void setupNameRecord() {
        setStageTitle("Remember Name");
        reset();
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

        currentSubscriptions = setupSaveKeyActions(
                action, mainButton, KEY_CODES, KEY_COMBINATIONS, remember, keyWords);
        gridPane.add(mainButton, 0, TEXT_AREA + 1);
    }

    private void setupGeneralRecord(final BaseRecord currentEdit) {
        setStageTitle("General Record");
        reset();
        final TextField keyWords = currentEdit != null
                ? new TextField(currentEdit.getUserTagsRaw()) : new TextField();
        keyWords.setPromptText("Key words for tagged for search");
        gridPane.add(keyWords, 0, KEY_WORDS);

        final HTMLEditor htmlEditor = new HTMLEditor();
        if (currentEdit != null) {
            htmlEditor.setHtmlText(currentEdit.getMainInfo());
        }

        gridPane.add(htmlEditor, 0, TEXT_AREA);
        final Runnable action = () -> {
            if (currentEdit != null) {
                interactor.updateRecord(currentEdit, keyWords.getText(), htmlEditor.getHtmlText());
            } else {
                interactor.createGeneralRecord(
                        keyWords.getText(), UiUtils.formatForSave(htmlEditor.getHtmlText()));
            }
            keyWords.clear();
            htmlEditor.setHtmlText("");
            System.out.println("Saved General note");
        };
        final Button mainButton = new Button("Save Info");
        currentSubscriptions = setupSaveKeyActions(
                action, mainButton, KEY_CODES, KEY_COMBINATIONS, htmlEditor, keyWords);
        gridPane.add(mainButton, 0, TEXT_AREA + 1);
    }

    private void reset() {
        if (currentSubscriptions != null && !currentSubscriptions.isDisposed()) {
            currentSubscriptions.dispose();
            currentSubscriptions = null;
        }
        gridPane.getChildren().retainAll(instructions);
    }

    private void setStageTitle(String stageTitle) {
        if (stage != null) {
            stage.setTitle(stageTitle);
        }
    }
}
