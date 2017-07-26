package com.khorashadi.main;

import com.khorashadi.models.BaseRecord;
import com.khorashadi.models.GeneralRecord;
import com.khorashadi.store.MoshiFileWriter;
import com.khorashadi.store.Serializer;
import com.khorashadi.ui.Memorize;
import com.khorashadi.ui.Search;
import com.khorashadi.validation.ObjectValidatorRaveImpl;
import com.squareup.moshi.Types;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

import javafx.application.Application;

public class Interactor {
    private final Organizer organizer;
    private final Serializer<Collection<GeneralRecord>> generalNoteSerializer;
    private final Search search;
    private final Memorize memorize;

    public Interactor(Application.Parameters parameters, Memorize memorize) {
        this(new MoshiFileWriter<>(
                Types.newParameterizedType(Collection.class, GeneralRecord.class),
                parameters.getRaw().get(0), "generalNotes.json"),
                new Organizer(new ObjectValidatorRaveImpl()), memorize);
    }

    Interactor(Serializer<Collection<GeneralRecord>> serializer, Organizer organizer,
               Memorize memorize) {
        if (serializer.fileExists()) {
            organizer.setGeneralNotes(serializer.noExceptionRead());
        }
        this.memorize = memorize;
        this.organizer = organizer;
        generalNoteSerializer = serializer;
        search = new Search(this);
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

    public Collection<GeneralRecord> searchData(
            SearchCategory searchCategory,
            String terms,
            boolean searchAll) {
        String[] termSplit = processTerms(terms);
        switch (searchCategory) {
            case GENERAL:
                return organizer.searchGeneralNotes(termSplit, searchAll);
        }
        return Collections.emptyList();
    }

    private String[] processTerms(String terms) {
        return terms.split(" ");
    }

    public void deleteEntry(BaseRecord lastEntry) {
        organizer.deleteEntry(lastEntry);
        writeGeneralNotesBack();
    }

    public void showFindDialog() {
        search.showFindDialog();
    }

    public void editRecord(BaseRecord baseRecord) {
        memorize.editRecord(baseRecord);
        memorize.setFocus();
    }

    public void updateRecord(BaseRecord baseRecord, String tags, String mainInfo) {
        organizer.deleteEntry(baseRecord);
        organizer.addGeneralNote(GeneralRecord.updateGeneralRecord(baseRecord, tags, mainInfo));
        writeGeneralNotesBack();
    }

    public enum SearchCategory {
        PEOPLE,
        GENERAL,
        TASKS
    }
}
