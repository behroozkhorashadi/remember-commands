package com.khorashadi.ui;

import com.khorashadi.main.Interactor;
import com.khorashadi.models.BaseRecord;
import com.khorashadi.models.GeneralRecord;

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


public class SearchUI {
    private final TextField searchTerms;
    private final WebView webView;
    private final Button mainButton;
    private final GridPane gridPane;
    private BaseRecord lastEntry = null;
    private Interactor.SearchCategory searchCategory = GENERAL;
    private ListView<BaseRecord> list = new ListView<>();
    private Stage searchStage = new Stage();
    private CheckBox searchAll = new CheckBox("SearchUI All");

    public SearchUI() {
        searchStage.setTitle("SearchUI");
        StackPane root = new StackPane();
        Scene scene = new Scene(root);
        searchStage.setScene(scene);
        gridPane = new GridPane();
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.setVgap(5);
        gridPane.setHgap(5);

        root.getChildren().add(gridPane);
        gridPane.getChildren().clear();

        // maybe add a label.
        searchTerms = new TextField();
        searchTerms.setPromptText("SearchUI");
        mainButton = new Button("Start SearchUI");

        //Row 1
        gridPane.add(searchTerms, 0, 0);
        gridPane.add(mainButton, 1, 0);
        gridPane.add(searchAll, 2, 0);

        webView = new WebView();

        list.setPrefWidth(150);
        list.setPrefHeight(70);
        gridPane.add(list, 0, 1);
        list.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        webView.getEngine().loadContent(
                                UiUtils.getSaveInfoDisplayFormat(newValue, searchTerms.getText()));
                        this.lastEntry = newValue;
                    }
                });
        gridPane.add(webView, 1, 1, 3, 1);
        setupSearchKeyboardShortcuts(scene);
    }

    public void setupInteraction(final Interactor interactor) {
        // setup main interaction buttons.
        final Runnable action = () -> {
            displaySearchResults(interactor.searchRecords(
                    searchTerms.getText(), searchAll.isSelected()));
            list.requestFocus();
        };
        KeyCode[] keyCodes = {KeyCode.ENTER};
        KeyCombination[] combinations = {};
        UiUtils.setupSaveKeyActions(action, mainButton, keyCodes, combinations, searchTerms);
        setupButtons(interactor, gridPane);
    }

    public void showFindDialog() {
        clearContent();
        searchStage.show();
        searchStage.requestFocus();
    }

    private void clearContent() {
        searchTerms.clear();
        webView.getEngine().loadContent("");
        list.setItems(FXCollections.emptyObservableList());
        list.getSelectionModel().clearSelection();
        lastEntry = null;
    }

    private void setupButtons(Interactor interactor, GridPane gridPane) {
        Button backButton =  new Button("Back to Entry");
        backButton.setOnAction(
                e -> webView.getEngine().loadContent(
                        UiUtils.getSaveInfoDisplayFormat(lastEntry, searchTerms.getText())));
        Button deleteButton =  new Button("Delete Entry");
        deleteButton.setOnAction(event -> {
            if (lastEntry == null) {
                return;
            }
            interactor.deleteEntry(lastEntry);
            displaySearchResults(interactor.searchRecords(
                    searchTerms.getText(), searchAll.isSelected()));
            webView.getEngine().loadContent("");
            lastEntry = null;
        });
        Button editButton = new Button("Edit Entry");
        editButton.setOnAction(event -> {
            if (lastEntry != null) {
                interactor.editRecord(lastEntry);
                clearContent();
            }
        });

        gridPane.add(backButton, 1, 2);
        gridPane.add(deleteButton, 2, 2);
        gridPane.add(editButton, 3, 2);
    }

    private void displaySearchResults(Collection<GeneralRecord> generalRecords) {
        list.setItems(FXCollections.observableArrayList(generalRecords));
    }

    private void setupSearchKeyboardShortcuts(Scene scene) {
        KeyComboActionPair commandF = new KeyComboActionPair(
                new KeyCodeCombination(KeyCode.F, KeyCombination.META_DOWN),
                () -> searchAll.setSelected(!searchAll.isSelected()));
        KeyComboActionPair commandW = new KeyComboActionPair(
                new KeyCodeCombination(KeyCode.W, KeyCombination.META_DOWN),
                () -> searchStage.hide());
        KeyComboActionPair commandS = new KeyComboActionPair(
                new KeyCodeCombination(KeyCode.S, KeyCombination.META_DOWN),
                searchTerms::requestFocus);
        UiUtils.setupKeyboardShortcuts(scene, commandW, commandF, commandS);
    }
}