package com.khorashadi.models;


public final class GeneralNote extends SaveInfo {

    public GeneralNote(String userTags, String mainInfo) {
        super(userTags, mainInfo);
        System.out.println(userTags);
    }

    @Override
    String getShortDisplayFormat() {
        return getUserTagsRaw();
    }

    @Override
    public String getFullDisplayFormat() {
        return getMainInfo();
    }
}
