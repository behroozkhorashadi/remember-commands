package com.khorashadi.store;

import com.khorashadi.models.GeneralRecord;
import com.squareup.moshi.Types;

import java.util.Collection;

public class MoshiWriterFactory {
    private MoshiWriterFactory() { }
    public static <T> MoshiFileWriter<Collection<T>> getFileWriter(Class<T> clazz, String dirPath) {
        if (clazz.equals(GeneralRecord.class)) {
            return new MoshiFileWriter<>(Types.newParameterizedType(Collection.class, clazz), dirPath,
                    "generalNotes.json");
        }
        throw new RuntimeException("Class type not supported " + clazz.getCanonicalName());
    }
}
