package com.vtence.molecule;

import com.vtence.molecule.helpers.Headers;
import com.vtence.molecule.http.ContentLanguage;
import com.vtence.molecule.http.ContentType;
import com.vtence.molecule.http.Cookie;
import com.vtence.molecule.http.HttpStatus;
import com.vtence.molecule.lib.BinaryBody;

import java.nio.charset.Charset;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static com.vtence.molecule.helpers.Charsets.ISO_8859_1;
import static com.vtence.molecule.http.HeaderNames.CONTENT_LANGUAGE;
import static com.vtence.molecule.http.HeaderNames.CONTENT_LENGTH;
import static com.vtence.molecule.http.HeaderNames.CONTENT_TYPE;
import static com.vtence.molecule.http.HeaderNames.LOCATION;
import static com.vtence.molecule.http.HttpDate.httpDate;
import static com.vtence.molecule.http.HttpStatus.SEE_OTHER;
import static com.vtence.molecule.lib.TextBody.text;
import static java.lang.Long.parseLong;
import static java.lang.String.valueOf;

/**
 * The HTTP response to write back to the client.
 */
public class Response {
    private final Headers headers = new Headers();
    private final Map<String, Cookie> cookies = new LinkedHashMap<String, Cookie>();

    private int statusCode = HttpStatus.OK.code;
    private String statusText = HttpStatus.OK.text;
    private Body body = BinaryBody.empty();

    public Response() {}

    /**
     * Sets the HTTP status for this response. This will set both the status code and the status text.
     *
     * <p>
     * The status is set to 200 OK by default.
     * </p>
     *
     * @param status the HTTP status to set
     */
    public Response status(HttpStatus status) {
        statusCode(status.code);
        statusText(status.text);
        return this;
    }

    /**
     * Sets the status code for this response. It is usually preferable to set the status and text
     * together with Response#status(com.vtence.molecule.http.HttpStatus).
     *
     * @see Response#status(com.vtence.molecule.http.HttpStatus)
     * @param code the status code to set
     */
    public Response statusCode(int code) {
        statusCode = code;
        return this;
    }

    /**
     * Gets the status code set on this response.
     *
     * @return the response status code
     */
    public int statusCode() {
        return statusCode;
    }

    /**
     * Sets the status text for this response. It is usually preferable to set the status and text
     * together with Response#status(com.vtence.molecule.http.HttpStatus).
     *
     * @see Response#status(com.vtence.molecule.http.HttpStatus)
     * @param text the status text to set
     */
    public Response statusText(String text) {
        statusText = text;
        return this;
    }

    /**
     * Gets the status text of this response.
     *
     * @return the response status text
     */
    public String statusText() {
        return statusText;
    }

    /**
     * Sends a SEE OTHER (303) redirect response to the client using the specified redirect location.
     *
     * @param location the url of the other location
     */
    public Response redirectTo(String location) {
        status(SEE_OTHER);
        header(LOCATION, location);
        return this;
    }

    /**
     * Checks if this response has a header of the specified name.
     *
     * @param name the header name
     * @return true is the header was found, false otherwise
     */
    public boolean hasHeader(String name) {
        return headers.has(name);
    }

    /**
     * Gets the names of all the headers to be sent with this response.
     * If the response has no header, the set will be empty.
     * <p>
     * Modifications to the returned set will not modify the response.
     * </p>
     * @return a (possibly empty) <code>Set</code> of header names to send
     */
    public Set<String> headerNames() {
        return headers.names();
    }

    /**
     * Gets the value of the specified header of this response. The name is case insensitive.
     *
     * <p>
     * In case there are multiple headers with that name, a comma separated list of values is returned.
     * </p>
     *
     * This method returns a null value if the response does not include any header of the specified name.
     *
     * @param name the name of the header to retrieve
     * @return the header value or null
     */
    public String header(String name) {
        return headers.get(name);
    }

    /**
     * Gets the value of the specified header as a <code>long</code>. The name is case insensitive.
     *
     * <p>
     * In case there are multiple headers with that name, a comma separated list of values is returned.
     * </p>
     *
     * This method returns -1 if the response does not include any header of the specified name.
     *
     * @param name the name of the header to retrieve
     * @return the long header value or -1
     * @throws java.lang.NumberFormatException is the header value cannot be converted to a <code>long</code>
     */
    public long headerAsLong(String name) {
        String value = header(name);
        return value != null ? parseLong(value) : -1;
    }

