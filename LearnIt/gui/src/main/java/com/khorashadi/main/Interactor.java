package com.khorashadi.main;

import com.khorashadi.models.BaseRecord;
import com.khorashadi.models.GeneralRecord;
import com.khorashadi.store.MoshiWriterFactory;
import com.khorashadi.store.Serializer;
import com.khorashadi.ui.Memorize;
import com.khorashadi.ui.Search;
import com.khorashadi.ui.UiUtils;
import com.khorashadi.validation.ObjectValidatorRaveImpl;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

import javafx.application.Application;

public class Interactor {
    private final Organizer organizer;
    private final Search search;
    private final Memorize memorize;
    private final Serializer<Collection<GeneralRecord>> generalNoteSerializer;

    public Interactor(Application.Parameters parameters, Memorize memorize) {
        this(new Organizer(new ObjectValidatorRaveImpl()),
                memorize,
                new Search(),
                MoshiWriterFactory.getFileWriter(GeneralRecord.class, parameters.getRaw().get(0)));
    }

    Interactor(Organizer organizer,
               Memorize memorize,
               Search search,
               Serializer<Collection<GeneralRecord>> generalNoteSerializer) {
        if (generalNoteSerializer.fileExists()) {
            organizer.setGeneralNotes(generalNoteSerializer.noExceptionRead());
        }
        this.memorize = memorize;
        this.organizer = organizer;
        this.generalNoteSerializer = generalNoteSerializer;
        this.search = search;
        search.setupInteraction(this);
    }

    public void createGeneralRecord(String tags, String mainInfo) {
        GeneralRecord note = new GeneralRecord(tags, mainInfo);
        organizer.addGeneralNote(note);
        writeGeneralNotesBack();
    }

    public Collection<GeneralRecord> searchRecords(
            SearchCategory searchCategory,
            String terms,
            boolean searchAll) {
        String[] termSplit = UiUtils.processTerms(terms);
        switch (searchCategory) {
            case GENERAL:
                return organizer.searchGeneralNotes(termSplit, searchAll);
        }
        return Collections.emptyList();
    }

    public void deleteEntry(BaseRecord lastEntry) {
        organizer.deleteEntry(lastEntry);
        writeGeneralNotesBack();
    }

    public void showFindDialog() {
        search.showFindDialog();
    }

    public void updateRecord(BaseRecord baseRecord, String tags, String mainInfo) {
        organizer.deleteEntry(baseRecord);
        organizer.addGeneralNote(GeneralRecord.createGeneralRecord(baseRecord, tags, mainInfo));
        writeGeneralNotesBack();
    }

    public void editRecord(BaseRecord baseRecord) {
        memorize.editRecord(baseRecord);
        memorize.setFocus();
    }

    private void writeGeneralNotesBack() {
        Collection<GeneralRecord> generalRecords = organizer.getGeneralNoteCollection();
        try {
            generalNoteSerializer.writeBytes(generalRecords);
        } catch (IOException e) {
            //TODO: surface this to the users somehow.
            e.printStackTrace();
        }
    }

    public enum SearchCategory {
        PEOPLE,
        GENERAL,
        TASKS;
    }
}
