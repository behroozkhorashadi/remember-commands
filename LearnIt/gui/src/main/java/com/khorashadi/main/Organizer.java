package com.khorashadi.main;

import com.khorashadi.models.GeneralNote;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Organizer {

    Collection<GeneralNoteWrapper> generalNotes = new LinkedList<>();

    public void addGeneralNote(GeneralNote generalNote) {
        generalNotes.add(new GeneralNoteWrapper(generalNote));
    }

    public Collection<GeneralNote> getGeneralNoteCollection() {
        return generalNotes.stream()
                .map(new Function<GeneralNoteWrapper, GeneralNote>() {
                    @Override
                    public GeneralNote apply(GeneralNoteWrapper generalNoteWrapper) {
                        return generalNoteWrapper.generalNote;
                    }
                })
                .collect(Collectors.toList());
    }

    public void setGeneralNotes(Collection<GeneralNote> generalNotes) {
        if (generalNotes == null) {
            return;
        }
        this.generalNotes = generalNotes.stream()
                .map(new Function<GeneralNote, GeneralNoteWrapper>() {
                    @Override
                    public GeneralNoteWrapper apply(GeneralNote generalNote) {
                        return new GeneralNoteWrapper(generalNote);
                    }
                })
                .collect(Collectors.toList());
    }

    public Collection<GeneralNote> searchGeneralNotes(String[] terms) {
        return generalNotes.stream().filter(generalNoteWrapper -> {
            for (String s : terms) {
                if (generalNoteWrapper.hasTag(s)) {
                    return true;
                }
            }
            return false;
        }).map(new Function<GeneralNoteWrapper, GeneralNote>() {
            @Override
            public GeneralNote apply(GeneralNoteWrapper generalNoteWrapper) {
                return generalNoteWrapper.generalNote;
            }
        }).collect(Collectors.toList());
    }

    private static class GeneralNoteWrapper {
        private final Set<String> tagSet = new HashSet<>();
        private final GeneralNote generalNote;

        private GeneralNoteWrapper(GeneralNote note) {
            generalNote = note;
            for (String tag : note.getUserTags()) {
                tagSet.add(tag);
            }
        }

        private boolean hasTag(String s) {
            return tagSet.contains(s);
        }
    }
}
