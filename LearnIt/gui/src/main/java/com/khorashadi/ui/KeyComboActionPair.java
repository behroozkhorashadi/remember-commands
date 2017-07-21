package com.khorashadi.ui;

import javafx.scene.input.KeyCombination;

public class KeyComboActionPair {
    private final KeyCombination keyCombination;
    private final Runnable runnable;

    public KeyComboActionPair(final KeyCombination keyCombination, Runnable runnable) {
        this.keyCombination = keyCombination;
        this.runnable = runnable;
    }
    public KeyCombination getKeyCombination() {
        return keyCombination;
    }

    public Runnable getRunnable() {
        return runnable;
    }
}
