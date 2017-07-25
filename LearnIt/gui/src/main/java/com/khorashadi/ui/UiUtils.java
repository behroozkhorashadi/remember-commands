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
import javafx.scene.control.Control;
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
            final Control... textInputControls) {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(
                JavaFxObservable.actionEventsOf(button).subscribe(actionEvent -> action.run()));
        for (Control textInputControl : textInputControls) {
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
            if (s.startsWith("http")) {
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

    static String findReplaceRegexUrl(String input) {
        Pattern pattern = Pattern.compile(
                "\\b(((ht|f)tp(s?)\\:\\/\\/|~\\/|\\/)|www.)" +
                        "(\\w+:\\w+@)?(([-\\w]+\\.)+(com|org|net|gov" +
                        "|mil|biz|info|mobi|name|aero|jobs|museum" +
                        "|travel|[a-z]{2}))(:[\\d]{1,5})?" +
                        "(((\\/([-\\w~!$+|.,=]|%[a-f\\d]{2})+)+|\\/)+|\\?|#)?" +
                        "((\\?([-\\w~!$+|.,*:]|%[a-f\\d{2}])+=?" +
                        "([-\\w~!$+|.,*:=]|%[a-f\\d]{2})*)" +
                        "(&(?:[-\\w~!$+|.,*:]|%[a-f\\d{2}])+=?" +
                        "([-\\w~!$+|.,*:=]|%[a-f\\d]{2})*)*)*" +
                        "(#([-\\w~!$+|.,*:=]|%[a-f\\d]{2})*)?\\b");

        StringBuffer sb = new StringBuffer();
        Matcher m = pattern.matcher(input);

        while (m.find()) {
            // Avoids throwing a NullPointerException in the case that you
            // Don't have a replacement defined in the map for the match
            String repString =  createHref(m.group());
            m.appendReplacement(sb, repString);
        }
        m.appendTail(sb);
        return sb.toString();
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
