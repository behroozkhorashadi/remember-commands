package com.khorashadi.ui;

import com.khorashadi.main.Interactor;
import com.khorashadi.models.GeneralNote;
import com.khorashadi.models.SaveInfo;

import java.util.Collection;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import static javafx.scene.input.KeyEvent.KEY_RELEASED;


public class Search {
    private final Interactor interactor;
    private Interactor.SearchCategory searchCategory = GENERAL;
    private ListView<SaveInfo> list;

    Search(Interactor interactor) {
        this.interactor = interactor;
    }

    void showFindDialog() {
        Stage searchStage = new Stage();
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
        final TextField keyWords = new TextField();
        keyWords.setPromptText("Search");
        final Runnable action = new Runnable() {
            @Override
            public void run() {
                displaySearch(interactor.searchData(searchCategory, keyWords.getText()));
            }
        };
        final Button mainButton = new Button();
        mainButton.setText("Start Search");
        UiUtils.setupKeyActions(action, mainButton, keyWords);
        gridPane.add(keyWords, 0, 0);
        gridPane.add(mainButton, 1, 0);

        final TextArea textArea = new TextArea();

        list = new ListView<SaveInfo>();
        list.setPrefWidth(150);
        list.setPrefHeight(70);
        gridPane.add(list, 0, 1);
        list.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<SaveInfo>() {
            @Override
            public void changed(ObservableValue<? extends SaveInfo> observable, SaveInfo oldValue,
                                SaveInfo newValue) {
                textArea.setText(newValue.getFullDisplayFormat());
            }
        });

        textArea.setEditable(false);
        gridPane.add(textArea, 1, 1);
        setupSearchKeyboardShortcuts(scene, gridPane);
        searchStage.show();
    }

    private void displaySearch(Collection<GeneralNote> generalNotes) {
        list.setItems(FXCollections.observableArrayList(generalNotes));
    }

    private void setupSearchKeyboardShortcuts(Scene scene, GridPane gridPane) {
        final KeyCombination commandR = new KeyCodeCombination(KeyCode.R, KeyCombination.META_DOWN);
        scene.addEventHandler(KEY_RELEASED, event -> {
            if (commandR.match(event)) {
                System.out.println("Search Memory");
                searchCategory = GENERAL;
            }
        });
        final KeyCombination commandP = new KeyCodeCombination(KeyCode.P, KeyCombination.META_DOWN);
        scene.addEventHandler(KEY_RELEASED, event -> {
            if (commandP.match(event)) {
                System.out.println("Find Person");
                searchCategory = PEOPLE;
            }
        });
        final KeyCombination commandT = new KeyCodeCombination(KeyCode.T, KeyCombination.META_DOWN);
        scene.addEventHandler(KEY_RELEASED, event -> {
            if (commandT.match(event)) {
                System.out.println("Find Task");
                searchCategory = TASKS;
            }
        });
    }
}
