package com.khorashadi.store;


import java.io.IOException;

public interface Serializer<T> {
    void writeBytes(T object) throws IOException;
    boolean fileExists();
    T read() throws IOException;
    T noExceptionRead();
}
