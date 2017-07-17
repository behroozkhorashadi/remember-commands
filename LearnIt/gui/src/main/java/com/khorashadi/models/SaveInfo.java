package com.khorashadi.models;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public abstract class SaveInfo {
    private final String uuid = UUID.randomUUID().toString();

    private final List<String> userTags;

    private final String userTagsRaw;

    private final List<String> autoTags;
    private String mainInfo;
    public SaveInfo(String userTags, String mainInfo) {
        this.userTags = new LinkedList<>();
        autoTags = new LinkedList<>();
        userTagsRaw = userTags;
        for (String s : userTags.split(" ")) {
            this.userTags.add(s);
        }
        this.mainInfo = mainInfo;
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

    abstract String getShortDisplayFormat();
    public abstract String getFullDisplayFormat();
    //user generated tags
    // tool generated tags
    // main info
    // date
    // type enum Quick Note, Task, Person, General Note
}
