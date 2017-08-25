package com.khorashadi.main;

import com.khorashadi.index.IndexUtils;
import com.khorashadi.index.RecordSearcher;
import com.khorashadi.models.BaseRecord;
import com.khorashadi.models.GeneralRecord;
import com.khorashadi.store.MoshiWriterFactory;
import com.khorashadi.store.Serializer;
import com.khorashadi.ui.Memorize;
import com.khorashadi.ui.SearchUI;
import com.khorashadi.validation.ObjectValidatorRaveImpl;

import org.apache.lucene.queryparser.classic.ParseException;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

public class Interactor {
    private final Organizer organizer;
    private final SearchUI searchUI;
    private final Memorize memorize;
    private final Serializer<Collection<GeneralRecord>> generalNoteSerializer;

    public Interactor(String baseDirectory, Memorize memorize) throws IOException {
        this(new Organizer(new ObjectValidatorRaveImpl(), baseDirectory),
                memorize,
                new SearchUI(),
                baseDirectory,
                MoshiWriterFactory.getFileWriter(GeneralRecord.class, baseDirectory));
    }

    Interactor(Organizer organizer,
               Memorize memorize,
               SearchUI searchUI,
               String baseDirectory,
               Serializer<Collection<GeneralRecord>> generalNoteSerializer) throws IOException {
        if (generalNoteSerializer.fileExists()) {
            organizer.setGeneralNotes(generalNoteSerializer.noExceptionRead(), baseDirectory);
        }
        this.memorize = memorize;
        this.organizer = organizer;
        this.generalNoteSerializer = generalNoteSerializer;
        this.searchUI = searchUI;
        searchUI.setupInteraction(this);
        organizer.setRecordSearcher(
                new RecordSearcher(IndexUtils.getIndexDirectoryFromBase(baseDirectory)));
    }

    public void createGeneralRecord(String tags, String mainInfo) {
        GeneralRecord note = new GeneralRecord(tags, mainInfo);
        organizer.addGeneralNote(note);
        writeGeneralNotesBack();
    }

    public Collection<GeneralRecord> searchRecords(String terms, boolean searchAll) {
        try {
            return organizer.searchGeneralRecords(terms, searchAll);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    public void deleteEntry(BaseRecord lastEntry) {
        organizer.deleteEntry(lastEntry);
        writeGeneralNotesBack();
    }

    public void showFindDialog() {
        searchUI.showFindDialog();
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
