package com.khorashadi.store;


import com.khorashadi.models.DateJsonAdapter;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;

import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

public class MoshiFileWriter<T> implements Serializer<T>{
    private final JsonAdapter<T> jsonAdapter;
    private final File file;

    public MoshiFileWriter(Type classType, String dirPath, String fileName) {
        File dir = new File(dirPath);
        if (!dir.exists() || !dir.isDirectory()) {
            throw new IllegalArgumentException("Directory path does not exist or is not " +
                    "a directory: " + dirPath);
        }
        file = new File(dirPath, fileName);
        Moshi moshi = new Moshi.Builder()
                .add(new DateJsonAdapter())
                .build();
        jsonAdapter = moshi.adapter(classType);
    }

    @Override
    public void writeBytes(T object) throws IOException {
        String json = jsonAdapter.toJson(object);
        BufferedSink sink = Okio.buffer(Okio.sink(file));
        sink.writeUtf8(json);
        sink.close();
    }

    @Override
    public boolean fileExists() {
        return file.exists();
    }

    @Override
    public T read() throws IOException {
        BufferedSource source = Okio.buffer(Okio.source(file));
        String json = source.readUtf8();
        T object = jsonAdapter.fromJson(json);
        return object;
    }

    @Override
    public T noExceptionRead() {
        try {
            return read();
        } catch (IOException e) {
            return null;
        }
    }
}