    /**
     * Adds a response header with the given name and value to this response.
     * <p>
     * This method allows response headers to have multiple values. The new value will be added to the list
     * of existing values for that header name.
     * </p>
     *
     * @param name the name of the header to send
     * @param value the additional value for that header
     */
    public Response addHeader(String name, String value) {
        headers.add(name, value);
        return this;
    }

    /**
     * Sets a header with the given name and value to be sent with this response.
     * If the header has already been set, the new value overwrites the previous one.
     * <p>
     * The {@link Response#hasHeader(String)} method can be used to test for the presence of the header before setting its value.
     * </p>
     *
     * @param name the name of the header to send
     * @param value the new value for that header
     */
    public Response header(String name, String value) {
        headers.put(name, value);
        return this;
    }

    /**
     * Sets a header with the given name and date value to be sent with this response.
     * If the header has already been set, the new value overwrites the previous one.
     * <p>
     * The {@link Response#hasHeader(String)} method can be used to test for the presence of the header before setting its value.
     * </p>
     *
     * @param name the name of the header to send
     * @param value the new date value for that header
     */
    public Response header(String name, Date value) {
        return header(name, httpDate(value));
    }

    /**
     * Sets a header with the given name and value to be sent with this response.
     * If the header has already been set, the new value overwrites the previous one.
     * <p>
     * The {@link Response#hasHeader(String)} method can be used to test for the presence of the header before setting its value.
     * </p>
     *
     * @param name the name of the header to send
     * @param value the new value for that header
     */
    public Response header(String name, Object value) {
        return header(name, valueOf(value));
    }

    /**
     * Removes the value of the specified header on this response. The name is case insensitive.
     *
     * <p>
     * In case there are multiple headers with that name, all values are removed.
     * </p>
     *
     * @param name the name of the header to remove
     */
    public Response removeHeader(String name) {
        headers.remove(name);
        return this;
    }

    /**
     * Gets the content type of this response.
     *
     * <p>Note that getting the content type can also be done explicitly using
     * {@link Response#header}. This is a convenient method for doing so.</p>
     *
     * @return the content type header value or null
     */
    public String contentType() {
        return header(CONTENT_TYPE);
    }

    /**
     * Sets the content type for this response. If the content type specifies a charset, this charset will
     * be used to encode text based responses.
     *
     * <p>Note that setting the content type can also be done explicitly using {@link Response#header}.
     *  This is a convenient method for doing so.</p>
     *
     * @see Response#charset(String) 
     * @param contentType the new content type value to set
     */
    public Response contentType(String contentType) {
        header(CONTENT_TYPE, contentType);
        return this;
    }

    /**
     * Gets the content length of this response.
     *
     * <p>Note that getting the content length can also be done explicitly using
     * {@link Response#header}. This is a convenient method for doing so.</p>
     *
     * @return the content length header value or null
     */
    public long contentLength() {
        return headerAsLong(CONTENT_LENGTH);
    }

    /**
     * Sets the content length for this response.
     *
     * <p>Note that setting the content length can also be done explicitly using {@link Response#header}.
     *  This is a convenient method for doing so</p>
     *
     * @param length the new content length value to set
     */
    public Response contentLength(long length) {
        header(CONTENT_LENGTH, length);
        return this;
    }

    /**
     * Sets a cookie on this response. The cookie will be added as a <code>Set-Cookie</code> header
     * when the response is committed.
     *
     * <p>If a cookie with the same name already exists, it is replaced by the new cookie.</p>
     *
     * @param cookie the cookie to return to the client
     */
    public Response cookie(Cookie cookie) {
        cookies.put(cookie.name(), cookie);
        return this;
    }

    /**
     * Sets a cookie with a specific name and value on this response.
     * The cookie will be added as a <code>Set-Cookie</code> header when the response is committed.
     *
     * <p>If a cookie with that name already exists, it is replaced by the new value.</p>
     *
     * @param name the name of the cookie to set
     * @param value the value to return to the client
     */
    public Response cookie(String name, String value) {
        return cookie(new Cookie(name, value));
    }

    /**
     * Removes a cookie from this response. The cookie will not be sent as a <code>Set-Cookie</code> header
     * when the response is committed.
     *
     * If no cookie with that name exists, the operation does nothing.
     *
     * @param name the name of the cookie to remove
     */
    public Response removeCookie(String name) {
        cookies.remove(name);
        return this;
    }

    /**
     * Checks if a cookie with a specific name has been set on this response.
     *
     * @param name the name of the cookie to check
     * @return true if cookie is set
     */
    public boolean hasCookie(String name) {
        return cookies.containsKey(name);
    }

