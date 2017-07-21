package com.khorashadi.main;

import com.khorashadi.models.GeneralRecord;
import com.khorashadi.validation.ObjectValidator;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Organizer {


    private final ObjectValidator objectValidator;

    public Organizer(ObjectValidator objectValidator) {
        this.objectValidator = objectValidator;
    }

    Collection<GeneralNoteWrapper> generalNotes = new LinkedList<>();

    public void addGeneralNote(GeneralRecord generalRecord) {
        generalNotes.add(new GeneralNoteWrapper(generalRecord));
    }

    public Collection<GeneralRecord> getGeneralNoteCollection() {
        return generalNotes.stream()
                .map(new Function<GeneralNoteWrapper, GeneralRecord>() {
                    @Override
                    public GeneralRecord apply(GeneralNoteWrapper generalNoteWrapper) {
                        return generalNoteWrapper.generalRecord;
                    }
                })
                .collect(Collectors.toList());
    }

    public void setGeneralNotes(Collection<GeneralRecord> generalRecords) {
        if (generalRecords == null) {
            return;
        }
        this.generalNotes = generalRecords.stream()
                .filter(objectValidator::isValidObject) // drop any invalid items
                .map(GeneralNoteWrapper::new)
                .collect(Collectors.toList());
    }

    public Collection<GeneralRecord> searchGeneralNotes(String[] terms) {
        return generalNotes.stream().filter(generalNoteWrapper -> {
            for (String s : terms) {
                if (generalNoteWrapper.hasTag(s)) {
                    return true;
                }
            }
            return false;
        }).map(new Function<GeneralNoteWrapper, GeneralRecord>() {
            @Override
            public GeneralRecord apply(GeneralNoteWrapper generalNoteWrapper) {
                return generalNoteWrapper.generalRecord;
            }
        }).sorted(new Comparator<GeneralRecord>() {
            @Override
            public int compare(GeneralRecord g1, GeneralRecord g2) {
                return g1.getTimePoint().compareTo(g2.getTimePoint());
            }
        }).collect(Collectors.toList());
    }

    private static class GeneralNoteWrapper {
        private final Set<String> tagSet = new HashSet<>();
        private final GeneralRecord generalRecord;

        private GeneralNoteWrapper(GeneralRecord note) {
            generalRecord = note;
            for (String tag : note.getUserTags()) {
                tagSet.add(tag);
            }
        }

        private boolean hasTag(String s) {
            return tagSet.contains(s);
        }
    }
}
