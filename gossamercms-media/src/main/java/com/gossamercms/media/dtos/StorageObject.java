package com.gossamercms.media.dtos;

import lombok.Getter;

import java.io.IOException;
import java.io.InputStream;

@Getter
public class StorageObject implements AutoCloseable {

    private final InputStream inputStream;
    private final String storageKey;
    private final long contentLength;

    public StorageObject(
            String storageKey,
            InputStream inputStream,
            long contentLength
    ) {
        this.storageKey = storageKey;
        this.inputStream = inputStream;
        this.contentLength = contentLength;
    }



    @Override
    public void close() throws IOException {
        inputStream.close();
    }
}