package com.khorashadi.models;

import com.khorashadi.validation.RaveFactory;
import com.uber.rave.annotation.Validated;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Validated(factory = RaveFactory.class)
public abstract class BaseRecord {
    private final String uuid;
    private final List<String> userTags;

    private final String userTagsRaw;

    private final LocalDateTime timePoint;

    private final SaveType saveType;

    private final String mainInfo;

    public BaseRecord(String userTags, String mainInfo, SaveType saveType) {
        this(userTags, mainInfo, saveType, LocalDateTime.now(), UUID.randomUUID().toString());
    }
    public BaseRecord(String userTags,
                      String mainInfo,
                      SaveType saveType,
                      LocalDateTime localDateTime,
                      String uuid) {
        this.uuid = uuid;
        this.userTags = new LinkedList<>();
        userTagsRaw = userTags;
        for (String s : userTags.split(" ")) {
            this.userTags.add(s);
        }
        this.mainInfo = mainInfo;
        timePoint = localDateTime;
        this.saveType = saveType;
    }

    public LocalDateTime getTimePoint() {
        return timePoint;
    }

    public String getUserTagsRaw() {
        return userTagsRaw;
    }

    public SaveType getSaveType() {
        return saveType;
    }

    public List<String> getUserTags() {
        return userTags;
    }

    public String getMainInfo() {
        return mainInfo;
    }

    public String getUuid() {
        return uuid;
    }

    @Override
    public String toString() {
        return getShortDisplayFormat();
    }

    public enum SaveType {
        GENERAL_RECORD,
        QUICK_NOTE,
        TASK
    }

    abstract String getShortDisplayFormat();
}
