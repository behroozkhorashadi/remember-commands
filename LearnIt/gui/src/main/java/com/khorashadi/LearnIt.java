package com.khorashadi;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class LearnIt extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Learn It");
        StackPane root = new StackPane();
        setupComboBox(root);
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void setupComboBox(StackPane root) {
        final GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.setVgap(5);
        gridPane.setHgap(5);

        ObservableList<String> options =
                FXCollections.observableArrayList(
                        "Remember",
                        "Name"
                );
        final ComboBox comboBox = new ComboBox(options);
        comboBox.getSelectionModel().selectFirst();
        comboBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (comboBox.getValue() != null &&
                        !comboBox.getValue().toString().isEmpty()){
                    switch (comboBox.getValue().toString()) {
                        case "Remember":
                            System.out.println("Remember");
                            setupRemember(gridPane);
                            //remove any name stuff.
                            break;
                        case "Name":
                            System.out.println("Name");
                            //remove any remember stuff.
                            break;
                    }
                }
            }
        });
        gridPane.add(comboBox, 0, 0);
        setupRemember(gridPane);
        Button saveButton = new Button();
        saveButton.setText("Save Info");
        saveButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                System.out.println("Saved");
            }
        });
        gridPane.add(saveButton, 0, 3);

        root.getChildren().add(gridPane);

    }

    private void setupNameRemember(StackPane root) {

    }

    private void setupRemember(GridPane gridPane) {
        final TextField keyWords = new TextField();
        keyWords.setPromptText("Key words for tagged for search");
        gridPane.add(keyWords, 0, 1);

        final TextArea remember = new TextArea();
        remember.setPromptText("The stuff you want to remember.");
        gridPane.add(remember, 0, 2);
    }
}
