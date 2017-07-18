package com.khorashadi.ui;

import com.khorashadi.models.BaseRecord;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

import static javafx.scene.input.KeyEvent.KEY_RELEASED;


public class UiUtils {
    private static final KeyCombination SHIFT_ENTER
            = new KeyCodeCombination(KeyCode.ENTER, KeyCombination.SHIFT_DOWN);
    private static final KeyCombination COMMAND_S
            = new KeyCodeCombination(KeyCode.S, KeyCombination.META_DOWN);

    static void setupKeyActions(
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

    static String getSaveInfoDisplayFormat(BaseRecord baseRecord) {
        return baseRecord.getMainInfo();
    }
}
