package com.khorashadi.main;

import com.khorashadi.models.BaseRecord;
import com.khorashadi.models.GeneralRecord;
import com.khorashadi.store.MoshiFileWriter;
import com.khorashadi.store.Serializer;
import com.khorashadi.validation.ObjectValidatorRaveImpl;
import com.squareup.moshi.Types;

import java.io.IOException;
import java.util.Collection;

import javafx.application.Application;

public class Interactor {
    private final Organizer organizer;
    private final Serializer<Collection<GeneralRecord>> generalNoteSerializer;

    public Interactor(Application.Parameters parameters) {
        this(new MoshiFileWriter<>(Types.newParameterizedType(Collection.class, GeneralRecord.class),
                parameters.getRaw().get(0), "generalNotes.json"),
                new Organizer(new ObjectValidatorRaveImpl()));
    }

    Interactor(Serializer<Collection<GeneralRecord>> serializer, Organizer organizer) {
        if (serializer.fileExists()) {
            organizer.setGeneralNotes(serializer.noExceptionRead());
        }
        this.organizer = organizer;
        generalNoteSerializer = serializer;
    }

    public void createGeneralNote(String tags, String mainInfo) {
        GeneralRecord note = new GeneralRecord(tags, mainInfo);
        organizer.addGeneralNote(note);
        writeGeneralNotesBack();
    }

    public void writeGeneralNotesBack() {
        Collection<GeneralRecord> generalRecords = organizer.getGeneralNoteCollection();
        try {
            generalNoteSerializer.writeBytes(generalRecords);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Collection<GeneralRecord> searchData(SearchCategory searchCategory, String terms) {
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

    public void deleteEntry(BaseRecord lastEntry) {
        organizer.deleteEntry(lastEntry);
        writeGeneralNotesBack();
    }

    public enum SearchCategory {
        PEOPLE,
        GENERAL,
        TASKS
    }
}
