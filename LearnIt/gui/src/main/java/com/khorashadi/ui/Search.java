package com.khorashadi.ui;

import com.khorashadi.main.Interactor;
import com.khorashadi.models.GeneralRecord;
import com.khorashadi.models.BaseRecord;

import java.util.Collection;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
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


class Search {
    private final TextField searchTerms;
    private final TextArea textArea;
    private Interactor.SearchCategory searchCategory = GENERAL;
    private ListView<BaseRecord> list = new ListView<>();
    private Stage searchStage = new Stage();

    Search(Interactor interactor) {
        searchStage.setTitle("Search");
        StackPane root = new StackPane();
        Scene scene = new Scene(root);
        searchStage.setScene(scene);
        final GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.setVgap(5);
        gridPane.setHgap(5);

        root.getChildren().add(gridPane);
        gridPane.getChildren().clear();

        // maybe add a label.
        searchTerms = new TextField();
        searchTerms.setPromptText("Search");
        final Runnable action = () ->
                displaySearch(interactor.searchData(searchCategory, searchTerms.getText()));
        final Button mainButton = new Button("Start Search");
        KeyCode[] keyCodes = {KeyCode.ENTER};
        KeyCombination[] combinations = {};
        UiUtils.setupSaveKeyActions(action, mainButton, keyCodes, combinations, searchTerms);
        gridPane.add(searchTerms, 0, 0);
        gridPane.add(mainButton, 1, 0);

        textArea = new TextArea();

        list.setPrefWidth(150);
        list.setPrefHeight(70);
        gridPane.add(list, 0, 1);
        list.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        textArea.setText(UiUtils.getSaveInfoDisplayFormat(newValue));
                    }
                });
        textArea.setEditable(false);
        gridPane.add(textArea, 1, 1);
        setupSearchKeyboardShortcuts(scene);
    }

    void showFindDialog() {
        searchTerms.clear();
        searchTerms.requestFocus();
        textArea.clear();
        list.getSelectionModel().clearSelection();
        searchStage.show();
    }

    private void displaySearch(Collection<GeneralRecord> generalRecords) {
        list.setItems(FXCollections.observableArrayList(generalRecords));
    }

    private void setupSearchKeyboardShortcuts(Scene scene) {
        KeyComboActionPair commandR = new KeyComboActionPair(
                new KeyCodeCombination(KeyCode.R, KeyCombination.META_DOWN),
                () -> searchCategory = GENERAL);
        KeyComboActionPair commandP = new KeyComboActionPair(
                new KeyCodeCombination(KeyCode.P, KeyCombination.META_DOWN),
                () -> searchCategory = PEOPLE);
        KeyComboActionPair commandT = new KeyComboActionPair(
                new KeyCodeCombination(KeyCode.T, KeyCombination.META_DOWN),
                () -> searchCategory = TASKS);
        KeyComboActionPair commandW = new KeyComboActionPair(
                new KeyCodeCombination(KeyCode.W, KeyCombination.META_DOWN),
                () -> searchStage.hide());
        UiUtils.setupKeyboardShortcuts(scene, commandR, commandP, commandT, commandW);
    }
}
