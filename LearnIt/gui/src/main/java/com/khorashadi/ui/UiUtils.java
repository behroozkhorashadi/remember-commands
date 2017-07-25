package com.khorashadi.ui;

import com.khorashadi.models.BaseRecord;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.rxjavafx.observables.JavaFxObservable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;

import static javafx.scene.input.KeyEvent.KEY_RELEASED;


public final class UiUtils {
    public static final KeyCombination SHIFT_ENTER
            = new KeyCodeCombination(KeyCode.ENTER, KeyCombination.SHIFT_DOWN);
    public static final KeyCombination COMMAND_S
            = new KeyCodeCombination(KeyCode.S, KeyCombination.META_DOWN);

    private UiUtils() { }

    static CompositeDisposable setupSaveKeyActions(
            final Runnable action,
            final Button button,
            final KeyCode[] keyCodes,
            final KeyCombination[] keyCombinations,
            final TextInputControl... textInputControls) {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(
                JavaFxObservable.actionEventsOf(button).subscribe(actionEvent -> action.run()));
        for (TextInputControl textInputControl : textInputControls) {
            compositeDisposable.add(JavaFxObservable.eventsOf(textInputControl, KEY_RELEASED)
                    .subscribe(keyEvent -> {
                        for (KeyCombination keyCombination : keyCombinations) {
                            if (keyCombination.match(keyEvent)) {
                                action.run();
                                return;
                            }
                        }
                        for (KeyCode keyCode : keyCodes) {
                            if (keyCode.equals(keyEvent.getCode())) {
                                action.run();
                                return;
                            }
                        }
                    }));
        }
        return compositeDisposable;
    }

    static String getSaveInfoDisplayFormat(BaseRecord baseRecord) {
        if (baseRecord == null) {
            return "";
        }
        return findReplaceUrl(baseRecord.getMainInfo());
    }

    static Disposable setupKeyboardShortcuts(
            Scene scene,
            KeyComboActionPair... keyComboActionPairs) {
        return JavaFxObservable.eventsOf(scene, KEY_RELEASED).subscribe(event -> {
            for (KeyComboActionPair pair : keyComboActionPairs) {
                if (pair.getKeyCombination().match(event)) {
                    pair.getRunnable().run();
                    return;
                }
            }
        });
    }

    static String findReplaceUrl(String input) {
        String[] split = input.split("\\s+");
        for (String s : split) {
            if (s.startsWith("http") || s.startsWith("www")) {
                try {
                    new URL(s);
                } catch (MalformedURLException e) {
                    continue;
                }
                input = input.replace(s, createHref(s));
            }
        }
        return input;
    }

    private static String createHref(String url) {
        StringBuilder builder = new StringBuilder();
        builder.append("<a href='");
        builder.append(url);
        builder.append("'>");
        builder.append(url);
        builder.append("</a>");
        return builder.toString();
    }
}
