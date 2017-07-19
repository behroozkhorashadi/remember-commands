package com.khorashadi.models;

import com.khorashadi.validation.RaveFactory;
import com.uber.rave.annotation.Validated;

@Validated(factory = RaveFactory.class)
public final class GeneralRecord extends BaseRecord {

    public GeneralRecord(String userTags, String mainInfo) {
        super(userTags, mainInfo, SaveType.GENERAL_RECORD);
        System.out.println(userTags);
    }

    @Override
    String getShortDisplayFormat() {
        return getUserTagsRaw();
    }
}
