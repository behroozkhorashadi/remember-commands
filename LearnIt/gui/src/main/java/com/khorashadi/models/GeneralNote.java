package com.khorashadi.models;


public final class GeneralNote extends SaveInfo {

    public GeneralNote(String userTags, String mainInfo) {
        super(userTags, mainInfo, SaveType.GENERAL_NOTE);
        System.out.println(userTags);
    }

    @Override
    String getShortDisplayFormat() {
        return getUserTagsRaw();
    }
}
