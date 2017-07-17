package com.khorashadi.main;

import com.khorashadi.models.GeneralNote;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class Organizer {

    Collection<GeneralNote> generalNotes = new LinkedList<>();

    public void addGeneralNote(GeneralNote generalNote) {
        generalNotes.add(generalNote);
    }

    public Collection<GeneralNote> getGeneralNoteCollection() {
        return generalNotes;
    }

    public void setGeneralNotes(Collection<GeneralNote> generalNotes) {
        if (generalNotes == null) {
            return;
        }
        this.generalNotes = generalNotes;
    }

    public Collection<GeneralNote> searchGeneralNotes(String[] terms) {
        return generalNotes;
    }
}
