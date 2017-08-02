package com.khorashadi.main;

import com.khorashadi.index.IndexUtils;
import com.khorashadi.index.Indexer;
import com.khorashadi.index.IndexConstants;
import com.khorashadi.index.RecordSearcher;
import com.khorashadi.models.BaseRecord;
import com.khorashadi.models.GeneralRecord;
import com.khorashadi.validation.ObjectValidator;

import org.apache.lucene.queryparser.classic.ParseException;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Organizer {


    private final ObjectValidator objectValidator;
    private final String baseDirectory;

    private RecordSearcher recordSearcher;

    private Collection<GeneralRecordWrapper> generalNotes = new LinkedList<>();

    public Organizer(ObjectValidator objectValidator, String baseDirectory) {
        this.objectValidator = objectValidator;
        this.baseDirectory = baseDirectory;
    }

    public void setRecordSearcher(RecordSearcher recordSearcher) {
        this.recordSearcher = recordSearcher;
    }

    public void addGeneralNote(GeneralRecord generalRecord) {
        generalNotes.add(new GeneralRecordWrapper(generalRecord));
    }

    public List<GeneralRecord> searchGeneralRecords(final String query, boolean searchAll)
            throws IOException, ParseException {
        if (query.equals("*")) {
            return generalNotes.stream().map(recordWrapper -> recordWrapper.generalRecord).collect(Collectors.toList());
        }
        if (recordSearcher == null) {
            return generalNotes
                .stream()
                .filter(generalRecordWrapper ->
                        filterGeneralRecords(query, generalRecordWrapper, searchAll))
                .map(generalRecordWrapper -> new RankPair(generalRecordWrapper, new RankInfo()))
                .sorted((pair1, pair2) -> pair1.rankInfo.rankValue - pair2.rankInfo.rankValue)
                .map(pair -> pair.recordWrapper.generalRecord)
                .collect(Collectors.toList());
        }
        final String newQuery = reshapeQuery(query, searchAll);
        final Map<String, Float> resultMap = recordSearcher.runQuery(newQuery, 20);
        return generalNotes
                .stream()
                .filter(recordWrapper ->
                        resultMap.containsKey(recordWrapper.generalRecord.getUuid()))
                .sorted((o1, o2) -> {
                    float score1 = resultMap.get(o1.generalRecord.getUuid());
                    float score2 = resultMap.get(o2.generalRecord.getUuid());
                    return (int) (score1 - score2);
                })
                .map(recordWrapper -> recordWrapper.generalRecord)
                .collect(Collectors.toList());
    }

    private String reshapeQuery(String query, boolean searchAll) {
        if (searchAll) {
            StringBuilder builder = new StringBuilder();
            builder.append(IndexConstants.CONTENTS);
            builder.append(": ");
            builder.append(query);
            builder.append(" OR ");
            builder.append(IndexConstants.TAGS);
            builder.append(": ");
            builder.append(query);
            query = builder.toString();
            System.out.println(query);
        }
        return query;
    }

    public void deleteEntry(BaseRecord record) {
        Iterator<GeneralRecordWrapper> iter = generalNotes.iterator();
        while (iter.hasNext()) {
            BaseRecord baseRecord = iter.next().generalRecord;
            if (baseRecord.getUuid().equals(record.getUuid())) {
                iter.remove();
                return;
            }
        }
    }

    void setGeneralNotes(Collection<GeneralRecord> generalRecords, String baseDir)
            throws IOException {
        Indexer indexer = new Indexer(IndexUtils.getIndexDirectoryFromBase(baseDir));
        if (generalRecords == null) {
            return;
        }
        this.generalNotes = generalRecords.stream()
                .filter(objectValidator::isValidObject) // drop any invalid items
                .map(GeneralRecordWrapper::new)
                .collect(Collectors.toList());
        for (GeneralRecordWrapper generalRecordWrapper : generalNotes) {
            indexer.indexDoc(generalRecordWrapper.generalRecord);
        }
        indexer.close();
    }

    Collection<GeneralRecord> getGeneralNoteCollection() {
        return generalNotes.stream()
                .map(generalRecordWrapper -> generalRecordWrapper.generalRecord)
                .collect(Collectors.toList());
    }

    private static boolean filterGeneralRecords(
            String terms,
            GeneralRecordWrapper generalRecordWrapper,
            boolean searchAll) {
        String[] split = terms.split(" ");
        boolean getAll = (split.length == 1 && split[0].equals("*"));
        for (String s : split) {
            if (getAll || generalRecordWrapper.hasTag(s)) {
                return true;
            } else if (searchAll && generalRecordWrapper.checkAll(s)) {
                return true;
            }
        }
        return false;
    }

    static RankPair rankRecord(String[] terms, GeneralRecordWrapper recordWrapper) {
        RankInfo rankInfo = new RankInfo();
        return new RankPair(recordWrapper, rankInfo);
    }

    private static class GeneralRecordWrapper {
        private final Set<String> tagSet = new HashSet<>();
        private final GeneralRecord generalRecord;

        private GeneralRecordWrapper(GeneralRecord note) {
            generalRecord = note;
            for (String tag : note.getUserTags()) {
                tagSet.add(tag);
            }
        }

        private boolean hasTag(String s) {
            return tagSet.contains(s);
        }

        public boolean checkAll(String s) {
            return generalRecord.getMainInfo().contains(s);
        }
    }

    private static class RankInfo {
        private int rankValue = 0;
    }

    private static class RankPair {
        private final GeneralRecordWrapper recordWrapper;
        private final RankInfo rankInfo;

        private RankPair(GeneralRecordWrapper recordWrapper, RankInfo rankInfo) {
            this.recordWrapper = recordWrapper;
            this.rankInfo = rankInfo;
        }
    }
}