    /**
     * Gets a cookie set on this response under a specific name.
     *
     * If the cookie exists as an HTTP header then it is returned
     * as a <code>Cookie</code> object - with the name, value, path as well as the optional domain part.
     *
     * @param name the name of the cookie to acquire
     * @return the cookie with that name or null
     */
    public Cookie cookie(String name) {
        return cookies.get(name);
    }

    /**
     * Gets the names of all cookies set on this response, or an empty set if none exist.
     *
     * Note that modifications to the set will not change cookies set on the response.
     *
     * @return the set of cookie names to return to the client
     */
    public Set<String> cookieNames() {
        return new HashSet<String>(cookies.keySet());
    }

    /**
     * Replaces the MIME charset of the content type of this response. If no charset is set, <code>ISO-8859-1</code>
     * is assumed. This method has no effect if no content type has been set on the response.
     *
     * Calling {@link Response#contentType(String)} with the value of <code>text/html</code> then
     * calling this method with the value <code>UTF-8</code> is equivalent to calling <code>contentType</code>
     * with the value <code>text/html; charset=UTF-8</code>
     * <p>
     * Note that the charset will be used for encoding character based bodies.
     * </p>
     *
     * @param charsetName the name of the character encoding to use
     */
    public Response charset(String charsetName) {
        ContentType contentType = ContentType.of(this);
        if (contentType == null) return this;
        contentType(new ContentType(contentType.type(), contentType.subType(), charsetName).toString());
        return this;
    }

    /**
     * Reads the MIME charset of this response. The charset is read from the content-type header. A default charset
     * of <code> ISO-8859-1</code> is assumed.
     *
     * @return the charset set in the content type header or <code> ISO-8859-1</code>
     */
    public Charset charset() {
        ContentType contentType = ContentType.of(this);
        if (contentType == null || contentType.charset() == null) {
            return ISO_8859_1;
        }
        return contentType.charset();
    }

    /**
     * Sets the language of this response using the <code>Content-Language</code> header.
     * If the <code>Content-Language</code> header is already set, its value is replaced by the new locale.
     *
     * @param locale the new language of this response
     */
    public Response locale(Locale locale) {
        header(CONTENT_LANGUAGE, new ContentLanguage().add(locale));
        return this;
    }

    /**
     * Adds a language to the list of languages of this response. Languages are sent to the client
     * using the <code>Content-Language</code> header.
     *
     * @param locale the additional language of this response
     */
    public Response addLocale(Locale locale) {
        header(CONTENT_LANGUAGE, ContentLanguage.of(this).add(locale));
        return this;
    }

    /**
     * Gets the default locale from this response <code>Content-Language</code> header.
     * If more than one <code>Content-Language</code> header value exists, the first one is returned.
     *
     * @return the default language of this response or null
     */
    public Locale locale() {
        List<Locale> locales = locales();
        return locales.isEmpty() ? null : locales.get(0);
    }

    /**
     * Gets the list of locales from this response <code>Content-Language</code> header. The
     * locales are provided in preference order. If the header is not present, the list will be empty.
     *
     * @return the list (which may be empty) of languages to send to the client
     */
    public List<Locale> locales() {
        return ContentLanguage.of(this).locales();
    }

    /**
     * Removes a specific locale from this response <code>Content-Language</code> header.
     * If that locale is not present in the list of values of the header, this method does nothing.
     *
     * @param locale the locale to remove from the list of languages to send to the client
     */
    public Response removeLocale(Locale locale) {
        header(CONTENT_LANGUAGE, ContentLanguage.of(this).remove(locale));
        return this;
    }

    /**
     * Sets the text content to write back to the client as the body of this response. The text content will be encoded
     * using the charset of the response.
     *
     * @see Response#charset(String)
     * @see Response#contentType(String)
     * @param text the text of the body to write back to the client
     */
    public Response body(String text) {
        return body(text(text));
    }

    /**
     * Sets the body to write back to the client with this response. Character bodies will be encoded
     * using the charset of the response.
     *
     * @see Response#charset(String)
     * @see Response#contentType(String)
     * @param body the body to write back to the client
     */
    public Response body(Body body) {
        this.body = body;
        return this;
    }

    /**
     * Gets the body to write back to the client with this response.
     *
     * @return the body to send to the client
     */
    public Body body() {
        return body;
    }

    /**
     * Gets the size of the body of this response.
     *
     * @return the size of the body as a long
     */
    public long size() {
        return body.size(charset());
    }

    /**
     * Checks whether the body of this response is empty, i.e. has a size of <code>0</code>.
     *
     * @return true is the body is empty, false otherwise
     */
    public boolean empty() {
        return size() == 0;
    }
}