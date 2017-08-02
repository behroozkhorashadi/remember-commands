package com.khorashadi.ui;

import javafx.scene.input.KeyCombination;

class KeyComboActionPair {
    private final KeyCombination keyCombination;
    private final Runnable runnable;

    KeyComboActionPair(final KeyCombination keyCombination, Runnable runnable) {
        this.keyCombination = keyCombination;
        this.runnable = runnable;
    }

    KeyCombination getKeyCombination() {
        return keyCombination;
    }

    Runnable getRunnable() {
        return runnable;
    }
}
