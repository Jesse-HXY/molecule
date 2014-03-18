package com.vtence.molecule;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.Charset;

public interface Response {

    void redirectTo(String location);

    String header(String name);

    void header(String name, String value);

    void headerDate(String name, long date);

    void removeHeader(String name);

    void cookie(String name, String value);

    void contentType(String contentType);

    String contentType();

    int statusCode();

    void status(HttpStatus status);

    void statusCode(int code);

    long contentLength();

    void contentLength(long length);

    Charset charset();

    String charsetName();

    OutputStream outputStream() throws IOException;

    OutputStream outputStream(int bufferSize) throws IOException;

    Writer writer() throws IOException;

    void body(String body) throws IOException;

    void reset() throws IOException;

    <T> T unwrap(Class<T> type);
}
