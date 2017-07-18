package com.khorashadi.models;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public abstract class BaseRecord {
    private final String uuid = UUID.randomUUID().toString();
    private final List<String> userTags;
    private final String userTagsRaw;
    private final LocalDateTime timePoint;
    private final SaveType saveType;

    private String mainInfo;
    public BaseRecord(String userTags, String mainInfo, SaveType saveType) {
        this(userTags, mainInfo, saveType, LocalDateTime.now());
    }

    public BaseRecord(String userTags,
                      String mainInfo,
                      SaveType saveType,
                      LocalDateTime localDateTime) {
        this.userTags = new LinkedList<>();
        userTagsRaw = userTags;
        for (String s : userTags.split(" ")) {
            this.userTags.add(s);
        }
        this.mainInfo = mainInfo;
        timePoint = localDateTime;
        this.saveType = saveType;
    }

    public String getUserTagsRaw() {
        return userTagsRaw;
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
        PERSON,
        TASK
    }

    abstract String getShortDisplayFormat();
}
