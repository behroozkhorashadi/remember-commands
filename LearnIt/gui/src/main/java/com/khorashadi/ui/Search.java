package com.khorashadi.ui;

import com.khorashadi.main.Interactor;
import com.khorashadi.models.GeneralRecord;
import com.khorashadi.models.BaseRecord;

import java.util.Collection;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import static com.khorashadi.main.Interactor.SearchCategory.GENERAL;
import static com.khorashadi.main.Interactor.SearchCategory.PEOPLE;
import static com.khorashadi.main.Interactor.SearchCategory.TASKS;


class Search {
    private final TextField searchTerms;
    private final WebView webView;
    private BaseRecord lastEntry = null;
    private Interactor.SearchCategory searchCategory = GENERAL;
    private ListView<BaseRecord> list = new ListView<>();
    private Stage searchStage = new Stage();
    private CheckBox searchAll = new CheckBox("Search All");

    Search(final Interactor interactor) {
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
                displaySearchResults(interactor.searchData(
                        searchCategory, searchTerms.getText(), searchAll.isSelected()));
        final Button mainButton = new Button("Start Search");
        KeyCode[] keyCodes = {KeyCode.ENTER};
        KeyCombination[] combinations = {};
        UiUtils.setupSaveKeyActions(action, mainButton, keyCodes, combinations, searchTerms);

        //Row 1
        gridPane.add(searchTerms, 0, 0);
        gridPane.add(mainButton, 1, 0);
        gridPane.add(searchAll, 2, 0);

        webView = new WebView();
        setupButtons(interactor, gridPane);

        list.setPrefWidth(150);
        list.setPrefHeight(70);
        gridPane.add(list, 0, 1);
        list.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        webView.getEngine().loadContent(UiUtils.getSaveInfoDisplayFormat(newValue));
                        this.lastEntry = newValue;
                    }
                });
        gridPane.add(webView, 1, 1, 2, 1);
        setupSearchKeyboardShortcuts(scene);
    }

    void showFindDialog() {
        searchTerms.clear();
        searchTerms.requestFocus();
        webView.getEngine().load("");
        list.getSelectionModel().clearSelection();
        lastEntry = null;
        searchStage.show();
    }

    private void setupButtons(Interactor interactor, GridPane gridPane) {
        Button backButton =  new Button("Back to Entry");
        backButton.setOnAction(
                e -> webView.getEngine().loadContent(UiUtils.getSaveInfoDisplayFormat(lastEntry)));
        Button deleteButton =  new Button("Delete Entry");
        deleteButton.setOnAction(event -> {
            if (lastEntry == null) {
                return;
            }
            interactor.deleteEntry(lastEntry);
            displaySearchResults(interactor.searchData(
                    searchCategory, searchTerms.getText(), searchAll.isSelected()));
            webView.getEngine().loadContent("");
            lastEntry = null;
        });
        gridPane.add(backButton, 1, 2);
        gridPane.add(deleteButton, 2, 2);
    }

    private void displaySearchResults(Collection<GeneralRecord> generalRecords) {
        list.setItems(FXCollections.observableArrayList(generalRecords));
    }

    private void setupSearchKeyboardShortcuts(Scene scene) {
        KeyComboActionPair commandA = new KeyComboActionPair(
                new KeyCodeCombination(KeyCode.A, KeyCombination.META_DOWN),
                () -> searchAll.setSelected(!searchAll.isSelected()));
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
        UiUtils.setupKeyboardShortcuts(scene, commandR, commandP, commandT, commandW, commandA);
    }
}
