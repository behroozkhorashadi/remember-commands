package com.khorashadi.models;

import com.khorashadi.validation.RaveFactory;
import com.uber.rave.annotation.Validated;

import java.time.LocalDateTime;

@Validated(factory = RaveFactory.class)
public final class GeneralRecord extends BaseRecord {

    public GeneralRecord(String userTags, String mainInfo) {
        super(userTags, mainInfo, SaveType.GENERAL_RECORD);
    }

    GeneralRecord(String userTags, String mainInfo, LocalDateTime time, String uuid) {
        super(userTags, mainInfo, SaveType.GENERAL_RECORD, time, uuid);
    }

    @Override
    String getShortDisplayFormat() {
        return getUserTagsRaw();
    }

    public static GeneralRecord updateGeneralRecord(
            BaseRecord baseRecord, String tags, String mainInfo) {
        return new GeneralRecord(tags, mainInfo, baseRecord.getTimePoint(), baseRecord.getUuid());
    }
}
