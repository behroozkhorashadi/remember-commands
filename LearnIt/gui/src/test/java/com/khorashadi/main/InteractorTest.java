package com.khorashadi.main;

import com.khorashadi.models.GeneralRecord;
import com.khorashadi.store.Serializer;
import com.khorashadi.ui.Memorize;
import com.khorashadi.ui.SearchUI;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.AdditionalMatchers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;

import static com.google.common.truth.Truth.assertThat;
import static com.khorashadi.main.Interactor.SearchCategory.GENERAL;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class InteractorTest {
    @Mock
    private Organizer organizer;
    @Mock
    private Memorize memorize;
    @Mock
    private Serializer<Collection<GeneralRecord>> generalRecordSerializer;
    @Mock
    private SearchUI searchUI;
    private Interactor interactor;
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private GeneralRecord generalRecord;
    @Before
    public void setUp() throws Exception {
        interactor = new Interactor(organizer, memorize, searchUI, generalRecordSerializer);
        generalRecord = new GeneralRecord("Sample", "Sample main");
    }

    @Test
    public void interactorConstructor_whenFileNotThere_shouldntSetGeneralNotes() {
        when(generalRecordSerializer.fileExists()).thenReturn(false);
        verify(organizer, never()).setGeneralNotes(any());
    }

    @Test
    public void interactorConstructor_whenFileThere_shouldSetGeneralNotes() {
        when(generalRecordSerializer.fileExists()).thenReturn(true);
        new Interactor(organizer, memorize, searchUI, generalRecordSerializer);
        verify(organizer).setGeneralNotes(any());
        verify(generalRecordSerializer).noExceptionRead();
    }

    @Test
    public void createGeneralNote_whenAddingGeneralRecord_shouldAddToOrganizerAndWriteData()
            throws IOException {
        String tags = "SampleNote tags";
        String mainInfo = "Sample Main Info";
        interactor.createGeneralRecord(tags, mainInfo);
        ArgumentCaptor<GeneralRecord> captor = ArgumentCaptor.forClass(GeneralRecord.class);
        verify(organizer).addGeneralNote(captor.capture());
        GeneralRecord generalRecord = captor.getValue();
        assertThat(generalRecord.getUserTagsRaw()).isEqualTo(tags);
        assertThat(generalRecord.getMainInfo()).isEqualTo(mainInfo);
        // check for write
        verify(generalRecordSerializer).writeBytes(any());
    }

    @Test
    public void searchRecrods_whenSearching_shouldCallOrganizerSearch() {
        String tags = "SampleNote tags";
        Collection<GeneralRecord> expectedResult = new LinkedList<>();
        expectedResult.add(new GeneralRecord("", ""));
        when(organizer.searchGeneralRecords(any(String[].class), anyBoolean()))
                .thenReturn(expectedResult);
        Collection<GeneralRecord> result = interactor.searchRecords(GENERAL, tags, false);
        verify(organizer).searchGeneralRecords(
                AdditionalMatchers.aryEq(new String[] {"SampleNote", "tags"}), eq(false));
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    public void deleteEntry_whenDeletingEntry_shouldCallThroughToOrganizerAndWriteBackResult()
            throws IOException {
        interactor.deleteEntry(generalRecord);
        verify(organizer).deleteEntry(generalRecord);
        verify(generalRecordSerializer).writeBytes(any());
    }

    @Test
    public void updateRecord_whenUpdatingEntry_shouldCallThroughToOrganizerAndWriteBackResult()
            throws IOException {
        interactor.updateRecord(generalRecord, "New Tags", "New Main");
        verify(organizer).deleteEntry(generalRecord);
        verify(organizer).addGeneralNote(any());
        verify(generalRecordSerializer).writeBytes(any());
    }

    @Test
    public void editRecord_whenEditingEntry_shouldSendToMainEditUi()
            throws IOException {
        interactor.editRecord(generalRecord);
        verify(memorize).editRecord(generalRecord);
        verify(memorize).setFocus();
    }


}
