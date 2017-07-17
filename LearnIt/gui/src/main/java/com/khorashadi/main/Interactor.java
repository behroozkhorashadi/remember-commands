package com.khorashadi.main;

import com.khorashadi.models.GeneralNote;
import com.khorashadi.store.MoshiFileWriter;
import com.squareup.moshi.Types;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import javafx.application.Application;

public class Interactor {
    private final Organizer organizer = new Organizer();
    private final MoshiFileWriter<Collection<GeneralNote>> gNoteWriter;

    public Interactor(Application.Parameters parameters) {
        gNoteWriter = new MoshiFileWriter<>(
                Types.newParameterizedType(Collection.class, GeneralNote.class),
                parameters.getRaw().get(0), "generalNotes.json");
        if (gNoteWriter.fileExists()) {
            organizer.setGeneralNotes(gNoteWriter.noExceptionRead());
        }
        System.out.println(String.join(", ", parameters.getRaw()));
    }

    public void createGeneralNote(String tags, String mainInfo) {
        GeneralNote note = new GeneralNote(tags, mainInfo);
        organizer.addGeneralNote(note);
        Collection<GeneralNote> generalNotes = organizer.getGeneralNoteCollection();
        try {
            gNoteWriter.writeBytes(generalNotes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Collection<GeneralNote> searchData(SearchCategory searchCategory, String terms) {
        String[] termSplit = terms.split(" ");
        switch (searchCategory) {
            case GENERAL:
                return organizer.searchGeneralNotes(termSplit);
        }
        return null;
    }

    private String[] processTags(String tags) {
        return tags.split(" ");
    }

    public enum SearchCategory {
        PEOPLE,
        GENERAL,
        TASKS
    }
}
