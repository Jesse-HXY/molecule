package com.vtence.molecule.support;

import com.vtence.molecule.HttpStatus;
import com.vtence.molecule.Response;
import com.vtence.molecule.util.Charsets;
import com.vtence.molecule.util.HttpDate;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static java.lang.Long.parseLong;
import static java.lang.String.valueOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertThat;

public class MockResponse implements Response {

    private final ByteArrayOutputStream output = new ByteArrayOutputStream();

    private final Map<String, String> headers = new HashMap<String, String>();
    private final Map<String, String> cookies = new HashMap<String, String>();

    private HttpStatus status;
    int bufferSize = 0;

    public static MockResponse aResponse() {
        return new MockResponse();
    }

    public void redirectTo(String location) {
        header("Location", location);
    }

    public void assertRedirectedTo(String expectedLocation) {
        assertThat("redirection", header("Location"), equalTo(expectedLocation));
    }

    public void header(String name, String value) {
        if (output.size() > 0)
            throw new IllegalStateException("Cannot set header once response is committed");
        headers.put(name, value);
    }

    public void headerDate(String name, long date) {
        header(name, HttpDate.format(date));
    }

    public String header(String name) {
        return headers.get(name);
    }

    public void removeHeader(String name) {
        headers.remove(name);
    }

    public void cookie(String name, String value) {
        cookies.put(name, value);
    }

    public void assertNoHeader(String name) {
        assertHeader(name, nullValue());
    }

    public void assertHeader(String name, String value) {
        assertHeader(name, equalTo(value));
    }

    public void assertHeader(String name, Matcher<? super String> valueMatcher) {
        assertThat(name, header(name), valueMatcher);
    }

    public void contentType(String contentType) {
        header("Content-Type", contentType);
    }

    public String contentType() {
        return header("Content-Type");
    }

    public void assertContentType(String contentType) {
        assertContentType(equalTo(contentType));
    }

    public void assertContentType(Matcher<? super String> contentTypeMatcher) {
        assertHeader("Content-Type", contentTypeMatcher);
    }

    public int statusCode() {
        return status.code;
    }

    public MockResponse withStatus(HttpStatus status) {
        status(status);
        return this;
    }

    public void status(HttpStatus status) {
        this.status = status;
    }

    public void statusCode(int code) {
        status(HttpStatus.forCode(code));
    }

    public void assertStatus(int code) {
        assertThat("status code", status.code, equalTo(code));
    }

    public void assertStatus(HttpStatus expected) {
        assertThat("status", status, equalTo(expected));
    }

    public long contentLength() {
        return header("Content-Length") != null ? parseLong(header("Content-Length")) : 0;
    }

    public void contentLength(long length) {
        header("Content-Length", valueOf(length));
    }

    public Charset charset() {
        if (contentType() == null) return Charsets.ISO_8859_1;
        Charset charset = parseCharset(contentType());
        return charset != null ? charset : Charsets.ISO_8859_1;
    }

    public OutputStream outputStream() throws IOException {
        bufferSize = 0;
        return output;
    }

    public OutputStream outputStream(int bufferSize) throws IOException {
        if (bufferSize <= 0) return outputStream();
        this.bufferSize = bufferSize;
        return new BufferedOutputStream(output, bufferSize);
    }

    public Writer writer() throws IOException {
        return new OutputStreamWriter(outputStream(), charset());
    }

    public void body(String body) throws IOException {
        byte[] content = body.getBytes(charset());
        outputStream(content.length).write(content);
    }

    public void assertBody(String body) {
        assertBody(equalTo(body));
    }

    public void assertBody(Matcher<? super String> bodyMatcher) {
        assertThat("body", new String(content(), charset()), bodyMatcher);
    }

    public String body() {
        return new String(content(), charset());
    }

    public void assertContent(byte[] content) {
        assertArrayEquals("content", content, content());
    }

    public byte[] content() {
        return output.toByteArray();
    }

    public boolean empty() {
        return output.size() == 0;
    }

    public InputStream stream() {
        return new ByteArrayInputStream(content());
    }

    public void assertBufferSize(int size) {
        assertBufferSize(equalTo(size));
    }

    public void assertBufferSize(Matcher<Integer> sizeMatcher) {
        assertThat("buffer size", bufferSize, sizeMatcher);
    }

    public void assertContentSize(long size) {
        assertThat("content size", content().length, is((int) size));
    }

    public void assertContentEncodedAs(String encoding) throws IOException {
        assertThat("content encoding", CharsetDetector.detectedCharset(content()).toLowerCase(), containsString(encoding.toLowerCase()));
    }

    public void reset() throws IOException {
        output.reset();
    }

    public <T> T unwrap(Class<T> type) {
        throw new UnsupportedOperationException();
    }

    public MockResponse withContentType(String contentType) {
        contentType(contentType);
        return this;
    }

    public void assertCookie(String name, String value) {
        assertThat("cookies", cookies, Matchers.hasEntry(name, value));
    }

    public String toString() {
        return "mock response (" + output + ")";
    }

    private static final String TYPE = "[^/]+";
    private static final String SUBTYPE = "[^;]+";
    private static final String CHARSET = "charset=([^;]+)";
    private static final Pattern CONTENT_TYPE_FORMAT = Pattern.compile(String.format("%s/%s(?:;\\s*%s)+", TYPE, SUBTYPE, CHARSET));
    private static final int ENCODING = 1;

    private static Charset parseCharset(String contentType) {
        java.util.regex.Matcher matcher = CONTENT_TYPE_FORMAT.matcher(contentType);
        if (!matcher.matches()) return null;
        return Charset.forName(matcher.group(ENCODING));
    }
}